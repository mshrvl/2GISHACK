package com.skytech.smartskyposlib.ui

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skytech.smartskyposlib.PaymentActivity
import com.skytech.smartskyposlib.TransactionResult
import com.skytech.smartskyposlib.ui.R
import timber.log.Timber

abstract class PaymentFragment : Fragment() {

    companion object {
        private const val TAG = "PaymentFragment"
    }

    private var lastTransactionParams: TransactionParams? = null

    private fun transactionContact(type: String) =
        object : ActivityResultContract<TransactionParams, TransactionResult?>() {
            override fun createIntent(context: Context, input: TransactionParams): Intent {
                val intent = Intent(context, SomersPayActivity::class.java)
                intent.putExtra(PaymentActivity.PARAMS_KEY, input)
                intent.putExtra(PaymentActivity.TYPE_KEY, type)

                lastTransactionParams = input
                return intent
            }

            override fun parseResult(resultCode: Int, intent: Intent?): TransactionResult? {
                return if (resultCode == Activity.RESULT_OK) {
                    Timber.tag(TAG).e("PARSE RESULT SUCCESS}")
                    return intent?.parcelable(PaymentActivity.RESULT_KEY)
                } else {
                    null
                }
            }
        }

    protected val paymentResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_PAYMENT)) {
            requireActivity().runOnUiThread { onTransaction(PaymentActivity.TYPE_PAYMENT, it) }
        }
    protected val fpsPaymentResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_QR_PAYMENT)) {
            requireActivity().runOnUiThread { onTransaction(PaymentActivity.TYPE_QR_PAYMENT, it) }
        }
    protected val ecomPaymentResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_ECOM_PAYMENT)) {
            requireActivity().runOnUiThread { onTransaction(PaymentActivity.TYPE_ECOM_PAYMENT, it) }
        }
    protected val cancelResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_CANCEL)) {
            requireActivity().runOnUiThread { onTransaction(PaymentActivity.TYPE_CANCEL, it) }
        }
    protected val refundResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_REFUND)) {
            requireActivity().runOnUiThread { onTransaction(PaymentActivity.TYPE_REFUND, it) }
        }
    protected val preAuthResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_PRE_AUTH)) {
            requireActivity().runOnUiThread { onTransaction(PaymentActivity.TYPE_PRE_AUTH, it) }
        }
    protected val preAuthIncrementResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_PRE_AUTH_INCREMENT)) {
            requireActivity().runOnUiThread {
                onTransaction(PaymentActivity.TYPE_PRE_AUTH_INCREMENT, it)
            }
        }
    protected val preAuthConfirmResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_PRE_AUTH_CONFIRM)) {
            requireActivity().runOnUiThread {
                onTransaction(PaymentActivity.TYPE_PRE_AUTH_CONFIRM, it)
            }
        }
    protected val qrRefundResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_QR_REFUND)) {
            requireActivity().runOnUiThread { onTransaction(PaymentActivity.TYPE_QR_REFUND, it) }
        }
    protected val b2cCardTransferResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_B2C_CARD_TRANSFER)) {
            requireActivity().runOnUiThread {
                onTransaction(PaymentActivity.TYPE_B2C_CARD_TRANSFER, it)
            }
        }
    protected val cardReadingResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_READ_CARD_DETAILS)) {
            requireActivity().runOnUiThread {
                onTransaction(PaymentActivity.TYPE_READ_CARD_DETAILS, it)
            }
        }
    protected val ecPurchaseResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_EC_PURCHASE)) {
            requireActivity().runOnUiThread {
                onTransaction(PaymentActivity.TYPE_EC_PURCHASE, it)
            }
        }
    protected val ecRefundResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_EC_REFUND)) {
            requireActivity().runOnUiThread {
                onTransaction(PaymentActivity.TYPE_EC_REFUND, it)
            }
        }
    protected val balanceResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_BALANCE)) {
            requireActivity().runOnUiThread {
                onTransaction(PaymentActivity.TYPE_BALANCE, it)
            }
        }
    protected val cashInResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_CASH_IN)) {
            requireActivity().runOnUiThread {
                onTransaction(PaymentActivity.TYPE_CASH_IN, it)
            }
        }
    protected val cashOutResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_CASH_OUT)) {
            requireActivity().runOnUiThread {
                onTransaction(PaymentActivity.TYPE_CASH_OUT, it)
            }
        }
    protected val setPinResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_SET_PIN)) {
            requireActivity().runOnUiThread {
                onTransaction(PaymentActivity.TYPE_SET_PIN, it)
            }
        }
    protected val changePinResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_CHANGE_PIN)) {
            requireActivity().runOnUiThread {
                onTransaction(PaymentActivity.TYPE_CHANGE_PIN, it)
            }
        }
    protected val purchaseWithCashbackResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_PURCHASE_WITH_CASHBACK)) {
            requireActivity().runOnUiThread {
                onTransaction(PaymentActivity.TYPE_PURCHASE_WITH_CASHBACK, it)
            }
        }
    protected val unreferencedRefundResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_UNREFERENCED_REFUND)) {
            requireActivity().runOnUiThread { onTransaction(PaymentActivity.TYPE_UNREFERENCED_REFUND, it) }
        }
    protected val preAuthCancelResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_PRE_AUTH_CANCEL)) {
            requireActivity().runOnUiThread { onTransaction(PaymentActivity.TYPE_PRE_AUTH_CANCEL, it) }
        }
    protected val parRequestResult =
        registerForActivityResult(transactionContact(PaymentActivity.TYPE_PAYER_DETAILS)) {
            requireActivity().runOnUiThread {
                onTransaction(PaymentActivity.TYPE_PAYER_DETAILS, it)
            }
        }

    private fun onTransaction(type: String, transactionResult: TransactionResult?) {
        Timber.tag(TAG).i("onTransaction %s", transactionResult?.toLogString())
        transactionResult?.let { transaction ->
            when (transaction.code) {
                0 -> {
                    onTransactionSuccess(type, transaction)
                }

                301 -> {
                    // canceled by user
                    onTransactionCanceled()
                }

                else -> {
                    onTransFailed(type, transaction)
                }
            }
        } ?: onTransFailed(type, null)
    }

    abstract fun onTransactionSuccess(type: String, transactionResult: TransactionResult)

    abstract fun onTransactionCanceled()

    private fun onTransFailed(type: String, transactionResult: TransactionResult?) {
        Timber.tag(TAG).e(
            "onTransFailed code: %s, message: %s",
            transactionResult?.code,
            transactionResult?.message
        )
        val builder = MaterialAlertDialogBuilder(requireActivity(), R.style.SspAlertDialog)
        builder.setTitle(R.string.title_error_transaction_failed)
            .setCancelable(false)
            .setMessage(transactionResult?.message)
            .setPositiveButton(R.string.button_repeat) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                Timber.tag(TAG).i("button_repeat")
                repeatLastTransaction(type)
            }
            .setNegativeButton(R.string.button_cancel) { dialog: DialogInterface, _: Int ->
                dialog.cancel()
                onTransactionCanceled()
            }

        builder.show()
    }

    protected fun repeatLastTransaction(type: String) =
        lastTransactionParams?.let {
            when (type) {
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
                else -> null
            }?.launch(it)
        }.also { Timber.tag(TAG).i("repeatLastTransaction $type") }
}

// Data class for transaction parameters
data class TransactionParams(
    val amount: Double,
    val currency: String,
    val description: String? = null,
    val terminalId: String? = null
)

// Extension function to get parcelable from intent
inline fun <reified T : android.os.Parcelable> Intent.parcelable(key: String): T? = 
    getParcelableExtra(key)