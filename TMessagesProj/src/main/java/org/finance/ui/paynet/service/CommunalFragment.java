package org.finance.ui.paynet.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.finance.data.model.paynet.chekcomunal.PaymentCommunalGasModel;
import org.finance.data.model.paynet.chekcomunal.PaymentComunalChekModel;
import org.finance.data.model.paynet.service.FieldValues;
import org.finance.data.model.paynet.service.PaymentServiceModel;
import org.finance.data.room.DataBaseClient;
import org.finance.data.room.SuperAppDatabase;
import org.finance.data.service.ApiCallback;
import org.finance.data.service.PaynetService;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/** @noinspection ALL*/
public class CommunalFragment extends BaseFragment {
   private ChipGroup chipGroup;
    private String imageUrl;
    private EditText accountNumberInput;
    private Button nextButton;
    private TextView selectedRegion;
    private TextView errorTextView;

    private ProgressBar progressBar;
    private final PaymentServiceModel paymentServiceModel;
    private BottomSheetDialog bottomSheetDialog;
    private String regionName;
    private String regionId;
    private View rootView;
    private SuperAppDatabase database;
    TLRPC.User user;
    public CommunalFragment(PaymentServiceModel serviceModel, String imageUrl) {
        this.imageUrl = imageUrl;
        this.paymentServiceModel = serviceModel;
        List<FieldValues> fieldValues = paymentServiceModel.getServices().get(0).getFields().get(0).getFieldValues();
        regionName = fieldValues.get(0).getTitleUz();
        regionId = fieldValues.get(0).getId().toString();
    }

    @Override
    public View createView(@NonNull Context context) {
        actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
        actionBar.setTitle(paymentServiceModel.getTitleShort());
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
        if (rootView == null) {
            rootView = LayoutInflater.from(context).inflate(R.layout.communal_payment_layout, null);
            initViews(rootView, context);
        }
        rootView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View view = rootView.findFocus();
                if (view != null) {
                    view.clearFocus();
                }
            }
            return true;
        });
        return rootView;
    }

    private void initViews(View view, Context context) {
        selectedRegion = view.findViewById(R.id.selectedRegion);
        accountNumberInput = view.findViewById(R.id.accountNumberInput);
        nextButton = view.findViewById(R.id.nextButton);
        chipGroup = view.findViewById(R.id.chipGroup);
        progressBar = view.findViewById(R.id.progressBar);
        showSavedAccountNumbers();
        errorTextView=view.findViewById(R.id.errorTextView);
        selectedRegion.setText(regionName);
        selectedRegion.setOnClickListener(v -> {
            try {
                showBottomSheet(context);
            } catch (Exception e) {
                Toast.makeText(getContext(), "" + e, Toast.LENGTH_SHORT).show();
            }
        });

        accountNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handleAccountNumberChange(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        nextButton.setOnClickListener(view1 -> {
            nextButton.setText("");
            JsonObject jsonObject = new JsonObject();
            String shotNumber = accountNumberInput.getText().toString().replace(regionId, "")
                    .replace("-", "")
                    .replace(" ", "");
            jsonObject.addProperty("service_id", paymentServiceModel.getServices().get(0).getId());

            JsonObject fieldsObject = new JsonObject();
            fieldsObject.addProperty(paymentServiceModel.getServices().get(0).getFields().get(0).getName(), regionId);
            fieldsObject.addProperty(paymentServiceModel.getServices().get(0).getFields().get(1).getName(), shotNumber);

            jsonObject.add("fields", fieldsObject);
            nextButton.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);

            PaynetService paymentService = new PaynetService(context);
            paymentService.paymentCommunalChek(new ApiCallback<JsonObject>() {
                @Override
                public void onSuccess(JsonObject response) {
                    nextButton.setEnabled(true);
                    nextButton.setText("Дальше");
                    progressBar.setVisibility(View.GONE);
                    Gson gson = new Gson();
                    try {
                        saveAccountNumber(shotNumber);
                        PaymentCommunalGasModel model = gson.fromJson(response, PaymentCommunalGasModel.class);
                        PaymentComunalChekModel model2 = gson.fromJson(response, PaymentComunalChekModel.class);

                        if (model != null && model.getResult().getReceiver().getFields().getLicshet() != null) {
                            if(model.getResult().getDescription().equals("Проведен успешно")){
                                database.cardsDao().getAllCardBalancesWithCards().observe(getViewLifecycleOwner(),getCardBalanceWithCards -> {
                                    presentFragment(new CommunalInfoFragment(null, model, paymentServiceModel,getCardBalanceWithCards));});
                                }else {
                               errorTextView.setEnabled(true);
                            }
                        } else if (model2 != null && model2.getResult() != null) {
                            database.cardsDao().getAllCardBalancesWithCards().observe(getViewLifecycleOwner(),getCardBalanceWithCards -> {
                            presentFragment(new CommunalInfoFragment(model2, null, paymentServiceModel,getCardBalanceWithCards));});
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    nextButton.setText("Дальше");
                    nextButton.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }, jsonObject);
        });

    }


    private void showBottomSheet(Context context) {
        bottomSheetDialog = new BottomSheetDialog(context);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackgroundColor(Color.WHITE);

        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = 12;
            }
        });

        layout.addView(recyclerView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                24
        ));

        CardAdapter cardAdapter = new CardAdapter(new ArrayList<>(), context);
        recyclerView.setAdapter(cardAdapter);
        region(cardAdapter);

        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.show();
    }
    @SuppressLint("SetTextI18n")
    private void updateCardLayout() {
  selectedRegion.setText(regionName);
    }
    private void region(CardAdapter cardAdapter) {
        List<FieldValues> fieldValues = paymentServiceModel.getServices().get(0).getFields().get(0).getFieldValues();
        try {
            cardAdapter.updateCardList(fieldValues);
        }catch (Exception e) {
            Toast.makeText(getContext(), ""+e, Toast.LENGTH_SHORT).show();
        }
    }

    private class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
        private List<FieldValues> cardList;
        private final Context context;
        public CardAdapter(List<FieldValues> cardList, Context context) {
            this.cardList = cardList;
            this.context = context;
        }
        @NonNull
        @Override
        public CardAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(context);
            textView.setPadding(24, 16, 24, 16);
            textView.setTextSize(16);
            textView.setTextColor(Color.BLACK);
            return new CardViewHolder(textView);
        }
        @Override
        public void onBindViewHolder(@NonNull CardAdapter.CardViewHolder holder, int position) {
            FieldValues cardItem = cardList.get(position);
            holder.bind(cardItem,position);
        }

        @Override
        public int getItemCount() {
            return cardList.size();
        }
        @SuppressLint("NotifyDataSetChanged")
        public void updateCardList(List<FieldValues> newCardList) {
            this.cardList = newCardList;
            notifyDataSetChanged();
        }
        class CardViewHolder extends RecyclerView.ViewHolder {
            final TextView textView;

            public CardViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = (TextView) itemView;
            }
            @SuppressLint("DefaultLocale")
            public void bind(FieldValues cardItem,int position) {
                textView.setText(String.format( cardItem.getTitleUz()));

                itemView.setOnClickListener(v -> {
                    if (position != RecyclerView.NO_POSITION) {
                        FieldValues selectedCard = cardList.get(position);
                        CommunalFragment.this.regionName = String.valueOf(selectedCard.getTitleUz());
                        CommunalFragment.this.regionId=String.valueOf(selectedCard.getId());
                        updateCardLayout();

                        CommunalFragment.this.bottomSheetDialog.dismiss();

                    }
                });
            }
        }
    }
    private void handleAccountNumberChange(@NonNull String accountNumber) {
        nextButton.setEnabled(!accountNumber.isEmpty());
        nextButton.setBackgroundResource(R.drawable.rounded_button);
    }
    private void saveAccountNumber(String accountNumber) {
        SharedPreferences preferences = getContext().getSharedPreferences("account_prefs", Context.MODE_PRIVATE);
        Set<String> accounts = preferences.getStringSet("saved_accounts", new HashSet<>());
        if (accounts.size() >= 3) {
            Iterator<String> iterator = accounts.iterator();
            if (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }
        accounts.add(accountNumber);
        preferences.edit().putStringSet("saved_accounts", accounts).apply();
    }

    private Set<String> getSavedAccountNumbers() {
        SharedPreferences preferences = getContext().getSharedPreferences("account_prefs", Context.MODE_PRIVATE);
        return preferences.getStringSet("saved_accounts", new HashSet<>());
    }

    private void showSavedAccountNumbers() {
        Set<String> savedAccounts = getSavedAccountNumbers();

        for (String account : savedAccounts) {
            TextView chip = new TextView(getContext());
            chip.setText(account);
            chip.setPadding(16, 8, 16, 8);
            chip.setBackgroundResource(R.drawable.rounded_edittext); // Rounded background
            chip.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_delete_black, 0);
            chip.setCompoundDrawablePadding(8);
            chip.setTextColor(Color.BLACK);
            chip.setTextSize(14);
            chip.setOnClickListener(v -> {
                accountNumberInput.setText(account);
            });
            chip.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int drawableEndPosition = chip.getRight() - chip.getCompoundDrawables()[2].getBounds().width();
                    if (event.getRawX() >= drawableEndPosition) {
                        removeAccountNumber(account);
                        chipGroup.removeView(chip);
                        return true;
                    }
                }
                return false;
            });

            chipGroup.addView(chip);
        }
    }
    private void removeAccountNumber(String accountNumber) {
        SharedPreferences preferences = getContext().getSharedPreferences("account_prefs", Context.MODE_PRIVATE);
        Set<String> accounts = preferences.getStringSet("saved_accounts", new HashSet<>());
        accounts.remove(accountNumber);
        preferences.edit().putStringSet("saved_accounts", accounts).apply();
    }

}
