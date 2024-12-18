package org.finance.ui.addCard.local;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.JsonObject;
import org.finance.data.model.add_card.AddCardInfoModel;
import org.finance.data.service.AddCardService;
import org.finance.data.service.ApiCallback;
import org.finance.helpers.RemoveView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;

import java.util.Calendar;

/** @noinspection ALL*/
public class AddCardFragment extends BaseFragment {
    private EditText cardNumberEditText;
    private EditText expiryDateEditText;
    private EditText cardNameEditText;
    private Button nextButton;
    private TextView cardNumberTextView;
    private TextView expiryDateTextView;
    private TextView expiryErrorTextView;
    private TextView cardNumberErrorTextView;
    private TextView cardNameErrorTextView;
    private TextView cardNameTextView;
    private AddCardInfoModel cardInfoModel;
    private ConstraintLayout constraintLayoutCard;
    private ImageView bankLogo;
    private  ImageView cardType;
    private Dialog progressDialog;
    private TextView charCounter;
    private TextView titleText;

    private ProgressBar progressBar;
    private AddCardService addCardService;
    private View createView;
    private  boolean isCard=false;
    private RelativeLayout rootView;

    @Override
    public View createView(Context context) {
        if (createView==null){
            createView=viewCreate(context);
        }
        return createView;
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    public View viewCreate(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        rootView = (RelativeLayout) inflater.inflate(R.layout.add_card_layout, null);
        addCardService = new AddCardService(getContext());

        actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
        actionBar.setBackgroundColor(Color.parseColor("#50A7EA"));
        actionBar.setTitle(LocaleController.getString(R.string.add_card));
        actionBar.setAllowOverlayTitle(true);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {

                    finishFragment();
                    clearViews();
                    RemoveView.clearsView(rootView);
                }
            }
        });



        cardNumberTextView = rootView.findViewById(R.id.cardNumber);
        titleText=rootView.findViewById(R.id.titleText);
        titleText.setText(LocaleController.getString(R.string.add_card_title));
        expiryDateTextView = rootView.findViewById(R.id.expiryDate);
        cardNameTextView = rootView.findViewById(R.id.cardHolderName);
        TextView cardNumberText=rootView.findViewById(R.id.cardNumberText);
        cardNumberText.setText(LocaleController.getString(R.string.Card_number));
        cardNameTextView.setText(LocaleController.getString(R.string.Card_name));
        TextView cardHolderNameText=rootView.findViewById(R.id.cardHolderNameText);
        cardHolderNameText.setText(LocaleController.getString(R.string.Card_name));
        constraintLayoutCard=rootView.findViewById(R.id.cardView);
        bankLogo=rootView.findViewById(R.id.bankLogo);
        expiryErrorTextView=rootView.findViewById(R.id.cardExpError);
        cardNumberErrorTextView =rootView.findViewById(R.id.cardNumberError);
        cardNameErrorTextView=rootView.findViewById(R.id.cardNameError);
        cardType=rootView.findViewById(R.id.cardTypeLogo);
        nextButton = rootView.findViewById(R.id.submitButton);
        nextButton.setText(LocaleController.getString(R.string.add_card));
        charCounter=rootView.findViewById(R.id.cardNameCounter);
        cardNumberEditText =  rootView.findViewById(R.id.cardNumberEditText);
        cardNumberEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
        progressBar=rootView.findViewById(R.id.progressBar);
        TextView expiryDateText=rootView.findViewById(R.id.expiryDateText);
        expiryDateText.setText(LocaleController.getString(R.string.card_exp));
        cardNumberEditText.addTextChangedListener(new TextWatcher() {
            private static final char SPACE = ' ';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    cardNumberEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0);
                } else {
                    cardNumberEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                cardNumberEditText.removeTextChangedListener(this);
                String input = s.toString().replaceAll("\\D", "");
                int length = input.length();
                StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < length && i < 16; i++) {
                    if (i > 0 && i % 4 == 0) {
                        formatted.append(SPACE);
                    }
                    formatted.append(input.charAt(i));
                }
                if (!formatted.toString().equals(s.toString())) {
                    cardNumberEditText.setText(formatted.toString());
                    cardNumberEditText.setSelection(formatted.length());
                }
                cardNumberEditText.addTextChangedListener(this);

                if (input.length() == 16) {
                    String cardNumber = cardNumberEditText.getText().toString().replaceAll("\\s+", "");
                    if (luhnChecksum(cardNumber) != 0) {
                        Vibrator v = (Vibrator) getParentActivity().getSystemService(Context.VIBRATOR_SERVICE);
                        if (v != null) {
                            v.vibrate(200);
                        }
                        Toast.makeText(context, "Karta raqam xato kiritildi", Toast.LENGTH_LONG).show();
                        cardNumberEditText.setBackgroundResource(R.drawable.rounded_edittext_error);
                        cardNumberErrorTextView.setVisibility(View.VISIBLE);
                        AndroidUtilities.shakeView(cardNumberEditText);
                    } else {
                        cardNumberErrorTextView.setVisibility(View.GONE);
                        cardNumberTextView.setText(cardNumberEditText.getText());
                        sendCardInfoRequest(input);
                    }
                } else {
                    cardNumberErrorTextView.setVisibility(View.GONE);
                    clearCardui();
                    cardNumberEditText.setBackgroundResource(R.drawable.rounded_edittext);
                }
            }
        });
        cardNumberEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (cardNumberEditText.getCompoundDrawables()[2] != null) {
                    if (event.getRawX() >= (cardNumberEditText.getRight() - cardNumberEditText.getCompoundDrawables()[2].getBounds().width())) {
                        clearCardui();
                        cardNumberEditText.setText("");
                        return true;
                    }
                }
            }
            return false;
        });
        cardNumberEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) getParentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(cardNumberEditText.getWindowToken(), 0);
                }
                return true;
            }
            return false;
        });



        expiryDateEditText = rootView.findViewById(R.id.expiryDateEditText);
        expiryDateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    expiryDateEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0);
                } else {
                    expiryDateEditText.setBackgroundResource(R.drawable.rounded_edittext);
                    expiryDateEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
                if (s.length() == 2) {
                    int month = Integer.parseInt(s.toString().substring(0, 2));
                    if (month < 1 || month > 12) {
                        expError();
                        Toast.makeText(context, "Oy 01 dan 12 gacha bo'lishi kerak.", Toast.LENGTH_SHORT).show();
                        expiryDateEditText.setText("");
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().replaceAll("\\D", "");  // Faqat raqamlarni oling
                StringBuilder formatted = new StringBuilder();
                if (!input.isEmpty()) {
                    formatted.append(input.charAt(0));
                }
                if (input.length() > 1) {
                    formatted.append(input.charAt(1));
                    if (input.length() > 2) {
                        formatted.append('/');
                        formatted.append(input.charAt(2));
                    }
                }
                if (input.length() > 3) {
                    formatted.append(input.charAt(3));
                }

                expiryDateEditText.removeTextChangedListener(this);
                expiryDateEditText.setText(formatted.toString());
                expiryDateEditText.setSelection(formatted.length());
                expiryDateEditText.addTextChangedListener(this);
                if (formatted.length() == 5) {
                    int month = Integer.parseInt(formatted.substring(0, 2));
                    int year = Integer.parseInt(formatted.substring(3, 5));
                    Calendar calendar = Calendar.getInstance();
                    int currentYear = calendar.get(Calendar.YEAR) % 100;
                    int minYear = (currentYear - 5 + 100) % 100;
                    int maxYear = (currentYear + 5) % 100;
                    if (!isValidYear(year, currentYear, minYear, maxYear)) {
                        expError();
                    } else {
                        expiryErrorTextView.setVisibility(View.GONE);
                        expiryDateTextView.setText(expiryDateEditText.getText());
                        cardNameEditText.requestFocus();
                        expiryDateEditText.setBackgroundResource(R.drawable.rounded_edittext);

                    }
                }
            }
            private boolean isValidYear(int year, int currentYear, int minYear, int maxYear) {
                if (minYear <= maxYear) {
                    return year >= minYear && year <= maxYear;
                } else {
                    return year >= minYear || year <= maxYear;
                }
            }
        });
        expiryDateEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (expiryDateEditText.getCompoundDrawables()[2] != null) {
                    if (event.getRawX() >= (expiryDateEditText.getRight() - expiryDateEditText.getCompoundDrawables()[2].getBounds().width())) {
                        expiryDateEditText.setText("");
                        expiryErrorTextView.setVisibility(View.GONE);
                        expiryDateEditText.setBackgroundResource(R.drawable.rounded_edittext);
                        return true;
                    }
                }
            }
            return false;
        });
        cardNameEditText = rootView.findViewById(R.id.cardNameEditText);
        cardNameEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        cardNameEditText.setHint(LocaleController.getString(R.string.enter_the_card_name));
        cardNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                charCounter.setText(length + "/20");
                if (s.length() > 0) {
                    cardNameEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0);
                } else {
                    cardNameEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();

                if (input.length() > 20) {
                    s.delete(20, input.length());

                    return;
                }
                if (input.length() >= 3) {
                    cardNameTextView.setText(input);
                    nextButton.setEnabled(true);
                    nextButton.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_button));
                } else {
                    nextButton.setEnabled(false);
                    nextButton.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_empty_button));
                    cardNameTextView.setText(LocaleController.getString(R.string.Card_name));
                }
            }
        });
        cardNameEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (cardNameEditText.getCompoundDrawables()[2] != null) {
                    if (event.getRawX() >= (cardNameEditText.getRight() - cardNameEditText.getCompoundDrawables()[2].getBounds().width())) {
                        cardNameEditText.setText("");
                        return true;
                    }
                }
            }
            return false;
        });
        nextButton.setOnClickListener(v -> onNextButtonClick());
        return rootView;
    }

    private int luhnChecksum(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10;
    }

    private void onNextButtonClick() {
        if (isCard){
            String cardNumber = cardNumberEditText.getText().toString().replaceAll("\\s+", "");
            String expiryDate = expiryDateEditText.getText().toString();
            String cardName = cardNameEditText.getText().toString();
            hideKeyboardFromView(getContext(),nextButton);
            if (cardNumber.isEmpty() || cardNumber.length() < 16) {
                Vibrator v = (Vibrator) getParentActivity().getSystemService(Context.VIBRATOR_SERVICE);
                if (v != null) {
                    v.vibrate(200);
                }
                Toast.makeText(getContext(), "Please enter a valid card number.", Toast.LENGTH_SHORT).show();
                AndroidUtilities.shakeView(cardNumberEditText);
                return;
            }

            if (expiryDate.isEmpty() || !expiryDate.matches("\\d{2}/\\d{2}")) {
                Vibrator v = (Vibrator) getParentActivity().getSystemService(Context.VIBRATOR_SERVICE);
                if (v != null) {
                    v.vibrate(200);
                }
                Toast.makeText(getContext(), "Please enter a valid expiry date (MM/YY).", Toast.LENGTH_SHORT).show();
                AndroidUtilities.shakeView(expiryDateEditText);
                return;
            }

            if (cardName.isEmpty()) {
                Vibrator v = (Vibrator) getParentActivity().getSystemService(Context.VIBRATOR_SERVICE);
                if (v != null) {
                    v.vibrate(200);
                }
                Toast.makeText(getContext(), "Please enter a card name.", Toast.LENGTH_SHORT).show();
                AndroidUtilities.shakeView(cardNameEditText);
                return;
            }



            JsonObject requestObject = new JsonObject();
            requestObject.addProperty("card_name", cardName);
            requestObject.addProperty("card_number", cardNumber);
            requestObject.addProperty("expire", expiryDate);


            showProgressDialog();
            addCardService.addCardStepOne(new ApiCallback<JsonObject>() {
                @Override
                public void onSuccess(JsonObject response) {
                    dismissProgressDialog();
                    nextButton.setEnabled(true);
                    presentFragment(new PinInputFragment(response,requestObject ));
                }

                @Override
                public void onFailure(String errorMessage) {
                    dismissProgressDialog();
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }, requestObject);}
        else {
            Toast.makeText(getContext(),"Ushbu karta oldin qo'shilgan uni qayta qo'sholmaysiz",Toast.LENGTH_LONG).show();
        }
    }
    private void sendCardInfoRequest(String cardNumber) {
        JsonObject requestObject = new JsonObject();
        requestObject.addProperty("number", cardNumber);
        progressBar.setVisibility(View.VISIBLE);
        addCardService.addCardInfo(new ApiCallback<AddCardInfoModel>() {
            @Override
            public void onSuccess(AddCardInfoModel response) {
                progressBar.setVisibility(View.GONE);
                cardInfoModel=response;
                expiryDateEditText.requestFocus();
                cardNumberErrorTextView.setVisibility(View.VISIBLE);
                cardNumberErrorTextView.setTextColor(Color.parseColor("#1F1F1F"));
                cardNumberErrorTextView.setText(cardInfoModel.getResult().getOwner());
                isCard=true;
                if(cardInfoModel.getResult().getPcType()==1){
                    cardType.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_uzcard));
                }else if(cardInfoModel.getResult().getPcType()==3){
                    cardType.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.humo_card));
                }
                try {
                    updatedCardInfo();
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                cardNumberErrorTextView.setVisibility(View.VISIBLE);
                cardNumberErrorTextView.setText(errorMessage);
                cardNumberErrorTextView.setTextColor(Color.RED);
                isCard=false;
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        }, requestObject);
    }
    private void updatedCardInfo(){
        if (cardInfoModel!=null){
            Glide.with(getContext())
                    .load(BuildConfig.STATIC_URL +"app-superapp/static"+ cardInfoModel.getImageBack())
                    .apply(new RequestOptions().transform(new RoundedCorners(12)))  // Apply rounded corners
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            constraintLayoutCard.setBackground(resource);
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            constraintLayoutCard.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.add_card_background));
                        }
                    });
            Glide.with(getContext())
                    .load(BuildConfig.STATIC_URL +"app-superapp/static"+ cardInfoModel.getImageIcon())
                    .apply(new RequestOptions().transform(new RoundedCorners(4)))
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            bankLogo.setImageDrawable(resource);
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            bankLogo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_bank_logo));
                        }
                    });
        }

    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(getContext());
            progressDialog.setContentView(R.layout.progresbar_dialog);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    private void expError(){
        Vibrator v = (Vibrator) getParentActivity().getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            v.vibrate(200);
        }
        Toast.makeText(getContext(), "Amal qilish muddati xato kiritildi", Toast.LENGTH_LONG).show();
        expiryDateEditText.setBackgroundResource(R.drawable.rounded_edittext_error);
        expiryErrorTextView.setVisibility(View.VISIBLE);
        AndroidUtilities.shakeView(expiryDateEditText);
    }
    public void clearCardui(){
        constraintLayoutCard.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.add_card_background));
        bankLogo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_bank_logo));
        cardType.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_edittext));
        cardType.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_card_type));
        cardNumberTextView.setText("0000 0000 0000 0000");
        expiryDateTextView.setText("00/00");
        cardNameTextView.setText(LocaleController.getString(R.string.Card_name));
    }
    public static void hideKeyboardFromView(Context context, View view) {
        if (context != null && view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        RemoveView.clearsView(rootView);
        return super.onBackPressed();
    }
}
