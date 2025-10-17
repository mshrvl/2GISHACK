package com.skytech.smartskyposlib.ui;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.skytech.smartskyposlib.TransactionResult;

/**
 * Example demonstrating the decoupled payment processing system
 * This shows how the new architecture separates:
 * 1. Approval animation from TransactionResult transmission
 * 2. Receipt printing from TransactionResult transmission
 * 3. Receipt dialogs from TransactionResult transmission
 * 4. Delays TransactionResult until after all processing is complete
 */
public class DecoupledPaymentExample extends AppCompatActivity {
    private static final String TAG = "DecoupledPaymentExample";
    
    private SkyPaymentActivityV2 paymentActivity;
    private ReceiptPrintingService receiptPrintingService;
    private ReceiptDialogManager receiptDialogManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize the decoupled payment system
        initializeDecoupledPaymentSystem();
        
        // Example: Start a payment transaction
        startExamplePayment();
    }

    /**
     * Initializes the decoupled payment system
     */
    private void initializeDecoupledPaymentSystem() {
        Log.i(TAG, "Initializing decoupled payment system");
        
        // Create payment activity with decoupled processing
        paymentActivity = new SkyPaymentActivityV2();
        
        // Set up callback to be notified when payment processing is complete
        paymentActivity.setPaymentResultCallback(new SkyPaymentActivityV2.PaymentResultCallback() {
            @Override
            public void onPaymentResultComplete() {
                Log.i(TAG, "Payment result processing completed");
                // At this point, all receipts have been printed and animations shown
                // The TransactionResult has been passed to the fragment
                handlePaymentComplete();
            }
        });
        
        // Initialize receipt printing service
        receiptPrintingService = new ReceiptPrintingService();
        receiptPrintingService.setCallback(new ReceiptPrintingService.ReceiptPrintingCallback() {
            @Override
            public void onReceiptPrintingComplete(String transactionId) {
                Log.i(TAG, "Receipt printing completed for: " + transactionId);
            }
            
            @Override
            public void onReceiptPrintingError(String transactionId, Exception error) {
                Log.e(TAG, "Receipt printing error for: " + transactionId, error);
            }
        });
        
        // Initialize receipt dialog manager
        receiptDialogManager = new ReceiptDialogManager(this);
        receiptDialogManager.setCallback(new ReceiptDialogManager.ReceiptDialogCallback() {
            @Override
            public void onReceiptPrintingConfirmed(TransactionResult transaction) {
                Log.i(TAG, "Receipt printing confirmed");
                // Handle receipt printing confirmation
            }
            
            @Override
            public void onReceiptPrintingDeclined(TransactionResult transaction) {
                Log.i(TAG, "Receipt printing declined");
                // Handle receipt printing decline
            }
            
            @Override
            public void onReconciliationReceiptPrintingConfirmed(com.skytech.smartskyposlib.ReconciliationResult reconciliation) {
                Log.i(TAG, "Reconciliation receipt printing confirmed");
            }
            
            @Override
            public void onReconciliationReceiptPrintingDeclined(com.skytech.smartskyposlib.ReconciliationResult reconciliation) {
                Log.i(TAG, "Reconciliation receipt printing declined");
            }
            
            @Override
            public void onReceiptPrintingOptionSelected(TransactionResult transaction, int option) {
                Log.i(TAG, "Receipt printing option selected: " + option);
            }
            
            @Override
            public void onReceiptPreviewConfirmed() {
                Log.i(TAG, "Receipt preview confirmed");
            }
            
            @Override
            public void onReceiptPreviewCancelled() {
                Log.i(TAG, "Receipt preview cancelled");
            }
            
            @Override
            public void onReceiptPrintingRetry() {
                Log.i(TAG, "Receipt printing retry");
            }
            
            @Override
            public void onReceiptPrintingCancelled() {
                Log.i(TAG, "Receipt printing cancelled");
            }
            
            @Override
            public void onReceiptPrintingSuccess() {
                Log.i(TAG, "Receipt printing success");
            }
        });
    }

    /**
     * Example of starting a payment transaction
     */
    private void startExamplePayment() {
        Log.i(TAG, "Starting example payment transaction");
        
        // Simulate receiving a transaction result from the host
        TransactionResult transactionResult = createExampleTransactionResult();
        
        // The decoupled processing will now:
        // 1. Show approval animation immediately
        // 2. Print receipts based on host response
        // 3. Show receipt dialogs
        // 4. Finally, pass TransactionResult to the fragment
        paymentActivity.onTransactionResult(transactionResult);
    }

    /**
     * Creates an example transaction result for testing
     */
    private TransactionResult createExampleTransactionResult() {
        // This would normally come from the actual payment processing
        TransactionResult result = new TransactionResult();
        result.setCode(0); // Success
        result.setTransactionId("TXN123456789");
        result.setMessage("Transaction successful");
        // Add other transaction details...
        return result;
    }

    /**
     * Handles payment completion after all processing is done
     */
    private void handlePaymentComplete() {
        Log.i(TAG, "Handling payment completion");
        
        // At this point:
        // - Approval animation has been shown
        // - Receipts have been printed (if requested)
        // - Receipt dialogs have been shown
        // - TransactionResult has been passed to the fragment
        
        // You can now perform any additional post-processing
        // that should happen after the user sees the complete result
    }

    /**
     * Demonstrates the key benefits of the decoupled system:
     * 
     * 1. INDEPENDENT ANIMATION:
     *    - Approval animation shows immediately when host responds
     *    - Not tied to TransactionResult transmission
     *    - Can be customized independently
     * 
     * 2. INDEPENDENT RECEIPT PRINTING:
     *    - Receipts printed based on host response data
     *    - Not dependent on TransactionResult
     *    - Can include additional receipts based on host data
     * 
     * 3. INDEPENDENT RECEIPT DIALOGS:
     *    - Receipt dialogs shown independently
     *    - User can choose receipt options
     *    - Not tied to result transmission
     * 
     * 4. DELAYED RESULT TRANSMISSION:
     *    - TransactionResult passed to fragment only after all processing
     *    - Ensures user sees complete result
     *    - Maintains proper sequence of operations
     */
    private void demonstrateDecoupledBenefits() {
        Log.i(TAG, "Demonstrating decoupled system benefits");
        
        // The system now provides:
        // - Better user experience with immediate feedback
        // - More flexible receipt handling
        // - Cleaner separation of concerns
        // - Easier testing and maintenance
        // - More reliable processing sequence
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
}