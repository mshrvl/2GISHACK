package com.skytech.smartskyposlib.ui;

import android.os.Bundle;
import android.os.SystemClock;
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

public class SkyPaymentActivityV2 extends PaymentActivity {
    private static final String TAG = "SSPAAR SkyPaymentActiV2";
    private static final int RESULT_MARK_DISPLAY_TIME = 1200;
    
    SkyCardWaitFragment cardWaitFragment = new SkyCardWaitFragment();
    SkyInputFragment inputFragment = new SkyInputFragment();
    SkyInputFragment inputCurrencyFragment = new SkyInputFragment();
    SkySetupFragment setupFragment = new SkySetupFragment();
    
    @Nullable Fragment currentFragment;
    
    // New fields for decoupling
    private TransactionResult pendingTransactionResult;
    private ReconciliationResult pendingReconciliationResult;
    private boolean isProcessingReceipts = false;
    private boolean isShowingAnimation = false;
    private PaymentResultCallback paymentResultCallback;
    
    // Services for decoupled processing
    private ReceiptPrintingService receiptPrintingService;
    private ReceiptDialogManager receiptDialogManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.skytech_activity_payment2);
        
        setupFragment.setEnterTransition(new Fade());
        inputFragment.setEnterTransition(new Fade());
        cardWaitFragment.setEnterTransition(new Fade());
        setupFragment.setExitTransition(new Fade());
        inputFragment.setExitTransition(new Fade());
        cardWaitFragment.setExitTransition(new Fade());
        
        // Initialize decoupled services
        initializeServices();
        
        showSetupFragment();
    }
    
    /**
     * Initializes the decoupled services
     */
    private void initializeServices() {
        // Initialize receipt printing service
        receiptPrintingService = new ReceiptPrintingService();
        receiptPrintingService.setCallback(new ReceiptPrintingService.ReceiptPrintingCallback() {
            @Override
            public void onReceiptPrintingComplete(String transactionId) {
                Log.i(TAG, "Receipt printing completed for: " + transactionId);
                runOnUiThread(() -> {
                    receiptDialogManager.showReceiptPrintingSuccess();
                });
            }
            
            @Override
            public void onReceiptPrintingError(String transactionId, Exception error) {
                Log.e(TAG, "Receipt printing error for: " + transactionId, error);
                runOnUiThread(() -> {
                    receiptDialogManager.showReceiptPrintingError(error.getMessage());
                });
            }
        });
        
        // Initialize receipt dialog manager
        receiptDialogManager = new ReceiptDialogManager(this);
        receiptDialogManager.setCallback(new ReceiptDialogManager.ReceiptDialogCallback() {
            @Override
            public void onReceiptPrintingConfirmed(TransactionResult transaction) {
                Log.i(TAG, "Receipt printing confirmed for transaction: " + transaction.getTransactionId());
                startReceiptPrinting(transaction);
            }
            
            @Override
            public void onReceiptPrintingDeclined(TransactionResult transaction) {
                Log.i(TAG, "Receipt printing declined for transaction: " + transaction.getTransactionId());
                // Skip receipt printing and proceed to result display
                completePaymentResultProcessing();
            }
            
            @Override
            public void onReconciliationReceiptPrintingConfirmed(ReconciliationResult reconciliation) {
                Log.i(TAG, "Reconciliation receipt printing confirmed");
                startReconciliationReceiptPrinting(reconciliation);
            }
            
            @Override
            public void onReconciliationReceiptPrintingDeclined(ReconciliationResult reconciliation) {
                Log.i(TAG, "Reconciliation receipt printing declined");
                // Skip receipt printing and proceed to result display
                completePaymentResultProcessing();
            }
            
            @Override
            public void onReceiptPrintingOptionSelected(TransactionResult transaction, int option) {
                Log.i(TAG, "Receipt printing option selected: " + option);
                handleReceiptPrintingOption(transaction, option);
            }
            
            @Override
            public void onReceiptPreviewConfirmed() {
                Log.i(TAG, "Receipt preview confirmed");
                // Proceed with receipt printing
            }
            
            @Override
            public void onReceiptPreviewCancelled() {
                Log.i(TAG, "Receipt preview cancelled");
                completePaymentResultProcessing();
            }
            
            @Override
            public void onReceiptPrintingRetry() {
                Log.i(TAG, "Receipt printing retry requested");
                // Retry receipt printing
                if (pendingTransactionResult != null) {
                    startReceiptPrinting(pendingTransactionResult);
                } else if (pendingReconciliationResult != null) {
                    startReconciliationReceiptPrinting(pendingReconciliationResult);
                }
            }
            
            @Override
            public void onReceiptPrintingCancelled() {
                Log.i(TAG, "Receipt printing cancelled");
                completePaymentResultProcessing();
            }
            
            @Override
            public void onReceiptPrintingSuccess() {
                Log.i(TAG, "Receipt printing success acknowledged");
                completePaymentResultProcessing();
            }
        });
    }

    @Override
    public void onTerminalSelect(List<Terminal> terminals) {
        Log.i(TAG, "onTerminalSelect " + terminals.size());
        if (terminals.size() == 1) {
            selectTerminal(terminals.get(0));
        } else {
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
    }

    @Override
    public void onCurrencySelect(List<com.skytech.smartskyposlib.Currency> currencies) {
        Log.i(TAG, "onCurrencySelect " + currencies.size());
        if (currencies.size() == 1) {
            selectCurrency(currencies.get(0));
        } else {
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cardWaitFragment.onStateChanged(state, message);
            }
        });
    }

    @Override
    public void onQrLinkReceived(final String qrLink) {
        Log.i(TAG, "onQrLinkReceived " + qrLink);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cardWaitFragment.onQrLink(qrLink);
            }
        });
    }

    public void setupCameraPreview(@NonNull ViewGroup fillInto) {
    }

    @WorkerThread
    @Override
    public void onOperationNameReceived(String operationName) {
        Log.i(TAG, "onOperationNameChanged " + operationName);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cardWaitFragment.onOperationNameChanged(operationName);
            }
        });
    }

    @WorkerThread
    @Override
    public void onTransactionResult(final TransactionResult transaction) {
        if (transaction.getCode() != 0) {
            // For failed transactions, immediately pass the result
            runOnUiThread(() -> {
                cardWaitFragment.onTransactionResult(transaction);
            });
            return;
        }
        
        // For successful transactions, start the decoupled process
        pendingTransactionResult = transaction;
        startPaymentResultProcessing();
    }

    @WorkerThread
    @Override
    public void onReconciliationResult(final ReconciliationResult transaction) {
        if (transaction.getCode() != 0) {
            // For failed transactions, immediately pass the result
            runOnUiThread(() -> {
                cardWaitFragment.onReconciliationResult(transaction);
            });
            return;
        }
        
        // For successful transactions, start the decoupled process
        pendingReconciliationResult = transaction;
        startPaymentResultProcessing();
    }

    /**
     * Starts the decoupled payment result processing sequence:
     * 1. Show approval animation
     * 2. Print receipts
     * 3. Show receipt dialogs
     * 4. Finally, pass the TransactionResult to the fragment
     */
    private void startPaymentResultProcessing() {
        if (isProcessingReceipts) {
            Log.w(TAG, "Payment result processing already in progress");
            return;
        }
        
        isProcessingReceipts = true;
        isShowingAnimation = true;
        
        // Step 1: Show approval animation immediately
        runOnUiThread(() -> {
            showApprovalAnimation();
        });
        
        // Step 2: Start receipt printing process
        new Thread(() -> {
            try {
                // Wait for animation to complete
                SystemClock.sleep(RESULT_MARK_DISPLAY_TIME);
                isShowingAnimation = false;
                
                // Step 3: Print receipts based on host response
                printReceipts();
                
                // Step 4: Show receipt dialogs if needed
                runOnUiThread(() -> {
                    showReceiptDialogs();
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error during payment result processing", e);
            } finally {
                // Step 5: Finally, pass the result to the fragment
                completePaymentResultProcessing();
            }
        }).start();
    }

    /**
     * Shows the approval animation (checkmark, success state, etc.)
     */
    private void showApprovalAnimation() {
        Log.i(TAG, "Showing approval animation");
        // This method should trigger the visual approval animation
        // The actual implementation depends on your UI framework
        if (pendingTransactionResult != null) {
            cardWaitFragment.showApprovalAnimation(pendingTransactionResult);
        } else if (pendingReconciliationResult != null) {
            cardWaitFragment.showApprovalAnimation(pendingReconciliationResult);
        }
    }

    /**
     * Starts receipt printing process with user confirmation
     */
    private void startReceiptPrinting(TransactionResult transaction) {
        Log.i(TAG, "Starting receipt printing process for transaction: " + transaction.getTransactionId());
        
        // Show receipt printing confirmation dialog
        receiptDialogManager.showReceiptPrintingConfirmation(transaction);
    }
    
    /**
     * Starts reconciliation receipt printing process with user confirmation
     */
    private void startReconciliationReceiptPrinting(ReconciliationResult reconciliation) {
        Log.i(TAG, "Starting reconciliation receipt printing process");
        
        // Show receipt printing confirmation dialog
        receiptDialogManager.showReceiptPrintingConfirmation(reconciliation);
    }
    
    /**
     * Handles receipt printing option selection
     */
    private void handleReceiptPrintingOption(TransactionResult transaction, int option) {
        Log.i(TAG, "Handling receipt printing option: " + option);
        
        switch (option) {
            case 0: // Print all receipts
                printAllReceipts(transaction);
                break;
            case 1: // Customer receipt only
                printCustomerReceiptOnly(transaction);
                break;
            case 2: // Merchant receipt only
                printMerchantReceiptOnly(transaction);
                break;
            case 3: // Don't print
                completePaymentResultProcessing();
                break;
        }
    }
    
    /**
     * Prints all receipts for the transaction
     */
    private void printAllReceipts(TransactionResult transaction) {
        Log.i(TAG, "Printing all receipts for transaction: " + transaction.getTransactionId());
        
        receiptPrintingService.printTransactionReceipts(transaction)
                .thenRun(() -> {
                    Log.i(TAG, "All receipts printed successfully");
                    runOnUiThread(() -> {
                        receiptDialogManager.showReceiptPrintingSuccess();
                    });
                })
                .exceptionally(throwable -> {
                    Log.e(TAG, "Error printing all receipts", throwable);
                    runOnUiThread(() -> {
                        receiptDialogManager.showReceiptPrintingError(throwable.getMessage());
                    });
                    return null;
                });
    }
    
    /**
     * Prints only customer receipt
     */
    private void printCustomerReceiptOnly(TransactionResult transaction) {
        Log.i(TAG, "Printing customer receipt only for transaction: " + transaction.getTransactionId());
        // Implement customer receipt only printing
        completePaymentResultProcessing();
    }
    
    /**
     * Prints only merchant receipt
     */
    private void printMerchantReceiptOnly(TransactionResult transaction) {
        Log.i(TAG, "Printing merchant receipt only for transaction: " + transaction.getTransactionId());
        // Implement merchant receipt only printing
        completePaymentResultProcessing();
    }

    /**
     * Prints receipts based on the host response, not the TransactionResult
     */
    private void printReceipts() {
        Log.i(TAG, "Starting receipt printing process");
        
        // This method should handle receipt printing based on the actual host response
        // The implementation should be independent of TransactionResult transmission
        
        if (pendingTransactionResult != null) {
            // Show receipt printing options dialog
            receiptDialogManager.showReceiptPrintingOptions(pendingTransactionResult);
        }
        
        if (pendingReconciliationResult != null) {
            // Start reconciliation receipt printing
            startReconciliationReceiptPrinting(pendingReconciliationResult);
        }
    }

    /**
     * Shows receipt dialogs (print confirmation, etc.)
     */
    private void showReceiptDialogs() {
        Log.i(TAG, "Showing receipt dialogs");
        // This method should handle any receipt-related dialogs
        // These should be independent of TransactionResult transmission
        
        // The actual dialog showing is now handled by the ReceiptDialogManager
        // This method can be used for any additional dialog logic
    }

    /**
     * Completes the payment result processing by passing the result to the fragment
     */
    private void completePaymentResultProcessing() {
        Log.i(TAG, "Completing payment result processing");
        
        runOnUiThread(() -> {
            // Now that all receipts and animations are complete, pass the result
            if (pendingTransactionResult != null) {
                cardWaitFragment.onTransactionResult(pendingTransactionResult);
                pendingTransactionResult = null;
            }
            
            if (pendingReconciliationResult != null) {
                cardWaitFragment.onReconciliationResult(pendingReconciliationResult);
                pendingReconciliationResult = null;
            }
            
            // Reset processing state
            isProcessingReceipts = false;
            isShowingAnimation = false;
            
            // Notify callback if set
            if (paymentResultCallback != null) {
                paymentResultCallback.onPaymentResultComplete();
            }
        });
    }

    /**
     * Sets a callback to be notified when payment result processing is complete
     */
    public void setPaymentResultCallback(PaymentResultCallback callback) {
        this.paymentResultCallback = callback;
    }

    /**
     * Interface for payment result processing callbacks
     */
    public interface PaymentResultCallback {
        void onPaymentResultComplete();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Cleanup services
        if (receiptPrintingService != null) {
            receiptPrintingService.shutdown();
        }
        
        if (receiptDialogManager != null) {
            receiptDialogManager.hideCurrentDialog();
        }
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        if (currentFragment == setupFragment) {
            return;
        }
        super.onBackPressed();
    }

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
}