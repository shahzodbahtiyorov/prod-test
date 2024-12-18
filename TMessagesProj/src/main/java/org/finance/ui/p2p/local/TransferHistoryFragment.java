package org.finance.ui.p2p.local;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.JsonObject;
import org.finance.data.model.get_card.TransferCardModel;
import org.finance.data.service.ApiCallback;
import org.finance.data.service.P2PService;
import org.finance.ui.home.HomeFragment;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

/** @noinspection ALL */
public class TransferHistoryFragment extends BaseFragment {
    private final int summ;
    private final TransferCardModel transferCardModel;
    private P2PService p2PService;
    private Dialog progressDialog;

    public TransferHistoryFragment(int summ, TransferCardModel transferCardModel) {
        this.transferCardModel=transferCardModel;
        this.summ=summ;
    }
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public View createView(Context context) {
        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(context).inflate(R.layout.transaction_history_layout, null);
        actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
        actionBar.setTitle("Чек перевода");
        actionBar.setBackgroundColor(Color.parseColor("#50A7EA"));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                   navigation();
            }
        });
        actionBar.setAllowOverlayTitle(true);
       p2PService=new P2PService(context);
        TextView historyTitle =  rootView.findViewById(R.id.tvSuccessMessage);;
        ImageView imageView =  rootView.findViewById(R.id.imgSuccessIcon);

        TextView accountNumber =  rootView.findViewById(R.id.tvPhoneNumber);
        accountNumber.setText(transferCardModel!=null?transferCardModel.getCardNumber():"+998 (94) 999-99-99");

        TextView amountTitle =  rootView.findViewById(R.id.tvAmount);
        amountTitle.setText(formatMaskedCardNumber(formatWithSpaces(summ/100)+" UZS"));


        LinearLayout savedTransation =  rootView.findViewById(R.id.save_payments);
        LinearLayout detail = rootView.findViewById(R.id.payment_details);
        LinearLayout repetition = rootView.findViewById(R.id.repeat_payment);

        savedTransation.setOnClickListener(v -> {
            JsonObject jsonObject =new JsonObject();
            jsonObject.addProperty("card_number",transferCardModel.getCardNumber());
            jsonObject.addProperty("type",transferCardModel.getPcType());
            showProgressDialog();
            p2PService.saveCard(new ApiCallback<JsonObject>() {
                @Override
                public void onSuccess(JsonObject response) {
                    dismissProgressDialog();
                }
                @Override
                public void onFailure(String errorMessage) {
                    dismissProgressDialog();
                }
            },jsonObject);
        });
        Button nextButton =rootView.findViewById(R.id.btnClose);
        nextButton.setOnClickListener(v -> {
           navigation();
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
    public static String formatWithSpaces(int number) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        String formattedNumber = numberFormat.format(number);
        return formattedNumber.replace(",", " ");
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
    @Override
    public boolean onBackPressed() {
        navigation();
        return false;
    }
    public void navigation(){
        presentFragment(new HomeFragment("p2pView"),true);
    }
}

