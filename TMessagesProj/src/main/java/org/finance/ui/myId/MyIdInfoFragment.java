package org.finance.ui.myId;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import org.finance.data.model.myId.MyIdInfoModel;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

/** @noinspection ALL*/
public class MyIdInfoFragment extends BaseFragment {

    private final MyIdInfoModel myIdInfoModel;
private  LinearLayout rootLayout;
    public MyIdInfoFragment(MyIdInfoModel myIdInfoModel) {
        this.myIdInfoModel = myIdInfoModel;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View createView(Context context) {

        actionBar.setTitle(LocaleController.getString(R.string.personal_data));
        actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setBackgroundColor(Color.parseColor("#50A7EA"));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                    ((ViewGroup) rootLayout.getParent()).removeView(rootLayout);
                }
                super.onItemClick(id);
            }
        });

        rootLayout = new LinearLayout(context);
        rootLayout.setOrientation(LinearLayout.VERTICAL);

        // set padding to root layout
        rootLayout.setPadding(AndroidUtilities.dp(18), 16, AndroidUtilities.dp(18), 8);

        // "Введите свои данные" TextView
        TextView headerNameText = new TextView(context);
        headerNameText.setText(myIdInfoModel.getCommonData().getFirstName() + " " + myIdInfoModel.getCommonData().getLastName() + "\n" + myIdInfoModel.getCommonData().getMiddleName());
        headerNameText.setTextSize(22);
        headerNameText.setTextColor(Theme.getColor(Theme.key_chat_messageTextIn));
        headerNameText.setGravity(Gravity.START);

        LinearLayout.LayoutParams headerNameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        headerNameParams.setMargins(0, 30, 0, 30);
        headerNameText.setLayoutParams(headerNameParams);
        rootLayout.addView(headerNameText);

        // Verify icon row
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(16, 16, 16, 32);

        ImageView icon = new ImageView(context);
        icon.setImageResource(R.drawable.success_icon);
        row.addView(icon);

        TextView textView = new TextView(context);
        textView.setText(LocaleController.getString(R.string.verified_user));
        textView.setTextColor(Color.parseColor("#8E8E92"));
        textView.setTextSize(16);
        textView.setPadding(AndroidUtilities.dp(16), 0, 0, 16);
        row.addView(textView);

        rootLayout.addView(row);

        CardView cardView = new CardView(context);
        cardView.setCardBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));

        CardView.LayoutParams cardParams = new CardView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        cardView.setLayoutParams(cardParams);

        cardView.setRadius(8);
        cardView.setCardElevation(4);

        cardView.setContentPadding(8, 8, 8, 8);

        GradientDrawable border = new GradientDrawable();
        border.setCornerRadius(AndroidUtilities.dp(8));
        border.setStroke(4, Color.parseColor("#E4E4E4"));

        cardView.setBackground(border);

        LinearLayout itemLayout = new LinearLayout(context);
        itemLayout.setOrientation(LinearLayout.VERTICAL);

        cardView.addView(itemLayout);

        rootLayout.addView(cardView);

        LinearLayout birthRow = createRow(context, LocaleController.getString(R.string.date_of_birth), myIdInfoModel.getCommonData().getBirthDate(), false);
        LinearLayout passportRow = createRow(context,  LocaleController.getString(R.string.passport_series), myIdInfoModel.getDocData().getPassData(), true);
        LinearLayout expircyRow = createRow(context, LocaleController.getString(R.string.validity_period), myIdInfoModel.getDocData().getExpiryDate(), false);
        LinearLayout addressRow = createRow(context, LocaleController.getString(R.string.address), myIdInfoModel.getAddress().getPermanentAddress(), false);
        LinearLayout pinflRow = createRow(context, LocaleController.getString(R.string.PINFL), myIdInfoModel.getCommonData().getPinfl(), true);

        itemLayout.addView(birthRow);
        itemLayout.addView(createDashedLine(context));
        itemLayout.addView(passportRow);
        itemLayout.addView(createDashedLine(context));
        itemLayout.addView(expircyRow);
        itemLayout.addView(createDashedLine(context));
        itemLayout.addView(addressRow);
        itemLayout.addView(createDashedLine(context));
        itemLayout.addView(pinflRow);

        return rootLayout;
    }
    private View createDashedLine(Context context) {
        View dashedLine = new View(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                AndroidUtilities.dp(8)
        );
        params.setMargins(AndroidUtilities.dp(8), AndroidUtilities.dp(0), AndroidUtilities.dp(8), AndroidUtilities.dp(0));
        dashedLine.setLayoutParams(params);

        dashedLine.setBackground(ContextCompat.getDrawable(context, R.drawable.dashed_line_monitoring));

        return dashedLine;
    }



    private static LinearLayout createRow(Context context, String hint, String value, Boolean isCopyable) {
        LinearLayout column = new LinearLayout(context);
        column.setOrientation(LinearLayout.VERTICAL);
        column.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(12), AndroidUtilities.dp(4), AndroidUtilities.dp(4));

        // Hint text (Left aligned)
        TextView hintTextView = new TextView(context);
        hintTextView.setText(hint);
        hintTextView.setTextColor(Color.parseColor("#8E8E92"));
        hintTextView.setTextSize(14);
        hintTextView.setGravity(Gravity.START); // Left-align the hint text

        column.addView(hintTextView);

        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView valueTextView = new TextView(context);
        valueTextView.setText(value);
        valueTextView.setTextColor(Theme.getColor(Theme.key_chat_messageTextIn));
        valueTextView.setTextSize(16);
        valueTextView.setGravity(Gravity.START);

        LinearLayout.LayoutParams valueParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1); // Weight set to 1 for left alignment
        valueTextView.setLayoutParams(valueParams);
        row.addView(valueTextView);

        if (isCopyable) {
            ImageView copyIcon = new ImageView(context);
            copyIcon.setImageResource(R.drawable.file_copy);
            copyIcon.setScaleX(1.4f);
            copyIcon.setScaleY(1.4f);
            copyIcon.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard != null) {
                    Toast.makeText(context,value,Toast.LENGTH_LONG).show();
                    ClipData clip = ClipData.newPlainText("Copied", value);
                    clipboard.setPrimaryClip(clip);
                }
            });
            row.addView(copyIcon);
        }
        column.addView(row);

        return column;
    }

    @Override
    public boolean onBackPressed() {
        finishFragment();
        ((ViewGroup) rootLayout.getParent()).removeView(rootLayout);
        return false;
    }
}
