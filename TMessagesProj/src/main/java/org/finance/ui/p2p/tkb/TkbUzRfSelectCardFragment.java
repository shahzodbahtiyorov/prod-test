package org.finance.ui.p2p.tkb;



import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import org.finance.data.model.get_card.GetCardBalanceModel;
import org.finance.data.model.get_card.TransferCardModel;
import org.finance.data.service.ApiCallback;
import org.finance.data.service.TkbService;
import org.finance.ui.adapters.TransferCardAdapter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;



/** @noinspection ALL*/
public class TkbUzRfSelectCardFragment extends BaseFragment {
    private final String cardNumber;
    private final String summ;
    private GetCardBalanceModel.Cards cards;
    private View rootCreateView;
    private Dialog progressDialog;
    private LinearLayout linearLayout;
    private TkbService tkbService;
    private final GetCardBalanceModel cardList;
    public TkbUzRfSelectCardFragment(String cardInfoModel, String summ,GetCardBalanceModel getCardBalanceModel) {
        this.cardNumber = cardInfoModel;
        this.summ=summ;
        cardList=getCardBalanceModel;
        cards= getCardBalanceModel.getCards().get(0);
    }

    @Override
    public View createView(Context context) {
        if (rootCreateView==null){
            rootCreateView=viewCreate(context);
        }
        return rootCreateView;
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "MissingInflatedId", "SetTextI18n"})
    public View viewCreate(Context context) {
        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(context).inflate(R.layout.transfer_humo_uzcard_select_card_layout, null);
        @SuppressLint({"NewApi", "LocalSuppress"}) DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
        @SuppressLint({"NewApi", "LocalSuppress"}) LocalDateTime now = LocalDateTime.now();
        @SuppressLint({"NewApi", "LocalSuppress"}) String formattedDateTime = now.format(formatter);

        TextView cardNumberText = rootView.findViewById(R.id.senderCardNumber);
        cardNumberText.setText(formatMaskedCardNumber(cardNumber));
        tkbService = new TkbService(context);
        TextView transactionDate=rootView.findViewById(R.id.transactionDate);
        transactionDate.setText(formattedDateTime);

        TextView transferAmount=rootView.findViewById(R.id.transferAmount);
        transferAmount.setText(summ+" RUB");

        TextView receiverCardNumber=rootView.findViewById(R.id.receiverCardNumber);
        receiverCardNumber.setText(formatMaskedCardNumber(cards.getCardNumber()));
        TextView comission =rootView.findViewById(R.id.transferFee);
        comission.setText("0.0 RUB");
        TextView totalAmount=rootView.findViewById(R.id.totalAmount);
        totalAmount.setText(summ+" RUB");

        TextView cardDetails=rootView.findViewById(R.id.cardDetails);
        cardDetails.setText(formatCardInfo(cards.getCardNumber(),cards.getCardOwnerName()));
        TextView accountBalance=rootView.findViewById(R.id.accountBalance);

        ImageView cardLogo=rootView.findViewById(R.id.cardLogo);
        if (cards.getPcType() == 3) {
            cardLogo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.humo_card));
        } else if (cards.getPcType() == 1) {
            cardLogo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_uzcard));
        } else if (cards.getPcType() == 2) {
            cardLogo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_tkb_card));
        }

        accountBalance.setText(formatWithSpaces(cards.getBalance()));
        Button nextButton=rootView.findViewById(R.id.nextButton);

        linearLayout =rootView.findViewById(R.id.select_card);

        linearLayout.setOnClickListener(v -> {
            showBottomSheet(context);
        });
        nextButton.setOnClickListener(v -> {
            showProgressDialog();
            nextButton.setText("");
            nextButton.setEnabled(false);
            int number = Integer.parseInt(summ.replaceAll("\\s+", ""));
            performTransaction(context,cardNumber,number);

        });
        actionBar.setBackgroundColor(Color.parseColor("#50A7EA"));
        actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
        actionBar.setTitle("Перевод из Узбекистан в России");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                    clearViews();
                }
            }
        });
        return rootView;
    }
    public static String formatMaskedCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) {
            return cardNumber;
        }

        String part1 = cardNumber.substring(0, 4);
        String part2 = cardNumber.substring(4, 6) + "••";
        String part3 = "••••";
        String part4 = cardNumber.substring(12);

        return part1 + " " + part2 + " " + part3 + " " + part4;
    }


    public static String formatCardInfo(String cardNumber, String ownerName) {
        if (cardNumber == null || cardNumber.length() != 16 || ownerName == null || ownerName.isEmpty()) {
            return "";
        }

        String lastFourDigits = cardNumber.substring(12);
        return "•• " + lastFourDigits + " | " + ownerName;
    }
    public static String formatWithSpaces(double number) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        String formattedNumber = numberFormat.format(number);
        return formattedNumber.replace(",", " ") + " UZS"; // Replace commas with spaces
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
    private void showBottomSheet(Context context) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context,R.style.RoundedBottomSheetDialog);
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.my_cards_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        RecyclerView cardRecyclerView = bottomSheetView.findViewById(R.id.cardRecyclerView);
        TextView headerTitle = bottomSheetView.findViewById(R.id.headerTitle);
        ImageView closeButton = bottomSheetView.findViewById(R.id.closeButton);

        cardRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        cardRecyclerView.setAdapter(new TransferCardAdapter(context, cardList.getCards(), selectedCard -> {
            cards = selectedCard;
            updateSelectedCardUI(selectedCard);
            bottomSheetDialog.dismiss();
        },cards.getId()));

        cardRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = 12;
                outRect.left = 12;
                outRect.right = 12;
                outRect.top = 12;
            }
        });

        closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());


        bottomSheetDialog.show();
    }

    private void updateSelectedCardUI(GetCardBalanceModel.Cards cards) {
        TextView cardDetails = rootCreateView.findViewById(R.id.cardDetails);
        TextView accountBalance = rootCreateView.findViewById(R.id.accountBalance);
        ImageView cardLogo = rootCreateView.findViewById(R.id.cardLogo);
        TextView receiverCardNumber = rootCreateView.findViewById(R.id.receiverCardNumber);

        cardDetails.setText(formatCardInfo(cards.getCardNumber(), cards.getCardOwnerName()));
        accountBalance.setText(formatWithSpaces(cards.getBalance()));

        if (cards.getPcType() == 3) {
            cardLogo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.humo_card));
        } else if (cards.getPcType() == 1) {
            cardLogo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_uzcard));
        } else if (cards.getPcType() == 2) {
            cardLogo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_tkb_card));
        }
        receiverCardNumber.setText(formatMaskedCardNumber(cards.getCardNumber()));
    }
    private void performTransaction(Context context, String cardNumber, int amount) {
        showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("receiver", cardNumber);
        jsonObject.addProperty("token",cards.getCardUuid());
        jsonObject.addProperty("amount", amount);
        jsonObject.addProperty("currency", 643);

        tkbService.tkbUzRfCreate(new ApiCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject response) {
              dismissProgressDialog();
                TransferCardModel transferCardModel=new TransferCardModel();
                transferCardModel.setCardNumber(cardNumber);
                transferCardModel.setPcType(2);
              presentFragment(new TkbUzRfTranferPinCodeFragment(response,amount,transferCardModel));
            }

            @Override
            public void onFailure(String errorMessage) {
                dismissProgressDialog();
                Toast.makeText(context, "Transaction Failed: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        }, jsonObject);
    }

   private void transactionConfirm(Context context, JsonObject jsonObject) {
        showProgressDialog();
        tkbService.tkbUzRfConfirm(new ApiCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject response) {

             dismissProgressDialog();

            }

            @Override
            public void onFailure(String errorMessage) {
                dismissProgressDialog();
                Toast.makeText(context, "Transaction Failed: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        }, jsonObject);
    }

}

