package org.finance.ui.p2p.tkb;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import org.telegram.messenger.R;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonObject;
import org.finance.data.model.get_card.CardInfoModel;
import org.finance.data.model.tkb.tkb_service_info.TkbServiceInfoModel;
import org.finance.data.model.tkb.tranfer.TkbTranferCreateModel;
import org.finance.data.service.ApiCallback;
import org.finance.data.service.P2PService;
import org.finance.data.service.TkbService;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;

public class TransferRFUZFragment extends BaseFragment {

    private EditText cardNumberInput, amountInput;
    private TextView cardOwnerTextView, conversionRate, errorTextView;
    private Button nextButton;
    private ProgressBar progressBar;
    private ProgressBar progressBar2;

    private P2PService p2pService;
    private TkbService tkbService;
    private String cardNumber;
    private LinearLayout amountContainer;
    private CheckBox termsCheckbox;
    private TkbServiceInfoModel serviceInfoModel;
    private Dialog progressDialog;
    private ImageView tooltip;
    private TooltipPopup tooltipPopup;
    private boolean isTooltipShown = false;

    @SuppressLint("MissingInflatedId")
    @Override
    public View createView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.rf_uz_fragment_layout, null);
        actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
        actionBar.setTitle("Перевод из России в Узбекистан");
        actionBar.setAllowOverlayTitle(true);
        actionBar.setBackgroundColor(Color.parseColor("#50A7EA"));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                    clearViews();
                }
            }
        });

        cardNumberInput = rootView.findViewById(R.id.receiverCardNumberInput);
        cardNumberInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
        amountInput = rootView.findViewById(R.id.transferAmountInput);
        cardOwnerTextView = rootView.findViewById(R.id.card_owner_name);
        conversionRate = rootView.findViewById(R.id.conversionRate);
        errorTextView = rootView.findViewById(R.id.errorTextView);
        nextButton = rootView.findViewById(R.id.nextButton);
        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar2 = rootView.findViewById(R.id.progressBar2);

        amountContainer = rootView.findViewById(R.id.amountContainer);
        termsCheckbox = rootView.findViewById(R.id.termsCheckbox);
        p2pService = new P2PService(context);
        tkbService = new TkbService(context);

        tooltip = rootView.findViewById(R.id.cardIcon);

        // Initialize TooltipPopup
        tooltipPopup = new TooltipPopup(context,"Для отправки денег из России на карты HUMO, UZCARD  введите номер карты получателя!");

        tooltip.setOnClickListener(v -> {
            if (isTooltipShown) {
                tooltipPopup.dismiss();
            } else {
                tooltipPopup.show(cardNumberInput);
            }
            isTooltipShown = !isTooltipShown;
        });

        serviceInfo();
        setupListeners(context);

        return rootView;
    }

    private void setupListeners(Context context) {
        cardNumberInput.addTextChangedListener(new TextWatcher() {
            private static final char SPACE = ' ';
            private boolean isFormatting;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) {
                    return;
                }
                isFormatting = true;
                String input = s.toString().replaceAll("\\D", "");
                StringBuilder formatted = new StringBuilder();

                int length = input.length();
                for (int i = 0; i < length; i++) {
                    if (i > 0 && i % 4 == 0) {
                        formatted.append(SPACE);
                    }
                    formatted.append(input.charAt(i));
                }

                cardNumberInput.removeTextChangedListener(this);
                cardNumberInput.setText(formatted.toString());
                cardNumberInput.setSelection(formatted.length());
                cardNumberInput.addTextChangedListener(this);

                isFormatting = false;

                if (input.length() == 16) {
                    String cardNumber1 = cardNumberInput.getText().toString().replaceAll("\\s+", "");
                    if (luhnChecksum(cardNumber1) != 0) {
                        Vibrator v = (Vibrator) getParentActivity().getSystemService(Context.VIBRATOR_SERVICE);
                        if (v != null) {
                            v.vibrate(200);
                        }
                        Toast.makeText(context, "Karta raqam xato kiritildi", Toast.LENGTH_LONG).show();
                        AndroidUtilities.shakeView(cardNumberInput);
                    } else {
                        cardNumber = input;
                        sendCardInfoRequest(input);
                    }
                } else if (input.length() < 16) {
                    cardOwnerTextView.setVisibility(View.GONE);
                }
            }
        });


        amountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed during text changes
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    errorTextView.setVisibility(View.GONE);
                    return;
                }

                try {
                    int inputAmount = Integer.parseInt(s.toString());
                    if (serviceInfoModel != null) {
                        int min = serviceInfoModel.getCommission().getTransfer().get(0).getMin();
                        int max = serviceInfoModel.getCommission().getTransfer().get(0).getMax();

                        if (inputAmount < min || inputAmount > max) {
                            errorTextView.setText("Amount should be between " + min + " and " + max);
                            errorTextView.setVisibility(View.VISIBLE);
                            amountContainer.setBackgroundResource(R.drawable.rounded_edittext_error);
                        } else {
                            errorTextView.setVisibility(View.GONE);
                            amountContainer.setBackgroundResource(R.drawable.rounded_edittext);
                        }
                    }
                } catch (NumberFormatException ignored) {
                    // Handle invalid number format
                }
            }
        });

        nextButton.setOnClickListener(v -> {
            if (!termsCheckbox.isChecked()) {
                Toast.makeText(getContext(), "Please agree to the terms.", Toast.LENGTH_SHORT).show();
                return;
            }

            String amount = amountInput.getText().toString();
            if (cardNumber == null || cardNumber.isEmpty() || amount.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                return;
            }
            performTransaction(context, cardNumber, Integer.parseInt(amount));
        });
    }

    private void serviceInfo() {
        tkbService.tkbServiseInfo(new ApiCallback<TkbServiceInfoModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(TkbServiceInfoModel response) {
                serviceInfoModel = response;
                progressBar.setVisibility(View.GONE);
                if (serviceInfoModel != null) {
                    conversionRate.setText("1 RUB = " + serviceInfoModel.getRate().getBuy() + " UZS");
                    conversionRate.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error loading conversion rate: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendCardInfoRequest(String cardNumber) {
        progressBar2.setVisibility(View.VISIBLE);
        JsonObject requestObject = new JsonObject();
        requestObject.addProperty("receiver", cardNumber);

        p2pService.cardInfo(new ApiCallback<CardInfoModel>() {
            @Override
            public void onSuccess(CardInfoModel response) {
                String owner = response.getResult().getOwner();
                cardOwnerTextView.setText(owner);
                progressBar2.setVisibility(View.GONE);
                cardOwnerTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String errorMessage) {
                progressBar2.setVisibility(View.GONE);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        }, requestObject);
    }

    private void performTransaction(Context context, String cardNumber, int amount) {
        showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("card_number", cardNumber);
        jsonObject.addProperty("amount", amount);
        jsonObject.addProperty("currency", 643);

        tkbService.tkbtransferCreate(new ApiCallback<TkbTranferCreateModel>() {
            @Override
            public void onSuccess(TkbTranferCreateModel response) {
                dismissProgressDialog();
               Browser.openUrl(context,response.getResult().getFormUrl());

            }

            @Override
            public void onFailure(String errorMessage) {
                dismissProgressDialog();
                Toast.makeText(context, "Transaction Failed: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        }, jsonObject);
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
}
