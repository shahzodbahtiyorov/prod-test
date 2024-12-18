package org.finance.ui.card.mycard;

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

import org.finance.data.model.get_card.GetCardBalanceModel;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

/** @noinspection ALL*/
public class CardDetailsFragment extends BaseFragment {
    private final GetCardBalanceModel.Cards cardsModel;
    public CardDetailsFragment(GetCardBalanceModel.Cards cards){
        this.cardsModel=cards;
    }

    @Override
    public View createView(Context context) {
        actionBar.setTitle(LocaleController.getString(R.string.Card_Details));
        actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setBackgroundColor(Color.parseColor("#50A7EA"));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
                super.onItemClick(id);
            }
        });

        LinearLayout rootLayout = new LinearLayout(context);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(AndroidUtilities.dp(18), 16, AndroidUtilities.dp(18), 8);
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
        border.setStroke(4, Color.LTGRAY);

        cardView.setBackground(border);

        LinearLayout itemLayout = new LinearLayout(context);
        itemLayout.setOrientation(LinearLayout.VERTICAL);

        cardView.addView(itemLayout);

        rootLayout.addView(cardView);

        LinearLayout cardNumebr = createRow(context, LocaleController.getString(R.string.Card_number), cardsModel.getCardNumber(), true);
        LinearLayout cardOwner = createRow(context,  LocaleController.getString(R.string.Owner), cardsModel.getCardOwnerName(), false);
        LinearLayout bank = createRow(context, LocaleController.getString(R.string.Bank), cardsModel.getBank(), false);
        LinearLayout cardName = createRow(context, LocaleController.getString(R.string.Card_name),cardsModel.getCardName(), false);

        itemLayout.addView(cardNumebr);
        itemLayout.addView(createDashedLine(context));
        itemLayout.addView(cardOwner);
        itemLayout.addView(createDashedLine(context));
        itemLayout.addView(bank);
        itemLayout.addView(createDashedLine(context));
        itemLayout.addView(cardName);

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

        TextView hintTextView = new TextView(context);
        hintTextView.setText(hint);
        hintTextView.setTextColor(Color.parseColor("#8E8E92"));
        hintTextView.setTextSize(14);
        hintTextView.setGravity(Gravity.START);

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
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
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

}
