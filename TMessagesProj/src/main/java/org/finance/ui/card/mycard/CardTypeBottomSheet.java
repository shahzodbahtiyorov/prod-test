package org.finance.ui.card.mycard;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import org.finance.data.model.tkb.card_create.TkbCardCreadeModel;
import org.finance.data.service.ApiCallback;
import org.finance.data.service.TkbService;
import org.finance.ui.addCard.local.AddCardFragment;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.BaseFragment;

/** @noinspection ALL*/
public class CardTypeBottomSheet {
    private ProgressDialog progressDialog;
    private final Context context;
    private final BaseFragment baseFragment;
    private BottomSheetDialog bottomSheetDialog;

    public CardTypeBottomSheet(Context context, BaseFragment baseFragment) {
        this.context = context;

        this.baseFragment=baseFragment;

    }



    public void show() {
        TkbService tkbService=new TkbService(context);
        bottomSheetDialog = new BottomSheetDialog(context, R.style.RoundedBottomSheetDialog);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.RECTANGLE);
        background.setColor(Color.WHITE);
        background.setCornerRadii(new float[]{50, 50, 50, 50, 0, 0, 0, 0});
        layout.setBackground(background);



        RelativeLayout row = new RelativeLayout(context);
        row.setPadding(0,AndroidUtilities.dp(16),0,AndroidUtilities.dp(4));
        row.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        RelativeLayout.LayoutParams titleViewParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        titleViewParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        TextView title = new TextView(context);
        title.setText("Выберите тип карты, которую хотите добавить.");
        title.setTextSize(16);
        title.setTextColor(Color.BLACK);
        title.setLayoutParams(titleViewParams);
        row.addView(title);
        ImageView cancelIcon = new ImageView(context);
        cancelIcon.setImageResource(R.drawable.ic_delete_black);

        RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        iconParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        cancelIcon.setLayoutParams(iconParams);
        row.addView(cancelIcon);
        cancelIcon.setOnClickListener(v -> bottomSheetDialog.dismiss());



        layout.addView(row);

        View separator = new View(context);
        separator.setBackgroundColor(Color.parseColor("#E0E0E0"));
        LinearLayout.LayoutParams separatorParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        separatorParams.setMargins(0, 16, 0, 16);
        layout.addView(separator, separatorParams);

        layout.addView(createOptionLayout(
                R.drawable.ic_uzcard_humo,
                "Добавить карту Uzcard/Humo",
                v -> {
                    baseFragment.presentFragment(new AddCardFragment());
                    bottomSheetDialog.dismiss();
                }
        ));



        layout.addView(createOptionLayout(
                R.drawable.add_ru_card,
                "Добавить российскую карту",
                v ->  {
                    showProgressDialog();
                    tkbService.tkbCardRegister(new ApiCallback<TkbCardCreadeModel>() {
                        @Override
                        public void onSuccess(TkbCardCreadeModel response) {
                            dismissProgressDialog();
                            Browser.openUrl(context, response.getMessage().getForm_url());
                            JsonObject extId=new JsonObject();
                            extId.addProperty("ext_id",response.getMessage().getExt_id());
                            tkbService.tkbCardRegisterCallbek(new ApiCallback<JsonObject>() {
                                @Override
                                public void onSuccess(JsonObject response) {

                                }

                                @Override
                                public void onFailure(String errorMessage) {

                                }
                            },extId);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            dismissProgressDialog();  // Dismiss progress dialog on failure
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                    bottomSheetDialog.dismiss();


                }
        ));

        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.show();
    }

    private LinearLayout createOptionLayout(int iconResId, String label, View.OnClickListener listener) {
        LinearLayout optionLayout = new LinearLayout(context);
        optionLayout.setLayoutParams(new ViewGroup.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                AndroidUtilities.dp(54)
        ));
        optionLayout.setOrientation(LinearLayout.HORIZONTAL);
        optionLayout.setPadding(16, 16, 16, 16);
        optionLayout.setGravity(Gravity.CENTER_VERTICAL);

        ImageView icon = new ImageView(context);
        icon.setImageResource(iconResId);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(64, 64);
        icon.setLayoutParams(iconParams);
        optionLayout.addView(icon);

        TextView labelTextView = new TextView(context);
        labelTextView.setText(label);
        labelTextView.setTextSize(16);
        labelTextView.setTextColor(Color.BLACK);
        labelTextView.setPadding(16, 0, 0, 0);
        optionLayout.addView(labelTextView);

        optionLayout.setOnClickListener(listener);

        return optionLayout;
    }
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Загрузка...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}

