package org.finance.ui.paynet.service;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.gson.JsonObject;

import org.finance.data.service.ApiCallback;
import org.finance.data.service.PaynetService;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;

public class PaynetPinCodeFreagment extends BaseFragment {

    private EditText[] codeInputs;
    private Button nextButton;
    private TextView resendTextView;
    private final JsonObject jsonObject;
    private final int summ;
    private CountDownTimer countDownTimer;

    public PaynetPinCodeFreagment(JsonObject jsonObject, int summ) {
        this.jsonObject = jsonObject;
        this.summ = summ;
    }

    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Nullable
    @Override
    public View createView(@NonNull Context context) {
        ConstraintLayout layout = new ConstraintLayout(context);
        layout.setId(View.generateViewId());
        layout.setPadding(24, 24, 24, 24);
        layout.setBackgroundColor(Color.parseColor("#F5F5F5"));
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

        TextView pinDescription = new TextView(context);
        pinDescription.setId(View.generateViewId());
        pinDescription.setText("Код подтверждения будет отправлен на номер");
        pinDescription.setTextSize(16);
        pinDescription.setTextColor(Color.BLACK);
        pinDescription.setPadding(0, 24, 0, 0);
        layout.addView(pinDescription);

        codeInputs = new EditText[jsonObject.get("count").getAsInt()];
        LinearLayout codeLayout = new LinearLayout(context);
        codeLayout.setOrientation(LinearLayout.HORIZONTAL);
        codeLayout.setId(View.generateViewId());

        for (int i = 0; i < jsonObject.get("count").getAsInt(); i++) {
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

            int finalI = i;

            codeInputs[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && finalI < 5) {
                        codeInputs[finalI + 1].requestFocus();
                    }

                    if (s.length() == 1 && finalI == 5) {
                        hideKeyboard(codeInputs[finalI]);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

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

        resendTextView = new TextView(context);
        resendTextView.setId(View.generateViewId());
        resendTextView.setTextSize(16);
        resendTextView.setTextColor(Color.BLUE);
        layout.addView(resendTextView);
        startResendCodeTimer();

        resendTextView.setOnClickListener(v -> {
            startResendCodeTimer();
            resendCode();
        });

        nextButton = new Button(context);
        nextButton.setId(View.generateViewId());
        LinearLayout.LayoutParams nextButtonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                AndroidUtilities.dp(52)
        );

        int bottomMargin = AndroidUtilities.dp(24);
        nextButtonParams.setMargins(0, 0, 0, bottomMargin);
        nextButton.setLayoutParams(nextButtonParams);
        nextButton.setText("Следующий");
        nextButton.setTextSize(16);
        nextButton.setTextColor(Color.parseColor("#F6F6F6"));
        nextButton.setBackground(context.getDrawable(R.drawable.rounded_button));
        nextButton.setOnClickListener(v -> {
            StringBuilder pinCode = new StringBuilder();
            for (EditText codeInput : codeInputs) {
                pinCode.append(codeInput.getText().toString().trim());
            }

            if (pinCode.length() == jsonObject.get("count").getAsInt()) {
                nextButton.setText("...");
                nextButton.setEnabled(false);

                JsonObject requestObject = new JsonObject();
                requestObject.addProperty("code", pinCode.toString());
                requestObject.addProperty("sender_ext", jsonObject.get("sender").getAsString());
                requestObject.addProperty("receiver_ext", jsonObject.get("receiver").getAsString());
                PaynetService paynetService = new PaynetService(context);
                paynetService.paymentConfirm(new ApiCallback<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject response) {
                        nextButton.setText("Следующий");
                        nextButton.setEnabled(true);
                        presentFragment(new TransactionHistoryFragment(summ), true);
                        Toast.makeText(context, "To’lov muvaffaqiyatli amalga oshdi", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        nextButton.setText("Следующий");
                        nextButton.setEnabled(true);
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }, requestObject);
            } else {
                Toast.makeText(context, "Please enter all 6 digits", Toast.LENGTH_LONG).show();
            }
        });
        layout.addView(nextButton);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        constraintSet.connect(pinDescription.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP, 0);
        constraintSet.connect(pinDescription.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
        constraintSet.connect(pinDescription.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);

        constraintSet.connect(codeLayout.getId(), ConstraintSet.TOP, pinDescription.getId(), ConstraintSet.BOTTOM, 64);
        constraintSet.connect(codeLayout.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
        constraintSet.connect(codeLayout.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);

        constraintSet.connect(resendTextView.getId(), ConstraintSet.TOP, codeLayout.getId(), ConstraintSet.BOTTOM, 24);
        constraintSet.connect(resendTextView.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
        constraintSet.connect(resendTextView.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);

        constraintSet.connect(nextButton.getId(), ConstraintSet.BOTTOM, layout.getId(), ConstraintSet.BOTTOM, 16);
        constraintSet.connect(nextButton.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
        constraintSet.connect(nextButton.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);

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

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                resendTextView.setText(String.format("Resend code in: 00:%d", millisUntilFinished / 1000));
            }

            @SuppressLint("SetTextI18n")
            public void onFinish() {
                resendTextView.setText("Resend code");
                resendTextView.setEnabled(true);
            }
        }.start();
    }

    private void resendCode() {
        Toast.makeText(getContext(), "Code resent", Toast.LENGTH_SHORT).show();
    }
}
