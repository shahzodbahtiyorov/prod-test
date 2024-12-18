package org.finance.ui.card.mycard;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import org.finance.data.service.ApiCallback;
import org.finance.data.service.GetCardService;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;

import java.util.Objects;

/** @noinspection ALL*/
public class CardNameBottomSheet {

    private final Context context;
    private final BaseFragment baseFragment;
    private BottomSheetDialog bottomSheetDialog;
    private ProgressDialog progressDialog;
    private int cardId;

    public CardNameBottomSheet(Context context,BaseFragment baseFragment, int cardId) {
        this.baseFragment=baseFragment;
        this.context = context;
        this.cardId=cardId;
    }

    @SuppressLint("SetTextI18n")
    public void show() {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.RoundedBottomSheetDialog);

        Objects.requireNonNull(bottomSheetDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.RECTANGLE);
        background.setColor(Color.WHITE);
        background.setCornerRadii(new float[]{50, 50, 50, 50, 0, 0, 0, 0});
        layout.setBackground(background);

        RelativeLayout headerRow = new RelativeLayout(context);
        headerRow.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        headerRow.setPadding(0, AndroidUtilities.dp(12), 0, AndroidUtilities.dp(12));

        TextView title = new TextView(context);
        title.setText("Изменить название карты");
        title.setTextSize(16);
        title.setTextColor(Color.BLACK);
        RelativeLayout.LayoutParams titleViewParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        titleViewParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        title.setLayoutParams(titleViewParams);
        headerRow.addView(title);

        ImageView cancelIcon = new ImageView(context);
        cancelIcon.setImageResource(R.drawable.ic_delete_black);
        RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        iconParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        cancelIcon.setLayoutParams(iconParams);
        cancelIcon.setOnClickListener(v -> bottomSheetDialog.dismiss());
        headerRow.addView(cancelIcon);

        layout.addView(headerRow);
//        View headerDivide = new View(context);
//
//        headerDivide.setId(View.generateViewId());
//        headerDivide.setLayoutParams(new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics())
//        ));
//        headerDivide.setBackgroundColor(Color.parseColor("#D3D3D3"));
//        layout.addView(headerDivide);
        LinearLayout inputLayout = new LinearLayout(context);
        inputLayout.setOrientation(LinearLayout.HORIZONTAL);
        inputLayout.setPadding(16, 16, 16, 16);
        inputLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_edittext));

        EditText cardNameInput = new EditText(context);

        cardNameInput.setHint("Card holder name");
        cardNameInput.setMaxLines(1);
        cardNameInput.setTextSize(14);
        cardNameInput.setHintTextColor(Color.parseColor("#1F1F1F"));
        cardNameInput.setBackgroundResource(0);
        cardNameInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        cardNameInput.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        inputLayout.addView(cardNameInput);

        ImageView clearTextIcon = new ImageView(context);
        clearTextIcon.setImageResource(R.drawable.ic_clear);

        LinearLayout.LayoutParams clearIconParams = new LinearLayout.LayoutParams(
                AndroidUtilities.dp(24), AndroidUtilities.dp(24));
        clearIconParams.gravity = Gravity.CENTER_VERTICAL;

        clearTextIcon.setLayoutParams(clearIconParams);
        clearTextIcon.setOnClickListener(v -> cardNameInput.setText(""));
        inputLayout.addView(clearTextIcon);

        layout.addView(inputLayout);

        TextView charCounter = new TextView(context);
        charCounter.setText("0/20");
        charCounter.setGravity(Gravity.END);

        layout.addView(charCounter);

        cardNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                charCounter.setText(length + "/20");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        Button saveButton = new Button(context);
        saveButton.setText("Сохранить");
        saveButton.setGravity(Gravity.CENTER);
        saveButton.setPadding(16, 0, 16, 0);
        saveButton.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                AndroidUtilities.dp(48)
        );
        buttonParams.setMargins(AndroidUtilities.dp(0), AndroidUtilities.dp(24), AndroidUtilities.dp(0), AndroidUtilities.dp(24)); // Top and bottom margin
        saveButton.setLayoutParams(buttonParams);
        saveButton.setBackgroundResource(R.drawable.rounded_button);
        saveButton.setOnClickListener(v -> {
            String cardName = cardNameInput.getText().toString().trim();
            if (!cardName.isEmpty() && cardName.length() >=3) {
                showProgressDialog();
                cardNameUpdated(cardId,cardName);
                bottomSheetDialog.dismiss();
            } else {
                Vibrator vv = (Vibrator) baseFragment.getParentActivity().getSystemService(Context.VIBRATOR_SERVICE);
                if (vv != null) {
                    vv.vibrate(200);
                }
                AndroidUtilities.shakeView(inputLayout);
                Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show();
            }
        });

        layout.addView(saveButton);

        scrollView.addView(layout);
        bottomSheetDialog.setContentView(scrollView);
        bottomSheetDialog.show();
    }
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    private void cardNameUpdated(int id,String cardName) {
        GetCardService getCardService=new GetCardService(context);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name",cardName);
        jsonObject.addProperty("card_id", id);

        getCardService.cardNameUpdated(new ApiCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject response) {
                dismissProgressDialog();
                Toast.makeText(context, "Card name saved: " + cardName, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(String errorMessage) {
                dismissProgressDialog();
                Toast.makeText(context,   errorMessage, Toast.LENGTH_LONG).show();
            }
        }, jsonObject);
    }
}
