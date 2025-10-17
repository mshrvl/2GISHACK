package com.skytech.smartskyposlib.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.transition.Fade;

import com.skytech.smartskyposlib.Currency;
import com.skytech.smartskyposlib.ui.R;
import com.skytech.smartskyposlib.ReconciliationResult;
import com.skytech.smartskyposlib.Terminal;
import com.skytech.smartskyposlib.TransactionResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SkyPaymentActivityV2 extends PaymentActivity {
    private static final String TAG = "SSPAAR SkyPaymentActiV2";
    private static final int RESULT_MARK_DISPLAY_TIME = 1200;
    private static final int RECEIPT_PRINTING_TIMEOUT = 5000;

    // UI Components
    SkyCardWaitFragment cardWaitFragment = new SkyCardWaitFragment();
    SkyInputFragment inputFragment = new SkyInputFragment();
    SkyInputFragment inputCurrencyFragment = new SkyInputFragment();
    SkySetupFragment setupFragment = new SkySetupFragment();
    
    @Nullable Fragment currentFragment;

    // Transaction processing components
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final PaymentResultProcessor paymentResultProcessor = new PaymentResultProcessor();
    private final ReceiptPrintingManager receiptPrintingManager = new ReceiptPrintingManager();
    
    // State management
    private TransactionResult pendingTransactionResult;
    private ReconciliationResult pendingReconciliationResult;
    private boolean isProcessingResult = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.skytech_activity_payment2);

        setupFragmentTransitions();
        initializeResultProcessor();
        showSetupFragment();
    }

    private void setupFragmentTransitions() {
        setupFragment.setEnterTransition(new Fade());
        inputFragment.setEnterTransition(new Fade());
        cardWaitFragment.setEnterTransition(new Fade());
        setupFragment.setExitTransition(new Fade());
        inputFragment.setExitTransition(new Fade());
        cardWaitFragment.setExitTransition(new Fade());
    }

    private void initializeResultProcessor() {
        paymentResultProcessor.setAnimationCompleteListener(() -> {
            Log.d(TAG, "Animation completed, proceeding with result processing");
            proceedWithResultProcessing();
        });

        receiptPrintingManager.setPrintingCompleteListener(() -> {
            Log.d(TAG, "Receipt printing completed, finalizing transaction");
            finalizeTransaction();
        });
    }

    @Override
    public void onTerminalSelect(List<Terminal> terminals) {
        Log.i(TAG, "onTerminalSelect " + terminals.size());
        if (terminals.size() == 1) {
            selectTerminal(terminals.get(0));
        } else {
            showTerminalSelectionFragment(terminals);
        }
    }

    private void showTerminalSelectionFragment(List<Terminal> terminals) {
        showTerminalFragment();
        inputFragment.setComment("Прочие терминалы");
        inputFragment.setTitle("Выберите терминал");
        
        ArrayList<SkyInputItemAdapter.InputItem> inputItems = new ArrayList<>();
        for (Terminal terminal : terminals) {
            SkyInputItemAdapter.InputItem item = new SkyInputItemAdapter.InputItem();
            item.text = terminal.getTerminalName();
            item.any = terminal;
            inputItems.add(item);
        }
        
        inputFragment.setInputItems(inputItems);
        inputFragment.setListener(new SkyInputItemAdapter.InputItemClickListener() {
            @Override
            public void onClick(View view, SkyInputItemAdapter.InputItem item) {
                Terminal terminal = (Terminal) item.any;
                selectTerminal(terminal);
            }
        });
    }

    @Override
    public void onCurrencySelect(List<Currency> currencies) {
        Log.i(TAG, "onCurrencySelect " + currencies.size());
        if (currencies.size() == 1) {
            selectCurrency(currencies.get(0));
        } else {
            showCurrencySelectionFragment(currencies);
        }
    }

    private void showCurrencySelectionFragment(List<Currency> currencies) {
        inputCurrencyFragment.setComment("Прочие валюты");
        inputCurrencyFragment.setTitle("Выберите валюту");
        
        ArrayList<SkyInputItemAdapter.InputItem> inputItems = new ArrayList<>();
        for (Currency currency : currencies) {
            SkyInputItemAdapter.InputItem item = new SkyInputItemAdapter.InputItem();
            item.text = currency.getCurrencyCaption();
            item.any = currency;
            inputItems.add(item);
        }
        
        inputCurrencyFragment.setInputItems(inputItems);
        inputCurrencyFragment.setListener(new SkyInputItemAdapter.InputItemClickListener() {
            @Override
            public void onClick(View view, SkyInputItemAdapter.InputItem item) {
                Currency currency = (Currency) item.any;
                selectCurrency(currency);
            }
        });
        showCurrencyFragment();
    }

    @Override
    public void onTransactionStarted() {
        if (!getLifecycle().getCurrentState().equals(Lifecycle.State.RESUMED)) {
            Log.d(TAG, "onTransactionStarted Activity is finished " + isDestroyed() + " " + isFinishing());
            return;
        }
        showCardWaitFragment();
    }

    @Override
    public void onStateChanged(final int state, final String message) {
        runOnUiThread(() -> cardWaitFragment.onStateChanged(state, message));
    }

    @Override
    public void onQrLinkReceived(final String qrLink) {
        Log.i(TAG, "onQrLinkReceived " + qrLink);
        runOnUiThread(() -> cardWaitFragment.onQrLink(qrLink));
    }

    @WorkerThread
    @Override
    public void onOperationNameReceived(String operationName) {
        Log.i(TAG, "onOperationNameChanged " + operationName);
        runOnUiThread(() -> cardWaitFragment.onOperationNameChanged(operationName));
    }

    /**
     * Host response received - start the decoupled processing flow
     * This method handles the host response and initiates independent processing chains
     */
    @WorkerThread
    @Override
    public void onTransactionResult(final TransactionResult transaction) {
        Log.i(TAG, "Host response received, starting decoupled processing");
        
        if (transaction.getCode() != 0) {
            // Handle error case immediately
            runOnUiThread(() -> handleTransactionError(transaction));
            return;
        }

        synchronized (this) {
            if (isProcessingResult) {
                Log.w(TAG, "Already processing a transaction result, ignoring duplicate");
                return;
            }
            isProcessingResult = true;
            pendingTransactionResult = transaction;
        }

        // Start independent processing chains
        startDecoupledProcessing(transaction);
    }

    @WorkerThread
    @Override
    public void onReconciliationResult(final ReconciliationResult transaction) {
        Log.i(TAG, "Reconciliation host response received, starting decoupled processing");
        
        if (transaction.getCode() != 0) {
            // Handle error case immediately
            runOnUiThread(() -> handleReconciliationError(transaction));
            return;
        }

        synchronized (this) {
            if (isProcessingResult) {
                Log.w(TAG, "Already processing a result, ignoring duplicate");
                return;
            }
            isProcessingResult = true;
            pendingReconciliationResult = transaction;
        }

        // Start independent processing chains
        startDecoupledReconciliationProcessing(transaction);
    }

    /**
     * Start independent processing chains for transaction result
     */
    private void startDecoupledProcessing(TransactionResult transaction) {
        executorService.execute(() -> {
            // Chain 1: Show success animation (independent)
            CompletableFuture<Void> animationFuture = CompletableFuture.runAsync(() -> {
                runOnUiThread(() -> {
                    cardWaitFragment.onTransactionResult(transaction);
                    paymentResultProcessor.startSuccessAnimation(RESULT_MARK_DISPLAY_TIME);
                });
            });

            // Chain 2: Handle receipt printing based on host response (independent)
            CompletableFuture<Void> printingFuture = CompletableFuture.runAsync(() -> {
                handleReceiptPrinting(transaction);
            });

            // Wait for both chains to complete before sending TransactionResult
            CompletableFuture.allOf(animationFuture, printingFuture)
                .thenRun(() -> {
                    Log.i(TAG, "All processing chains completed, ready to send TransactionResult");
                    // TransactionResult will be sent after all processing is complete
                })
                .exceptionally(throwable -> {
                    Log.e(TAG, "Error in processing chains", throwable);
                    return null;
                });
        });
    }

    /**
     * Start independent processing chains for reconciliation result
     */
    private void startDecoupledReconciliationProcessing(ReconciliationResult transaction) {
        executorService.execute(() -> {
            // Chain 1: Show success animation (independent)
            CompletableFuture<Void> animationFuture = CompletableFuture.runAsync(() -> {
                runOnUiThread(() -> {
                    cardWaitFragment.onReconciliationResult(transaction);
                    paymentResultProcessor.startSuccessAnimation(RESULT_MARK_DISPLAY_TIME);
                });
            });

            // Chain 2: Handle receipt printing based on host response (independent)
            CompletableFuture<Void> printingFuture = CompletableFuture.runAsync(() -> {
                handleReconciliationReceiptPrinting(transaction);
            });

            // Wait for both chains to complete
            CompletableFuture.allOf(animationFuture, printingFuture)
                .thenRun(() -> {
                    Log.i(TAG, "All reconciliation processing chains completed");
                })
                .exceptionally(throwable -> {
                    Log.e(TAG, "Error in reconciliation processing chains", throwable);
                    return null;
                });
        });
    }

    /**
     * Handle receipt printing based on host response, not TransactionResult
     */
    private void handleReceiptPrinting(TransactionResult transaction) {
        Log.i(TAG, "Starting receipt printing based on host response");
        
        // Check if receipt printing is required based on host response
        if (shouldPrintReceipt(transaction)) {
            receiptPrintingManager.printTransactionReceipt(transaction, new ReceiptPrintingCallback() {
                @Override
                public void onPrintingStarted() {
                    Log.d(TAG, "Receipt printing started");
                    runOnUiThread(() -> showReceiptPrintingDialog());
                }

                @Override
                public void onPrintingCompleted(boolean success) {
                    Log.d(TAG, "Receipt printing completed: " + success);
                    runOnUiThread(() -> hideReceiptPrintingDialog());
                }

                @Override
                public void onPrintingError(String error) {
                    Log.e(TAG, "Receipt printing error: " + error);
                    runOnUiThread(() -> showReceiptPrintingError(error));
                }
            });
        } else {
            Log.d(TAG, "No receipt printing required");
            // Notify that printing is "complete" (not required)
            receiptPrintingManager.notifyPrintingComplete();
        }
    }

    /**
     * Handle reconciliation receipt printing
     */
    private void handleReconciliationReceiptPrinting(ReconciliationResult transaction) {
        Log.i(TAG, "Starting reconciliation receipt printing based on host response");
        
        if (shouldPrintReconciliationReceipt(transaction)) {
            receiptPrintingManager.printReconciliationReceipt(transaction, new ReceiptPrintingCallback() {
                @Override
                public void onPrintingStarted() {
                    Log.d(TAG, "Reconciliation receipt printing started");
                    runOnUiThread(() -> showReceiptPrintingDialog());
                }

                @Override
                public void onPrintingCompleted(boolean success) {
                    Log.d(TAG, "Reconciliation receipt printing completed: " + success);
                    runOnUiThread(() -> hideReceiptPrintingDialog());
                }

                @Override
                public void onPrintingError(String error) {
                    Log.e(TAG, "Reconciliation receipt printing error: " + error);
                    runOnUiThread(() -> showReceiptPrintingError(error));
                }
            });
        } else {
            receiptPrintingManager.notifyPrintingComplete();
        }
    }

    /**
     * Called when animation processing is complete
     */
    private void proceedWithResultProcessing() {
        Log.d(TAG, "Animation complete, checking if ready to finalize");
        checkAndFinalizeIfReady();
    }

    /**
     * Called when receipt printing is complete
     */
    private void finalizeTransaction() {
        Log.d(TAG, "Receipt printing complete, checking if ready to finalize");
        checkAndFinalizeIfReady();
    }

    /**
     * Check if both animation and printing are complete, then send TransactionResult
     */
    private void checkAndFinalizeIfReady() {
        if (paymentResultProcessor.isAnimationComplete() && receiptPrintingManager.isPrintingComplete()) {
            Log.i(TAG, "Both animation and printing complete, sending TransactionResult");
            sendFinalTransactionResult();
        }
    }

    /**
     * Send the final TransactionResult after all processing is complete
     */
    private void sendFinalTransactionResult() {
        mainHandler.post(() -> {
            synchronized (this) {
                if (pendingTransactionResult != null) {
                    Log.i(TAG, "Sending final TransactionResult");
                    // Send the result to the calling activity/fragment
                    deliverTransactionResult(pendingTransactionResult);
                    pendingTransactionResult = null;
                } else if (pendingReconciliationResult != null) {
                    Log.i(TAG, "Sending final ReconciliationResult");
                    deliverReconciliationResult(pendingReconciliationResult);
                    pendingReconciliationResult = null;
                }
                isProcessingResult = false;
            }
        });
    }

    /**
     * Deliver the transaction result to the calling component
     */
    private void deliverTransactionResult(TransactionResult result) {
        // Set result and finish activity
        setResult(RESULT_OK, createResultIntent(result));
        finish();
    }

    /**
     * Deliver the reconciliation result to the calling component
     */
    private void deliverReconciliationResult(ReconciliationResult result) {
        // Set result and finish activity
        setResult(RESULT_OK, createReconciliationResultIntent(result));
        finish();
    }

    // Utility methods for determining receipt printing requirements
    private boolean shouldPrintReceipt(TransactionResult transaction) {
        // Implement logic based on transaction type, terminal settings, etc.
        return transaction.shouldPrintMerchantReceipt() || transaction.shouldPrintCustomerReceipt();
    }

    private boolean shouldPrintReconciliationReceipt(ReconciliationResult transaction) {
        // Implement logic for reconciliation receipts
        return transaction.shouldPrintReceipt();
    }

    // UI methods for receipt printing dialogs
    private void showReceiptPrintingDialog() {
        // Show receipt printing progress dialog
        cardWaitFragment.showReceiptPrintingProgress();
    }

    private void hideReceiptPrintingDialog() {
        // Hide receipt printing dialog
        cardWaitFragment.hideReceiptPrintingProgress();
    }

    private void showReceiptPrintingError(String error) {
        // Show receipt printing error dialog
        cardWaitFragment.showReceiptPrintingError(error);
    }

    // Error handling methods
    private void handleTransactionError(TransactionResult transaction) {
        Log.e(TAG, "Transaction error: " + transaction.getMessage());
        deliverTransactionResult(transaction);
    }

    private void handleReconciliationError(ReconciliationResult transaction) {
        Log.e(TAG, "Reconciliation error: " + transaction.getMessage());
        deliverReconciliationResult(transaction);
    }

    // Camera preview setup (unchanged)
    public void setupCameraPreview(@NonNull ViewGroup fillInto) {
        // Implementation remains the same
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        if (currentFragment == setupFragment) {
            return;
        }
        super.onBackPressed();
    }

    // Fragment management methods (unchanged)
    private void showSetupFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.skytechCl, setupFragment, null)
                .commit();
        currentFragment = setupFragment;
    }

    private void showTerminalFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.skytechCl, inputFragment, null);
        if (currentFragment != setupFragment) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
        currentFragment = inputFragment;
    }

    private void showCurrencyFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.skytechCl, inputCurrencyFragment, null);
        if (currentFragment != setupFragment) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
        currentFragment = inputCurrencyFragment;
    }

    private void showCardWaitFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction
                .replace(R.id.skytechCl, cardWaitFragment, null)
                .addToBackStack(null)
                .commit();
        currentFragment = cardWaitFragment;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    // Abstract methods to be implemented by subclasses
    protected abstract Intent createResultIntent(TransactionResult result);
    protected abstract Intent createReconciliationResultIntent(ReconciliationResult result);
}