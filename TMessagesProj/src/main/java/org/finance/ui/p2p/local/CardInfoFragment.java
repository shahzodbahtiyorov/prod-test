package org.finance.ui.p2p.local;



import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;

import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.finance.data.model.get_card.CardInfoModel;
import org.finance.data.model.get_card.TransferCardModel;
import org.finance.data.service.ApiCallback;
import org.finance.data.service.P2PService;
import org.finance.ui.adapters.SaveCardAdapter;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CardInfoFragment extends BaseFragment {

    private EditText cardNumberInput;
    private Dialog progressDialog;
    private P2PService p2pService;
    private CardInfoModel cardInfoModel;
    private View rootView;
    private TextView cardOwnerName;
    private  ImageView searchIcon;
    private RecyclerView recyclerView;
    private   ProgressBar progressBar;
    private   TransferCardModel transferCardModel;
    private TextView receiver;
    private final List<TransferCardModel> originalCardList = new ArrayList<>();
    private SaveCardAdapter saveCardAdapter;
    private final int  id;
    public CardInfoFragment(int id){
        this.id=id;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Nullable
    @Override
    public View createView(@NonNull Context context) {
        if (rootView==null){
            rootView=viewCreate(context);
        }
        return rootView;
    }
    @SuppressLint({"UseCompatLoadingForDrawables", "MissingInflatedId", "ClickableViewAccessibility", "NotifyDataSetChanged"})
    public View viewCreate(Context context) {
        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(context).inflate(R.layout.transfer_uzcard_humo_layout, null);
        p2pService = new P2PService(context);
        actionBar.setBackgroundColor(Color.parseColor("#50A7EA"));
        actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
        actionBar.setTitle("Перевод на карты Uzcard/Humo");
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
        Button nextButton = rootView.findViewById(R.id.nextButton);
        searchIcon=rootView.findViewById(R.id.searchIcon);
        cardOwnerName=rootView.findViewById(R.id.cardOwnerName);
        cardNumberInput = rootView.findViewById(R.id.cardNumberInput);
        transferCardModel = new TransferCardModel();
        recyclerView=rootView.findViewById(R.id.recentTransactionsContainer);
        receiver=rootView.findViewById(R.id.recentTransactionsLabel);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = 12;
                outRect.left = 12;
                outRect.right = 12;
                outRect.top = 12;
            }
        });
        progressBar=rootView.findViewById(R.id.progressBar);
        cardNumberInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        int marginInDp = 24;
        int marginInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, marginInDp, context.getResources().getDisplayMetrics());

        layoutParams.setMargins(marginInPx, 0, marginInPx, 0);
        cardNumberInput.setLayoutParams(layoutParams);
        cardNumberInput.requestFocus();
        cardNumberInput.addTextChangedListener(new TextWatcher() {
            private static final char SPACE = ' ';
            private boolean isFormatting;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
              if (s.length()>0){
                  filterCardList(s.toString().replaceAll("\\D", ""));
              }else if (s.length()==0){
                  saveCardAdapter.updateList(originalCardList);
              }
              if (s.length() > 0) {
                  cardNumberInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0);

                } else {
                  cardNumberInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                }
            }
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
                  if (s.length() == 0) {
                   saveCardAdapter.updateList(originalCardList);
                    }
                  else if (input.length() == 16) {
                    String cardNumber = cardNumberInput.getText().toString().replaceAll("\\s+", "");
                    if (luhnChecksum(cardNumber) != 0) {
                        Vibrator v = (Vibrator) getParentActivity().getSystemService(Context.VIBRATOR_SERVICE);
                        if (v != null) {
                            v.vibrate(200);
                        }
                        Toast.makeText(context, "Karta raqam xato kiritildi", Toast.LENGTH_LONG).show();
                        AndroidUtilities.shakeView(cardNumberInput);
                    } else {
                        sendCardInfoRequest(input);
                    }
                }else {
                    clearUi();
                }
            }
        });
        cardNumberInput.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (cardNumberInput.getCompoundDrawables()[2] != null) {
                    if (event.getRawX() >= (cardNumberInput.getRight() - cardNumberInput.getCompoundDrawables()[2].getBounds().width())) {
                        cardNumberInput.setText("");
                        if (!originalCardList.isEmpty()) {
                            saveCardAdapter.updateList(originalCardList);
                            receiver.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        return true;
                    }
                }
            }
            return false;
        });
        cardNumberInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) getParentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(cardNumberInput.getWindowToken(), 0);
                }
                return true;
            }
            return false;
        });
        nextButton.setOnClickListener(v -> {
            if (cardInfoModel != null) {
                transferCardModel.setCardNumber(cardInfoModel.getResult().getCardNumber());
                transferCardModel.setCardName(cardInfoModel.getResult().getOwner());
                transferCardModel.setPcType(cardInfoModel.getResult().getPcType());
                cardNumberInput.clearFocus();
                presentFragment(new TransferAmountFragment(id,transferCardModel));
                    InputMethodManager imm = (InputMethodManager) getParentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

            } else {
                Toast.makeText(context, "Ma'lumotlarni kiriting", Toast.LENGTH_LONG).show();
            }
        });
        getReceiverCard(this);

        return rootView;
    }
    private void sendCardInfoRequest(String cardNumber) {
        showProgressDialog();
        JsonObject requestObject = new JsonObject();
        requestObject.addProperty("receiver", cardNumber);
        p2pService.cardInfo(new ApiCallback<CardInfoModel>() {
            @Override
            public void onSuccess(CardInfoModel response) {

                cardInfoModel = response;
                updateCardLayout(response);
                dismissProgressDialog();
            }
            @Override
            public void onFailure(String errorMessage) {
                dismissProgressDialog();
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        }, requestObject);
    }
    private void updateCardLayout(CardInfoModel cardInfoModel) {
        cardOwnerName.setVisibility(View.VISIBLE);
        cardOwnerName.setText(cardInfoModel.getResult().getOwner());
        if (cardInfoModel.getResult().getPcType() == 3) {
            searchIcon.setImageResource( R.drawable.humo_card);
        } else if (cardInfoModel.getResult().getPcType()  == 1) {
            searchIcon.setImageResource( R.drawable.ic_uzcard);
        } else if (cardInfoModel.getResult().getPcType() == 2) {
            searchIcon.setImageResource(R.drawable.ic_tkb_card);
        }
    }

    public void clearViews() {
        cardNumberInput.setText("");
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
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        }
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    private void clearUi(){

        cardOwnerName.setVisibility(View.GONE);
        searchIcon.setImageResource(R.drawable.ic_search);
    }
    private void getReceiverCard(BaseFragment baseFragment) {
        p2pService.getreceiredCard(new ApiCallback<List<TransferCardModel>>() {
            @Override
            public void onSuccess(List<TransferCardModel> response) {
                progressBar.setVisibility(View.GONE);
                receiver.setVisibility(View.VISIBLE);
                originalCardList.clear();
                originalCardList.addAll(response);
                saveCardAdapter = new SaveCardAdapter(getContext(), response, baseFragment,id);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(saveCardAdapter);
            }
            @Override
            public void onFailure(String errorMessage) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    private void filterCardList(String query) {
        List<TransferCardModel> filteredList = new ArrayList<>();
        for (TransferCardModel card : originalCardList) {
            if (card.getCardNumber() != null && card.getCardNumber().contains(query)) {
                filteredList.add(card);
            }
        }
        if (saveCardAdapter != null &&!filteredList.isEmpty()) {
            receiver.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            saveCardAdapter.updateList(filteredList);
        }else {
            receiver.setVisibility(View.GONE);
           recyclerView.setVisibility(View.GONE);
        }
    }

}
