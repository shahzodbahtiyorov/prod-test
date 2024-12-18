package org.finance.ui.p2p.local;


import static org.finance.helpers.SuperAppFormatters.formatCardInfo;
import static org.finance.helpers.SuperAppFormatters.formatWithSpaces;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.finance.data.model.get_card.GetCardBalanceWithCards;
import org.finance.data.model.get_card.TransferCardModel;
import org.finance.data.service.ApiCallback;
import org.finance.data.service.P2PService;
import org.finance.helpers.SuperAppFormatters;
import org.finance.ui.adapters.TransferCardAdapter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;



/** @noinspection ALL*/
public class TransferSelectCardFragment extends BaseFragment {
    private final TransferCardModel cardInfoModel;
    private final String summ;
    private GetCardBalanceModel.Cards cards;
    private View rootCreateView;
    private Dialog progressDialog;
    private LinearLayout selectCard;
    private final GetCardBalanceWithCards cardList;
    private int cardId;

    public TransferSelectCardFragment(int id, TransferCardModel cardInfoModel, String summ, GetCardBalanceWithCards getCardBalanceModel) {
        this.cardInfoModel = cardInfoModel;
        this.summ = summ;
        this.cardList = getCardBalanceModel;
        this.cardId=id;
        for (GetCardBalanceModel.Cards card : getCardBalanceModel.cards) {
            if (id==card.getId()) {

                this.cards = card;

                break;
            }
        }

        if (this.cards == null && !getCardBalanceModel.cards.isEmpty()) {
            this.cards = getCardBalanceModel.cards.get(0);
        }
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
        cardNumberText.setText(SuperAppFormatters.formatMaskedCardNumber(cardInfoModel.getCardNumber()));

        TextView transactionDate=rootView.findViewById(R.id.transactionDate);
        transactionDate.setText(formattedDateTime);

        TextView transferAmount=rootView.findViewById(R.id.transferAmount);
        transferAmount.setText(summ+" UZS");

        TextView receiverCardNumber=rootView.findViewById(R.id.receiverCardNumber);
        receiverCardNumber.setText(SuperAppFormatters.formatMaskedCardNumber(cards.getCardNumber()));

        TextView totalAmount=rootView.findViewById(R.id.totalAmount);
        totalAmount.setText(summ+" UZS");

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
        selectCard=rootView.findViewById(R.id.select_card);
        selectCard.setOnClickListener(v -> {
            showBottomSheet(context);
        });
        nextButton.setOnClickListener(v -> {
           if (!cardInfoModel.getCardNumber().equals(cards.getCardNumber())){
               showProgressDialog();
               nextButton.setText("");
               nextButton.setEnabled(false);
               int number = Integer.parseInt(summ.replaceAll("\\s+", ""))*100;
               P2PService p2PService = new P2PService(context);
               JsonObject requestObject = new JsonObject();
               requestObject.addProperty("amount", number);
               requestObject.addProperty("receiver", cardInfoModel.getCardNumber());
               requestObject.addProperty("ext_id", cards.getCardUuid());

               p2PService.cardTransfer(new ApiCallback<JsonObject>() {
                   @Override
                   public void onSuccess(JsonObject response) {
                       dismissProgressDialog();
                       nextButton.setText("Следующий");
                       nextButton.setEnabled(true);
                       presentFragment(new TranferPinCodeFragment(response,number,cardInfoModel,requestObject),true);
                   }

                   @Override
                   public void onFailure(String errorMessage) {
                       dismissProgressDialog();
                       nextButton.setText("Следующий");
                       nextButton.setEnabled(true);
                       Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                   }
               }, requestObject);
           }else {
               Toast.makeText(context, "Qabul qilivchi va yuboruvchi karta raqamlari bir xil", Toast.LENGTH_SHORT).show();
           }
        });
        actionBar.setBackgroundColor(Color.parseColor("#50A7EA"));
        actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
        actionBar.setTitle("Перевод на карты Uzcard/Humo");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                presentFragment(new TransferAmountFragment(cardId,cardInfoModel),true);
            }
        });
        return rootView;
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
        cardRecyclerView.setAdapter(new TransferCardAdapter(context, cardList.cards, selectedCard -> {
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

    private void updateSelectedCardUI(GetCardBalanceModel.Cards selectedCard) {
        TextView cardDetails = rootCreateView.findViewById(R.id.cardDetails);
        TextView accountBalance = rootCreateView.findViewById(R.id.accountBalance);
        ImageView cardLogo = rootCreateView.findViewById(R.id.cardLogo);
        TextView receiverCardNumber = rootCreateView.findViewById(R.id.receiverCardNumber);

        cardDetails.setText(formatCardInfo(selectedCard.getCardNumber(), selectedCard.getCardOwnerName()));
        accountBalance.setText(formatWithSpaces(selectedCard.getBalance()));

        if (selectedCard.getPcType() == 3) {
            cardLogo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.humo_card));
        } else if (selectedCard.getPcType() == 1) {
            cardLogo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_uzcard));
        } else if (selectedCard.getPcType() == 2) {
            cardLogo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_tkb_card));
        }
        receiverCardNumber.setText(SuperAppFormatters.formatMaskedCardNumber(selectedCard.getCardNumber()));
    }
    @Override
    public boolean onBackPressed() {
        presentFragment(new TransferAmountFragment(cardId,cardInfoModel),true);
        return  true;
    }
}
