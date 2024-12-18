package org.finance.ui.addCard.local;

import static org.telegram.messenger.AndroidUtilities.hideKeyboard;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import com.google.gson.JsonObject;
import org.finance.data.service.AddCardService;
import org.finance.data.service.ApiCallback;
import org.finance.helpers.RemoveView;
import org.finance.ui.home.HomeFragment;
import org.finance.ui.ui_helper.ProgresBarDialog;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;


/** @noinspection ALL*/
public class PinInputFragment extends BaseFragment {

    private EditText[] codeInputs;
    private Button nextButton;
    private  JsonObject jsonObject;
    private ProgressBar progressBar;
    private TextView resendTextView;
    private CountDownTimer countDownTimer;
    private final int count;
    String  phoneMask;
    TLRPC.User user;
    private final JsonObject requstObject;

    public PinInputFragment(JsonObject jsonObject,JsonObject requstObject) {
        this.jsonObject = jsonObject;
        this.requstObject=requstObject;
        phoneMask=jsonObject.get("phone").getAsString();
        count=jsonObject.get("count").getAsInt();
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables", "ClickableViewAccessibility"})
    @Nullable
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
                    RemoveView.clearsView(layout);
                    finishFragment();
                    clearViews();
                }
            }
        });
        user = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
        TextView confirmationText = new TextView(context);
        confirmationText.setId(View.generateViewId());
        confirmationText.setText(LocaleController.getString(R.string.otp_code_number)+"  " +maskPhoneNumber( phoneMask));
        confirmationText.setTextSize(16); // 16sp
        confirmationText.setTypeface(Typeface.create("SF Pro Text", Typeface.NORMAL));
        confirmationText.setTextColor(Color.parseColor("#1F1F1F"));
        confirmationText.setLineSpacing(0, 1);
        layout.addView(confirmationText);

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
        nextButton.setText(LocaleController.getString(R.string.add_card));
        nextButton.setTextSize(16);
        nextButton.setTextColor(Color.parseColor("#F6F6F6"));
        nextButton.setBackground(context.getDrawable(R.drawable.rounded_button));

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

        constraintSet.connect(confirmationText.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP, AndroidUtilities.dp(12));
        constraintSet.connect(confirmationText.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
        constraintSet.connect(confirmationText.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);

        constraintSet.connect(codeLayout.getId(), ConstraintSet.TOP, confirmationText.getId(), ConstraintSet.BOTTOM, AndroidUtilities.dp(24));
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

        resendTextView.setOnClickListener(v -> {
            resendCode();
        });

        nextButton.setOnClickListener(v -> {
            StringBuilder code = new StringBuilder();
            for (EditText input : codeInputs) {
                code.append(input.getText().toString().trim());
            }
            if (code.length() == count) {
                nextButton.setText("");
                nextButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                JsonObject requestObject = new JsonObject();
                requestObject.addProperty("code", code.toString());
                requestObject.addProperty("card_number", jsonObject.get("card_number").getAsString());
                requestObject.addProperty("expire", jsonObject.get("expire").getAsString());
                requestObject.addProperty("card_name", jsonObject.get("card_name").getAsString());

                AddCardService addCardService = new AddCardService(context);
                if (jsonObject.get("ext_id") != null) {
                    requestObject.addProperty("ext_id", jsonObject.get("ext_id").getAsString());
                }

                if (jsonObject.get("ext_id") != null && requestObject.get("ext_id") != null) {
                    addCardService.addCardStepTwo(new ApiCallback<JsonObject>() {
                        @Override
                        public void onSuccess(JsonObject response) {
                            progressBar.setVisibility(View.GONE);
                            nextButton.setText(LocaleController.getString(R.string.add_card));
                            nextButton.setEnabled(true);
                            presentFragment(new HomeFragment("financeView"),true);
                            Toast.makeText(context, "Karta qo'shildi", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            progressBar.setVisibility(View.GONE);
                            nextButton.setText(LocaleController.getString(R.string.add_card));
                            nextButton.setEnabled(true);
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }, requestObject);

                } else {

                    addCardService.addHumoCardStepTwo(new ApiCallback<JsonObject>() {
                        @Override
                        public void onSuccess(JsonObject response) {
                            progressBar.setVisibility(View.GONE);
                            nextButton.setText(LocaleController.getString(R.string.add_card));
                            nextButton.setEnabled(true);
                            presentFragment(new HomeFragment("financeView"),true);
                            Toast.makeText(context, "Karta qo'shildi", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            progressBar.setVisibility(View.GONE);
                            nextButton.setText(LocaleController.getString(R.string.add_card));
                            nextButton.setEnabled(true);
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }, requestObject);
                }} else{
                Toast.makeText(context, "Введите правильный код", Toast.LENGTH_LONG).show();
            }
        });

        return layout;
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
        for (EditText codeInput : codeInputs) {
            codeInput.setText("");
        }
        if (codeInputs.length == 0) codeInputs[0].requestFocus();
        ProgresBarDialog progresBarDialog=new ProgresBarDialog(getContext());
        AddCardService addCardService = new AddCardService(getContext());
       progresBarDialog.showProgressDialog();
        addCardService.addCardStepOne(new ApiCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject response) {
                jsonObject=response;
                progresBarDialog.dismissProgressDialog();
                startResendCodeTimer();

            }

            @Override
            public void onFailure(String errorMessage) {
                progresBarDialog.dismissProgressDialog();
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        }, requstObject);


    }
    public static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() == 13) {
            return phoneNumber.substring(0, 7) + "*** ** " + phoneNumber.substring(11);
        } else {
            return phoneNumber;
        }
    }
}