package org.finance.ui.p2p.local;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.finance.data.model.get_card.TransferCardModel;
import org.finance.data.room.DataBaseClient;
import org.finance.data.room.SuperAppDatabase;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;


public class TransferAmountFragment extends BaseFragment {
    private final TransferCardModel cardInfoModel;
    private  View rootCreateView;
    private EditText amountInput;
    private SuperAppDatabase database;
    TLRPC.User user;
    private final int id;
    public TransferAmountFragment(int id,TransferCardModel cardInfoModel) {
        this.cardInfoModel = cardInfoModel;
        this.id=id;
    }

    @Override
    public View createView(Context context) {
        if (rootCreateView==null){
            rootCreateView=viewCreate(context);
        }
        return rootCreateView;
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "MissingInflatedId", "ClickableViewAccessibility"})
    public View viewCreate(Context context) {
        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(context).inflate(R.layout.transfer_amount_layout, null);
        TextView cardNumberText = rootView.findViewById(R.id.cardNumberText);
        cardNumberText.setText(formatCardNumber(cardInfoModel.getCardNumber()));
        TextView cardHolderName=rootView.findViewById(R.id.cardHolderName);
        cardHolderName.setText(cardInfoModel.getCardName());
        ImageView cardIcon=rootView.findViewById(R.id.cardIcon);
        user = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
        database = DataBaseClient.getInstance(context, user.phone).getAppDatabase();
        if (cardInfoModel.getPcType() == 3) {

            cardIcon.setImageResource( R.drawable.humo_card);
        } else if (cardInfoModel.getPcType()  == 1) {

            cardIcon.setImageResource( R.drawable.ic_uzcard);
        } else if (cardInfoModel.getPcType() == 2) {

            cardIcon.setImageResource(R.drawable.ic_tkb_card);
        }
         amountInput=rootView.findViewById(R.id.transferAmount);
        amountInput.requestFocus();
        amountInput.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    amountInput.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("\\s", "");

                    if (!cleanString.isEmpty()) {
                        long parsed = Long.parseLong(cleanString);
                        @SuppressLint("DefaultLocale") String formatted = String.format("%,d", parsed).replace(',', ' ');

                        current = formatted;
                        amountInput.setText(formatted);
                        amountInput.setSelection(formatted.length());
                    }

                    amountInput.addTextChangedListener(this);
                }
            }
        });
        Button nextButton=rootView.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (!amountInput.getText().toString().isEmpty()){
                amountInput.clearFocus();
                fetchCardBalance();
                InputMethodManager imm = (InputMethodManager) getParentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }else {
                Toast.makeText(context,"Summani kiriting",Toast.LENGTH_LONG).show();
            }


        });
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
        rootView.setOnTouchListener((View v, @SuppressLint("ClickableViewAccessibility") MotionEvent event) -> {
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
    public static String formatCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) {
            return cardNumber;
        }

        StringBuilder formattedNumber = new StringBuilder();
        for (int i = 0; i < cardNumber.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formattedNumber.append(" ");
            }
            formattedNumber.append(cardNumber.charAt(i));
        }
        return formattedNumber.toString();
    }

    private void fetchCardBalance() {

                database.cardsDao().getAllCardBalancesWithCards().observe(getViewLifecycleOwner(),getCardBalanceWithCards -> {
                 if (!getCardBalanceWithCards.cards.isEmpty()){
                     presentFragment(new TransferSelectCardFragment(id,cardInfoModel,amountInput.getText().toString(),getCardBalanceWithCards),true);
                 }
                });
            }


}
