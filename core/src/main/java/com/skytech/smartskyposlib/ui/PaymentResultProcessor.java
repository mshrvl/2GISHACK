package com.skytech.smartskyposlib.ui;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * Handles payment result animations independently from TransactionResult transmission.
 * This class decouples the visual feedback (success/failure animations) from the 
 * actual result delivery to ensure smooth UX while maintaining proper result timing.
 */
public class PaymentResultProcessor {
    private static final String TAG = "PaymentResultProcessor";
    
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private AnimationCompleteListener animationCompleteListener;
    private boolean isAnimationComplete = false;
    private Runnable animationCompleteRunnable;

    public interface AnimationCompleteListener {
        void onAnimationComplete();
    }

    /**
     * Set listener for animation completion events
     */
    public void setAnimationCompleteListener(AnimationCompleteListener listener) {
        this.animationCompleteListener = listener;
    }

    /**
     * Start success animation with specified duration
     * Animation runs independently of result transmission
     */
    public void startSuccessAnimation(int durationMs) {
        Log.d(TAG, "Starting success animation for " + durationMs + "ms");
        
        isAnimationComplete = false;
        
        // Cancel any existing animation
        if (animationCompleteRunnable != null) {
            mainHandler.removeCallbacks(animationCompleteRunnable);
        }
        
        // Schedule animation completion
        animationCompleteRunnable = () -> {
            Log.d(TAG, "Success animation completed");
            isAnimationComplete = true;
            
            if (animationCompleteListener != null) {
                animationCompleteListener.onAnimationComplete();
            }
        };
        
        mainHandler.postDelayed(animationCompleteRunnable, durationMs);
    }

    /**
     * Start failure animation with specified duration
     */
    public void startFailureAnimation(int durationMs) {
        Log.d(TAG, "Starting failure animation for " + durationMs + "ms");
        
        isAnimationComplete = false;
        
        // Cancel any existing animation
        if (animationCompleteRunnable != null) {
            mainHandler.removeCallbacks(animationCompleteRunnable);
        }
        
        // Schedule animation completion
        animationCompleteRunnable = () -> {
            Log.d(TAG, "Failure animation completed");
            isAnimationComplete = true;
            
            if (animationCompleteListener != null) {
                animationCompleteListener.onAnimationComplete();
            }
        };
        
        mainHandler.postDelayed(animationCompleteRunnable, durationMs);
    }

    /**
     * Cancel any ongoing animation
     */
    public void cancelAnimation() {
        Log.d(TAG, "Cancelling ongoing animation");
        
        if (animationCompleteRunnable != null) {
            mainHandler.removeCallbacks(animationCompleteRunnable);
            animationCompleteRunnable = null;
        }
        
        isAnimationComplete = true;
        
        if (animationCompleteListener != null) {
            animationCompleteListener.onAnimationComplete();
        }
    }

    /**
     * Check if animation is complete
     */
    public boolean isAnimationComplete() {
        return isAnimationComplete;
    }

    /**
     * Reset the processor state
     */
    public void reset() {
        Log.d(TAG, "Resetting PaymentResultProcessor");
        
        if (animationCompleteRunnable != null) {
            mainHandler.removeCallbacks(animationCompleteRunnable);
            animationCompleteRunnable = null;
        }
        
        isAnimationComplete = false;
    }

    /**
     * Force animation completion (for testing or emergency cases)
     */
    public void forceAnimationComplete() {
        Log.d(TAG, "Forcing animation completion");
        
        if (animationCompleteRunnable != null) {
            mainHandler.removeCallbacks(animationCompleteRunnable);
        }
        
        isAnimationComplete = true;
        
        if (animationCompleteListener != null) {
            animationCompleteListener.onAnimationComplete();
        }
    }
}