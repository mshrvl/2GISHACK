package com.skytech.smartskyposlib.ui

import android.content.Intent
import com.skytech.smartskyposlib.ReconciliationResult
import com.skytech.smartskyposlib.TransactionResult

/**
 * Concrete implementation of SkyPaymentActivityV2 with proper result handling.
 * This activity implements the decoupled payment processing architecture.
 */
class SomersPayActivity : SkyPaymentActivityV2() {
    
    companion object {
        private const val TAG = "SomersPayActivity"
    }

    /**
     * Create result intent for TransactionResult
     * This method is called only after all animations and receipt printing are complete
     */
    override fun createResultIntent(result: TransactionResult): Intent {
        val intent = Intent()
        intent.putExtra(PaymentActivity.RESULT_KEY, result)
        return intent
    }

    /**
     * Create result intent for ReconciliationResult
     * This method is called only after all animations and receipt printing are complete
     */
    override fun createReconciliationResultIntent(result: ReconciliationResult): Intent {
        val intent = Intent()
        intent.putExtra(PaymentActivity.RESULT_KEY, result)
        return intent
    }
}