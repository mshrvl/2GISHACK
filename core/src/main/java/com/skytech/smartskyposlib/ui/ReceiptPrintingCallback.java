package com.skytech.smartskyposlib.ui;

/**
 * Callback interface for receipt printing operations.
 * This interface allows the UI to respond to printing events independently
 * from TransactionResult transmission.
 */
public interface ReceiptPrintingCallback {
    
    /**
     * Called when receipt printing starts
     */
    void onPrintingStarted();
    
    /**
     * Called when receipt printing completes
     * @param success true if printing was successful, false otherwise
     */
    void onPrintingCompleted(boolean success);
    
    /**
     * Called when an error occurs during printing
     * @param error error message describing what went wrong
     */
    void onPrintingError(String error);
}