package org.finance.ui.p2p.tkb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.gson.JsonObject;


import org.finance.data.model.get_card.TransferCardModel;
import org.finance.data.service.ApiCallback;
import org.finance.data.service.P2PService;
import org.finance.data.service.TkbService;
import org.finance.ui.p2p.local.TransferHistoryFragment;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.tgnet.TLRPC;

public class TkbUzRfTranferPinCodeFragment extends BaseFragment {

    private EditText[] codeInputs;
    private Button nextButton;
    private final JsonObject jsonObject;
    private ProgressBar progressBar;
    private TextView resendTextView;
    private CountDownTimer countDownTimer;
    private final int amount;
    private final TransferCardModel transferCardModel;

    TLRPC.User user;
    public TkbUzRfTranferPinCodeFragment(JsonObject jsonObject,int amount,TransferCardModel transferCardModel) {
        this.jsonObject = jsonObject;
        this.amount=amount;
        this.transferCardModel=transferCardModel;

    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables", "ClickableViewAccessibility"})
    @Override
    public View createView(@NonNull Context context) {
        ConstraintLayout layout = new ConstraintLayout(context);
        layout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        layout.setId(View.generateViewId());
        layout.setPadding(24, 24, 24, 24);
        user = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();


        actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
        actionBar.setTitle("Код подтверждения");
        actionBar.setBackgroundColor(Color.parseColor("#50A7EA"));
        actionBar.setAllowOverlayTitle(true);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                    clearViews();
                }
            }
        });

        // Description Text
        TextView pinDescription = new TextView(context);
        pinDescription.setId(View.generateViewId());
        pinDescription.setText("Код подтверждения будет отправлен на номер " + formatPhoneNumber(user.phone));
        pinDescription.setTextSize(16);
        pinDescription.setTextColor(Color.BLACK);
        layout.addView(pinDescription);


        codeInputs = new EditText[6];
        LinearLayout codeLayout = new LinearLayout(context);
        codeLayout.setOrientation(LinearLayout.HORIZONTAL);
        codeLayout.setId(View.generateViewId());

        for (int i = 0; i < 6; i++) {
            codeInputs[i] = new EditText(context);
            codeInputs[i].setId(View.generateViewId());
            codeInputs[i].setInputType(InputType.TYPE_CLASS_NUMBER);
            codeInputs[i].setTextSize(20);
            codeInputs[i].setTextColor(Color.BLACK);
            codeInputs[i].setGravity(Gravity.CENTER);
            codeInputs[i].setMaxLines(1);
            codeInputs[i].setSingleLine(true);
            codeInputs[i].setBackground(context.getDrawable(R.drawable.edit_text_background));
            codeInputs[i].setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    AndroidUtilities.dp(48),
                    AndroidUtilities.dp(48)
            );
            params.setMarginEnd(AndroidUtilities.dp(12));
            codeInputs[i].setLayoutParams(params);
            codeLayout.addView(codeInputs[i]);


            final int finalI = i;
            codeInputs[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && finalI < 5) {
                        codeInputs[finalI + 1].requestFocus();
                    }
                    if (s.length() == 1 && finalI == 5) {
                        hideKeyboard(codeInputs[finalI]);
                    }
                }
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void afterTextChanged(Editable s) {}
            });

            // Backspace functionality
            codeInputs[i].setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                    if (codeInputs[finalI].getText().toString().isEmpty() && finalI > 0) {
                        codeInputs[finalI - 1].requestFocus();
                        codeInputs[finalI - 1].setText("");
                    }
                }
                return false;
            });
        }
        layout.addView(codeLayout);

        // Resend Code TextView
        resendTextView = new TextView(context);
        resendTextView.setId(View.generateViewId());
        resendTextView.setTextSize(16);
        resendTextView.setTextColor(Color.BLUE);
        layout.addView(resendTextView);
        startResendCodeTimer();

        // Resend code functionality
        resendTextView.setOnClickListener(v -> {
            startResendCodeTimer();
            resendCode();
        });

        // Button and ProgressBar setup
        FrameLayout buttonLayout = new FrameLayout(context);
        buttonLayout.setId(View.generateViewId());
        FrameLayout.LayoutParams buttonLayoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        buttonLayout.setLayoutParams(buttonLayoutParams);
        layout.addView(buttonLayout);

        nextButton = new Button(context);
        nextButton.setId(View.generateViewId());
        nextButton.setText("Следующий");
        nextButton.setTextSize(16);
        nextButton.setTextColor(Color.parseColor("#F6F6F6"));
        nextButton.setBackground(context.getDrawable(R.drawable.rounded_button));
        nextButton.setOnClickListener(v -> handleNextButtonClick(context));
        buttonLayout.addView(nextButton);

        // ProgressBar
        progressBar = new ProgressBar(context);
        FrameLayout.LayoutParams progressBarParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
        );
        progressBar.setVisibility(View.GONE);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
        buttonLayout.addView(progressBar, progressBarParams);

        // Constraints for views
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        constraintSet.connect(pinDescription.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP, 32);
        constraintSet.connect(pinDescription.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
        constraintSet.connect(pinDescription.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);

        constraintSet.connect(codeLayout.getId(), ConstraintSet.TOP, pinDescription.getId(), ConstraintSet.BOTTOM, 24);
        constraintSet.connect(codeLayout.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
        constraintSet.connect(codeLayout.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);

        constraintSet.connect(resendTextView.getId(), ConstraintSet.TOP, codeLayout.getId(), ConstraintSet.BOTTOM, 24);
        constraintSet.connect(resendTextView.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
        constraintSet.connect(resendTextView.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);

        constraintSet.connect(buttonLayout.getId(), ConstraintSet.BOTTOM, layout.getId(), ConstraintSet.BOTTOM, 16);
        constraintSet.connect(buttonLayout.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
        constraintSet.connect(buttonLayout.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);

        constraintSet.applyTo(layout);
        layout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View view = layout.findFocus();
                if (view != null) {
                    view.clearFocus();
                }
            }
            return true;
        });
        return layout;
    }

    private void handleNextButtonClick(Context context) {
        StringBuilder pinCode = new StringBuilder();
        for (EditText codeInput : codeInputs) {
            pinCode.append(codeInput.getText().toString().trim());
        }

        if (pinCode.length() == 6) {
            nextButton.setText("");
            progressBar.setVisibility(View.VISIBLE);
            nextButton.setEnabled(false);

            TkbService tkbService = new TkbService(context);
            JsonObject requestObject = new JsonObject();
            requestObject.addProperty("code", pinCode.toString());
            requestObject.addProperty("ext_id", jsonObject.get("ext_id").getAsString());
            requestObject.addProperty("token", jsonObject.get("token").getAsString());


            tkbService.tkbUzRfConfirm(new ApiCallback<JsonObject>() {
                @Override
                public void onSuccess(JsonObject response) {

                    progressBar.setVisibility(View.GONE);
                    presentFragment(new TransferHistoryFragment(amount,transferCardModel));

                }

                @Override
                public void onFailure(String errorMessage) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(context, "Transaction Failed: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            }, requestObject);
        }
    }




    private void startResendCodeTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        resendTextView.setEnabled(false);

        countDownTimer = new CountDownTimer(60000, 1000) {
            @SuppressLint("DefaultLocale")
            public void onTick(long millisUntilFinished) {
                resendTextView.setText(String.format("Отправить код: 00:%d", millisUntilFinished / 1000));
            }

            public void onFinish() {
                resendTextView.setText("Отправить код");
                resendTextView.setEnabled(true);
            }
        }.start();
    }

    private void resendCode() {
        Toast.makeText(getContext(), "Код повторно отправлен", Toast.LENGTH_SHORT).show();
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() != 12) {
            return phoneNumber;
        }

        String countryCode = "+998";
        String regionCode = phoneNumber.substring(3, 5);
        String maskedPart = "*** **";
        String lastTwoDigits = phoneNumber.substring(phoneNumber.length() - 2);

        return String.format("%s %s %s %s", countryCode, regionCode, maskedPart, lastTwoDigits);
    }

}

