package com.skytech.smartskyposlib.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.skytech.smartskyposlib.ReconciliationResult;
import com.skytech.smartskyposlib.TransactionResult;
import com.skytech.smartskyposlib.ui.R;

/**
 * Enhanced SkyCardWaitFragment that supports decoupled receipt printing UI.
 * This fragment handles transaction status display, animations, and receipt printing progress
 * independently from TransactionResult transmission.
 */
public class SkyCardWaitFragment extends Fragment {
    private static final String TAG = "SkyCardWaitFragment";
    
    // UI Components
    private TextView statusText;
    private TextView operationNameText;
    private ImageView statusIcon;
    private ProgressBar progressBar;
    private View qrCodeContainer;
    private ImageView qrCodeImage;
    
    // Receipt printing UI components
    private View receiptPrintingContainer;
    private TextView receiptPrintingText;
    private ProgressBar receiptPrintingProgress;
    private ImageView receiptPrintingIcon;
    
    // Animation components
    private ObjectAnimator successAnimator;
    private ObjectAnimator failureAnimator;
    
    // State tracking
    private boolean isShowingResult = false;
    private boolean isReceiptPrintingVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sky_card_wait, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        statusText = view.findViewById(R.id.statusText);
        operationNameText = view.findViewById(R.id.operationNameText);
        statusIcon = view.findViewById(R.id.statusIcon);
        progressBar = view.findViewById(R.id.progressBar);
        qrCodeContainer = view.findViewById(R.id.qrCodeContainer);
        qrCodeImage = view.findViewById(R.id.qrCodeImage);
        
        // Receipt printing UI components
        receiptPrintingContainer = view.findViewById(R.id.receiptPrintingContainer);
        receiptPrintingText = view.findViewById(R.id.receiptPrintingText);
        receiptPrintingProgress = view.findViewById(R.id.receiptPrintingProgress);
        receiptPrintingIcon = view.findViewById(R.id.receiptPrintingIcon);
        
        // Initially hide receipt printing UI
        if (receiptPrintingContainer != null) {
            receiptPrintingContainer.setVisibility(View.GONE);
        }
    }

    /**
     * Handle state changes during transaction processing
     */
    public void onStateChanged(int state, String message) {
        if (getActivity() == null) return;
        
        getActivity().runOnUiThread(() -> {
            if (statusText != null) {
                statusText.setText(message);
            }
            
            // Update UI based on state
            updateUIForState(state);
        });
    }

    /**
     * Handle operation name changes
     */
    public void onOperationNameChanged(String operationName) {
        if (getActivity() == null) return;
        
        getActivity().runOnUiThread(() -> {
            if (operationNameText != null) {
                operationNameText.setText(operationName);
            }
        });
    }

    /**
     * Handle QR code display
     */
    public void onQrLink(String qrLink) {
        if (getActivity() == null) return;
        
        getActivity().runOnUiThread(() -> {
            // Generate and display QR code
            displayQrCode(qrLink);
        });
    }

    /**
     * Handle transaction result display (animation only, not result transmission)
     */
    public void onTransactionResult(TransactionResult transaction) {
        if (getActivity() == null) return;
        
        getActivity().runOnUiThread(() -> {
            isShowingResult = true;
            
            if (transaction.getCode() == 0) {
                showSuccessAnimation(transaction);
            } else {
                showFailureAnimation(transaction);
            }
        });
    }

    /**
     * Handle reconciliation result display (animation only)
     */
    public void onReconciliationResult(ReconciliationResult transaction) {
        if (getActivity() == null) return;
        
        getActivity().runOnUiThread(() -> {
            isShowingResult = true;
            
            if (transaction.getCode() == 0) {
                showReconciliationSuccessAnimation(transaction);
            } else {
                showReconciliationFailureAnimation(transaction);
            }
        });
    }

    /**
     * Show receipt printing progress (independent of result transmission)
     */
    public void showReceiptPrintingProgress() {
        if (getActivity() == null) return;
        
        getActivity().runOnUiThread(() -> {
            isReceiptPrintingVisible = true;
            
            if (receiptPrintingContainer != null) {
                receiptPrintingContainer.setVisibility(View.VISIBLE);
            }
            
            if (receiptPrintingText != null) {
                receiptPrintingText.setText("Печать чека...");
            }
            
            if (receiptPrintingProgress != null) {
                receiptPrintingProgress.setVisibility(View.VISIBLE);
            }
            
            if (receiptPrintingIcon != null) {
                receiptPrintingIcon.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Hide receipt printing progress
     */
    public void hideReceiptPrintingProgress() {
        if (getActivity() == null) return;
        
        getActivity().runOnUiThread(() -> {
            isReceiptPrintingVisible = false;
            
            if (receiptPrintingContainer != null) {
                receiptPrintingContainer.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Show receipt printing error
     */
    public void showReceiptPrintingError(String error) {
        if (getActivity() == null) return;
        
        getActivity().runOnUiThread(() -> {
            if (receiptPrintingContainer != null) {
                receiptPrintingContainer.setVisibility(View.VISIBLE);
            }
            
            if (receiptPrintingText != null) {
                receiptPrintingText.setText("Ошибка печати: " + error);
            }
            
            if (receiptPrintingProgress != null) {
                receiptPrintingProgress.setVisibility(View.GONE);
            }
            
            if (receiptPrintingIcon != null) {
                receiptPrintingIcon.setVisibility(View.VISIBLE);
                receiptPrintingIcon.setImageResource(R.drawable.ic_error);
            }
        });
    }

    /**
     * Show receipt printing success
     */
    public void showReceiptPrintingSuccess() {
        if (getActivity() == null) return;
        
        getActivity().runOnUiThread(() -> {
            if (receiptPrintingText != null) {
                receiptPrintingText.setText("Чек напечатан");
            }
            
            if (receiptPrintingProgress != null) {
                receiptPrintingProgress.setVisibility(View.GONE);
            }
            
            if (receiptPrintingIcon != null) {
                receiptPrintingIcon.setVisibility(View.VISIBLE);
                receiptPrintingIcon.setImageResource(R.drawable.ic_check);
            }
        });
    }

    private void updateUIForState(int state) {
        // Update progress bar and status icon based on transaction state
        switch (state) {
            case 1: // Waiting for card
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                if (statusIcon != null) {
                    statusIcon.setVisibility(View.GONE);
                }
                break;
                
            case 2: // Processing
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                break;
                
            case 3: // Completed
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void displayQrCode(String qrLink) {
        if (qrCodeContainer != null) {
            qrCodeContainer.setVisibility(View.VISIBLE);
        }
        
        // Generate QR code image from link
        // This would typically use a QR code generation library
        if (qrCodeImage != null) {
            // QRCodeGenerator.generateQRCode(qrLink, qrCodeImage);
        }
    }

    private void showSuccessAnimation(TransactionResult transaction) {
        // Hide progress elements
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        
        // Show success icon
        if (statusIcon != null) {
            statusIcon.setVisibility(View.VISIBLE);
            statusIcon.setImageResource(R.drawable.ic_success);
        }
        
        // Update status text
        if (statusText != null) {
            statusText.setText("Операция выполнена успешно");
        }
        
        // Start success animation
        startSuccessIconAnimation();
    }

    private void showFailureAnimation(TransactionResult transaction) {
        // Hide progress elements
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        
        // Show failure icon
        if (statusIcon != null) {
            statusIcon.setVisibility(View.VISIBLE);
            statusIcon.setImageResource(R.drawable.ic_error);
        }
        
        // Update status text
        if (statusText != null) {
            statusText.setText("Ошибка: " + transaction.getMessage());
        }
        
        // Start failure animation
        startFailureIconAnimation();
    }

    private void showReconciliationSuccessAnimation(ReconciliationResult transaction) {
        // Similar to transaction success but for reconciliation
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        
        if (statusIcon != null) {
            statusIcon.setVisibility(View.VISIBLE);
            statusIcon.setImageResource(R.drawable.ic_success);
        }
        
        if (statusText != null) {
            statusText.setText("Сверка выполнена успешно");
        }
        
        startSuccessIconAnimation();
    }

    private void showReconciliationFailureAnimation(ReconciliationResult transaction) {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        
        if (statusIcon != null) {
            statusIcon.setVisibility(View.VISIBLE);
            statusIcon.setImageResource(R.drawable.ic_error);
        }
        
        if (statusText != null) {
            statusText.setText("Ошибка сверки: " + transaction.getMessage());
        }
        
        startFailureIconAnimation();
    }

    private void startSuccessIconAnimation() {
        if (statusIcon == null) return;
        
        // Cancel any existing animation
        if (successAnimator != null) {
            successAnimator.cancel();
        }
        
        // Create scale animation for success
        successAnimator = ObjectAnimator.ofFloat(statusIcon, "scaleX", 0.8f, 1.2f, 1.0f);
        successAnimator.setDuration(600);
        successAnimator.start();
    }

    private void startFailureIconAnimation() {
        if (statusIcon == null) return;
        
        // Cancel any existing animation
        if (failureAnimator != null) {
            failureAnimator.cancel();
        }
        
        // Create shake animation for failure
        failureAnimator = ObjectAnimator.ofFloat(statusIcon, "translationX", 0, -25, 25, -25, 25, 0);
        failureAnimator.setDuration(500);
        failureAnimator.start();
    }

    /**
     * Check if the fragment is currently showing a result
     */
    public boolean isShowingResult() {
        return isShowingResult;
    }

    /**
     * Check if receipt printing UI is visible
     */
    public boolean isReceiptPrintingVisible() {
        return isReceiptPrintingVisible;
    }

    /**
     * Reset the fragment to initial state
     */
    public void reset() {
        isShowingResult = false;
        isReceiptPrintingVisible = false;
        
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                
                if (statusIcon != null) {
                    statusIcon.setVisibility(View.GONE);
                }
                
                if (qrCodeContainer != null) {
                    qrCodeContainer.setVisibility(View.GONE);
                }
                
                if (receiptPrintingContainer != null) {
                    receiptPrintingContainer.setVisibility(View.GONE);
                }
                
                if (statusText != null) {
                    statusText.setText("Ожидание карты...");
                }
                
                if (operationNameText != null) {
                    operationNameText.setText("");
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        
        // Cancel any running animations
        if (successAnimator != null) {
            successAnimator.cancel();
        }
        
        if (failureAnimator != null) {
            failureAnimator.cancel();
        }
    }
}