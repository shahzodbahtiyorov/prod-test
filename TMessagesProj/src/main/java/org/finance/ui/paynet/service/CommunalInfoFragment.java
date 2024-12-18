package org.finance.ui.paynet.service;

import static org.finance.helpers.SuperAppFormatters.formatCardInfo;
import static org.finance.helpers.SuperAppFormatters.formatWithSpaces;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.util.Log;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import org.finance.data.model.get_card.GetCardBalanceModel;
import org.finance.data.model.get_card.GetCardBalanceWithCards;
import org.finance.data.model.paynet.chekcomunal.PaymentCommunalGasModel;
import org.finance.data.model.paynet.chekcomunal.PaymentComunalChekModel;
import org.finance.data.model.paynet.service.Fields;
import org.finance.data.model.paynet.service.PaymentServiceModel;
import org.finance.data.model.paynet.service.Services;
import org.finance.data.service.ApiCallback;
import org.finance.data.service.PaynetService;
import org.finance.ui.adapters.ComunalInfoAdapter;
import org.finance.ui.adapters.TransferCardAdapter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import java.util.ArrayList;


/** @noinspection ALL*/
public class CommunalInfoFragment extends BaseFragment {
    JsonObject jsonObject;
    private final GetCardBalanceWithCards cardList;
    private GetCardBalanceModel.Cards cards;
    boolean isNextButton = false;
    private final PaymentComunalChekModel paymentComunalChekModel;
    private final PaymentCommunalGasModel communalGasModel;
    private final PaymentServiceModel paymentServiceModel;
    private BottomSheetDialog bottomSheetDialog;
    private TextView cardDetailsText;
    JsonObject fields;
    private View rootCreateView;

    public CommunalInfoFragment(PaymentComunalChekModel paymentComunalChekModel, PaymentCommunalGasModel communalGasModel, PaymentServiceModel paymentServiceModel, GetCardBalanceWithCards getCardBalanceModel) {
        this.paymentComunalChekModel = paymentComunalChekModel;
        this.communalGasModel = communalGasModel;
        this.paymentServiceModel = paymentServiceModel;
        cardList=getCardBalanceModel;
        cards= getCardBalanceModel.cards.get(0);
    }

    @Override
    public View createView(@NonNull Context context) {
        if (rootCreateView == null) {
            rootCreateView = viewCreate(context);
        }
        return rootCreateView;
    }
    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    public View viewCreate(@NonNull Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.payment_amount_layout, null);

        actionBar.setBackgroundColor(Color.parseColor("#50A7EA"));
        actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
        actionBar.setTitle("Оплата");
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
        jsonObject = new JsonObject();
        EditText amountInput = rootView.findViewById(R.id.payment_amount);
        amountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String summ = editable.toString();
                if (!summ.isEmpty() && Integer.parseInt(summ) >= 500) {
                    isNextButton = true;
                    createFieldsJson(Integer.parseInt(summ));
                }
            }
        });

        TextView serviceType = rootView.findViewById(R.id.payment_info);
        LinearLayout cardBalanceLayout =rootView.findViewById(R.id.accountBalanceContainer);
        TextView accountBalance=rootView.findViewById(R.id.accountBalance);

        ImageView cardLogo=rootView.findViewById(R.id.cardLogo);
        if (cards.getPcType() == 3) {
            cardLogo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.humo_card));
        } else if (cards.getPcType() == 1) {
            cardLogo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_uzcard));
        } else if (cards.getPcType() == 2) {
            cardLogo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_tkb_card));
        }
        TextView cardDetails=rootView.findViewById(R.id.cardDetails);
        cardDetails.setText(formatCardInfo(cards.getCardNumber(),cards.getCardOwnerName()));
        accountBalance.setText(formatWithSpaces(cards.getBalance()));
        Button nextButton = rootView.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            nextButton.setEnabled(false);
            nextButton.setText("");
            PaynetService paynetService = new PaynetService(context);
            if (isNextButton) {
                paynetService.paymentCreate(new ApiCallback<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject response) {
                        nextButton.setEnabled(true);
                        nextButton.setText("Дальше");
                        presentFragment(new PaynetPinCodeFreagment(response, Integer.parseInt(amountInput.getText().toString())));
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        nextButton.setEnabled(true);
                        nextButton.setText("Дальше");
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }, jsonObject);
            }
        });

        cardBalanceLayout.setOnClickListener(view -> showBottomSheet(context));

        serviceType.setOnClickListener(view -> {
            try {
                showBottomSheetInfo(context);
            } catch (Exception e) {
                Log.d("ComunalError", e.getMessage());
            }
        });
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
    private void updateSelectedCardUI(GetCardBalanceModel.Cards selectedCard) {
        TextView cardDetails = rootCreateView.findViewById(R.id.cardDetails);
        TextView accountBalance = rootCreateView.findViewById(R.id.accountBalance);
        ImageView cardLogo = rootCreateView.findViewById(R.id.cardLogo);
        cardDetails.setText(formatCardInfo(selectedCard.getCardNumber(), selectedCard.getCardOwnerName()));
        accountBalance.setText(formatWithSpaces(selectedCard.getBalance()));
        jsonObject.addProperty("card_number",selectedCard.getCardUuid());
        if (selectedCard.getPcType() == 3) {
            cardLogo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.humo_card));
        } else if (selectedCard.getPcType() == 1) {
            cardLogo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_uzcard));
        } else if (selectedCard.getPcType() == 2) {
            cardLogo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_tkb_card));
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

    private void showBottomSheetInfo(Context context) {
        BottomSheetDialog bottomSheetDialogComunalInfo = new BottomSheetDialog(context);
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
                1f
        ));
        ComunalInfoAdapter cardAdapter = new ComunalInfoAdapter(new ArrayList<>(), context);
        recyclerView.setAdapter(cardAdapter);
        if (paymentComunalChekModel != null && paymentComunalChekModel.getResult() != null &&
                paymentComunalChekModel.getResult().getCheque() != null) {
            cardAdapter.updateCardList(paymentComunalChekModel.getResult().getCheque().getResponse());
        } else if (communalGasModel != null && communalGasModel.getResult() != null &&
                communalGasModel.getResult().getCheque() != null) {
            cardAdapter.updateCardList(communalGasModel.getResult().getCheque().getResponse());
        } else {
            cardAdapter.updateCardList(new ArrayList<>());
        }
        bottomSheetDialogComunalInfo.setContentView(layout);
        bottomSheetDialogComunalInfo.show();
    }

    private void createFieldsJson(int amount) {
        fields = new JsonObject();
        for (Services services : paymentServiceModel.getServices()) {
            if (services.getTitleRu().contains("Оплата")) {
                for (Fields inputField : services.getFields()) {
                    if ("COMBOBOX".equals(inputField.getFieldType()) && inputField.getRequired()) {
                        if (paymentComunalChekModel != null) {
                            fields.addProperty(inputField.getName(), paymentComunalChekModel.getResult().getReceiver().getFields().getSoato());
                        } else {
                            fields.addProperty(inputField.getName(), communalGasModel.getResult().getReceiver().getFields().getCode());
                        }
                    } else if ("STRING".equals(inputField.getFieldType()) && inputField.getRequired()) {
                        if (paymentComunalChekModel != null) {
                            fields.addProperty(inputField.getName(), paymentComunalChekModel.getResult().getReceiver().getFields().getCustomerCode());
                        } else {
                            fields.addProperty(inputField.getName(), communalGasModel.getResult().getReceiver().getFields().getLicshet());
                        }
                    } else if ("MONEY".equals(inputField.getFieldType()) && inputField.getRequired()) {
                        fields.addProperty(inputField.getName(), amount);
                        jsonObject.addProperty("service_id", services.getId());
                    }
                }
            }
            jsonObject.add("fields", fields);
            Log.d("Amount", jsonObject.toString());
        }
    }



}
