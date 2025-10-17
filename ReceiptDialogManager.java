package com.skytech.smartskyposlib.ui;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import com.skytech.smartskyposlib.ReconciliationResult;
import com.skytech.smartskyposlib.TransactionResult;

/**
 * Manages receipt-related dialogs independently of TransactionResult transmission
 * This class handles all receipt printing dialogs and user interactions
 */
public class ReceiptDialogManager {
    private static final String TAG = "ReceiptDialogManager";
    
    private final Context context;
    private ReceiptDialogCallback callback;
    private Dialog currentDialog;

    public ReceiptDialogManager(Context context) {
        this.context = context;
    }

    /**
     * Sets callback for dialog events
     */
    public void setCallback(ReceiptDialogCallback callback) {
        this.callback = callback;
    }

    /**
     * Shows receipt printing confirmation dialog
     * This should be called independently of TransactionResult transmission
     */
    public void showReceiptPrintingConfirmation(TransactionResult transaction) {
        Log.i(TAG, "Showing receipt printing confirmation for transaction: " + transaction.getTransactionId());
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Печать чеков")
                .setMessage("Печатать чеки для транзакции?")
                .setPositiveButton("Да", (dialog, which) -> {
                    Log.i(TAG, "User confirmed receipt printing");
                    if (callback != null) {
                        callback.onReceiptPrintingConfirmed(transaction);
                    }
                    dialog.dismiss();
                })
                .setNegativeButton("Нет", (dialog, which) -> {
                    Log.i(TAG, "User declined receipt printing");
                    if (callback != null) {
                        callback.onReceiptPrintingDeclined(transaction);
                    }
                    dialog.dismiss();
                })
                .setCancelable(false);
        
        currentDialog = builder.create();
        currentDialog.show();
    }

    /**
     * Shows receipt printing confirmation dialog for reconciliation
     */
    public void showReceiptPrintingConfirmation(ReconciliationResult reconciliation) {
        Log.i(TAG, "Showing receipt printing confirmation for reconciliation");
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Печать чеков")
                .setMessage("Печатать чеки для сверки?")
                .setPositiveButton("Да", (dialog, which) -> {
                    Log.i(TAG, "User confirmed reconciliation receipt printing");
                    if (callback != null) {
                        callback.onReconciliationReceiptPrintingConfirmed(reconciliation);
                    }
                    dialog.dismiss();
                })
                .setNegativeButton("Нет", (dialog, which) -> {
                    Log.i(TAG, "User declined reconciliation receipt printing");
                    if (callback != null) {
                        callback.onReconciliationReceiptPrintingDeclined(reconciliation);
                    }
                    dialog.dismiss();
                })
                .setCancelable(false);
        
        currentDialog = builder.create();
        currentDialog.show();
    }

    /**
     * Shows receipt printing progress dialog
     */
    public void showReceiptPrintingProgress() {
        Log.i(TAG, "Showing receipt printing progress");
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Печать чеков")
                .setMessage("Печатаем чеки...")
                .setCancelable(false);
        
        currentDialog = builder.create();
        currentDialog.show();
    }

    /**
     * Shows receipt printing options dialog
     */
    public void showReceiptPrintingOptions(TransactionResult transaction) {
        Log.i(TAG, "Showing receipt printing options for transaction: " + transaction.getTransactionId());
        
        String[] options = {"Печать все чеки", "Только клиентский чек", "Только торговый чек", "Не печатать"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Опции печати чеков")
                .setItems(options, (dialog, which) -> {
                    Log.i(TAG, "User selected option: " + which);
                    if (callback != null) {
                        callback.onReceiptPrintingOptionSelected(transaction, which);
                    }
                    dialog.dismiss();
                })
                .setCancelable(false);
        
        currentDialog = builder.create();
        currentDialog.show();
    }

    /**
     * Shows receipt preview dialog
     */
    public void showReceiptPreview(String receiptContent) {
        Log.i(TAG, "Showing receipt preview");
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Предварительный просмотр чека")
                .setMessage(receiptContent)
                .setPositiveButton("Печатать", (dialog, which) -> {
                    Log.i(TAG, "User confirmed receipt printing from preview");
                    if (callback != null) {
                        callback.onReceiptPreviewConfirmed();
                    }
                    dialog.dismiss();
                })
                .setNegativeButton("Отмена", (dialog, which) -> {
                    Log.i(TAG, "User cancelled receipt printing from preview");
                    if (callback != null) {
                        callback.onReceiptPreviewCancelled();
                    }
                    dialog.dismiss();
                })
                .setCancelable(false);
        
        currentDialog = builder.create();
        currentDialog.show();
    }

    /**
     * Shows receipt printing error dialog
     */
    public void showReceiptPrintingError(String errorMessage) {
        Log.e(TAG, "Showing receipt printing error: " + errorMessage);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Ошибка печати чеков")
                .setMessage("Ошибка при печати чеков: " + errorMessage)
                .setPositiveButton("Повторить", (dialog, which) -> {
                    Log.i(TAG, "User chose to retry receipt printing");
                    if (callback != null) {
                        callback.onReceiptPrintingRetry();
                    }
                    dialog.dismiss();
                })
                .setNegativeButton("Отмена", (dialog, which) -> {
                    Log.i(TAG, "User cancelled receipt printing retry");
                    if (callback != null) {
                        callback.onReceiptPrintingCancelled();
                    }
                    dialog.dismiss();
                })
                .setCancelable(false);
        
        currentDialog = builder.create();
        currentDialog.show();
    }

    /**
     * Shows receipt printing success dialog
     */
    public void showReceiptPrintingSuccess() {
        Log.i(TAG, "Showing receipt printing success");
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Печать чеков")
                .setMessage("Чеки успешно напечатаны")
                .setPositiveButton("OK", (dialog, which) -> {
                    Log.i(TAG, "User acknowledged receipt printing success");
                    if (callback != null) {
                        callback.onReceiptPrintingSuccess();
                    }
                    dialog.dismiss();
                })
                .setCancelable(false);
        
        currentDialog = builder.create();
        currentDialog.show();
    }

    /**
     * Hides current dialog
     */
    public void hideCurrentDialog() {
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
            currentDialog = null;
        }
    }

    /**
     * Checks if a dialog is currently showing
     */
    public boolean isDialogShowing() {
        return currentDialog != null && currentDialog.isShowing();
    }

    /**
     * Callback interface for receipt dialog events
     */
    public interface ReceiptDialogCallback {
        void onReceiptPrintingConfirmed(TransactionResult transaction);
        void onReceiptPrintingDeclined(TransactionResult transaction);
        void onReconciliationReceiptPrintingConfirmed(ReconciliationResult reconciliation);
        void onReconciliationReceiptPrintingDeclined(ReconciliationResult reconciliation);
        void onReceiptPrintingOptionSelected(TransactionResult transaction, int option);
        void onReceiptPreviewConfirmed();
        void onReceiptPreviewCancelled();
        void onReceiptPrintingRetry();
        void onReceiptPrintingCancelled();
        void onReceiptPrintingSuccess();
    }
}