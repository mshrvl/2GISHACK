package com.skytech.smartskyposlib.ui

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.activity.result.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Enhanced PaymentFragment with decoupled transaction processing.
 * This fragment handles payment operations with proper separation of concerns:
 * - Animation and UI feedback are independent of result transmission
 * - Receipt printing is based on host responses, not TransactionResult delivery
 * - TransactionResult is delivered only after all processing is complete
 */
abstract class PaymentFragment : Fragment() {

    companion object {
        private const val TAG = "PaymentFragment"
    }

    private var lastTransactionParams: TransactionParams? = null
    private var isProcessingTransaction = false

    /**
     * Enhanced transaction contract that supports decoupled processing
     */
    private fun transactionContact(type: String) =
        object : ActivityResultContract<TransactionParams, TransactionResult?>() {
            override fun createIntent(context: Context, input: TransactionParams): Intent {
                val intent = Intent(context, SomersPayActivity::class.java)
                intent.putExtra(PaymentActivity.PARAMS_KEY, input)
                intent.putExtra(PaymentActivity.TYPE_KEY, type)

                lastTransactionParams = input
                isProcessingTransaction = true
                return intent
            }

            override fun parseResult(resultCode: Int, intent: Intent?): TransactionResult? {
                isProcessingTransaction = false
                
                return if (resultCode == Activity.RESULT_OK) {
                    Timber.tag(TAG).i("Transaction completed successfully")
                    intent?.parcelable(PaymentActivity.RESULT_KEY)
                } else {
                    Timber.tag(TAG).w("Transaction cancelled or failed")
                    null
                }
            }
        }

    // Payment result launchers with enhanced processing
    protected val paymentResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_PAYMENT)) { result ->
            handleTransactionResult(PaymentActivity.TYPE_PAYMENT, result)
        }
    
    protected val fpsPaymentResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_QR_PAYMENT)) { result ->
            handleTransactionResult(PaymentActivity.TYPE_QR_PAYMENT, result)
        }
    
    protected val ecomPaymentResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_ECOM_PAYMENT)) { result ->
            handleTransactionResult(PaymentActivity.TYPE_ECOM_PAYMENT, result)
        }
    
    protected val cancelResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_CANCEL)) { result ->
            handleTransactionResult(PaymentActivity.TYPE_CANCEL, result)
        }
    
    protected val refundResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_REFUND)) { result ->
            handleTransactionResult(PaymentActivity.TYPE_REFUND, result)
        }
    
    protected val preAuthResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_PRE_AUTH)) { result ->
            handleTransactionResult(PaymentActivity.TYPE_PRE_AUTH, result)
        }
    
    protected val preAuthIncrementResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_PRE_AUTH_INCREMENT)) { result ->
            handleTransactionResult(PaymentActivity.TYPE_PRE_AUTH_INCREMENT, result)
        }
    
    protected val preAuthConfirmResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_PRE_AUTH_CONFIRM)) { result ->
            handleTransactionResult(PaymentActivity.TYPE_PRE_AUTH_CONFIRM, result)
        }
    
    protected val qrRefundResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_QR_REFUND)) { result ->
            handleTransactionResult(PaymentActivity.TYPE_QR_REFUND, result)
        }
    
    protected val b2cCardTransferResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_B2C_CARD_TRANSFER)) { result ->
            handleTransactionResult(PaymentActivity.TYPE_B2C_CARD_TRANSFER, result)
        }
    
    protected val cardReadingResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_READ_CARD_DETAILS)) { result ->
            handleTransactionResult(PaymentActivity.TYPE_READ_CARD_DETAILS, result)
        }
    
    protected val ecPurchaseResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_EC_PURCHASE)) { result ->
            handleTransactionResult(PaymentActivity.TYPE_EC_PURCHASE, result)
        }
    
    protected val ecRefundResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_EC_REFUND)) { result ->
            handleTransactionResult(PaymentActivity.TYPE_EC_REFUND, result)
        }
    
    protected val balanceResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_BALANCE)) { result ->
            handleTransactionResult(PaymentActivity.TYPE_BALANCE, result)
        }
    
    protected val cashInResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_CASH_IN)) { result ->
            handleTransactionResult(PaymentActivity.TYPE_CASH_IN, result)
        }
    
    protected val cashOutResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_CASH_OUT)) { result ->
            handleTransactionResult(PaymentActivity.TYPE_CASH_OUT, result)
        }
    
    protected val setPinResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_SET_PIN)) { result ->
            handleTransactionResult(PaymentActivity.TYPE_SET_PIN, result)
        }
    
    protected val changePinResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_CHANGE_PIN)) { result ->
            handleTransactionResult(PaymentActivity.TYPE_CHANGE_PIN, result)
        }
    
    protected val purchaseWithCashbackResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_PURCHASE_WITH_CASHBACK)) { result ->
            handleTransactionResult(PaymentActivity.TYPE_PURCHASE_WITH_CASHBACK, result)
        }
    
    protected val unreferencedRefundResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_UNREFERENCED_REFUND)) { result ->
            handleTransactionResult(PaymentActivity.TYPE_UNREFERENCED_REFUND, result)
        }
    
    protected val preAuthCancelResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_PRE_AUTH_CANCEL)) { result ->
            handleTransactionResult(PaymentActivity.TYPE_PRE_AUTH_CANCEL, result)
        }
    
    protected val parRequestResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_PAYER_DETAILS)) { result ->
            handleTransactionResult(PaymentActivity.TYPE_PAYER_DETAILS, result)
        }

    /**
     * Enhanced transaction result handling with proper logging and state management
     */
    private fun handleTransactionResult(type: String, transactionResult: TransactionResult?) {
        Timber.tag(TAG).i("Received final transaction result for %s: %s", type, transactionResult?.toLogString())
        
        // Note: At this point, all animations and receipt printing have been completed
        // The TransactionResult is delivered only after all processing is done
        
        lifecycleScope.launch {
            transactionResult?.let { transaction ->
                when (transaction.code) {
                    0 -> {
                        Timber.tag(TAG).i("Transaction successful: %s", type)
                        onTransactionSuccess(type, transaction)
                    }
                    301 -> {
                        Timber.tag(TAG).i("Transaction cancelled by user: %s", type)
                        onTransactionCanceled()
                    }
                    else -> {
                        Timber.tag(TAG).e("Transaction failed: %s, code: %d, message: %s", 
                            type, transaction.code, transaction.message)
                        onTransactionFailed(type, transaction)
                    }
                }
            } ?: run {
                Timber.tag(TAG).e("Transaction result is null for type: %s", type)
                onTransactionFailed(type, null)
            }
        }
    }

    /**
     * Enhanced failure handling with better UX
     */
    private fun onTransactionFailed(type: String, transactionResult: TransactionResult?) {
        Timber.tag(TAG).e(
            "Transaction failed - type: %s, code: %s, message: %s",
            type,
            transactionResult?.code,
            transactionResult?.message
        )

        // Show enhanced error dialog with retry option
        val builder = MaterialAlertDialogBuilder(requireActivity(), R.style.SspAlertDialog)
        builder.setTitle(R.string.title_error_transaction_failed)
            .setCancelable(false)
            .setMessage(formatErrorMessage(transactionResult))
            .setPositiveButton(R.string.button_repeat) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                Timber.tag(TAG).i("User chose to repeat transaction: %s", type)
                repeatLastTransaction(type)
            }
            .setNegativeButton(R.string.button_cancel) { dialog: DialogInterface, _: Int ->
                dialog.cancel()
                Timber.tag(TAG).i("User cancelled after error: %s", type)
                onTransactionCanceled()
            }

        // Add technical details button for debugging
        if (transactionResult?.code != null) {
            builder.setNeutralButton("Детали") { _, _ ->
                showTechnicalDetails(type, transactionResult)
            }
        }

        builder.show()
    }

    /**
     * Format error message for better user understanding
     */
    private fun formatErrorMessage(transactionResult: TransactionResult?): String {
        return when (transactionResult?.code) {
            null -> "Неизвестная ошибка. Попробуйте еще раз."
            302 -> "Операция отменена терминалом"
            303 -> "Ошибка связи с терминалом"
            304 -> "Таймаут операции"
            else -> transactionResult.message ?: "Ошибка ${transactionResult.code}"
        }
    }

    /**
     * Show technical details dialog
     */
    private fun showTechnicalDetails(type: String, transactionResult: TransactionResult) {
        val details = buildString {
            appendLine("Тип операции: $type")
            appendLine("Код ошибки: ${transactionResult.code}")
            appendLine("Сообщение: ${transactionResult.message}")
            transactionResult.rrn?.let { appendLine("RRN: $it") }
            transactionResult.authCode?.let { appendLine("Auth Code: $it") }
        }

        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Технические детали")
            .setMessage(details)
            .setPositiveButton("OK", null)
            .show()
    }

    /**
     * Enhanced repeat transaction with validation
     */
    protected fun repeatLastTransaction(type: String) {
        lastTransactionParams?.let { params ->
            if (isProcessingTransaction) {
                Timber.tag(TAG).w("Transaction already in progress, ignoring repeat request")
                return
            }

            Timber.tag(TAG).i("Repeating transaction: %s", type)
            
            val launcher = when (type) {
                PaymentActivity.TYPE_QR_PAYMENT -> fpsPaymentResult
                PaymentActivity.TYPE_PAYMENT -> paymentResult
                PaymentActivity.TYPE_CANCEL -> cancelResult
                PaymentActivity.TYPE_REFUND -> refundResult
                PaymentActivity.TYPE_QR_REFUND -> qrRefundResult
                PaymentActivity.TYPE_CHANGE_PIN -> changePinResult
                PaymentActivity.TYPE_SET_PIN -> setPinResult
                PaymentActivity.TYPE_PURCHASE_WITH_CASHBACK -> purchaseWithCashbackResult
                PaymentActivity.TYPE_UNREFERENCED_REFUND -> unreferencedRefundResult
                PaymentActivity.TYPE_PRE_AUTH_CANCEL -> preAuthCancelResult
                PaymentActivity.TYPE_BALANCE -> balanceResult
                PaymentActivity.TYPE_ECOM_PAYMENT -> ecomPaymentResult
                PaymentActivity.TYPE_PRE_AUTH -> preAuthResult
                PaymentActivity.TYPE_PRE_AUTH_INCREMENT -> preAuthIncrementResult
                PaymentActivity.TYPE_PRE_AUTH_CONFIRM -> preAuthConfirmResult
                PaymentActivity.TYPE_B2C_CARD_TRANSFER -> b2cCardTransferResult
                PaymentActivity.TYPE_READ_CARD_DETAILS -> cardReadingResult
                PaymentActivity.TYPE_CASH_IN -> cashInResult
                PaymentActivity.TYPE_CASH_OUT -> cashOutResult
                PaymentActivity.TYPE_EC_PURCHASE -> ecPurchaseResult
                PaymentActivity.TYPE_EC_REFUND -> ecRefundResult
                PaymentActivity.TYPE_PAYER_DETAILS -> parRequestResult
                else -> {
                    Timber.tag(TAG).e("Unknown transaction type for repeat: %s", type)
                    null
                }
            }
            
            launcher?.launch(params) ?: run {
                Timber.tag(TAG).e("Failed to find launcher for transaction type: %s", type)
            }
        } ?: run {
            Timber.tag(TAG).e("No last transaction parameters available for repeat")
        }
    }

    /**
     * Check if a transaction is currently being processed
     */
    protected fun isTransactionInProgress(): Boolean = isProcessingTransaction

    /**
     * Get the last transaction parameters (for debugging or retry logic)
     */
    protected fun getLastTransactionParams(): TransactionParams? = lastTransactionParams

    // Abstract methods to be implemented by subclasses
    abstract fun onTransactionSuccess(type: String, transactionResult: TransactionResult)
    abstract fun onTransactionCanceled()

    // Optional override methods
    open fun onTransactionStarted(type: String) {
        Timber.tag(TAG).d("Transaction started: %s", type)
    }

    open fun onTransactionCompleted(type: String) {
        Timber.tag(TAG).d("Transaction completed: %s", type)
    }
}