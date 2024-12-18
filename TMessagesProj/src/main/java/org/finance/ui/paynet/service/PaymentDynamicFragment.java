package org.finance.ui.paynet.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.finance.data.model.paynet.chekcomunal.Cheque;
import org.finance.data.model.paynet.service.Fields;
import org.finance.data.model.paynet.service.PaymentServiceModel;
import org.finance.data.model.paynet.service.Services;
import org.finance.data.room.DataBaseClient;
import org.finance.data.room.SuperAppDatabase;
import org.finance.data.service.ApiCallback;
import org.finance.data.service.PaynetService;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;

import java.util.HashMap;
import java.util.Map;

/** @noinspection ALL */
public class PaymentDynamicFragment extends BaseFragment {


    private final PaymentServiceModel serviceModel;
    private final String imageUrl;
    private JsonObject jsonObject;
    private JsonObject chekJson;
    private ProgressBar progressBar;
    private HashMap<Integer, EditText> dynamicInputsMap;
    private HashMap<Integer, String> fieldNameMap;
    private  Button nextButton;
    private  View rootView;
    private SuperAppDatabase database;
    TLRPC.User user;

    public PaymentDynamicFragment(PaymentServiceModel serviceModel, String imageUrl) {
        this.imageUrl = imageUrl;
        this.serviceModel = serviceModel;
        this.dynamicInputsMap = new HashMap<>();
        this.fieldNameMap = new HashMap<>();
        this.chekJson = new JsonObject();
    }

    @Override
    public View createView(Context context) {
        if(rootView==null){
            rootView=viewCreate(context);

        }
        return rootView;
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n", "ClickableViewAccessibility"})
    @Nullable
    public View viewCreate(@NonNull Context context) {
        ConstraintLayout layout = new ConstraintLayout(context);
        layout.setId(View.generateViewId());
        layout.setPadding(24, 24, 24, 24);
        layout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
        actionBar.setTitle(serviceModel.getTitleShort());
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
        actionBar.setAllowOverlayTitle(true);
        user = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
        database = DataBaseClient.getInstance(context, user.phone).getAppDatabase();
        ImageView serviceIcon = new ImageView(context);
        serviceIcon.setId(View.generateViewId());
        if (imageUrl!=null){
            serviceIcon.setLayoutParams(new ViewGroup.LayoutParams(AndroidUtilities.dp(128), AndroidUtilities.dp(128)));
        }else {
            serviceIcon.setLayoutParams(new ViewGroup.LayoutParams(AndroidUtilities.dp(0), AndroidUtilities.dp(0)));
        }
        layout.addView(serviceIcon);

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.emtpy_image)
                .error(R.drawable.emtpy_image)
                .into(serviceIcon);

        TextView serviceTitle = new TextView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                AndroidUtilities.dp(52)
        );
        serviceTitle.setId(View.generateViewId());
        serviceTitle.setText(serviceModel.getTitleShort());
        serviceTitle.setTextSize(20);
        serviceTitle.setTextColor(Color.BLACK);
        serviceTitle.setTypeface(null, Typeface.BOLD);
        serviceTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        layout.addView(serviceTitle);

        boolean isServiceProcessed = false;

        for (Services service : serviceModel.getServices()) {
            if (isServiceProcessed) {
                break;
            }

            for (Fields inputField : service.getFields()) {

                if (("PHONE".equals(inputField.getFieldType()) ||
                        "CARDBOX".equals(inputField.getFieldType()) ||
                        "STRING".equals(inputField.getFieldType()) ||
                        "REGEXBOX".equals(inputField.getFieldType()) ||
                        "NUMBER".equals(inputField.getFieldType())) &&
                        inputField.getRequired()) {

                    // Create the EditText for the field
                    EditText dynamicInput = new EditText(context);
                    int viewId = View.generateViewId();
                    dynamicInput.setId(viewId);
                    dynamicInput.setHint(inputField.getTitleUz());
                    dynamicInput.setTextSize(20);
                    dynamicInput.setLayoutParams(layoutParams);
                    dynamicInput.setTextColor(Color.BLACK);
                    dynamicInput.setHintTextColor(Color.parseColor("#AEAEAE"));
                    dynamicInput.setBackgroundResource(R.drawable.rounded_edittext);
                    dynamicInput.setOnEditorActionListener((v, actionId, event) -> {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            InputMethodManager imm = (InputMethodManager) getParentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.hideSoftInputFromWindow(dynamicInput.getWindowToken(), 0);
                            }
                            return true;
                        }
                        return false;
                    });
                    switch (inputField.getFieldType()) {
                        case "PHONE":
                            dynamicInput.setInputType(InputType.TYPE_CLASS_PHONE);
                            dynamicInput.setText("+998 ");
                            dynamicInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(17)});
                            dynamicInput.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                                @Override
                                public void afterTextChanged(Editable s) {
                                    String input = s.toString().replace("+998 ", "").replaceAll("\\D", "");

                                    StringBuilder formatted = new StringBuilder("+998 ");
                                    if (!input.isEmpty()) {
                                        formatted.append(input.substring(0, Math.min(input.length(), 2)));
                                    }
                                    if (input.length() > 2) {
                                        formatted.append(" ").append(input.substring(2, Math.min(input.length(), 5)));
                                    }
                                    if (input.length() > 5) {
                                        formatted.append("-").append(input.substring(5, Math.min(input.length(), 7)));
                                    }
                                    if (input.length() > 7) {
                                        formatted.append("-").append(input.substring(7, Math.min(input.length(), 9)));
                                    }

                                    dynamicInput.removeTextChangedListener(this);
                                    dynamicInput.setText(formatted);
                                    dynamicInput.setSelection(formatted.length());
                                    dynamicInput.addTextChangedListener(this);
                                }
                            });
                            break;
                        case "STRING":
                        case "CARDBOX":
                        case "REGEXBOX":
                            dynamicInput.setInputType(InputType.TYPE_CLASS_TEXT);
                            break;
                        case "NUMBER":
                            dynamicInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + inputField.getFieldType());
                    }

                    dynamicInputsMap.put(viewId, dynamicInput);
                    fieldNameMap.put(viewId, inputField.getName());
                    layout.addView(dynamicInput);
                    dynamicInput.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            checkAllInputsFilled();
                        }
                    });

                }
            }

            isServiceProcessed = true;
        }
        FrameLayout buttonLayout = new FrameLayout(context);
        buttonLayout.setLayoutParams(layoutParams);
        buttonLayout.setId(View.generateViewId());
        layout.addView(buttonLayout);
        nextButton = new Button(context);
        nextButton.setId(View.generateViewId());
        nextButton.setText("Дальше");
        nextButton.setTextSize(16);
        nextButton.setEnabled(false);
        nextButton.setLayoutParams(layoutParams);
        nextButton.setTextColor(Color.parseColor("#F6F6F6"));
        nextButton.setBackground(context.getDrawable(R.drawable.rounded_empty_button));
        nextButton.setOnClickListener(v -> {
            createServiceId();
            Log.d("Check JSON after creation", chekJson.toString());

            PaynetService paynetService = new PaynetService(context);

            if (chekJson != null && chekJson.get("fields") != null) {
                nextButton.setText("");
                nextButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                paynetService.paymentCommunalChek(new ApiCallback<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject response) {
                        try {
                            nextButton.setText("Дальше");
                            progressBar.setVisibility(View.GONE);
                            nextButton.setEnabled(true);

                            Log.d("Cheque Response", response.toString());

                            if (response.has("result")) {
                                JsonObject resultJson = response.getAsJsonObject("result");

                                if (resultJson != null && resultJson.has("cheque")) {
                                    JsonObject chequeJson = resultJson.getAsJsonObject("cheque");

                                    if (chequeJson != null) {
                                        Gson gson = new Gson();
                                        Cheque cheque = gson.fromJson(chequeJson, Cheque.class);
                                        database.cardsDao().getAllCardBalancesWithCards().observe(getViewLifecycleOwner(),getCardBalanceWithCards -> {
                                        presentFragment(new PaymentDynamicInfoFragment(serviceModel, cheque, jsonObject,getCardBalanceWithCards));});
                                    } else {
                                        throw new Exception("Cheque object is null");
                                    }
                                } else {
                                    throw new Exception("Cheque data is missing in the response");
                                }
                            } else {
                                throw new Exception("Result data is missing in the response");
                            }

                        } catch (Exception e) {
                            Log.d("Xatolik", e.getMessage());
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        nextButton.setText("Дальше");
                        nextButton.setEnabled(true);
                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }, chekJson);
            }
        });
        progressBar = new ProgressBar(context);
        FrameLayout.LayoutParams progressBarParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        progressBarParams.gravity = Gravity.CENTER;
        progressBar.setVisibility(View.GONE);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
        if (nextButton.getParent() != null) {
            ((ViewGroup) nextButton.getParent()).removeView(nextButton);
        }
        buttonLayout.addView(nextButton);

        if (progressBar.getParent() != null) {
            ((ViewGroup) progressBar.getParent()).removeView(progressBar);
        }
        buttonLayout.addView(progressBar, progressBarParams);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        constraintSet.connect(serviceIcon.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP, 48);
        constraintSet.connect(serviceIcon.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
        constraintSet.connect(serviceIcon.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);

        constraintSet.connect(serviceTitle.getId(), ConstraintSet.TOP, serviceIcon.getId(), ConstraintSet.BOTTOM, 8);
        constraintSet.connect(serviceTitle.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
        constraintSet.connect(serviceTitle.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);

        int previousViewId = serviceTitle.getId();
        for (View inputView : layout.getTouchables()) {
            if (inputView instanceof EditText) {
                constraintSet.connect(inputView.getId(), ConstraintSet.TOP, previousViewId, ConstraintSet.BOTTOM, 24);
                constraintSet.connect(inputView.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 24);
                constraintSet.connect(inputView.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 24);
                previousViewId = inputView.getId();
            }
        }
        constraintSet.connect(buttonLayout.getId(), ConstraintSet.BOTTOM, layout.getId(), ConstraintSet.BOTTOM, 24);
        constraintSet.connect(buttonLayout.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 24);
        constraintSet.connect(buttonLayout.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 24);
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

    private void createJsonFromInputs() {
        jsonObject = new JsonObject();
        for (Map.Entry<Integer, EditText> entry : dynamicInputsMap.entrySet()) {
            Integer viewId = entry.getKey();
            EditText editText = entry.getValue();
            String fieldName = fieldNameMap.get(viewId);
            String value = editText.getText().toString().trim().replace("+998", "")
                    .replace("-", "")
                    .replace(" ", "");
            ;
            try {
                jsonObject.addProperty(fieldName, value);

            } catch (Exception e) {
                Log.e("JSONError", "Error adding field to JSON", e);
            }
        }


    }

    private void createServiceId() {
        createJsonFromInputs();
        Log.d("JSON Object", jsonObject.toString());

        if (jsonObject != null) {
            for (Services service : serviceModel.getServices()) {

                Log.d("InputField Title", service.getTitleRu());

                if (service.getTitleRu() != null && service.getTitleRu().contains("Проверка")) {
                    try {
                        chekJson.addProperty("service_id", service.getId());
                        chekJson.add("fields", jsonObject);
                        Log.d("Check JSON", chekJson.toString()); // Log the chekJson
                    } catch (Exception e) {
                        Log.d("JSONException", e.getMessage());
                    }
                }

            }
        } else {
            Log.d("JSONError", "jsonObject is null");
        }
    }
    private void checkAllInputsFilled() {
        boolean allFilled = true;
        for (EditText input : dynamicInputsMap.values()) {
            if (input.getText().toString().trim().isEmpty()) {
                allFilled = false;
                break;
            }
        }
        if (allFilled) {
            nextButton.setEnabled(true);
            nextButton.setBackground(getContext().getDrawable(R.drawable.rounded_button)); // Change to the active button style
        } else {
            nextButton.setEnabled(false);
            nextButton.setBackground(getContext().getDrawable(R.drawable.rounded_empty_button)); // Change to the inactive button style
        }
    }
}
