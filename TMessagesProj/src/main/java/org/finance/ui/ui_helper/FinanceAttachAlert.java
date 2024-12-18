package org.finance.ui.ui_helper;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import org.finance.data.model.get_card.ChatUserCardModel;
import org.finance.data.model.get_card.GetCardBalanceModel;
import org.finance.data.room.DataBaseClient;
import org.finance.data.room.SuperAppDatabase;
import org.finance.data.service.ApiCallback;
import org.finance.data.service.GetCardService;
import org.finance.data.service.P2PService;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.LayoutHelper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

/** @noinspection ALL*/
@SuppressLint("ViewConstructor")
public class FinanceAttachAlert extends ChatAttachAlert.AttachAlertLayout {

    private enum FinanceState {
        loading, success, empty, error
    }

    private enum TransferState {
        error, success, loading
    }


//    public interface FinanceAttachDelegate {
//        void onSuccess(String msg);
//    }

    private FinanceState state = FinanceState.loading;
    private TransferState transferState;

    private JsonObject tranferBody;

    private final View rootView;
    private final ProgressBar progressBar;
    private final ProgressDialog progressDialog;
    private final TextView errorMessageView;
    private final TextView emptyMessageView;
    private final String currentPhone;

    private final TextView fromUserCardBalance;
    private final TextView fromUserCardDetails;
    private final ImageView fromUserCardImg;

    private final TextView toUserCardNumber;
    private final TextView toCardOwnerName;
    private final ImageView toUserCardImg;

    private ChatUserCardModel selectedUserCard;
    private GetCardBalanceModel.Cards selectedMycard;
    private ArrayList<ChatUserCardModel> userCards;
    private List<GetCardBalanceModel.Cards> savedCards;

    private final EditText tranferAmountEditText;
    private final P2PService p2pService;

    @SuppressLint({"SetTextI18n", "InflateParams"})
    public FinanceAttachAlert(ChatAttachAlert alert, Context context, Theme.ResourcesProvider resourcesProvider, String currentPhone) {
        super(alert, context, resourcesProvider);

        p2pService = new P2PService(getContext());

        // Set up ProgressBar for the loading state
        progressBar = new ProgressBar(context);
        this.currentPhone = currentPhone;
        progressBar.setVisibility(View.GONE);
        addView(progressBar, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

        // Set up TextView for the error message
        errorMessageView = new TextView(context);
        errorMessageView.setTextColor(Color.RED);
        errorMessageView.setGravity(Gravity.CENTER);
        errorMessageView.setVisibility(View.GONE);
        addView(errorMessageView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

        // Set up TextView for the empty state
        emptyMessageView = new TextView(context);
        emptyMessageView.setText(LocaleController.getString(R.string.Chat_user_cards_empty));
        emptyMessageView.setGravity(Gravity.CENTER);
        emptyMessageView.setVisibility(View.GONE);
        addView(emptyMessageView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));


        //
        rootView = LayoutInflater.from(context).inflate(R.layout.finance_attach_alert_layout, null);
        rootView.setPadding(AndroidUtilities.dp(12), AndroidUtilities.dp(64), AndroidUtilities.dp(12), AndroidUtilities.dp(8));

        LinearLayout fromUserCardsLayout = rootView.findViewById(R.id.fromChatUserCards);
        fromUserCardBalance = rootView.findViewById(R.id.accountBalance);
        fromUserCardDetails = rootView.findViewById(R.id.cardDetails);
        fromUserCardImg = rootView.findViewById(R.id.fromCardLogo);

        LinearLayout toUserCardsLayout = rootView.findViewById(R.id.toChatUserCards);
        toUserCardNumber = rootView.findViewById(R.id.toCardNumber);
        toCardOwnerName = rootView.findViewById(R.id.toCardOwnerName);
        toUserCardImg = rootView.findViewById(R.id.toCardLogo);

        fromUserCardsLayout.setOnClickListener(v -> openMyCardsSheet());

        toUserCardsLayout.setOnClickListener(v -> openUserCardsSheet());


        tranferAmountEditText = rootView.findViewById(R.id.transferAmount);

        Objects.requireNonNull(parentAlert.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setFocusable(true);
        parentAlert.setFocusable(true);

        Button nextButton = rootView.findViewById(R.id.nextButton);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Загрузка...");
        progressDialog.setCancelable(false);


        nextButton.setOnClickListener(v -> {
            if (tranferAmountEditText.getText().length() > 0) {
                final int temp = Integer.parseInt(tranferAmountEditText.getText().toString());
                if (temp < selectedMycard.getBalance()) {
                    if (transferState == null) {
                        transferState = TransferState.loading;
                        showProgressDialog();
                    }
                    transferOne();
                } else {
                    Toast.makeText(getContext(), "Balans yetarli emas", Toast.LENGTH_LONG).show();
                }
            }
        });
        tranferAmountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    nextButton.setBackgroundResource(R.drawable.rounded_button);
                } else {
                    nextButton.setBackgroundResource(R.drawable.rounded_empty_button);
                }
            }
        });


        addView(rootView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        // Load data
        loadUserCards();
    }

    private void transferOne() {

        if (transferState == TransferState.success){
            openOtpBottomSheet(tranferBody);
            return;
        }
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("amount", Integer.parseInt(tranferAmountEditText.getText().toString()) * 100);
        jsonObject.addProperty("ext_id", selectedMycard.getCardUuid());
        jsonObject.addProperty("receiver", selectedUserCard.getCardNumber());


        p2pService.cardTransfer(new ApiCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject response) {
                transferState = TransferState.success;
                tranferBody = response;
                dismissProgressDialog();
                openOtpBottomSheet(tranferBody);
            }

            @Override
            public void onFailure(String errorMessage) {
                transferState = TransferState.error;
                dismissProgressDialog();
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        }, jsonObject);
    }

    private void showProgressDialog() {
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void openOtpBottomSheet(JsonObject jsonObject) {

        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.finance_attach_pinput_layout, null);

        view.setBackgroundResource(R.drawable.bottom_sheet_radius);
        dialog.setContentView(view);

        EditText editText = view.findViewById(R.id.otpInput);
        Button nextButton = view.findViewById(R.id.nextButton);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    nextButton.setBackgroundResource(R.drawable.rounded_button);
                } else {
                    nextButton.setBackgroundResource(R.drawable.rounded_empty_button);
                }
            }
        });

        nextButton.setOnClickListener(v -> {
            if (editText.getText().length() == jsonObject.get("count").getAsInt()) {
                showProgressDialog();
                JsonObject requestObject = new JsonObject();
                requestObject.addProperty("code", editText.getText().toString());
                requestObject.addProperty("sender_ext_id", jsonObject.get("sender_ext_id").getAsString());
                requestObject.addProperty("receiver_ext_id", jsonObject.get("receiver_ext_id").getAsString());
                p2pService.cardTransferTwo(new ApiCallback<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject response) {
                        Toast.makeText(getContext(), "O'tkazma muvofaqqiyatli amalga oshirildi", Toast.LENGTH_LONG).show();
                        dismissProgressDialog();
                        dialog.dismiss();
                        parentAlert.dismiss();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        transferState = TransferState.error;
                        dismissProgressDialog();
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                }, requestObject);
            }
        });

        Objects.requireNonNull(dialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);



        dialog.show();

    }

    private void openUserCardsSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext(), R.style.RoundedBottomSheetDialog);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") View cardFilterView = inflater.inflate(R.layout.finance_attach_cards_layout, null);

        cardFilterView.setBackgroundResource(R.drawable.bottom_sheet_radius);

        dialog.setContentView(cardFilterView);
        RecyclerView cardRecyclerView = cardFilterView.findViewById(R.id.cardRecyclerView);
        ImageView closeButton = cardFilterView.findViewById(R.id.closeButton);
        cardRecyclerView.setVisibility(View.VISIBLE);

        closeButton.setOnClickListener(v -> dialog.dismiss());

        cardRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = 12;
                outRect.left = 12;
                outRect.right = 12;
                outRect.top = 12;
            }
        });
        cardRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cardRecyclerView.setAdapter(new UserCardsAdapter(i -> {
            selectedUserCard = userCards.get(i);
            updateSelectedUserCard();
            dialog.dismiss();
        }));

        dialog.show();
    }

    private void updateSelectedUserCard() {
        toCardOwnerName.setText(selectedUserCard.getCardOwnerName());
        toUserCardNumber.setText(selectedUserCard.getCardNumber());
        switch (selectedUserCard.getPcType()) {
            case 1:
                toUserCardImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_uzcard));
                break;
            case 3:
                toUserCardImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.humo_card));
                break;
        }
    }

    private void openMyCardsSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext(), R.style.RoundedBottomSheetDialog);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") View cardFilterView = inflater.inflate(R.layout.finance_attach_cards_layout, null);

        cardFilterView.setBackgroundResource(R.drawable.bottom_sheet_radius);

        dialog.setContentView(cardFilterView);
        RecyclerView cardRecyclerView = cardFilterView.findViewById(R.id.cardRecyclerView);
        ImageView closeButton = cardFilterView.findViewById(R.id.closeButton);
        cardRecyclerView.setVisibility(View.VISIBLE);

        closeButton.setOnClickListener(v -> dialog.dismiss());

        cardRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = 12;
                outRect.left = 12;
                outRect.right = 12;
                outRect.top = 12;
            }
        });
        cardRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cardRecyclerView.setAdapter(new MyCardsAdapter(i -> {
            selectedMycard = savedCards.get(i);
            updateSelectedMyCard();
            dialog.dismiss();
        }));

        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void updateSelectedMyCard() {
        fromUserCardBalance.setText(Double.toString(selectedMycard.getBalance()));
        fromUserCardDetails.setText(selectedMycard.getCardOwnerName());
        switch (selectedMycard.getPcType()) {
            case 1:
                fromUserCardImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_uzcard));
                break;
            case 3:
                fromUserCardImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.humo_card));
                break;
        }
    }

    @Override
    public void onHide() {
        super.onHide();
    }

    @Override
    public void onShow(ChatAttachAlert.AttachAlertLayout previousLayout) {
        parentAlert.actionBar.setTitle(LocaleController.getString(R.string.Finance));
    }

    @Override
    public int needsActionBar() {
        return 1;
    }

    public void setDelegate() {
    }


    private void loadUserCards() {
        state = FinanceState.loading;
        updateView();
        userCards = new ArrayList<>();

        // get my cards
        new Thread(() -> {
            TLRPC.User user = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
            SuperAppDatabase database = DataBaseClient.getInstance(getContext(), user.phone).getAppDatabase();
            savedCards = new ArrayList<>();
            if (!database.cardsDao().getCardsList().isEmpty()) {
                for (GetCardBalanceModel.Cards card : database.cardsDao().getCardsList()) {
                    if (card.getPcType() != 2 && card.isActive()) {
                        savedCards.add(card);
                    }
                }
            }
            selectedMycard = savedCards.get(0);
            updateSelectedMyCard();
        }).start();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("phone", currentPhone);

        GetCardService cardService = new GetCardService(getContext());
        cardService.getChatUserCards(new ApiCallback<ArrayList<ChatUserCardModel>>() {
            @Override
            public void onSuccess(ArrayList<ChatUserCardModel> response) {
                if (response.isEmpty()) {
                    state = FinanceState.empty;
                } else {
                    userCards = response;
                    userCards.removeIf(userCard -> userCard.getPcType() == 2);
                    selectedUserCard = response.get(0);
                    updateSelectedUserCard();
                    state = FinanceState.success;
                }
                updateView();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(String errorMessage) {
                state = FinanceState.error;
                errorMessageView.setText("Error: " + errorMessage);
                updateView();
            }
        }, jsonObject);
    }

    private void updateView() {
        // Hide all views initially
        progressBar.setVisibility(View.GONE);
        rootView.setVisibility(View.GONE);
        errorMessageView.setVisibility(View.GONE);
        emptyMessageView.setVisibility(View.GONE);

        switch (state) {
            case loading:
                progressBar.setVisibility(View.VISIBLE);
                break;

            case success:
                rootView.setVisibility(View.VISIBLE);
                break;

            case empty:
                emptyMessageView.setVisibility(View.VISIBLE);
                break;

            case error:
                errorMessageView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private class MyCardsAdapter extends RecyclerView.Adapter<MyCardsAdapter.CardViewHolder> {

        private int selectedPosition = -1;

        private final Consumer<Integer> selectionCallback;

        private MyCardsAdapter(Consumer<Integer> selectionCallback) {
            this.selectionCallback = selectionCallback;
        }

        @NonNull
        @Override
        public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.monitoring_card_item, parent, false);
            view.findViewById(R.id.checkIcon).setVisibility(GONE);
            return new CardViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CardViewHolder holder, @SuppressLint("RecyclerView") int position) {
            GetCardBalanceModel.Cards card = savedCards.get(position);
            holder.cardAmount.setText(String.format("%s", card.getBalance()));
            holder.cardDetails.setText(formatCardInfo(card.getCardNumber(), card.getCardOwnerName()));

            if (card.getPcType() == 3) {
                holder.cardAmount.setText(formatWithSpaces(card.getBalance()));
                holder.cardIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.humo_card));
            } else if (card.getPcType() == 1) {
                holder.cardAmount.setText(formatWithSpaces(card.getBalance()));
                holder.cardIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_uzcard));
            }

            holder.itemView.setOnClickListener(v -> {
                int previousPosition = selectedPosition;
                selectedPosition = position;


                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);
                selectionCallback.accept(position);
            });
        }

        @Override
        public int getItemCount() {
            return savedCards.size();
        }

        private class CardViewHolder extends RecyclerView.ViewHolder {
            ImageView cardIcon;
            TextView cardAmount, cardDetails;

            public CardViewHolder(@NonNull View itemView) {
                super(itemView);
                cardIcon = itemView.findViewById(R.id.cardIcon);

                cardAmount = itemView.findViewById(R.id.cardAmount);
                cardDetails = itemView.findViewById(R.id.cardDetails);
            }
        }
    }


    private class UserCardsAdapter extends RecyclerView.Adapter<UserCardsAdapter.CardViewHolder> {

        private int selectedPosition = -1;

        private final Consumer<Integer> selectionCallback;

        private UserCardsAdapter(Consumer<Integer> selectionCallback) {
            this.selectionCallback = selectionCallback;
        }

        @NonNull
        @Override
        public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.monitoring_card_item, parent, false);
            view.findViewById(R.id.checkIcon).setVisibility(GONE);
            return new CardViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CardViewHolder holder, @SuppressLint("RecyclerView") int position) {
            ChatUserCardModel card = userCards.get(position);
            holder.cardNumber.setText(String.format("%s", card.getCardNumber()));
            holder.cardOwnerName.setText(card.getCardOwnerName());

            if (card.getPcType() == 3) {
                holder.cardIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.humo_card));
            } else if (card.getPcType() == 1) {
                holder.cardIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_uzcard));
            }

            holder.itemView.setOnClickListener(v -> {
                int previousPosition = selectedPosition;
                selectedPosition = position;


                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);
                selectionCallback.accept(position);
            });
        }

        @Override
        public int getItemCount() {
            return userCards.size();
        }

        private class CardViewHolder extends RecyclerView.ViewHolder {
            ImageView cardIcon;
            TextView cardNumber, cardOwnerName;

            public CardViewHolder(@NonNull View itemView) {
                super(itemView);
                cardIcon = itemView.findViewById(R.id.cardIcon);
                cardNumber = itemView.findViewById(R.id.cardAmount);
                cardOwnerName = itemView.findViewById(R.id.cardDetails);
            }
        }
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
}

