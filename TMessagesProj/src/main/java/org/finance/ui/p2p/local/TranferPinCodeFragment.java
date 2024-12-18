package org.finance.ui.p2p.local;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
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
import org.finance.ui.ui_helper.ProgresBarDialog;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.tgnet.TLRPC;

public class TranferPinCodeFragment extends BaseFragment {

    private EditText[] codeInputs;
    private Button nextButton;
    private  JsonObject jsonObject;
    private ProgressBar progressBar;
    private TextView resendTextView;
    private CountDownTimer countDownTimer;
    private final int amount;
    private final TransferCardModel cardNumber;
    private final int count;
    private final JsonObject resendOtp;

    TLRPC.User user;
    public TranferPinCodeFragment(JsonObject jsonObject,int amount,TransferCardModel cardNumber,JsonObject resendOtp) {
        this.jsonObject = jsonObject;
        this.amount=amount;
        this.cardNumber=cardNumber;
        this.resendOtp=resendOtp;
        count=jsonObject.get("count").getAsInt();

    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables", "ClickableViewAccessibility"})
    @Override
    public View createView(@NonNull Context context) {
        ConstraintLayout layout = new ConstraintLayout(context);
        layout.setId(View.generateViewId());
        layout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        layout.setPadding(24, 24, 24, 24);
        actionBar.setBackgroundColor(Color.parseColor("#50A7EA"));
        actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
        actionBar.setTitle("Код подтверждения");
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
        user = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
        TextView pinDescription = new TextView(context);
        pinDescription.setId(View.generateViewId());
        pinDescription.setText("Код подтверждения будет отправлен на номер " + formatPhoneNumber(user.phone));
        pinDescription.setTextSize(16);
        pinDescription.setTextColor(Color.BLACK);
        layout.addView(pinDescription);

        final int inputCount = count;
        codeInputs = new EditText[inputCount];
        LinearLayout codeLayout = new LinearLayout(context);
        codeLayout.setOrientation(LinearLayout.HORIZONTAL);
        codeLayout.setId(View.generateViewId());

        for (int i = 0; i < inputCount; i++) {
            codeInputs[i] = new EditText(context);
            codeInputs[i].setId(View.generateViewId());
            codeInputs[i].setInputType(InputType.TYPE_CLASS_NUMBER);
            codeInputs[i].setTextSize(20);
            codeInputs[i].setTextColor(Color.BLACK);
            codeInputs[i].setGravity(Gravity.CENTER);
            codeInputs[i].setMaxLines(1);
            codeInputs[i].setSingleLine(true);
            codeInputs[i].setBackground(context.getDrawable(R.drawable.rounded_edittext));
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
                public void beforeTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int length) {
                    if (s.length() == 1 && finalI < inputCount - 1) {
                        codeInputs[finalI + 1].requestFocus();
                    }

                    if (s.length() == 1 && finalI == inputCount - 1) {
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

            codeInputs[i].setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    codeInputs[finalI].setBackground(context.getDrawable(R.drawable.rounded_edittext_focused));
                } else {
                    codeInputs[finalI].setBackground(context.getDrawable(R.drawable.rounded_edittext));
                }
            });
        }

        layout.addView(codeLayout);
        resendTextView = new TextView(context);
        resendTextView.setId(View.generateViewId());
        resendTextView.setTextSize(16);
        resendTextView.setTextColor(Color.BLACK);
        layout.addView(resendTextView);

        nextButton = new Button(context);
        nextButton.setId(View.generateViewId());
        nextButton.setText("Дальше");
        nextButton.setTextSize(16);
        nextButton.setTextColor(Color.parseColor("#F6F6F6"));
        nextButton.setBackground(context.getDrawable(R.drawable.rounded_button));
        nextButton.setOnClickListener(v -> handleNextButtonClick(context));

        LinearLayout.LayoutParams nextButtonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                AndroidUtilities.dp(52)
        );
        FrameLayout buttonLayout = new FrameLayout(context);
        buttonLayout.setLayoutParams(nextButtonParams);
        buttonLayout.setId(View.generateViewId());
        nextButton.setLayoutParams(nextButtonParams);
        buttonLayout.addView(nextButton);
        layout.addView(buttonLayout);

        progressBar = new ProgressBar(context);
        progressBar.setId(View.generateViewId());
        FrameLayout.LayoutParams progressBarParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        progressBarParams.gravity = Gravity.CENTER;
        progressBar.setVisibility(View.GONE);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);

        buttonLayout.addView(progressBar, progressBarParams);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        constraintSet.connect(pinDescription.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP, AndroidUtilities.dp(12));
        constraintSet.connect(pinDescription.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
        constraintSet.connect(pinDescription.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);

        constraintSet.connect(codeLayout.getId(), ConstraintSet.TOP, pinDescription.getId(), ConstraintSet.BOTTOM, AndroidUtilities.dp(24));
        constraintSet.connect(codeLayout.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
        constraintSet.connect(codeLayout.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);

        constraintSet.connect(resendTextView.getId(), ConstraintSet.TOP, codeLayout.getId(), ConstraintSet.BOTTOM, AndroidUtilities.dp(24));
        constraintSet.connect(resendTextView.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
        constraintSet.connect(resendTextView.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);

        constraintSet.connect(buttonLayout.getId(), ConstraintSet.BOTTOM, layout.getId(), ConstraintSet.BOTTOM, 24);
        constraintSet.connect(buttonLayout.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
        constraintSet.connect(buttonLayout.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);

        constraintSet.applyTo(layout);

        startResendCodeTimer();

        resendTextView.setOnClickListener(v -> resendCode());
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
            P2PService p2PService = new P2PService(context);
            JsonObject requestObject = new JsonObject();
            requestObject.addProperty("code", pinCode.toString());
            requestObject.addProperty("sender_ext_id", jsonObject.get("sender_ext_id").getAsString());
            requestObject.addProperty("receiver_ext_id", jsonObject.get("receiver_ext_id").getAsString());
            p2PService.cardTransferTwo(new ApiCallback<JsonObject>() {
                @Override
                public void onSuccess(JsonObject response) {
                    progressBar.setVisibility(View.GONE);
                    nextButton.setText("Дальше");
                    nextButton.setEnabled(true);
                    Toast.makeText(context, "O'tkazma muvofaqqiyatli amalga oshirildi", Toast.LENGTH_LONG).show();
                    presentFragment(new TransferHistoryFragment(
                            amount,cardNumber
                    ));
                }
                @Override
                public void onFailure(String errorMessage) {
                    progressBar.setVisibility(View.GONE);
                    nextButton.setText("Дальше");
                    nextButton.setEnabled(true);
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                }
            }, requestObject);
        } else {
            Toast.makeText(context, "Please enter all 6 digits", Toast.LENGTH_LONG).show();
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
                String timerText = String.format("00:%02d", millisUntilFinished / 1000);
                String fullText = String.format("%s: %s", LocaleController.getString(R.string.resend_code), timerText);

                SpannableString spannable = new SpannableString(fullText);
                int start = fullText.indexOf(timerText);
                int end = start + timerText.length();

                spannable.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                resendTextView.setText(spannable);
            }

            public void onFinish() {
                resendTextView.setText(LocaleController.getString(R.string.resend_code));
                resendTextView.setEnabled(true);
            }
        }.start();
    }

    private void resendCode() {
        ProgresBarDialog progresBarDialog=new ProgresBarDialog(getContext());
        P2PService p2PService = new P2PService(getContext());
       progresBarDialog.showProgressDialog();
        p2PService.cardTransfer(new ApiCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject response) {
                jsonObject=response;
                progresBarDialog.dismissProgressDialog();
                nextButton.setText("Дальше");
                nextButton.setEnabled(true);
                Toast.makeText(getContext(), "Код повторно отправлен", Toast.LENGTH_SHORT).show();
                startResendCodeTimer();
            }

            @Override
            public void onFailure(String errorMessage) {
                progresBarDialog.dismissProgressDialog();
                nextButton.setText("Дальше");
                nextButton.setEnabled(true);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        }, resendOtp);

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
