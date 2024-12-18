package org.finance.ui.paynet.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import org.finance.ui.home.HomeFragment;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import java.text.NumberFormat;
import java.util.Locale;

/** @noinspection ALL */
public class TransactionHistoryFragment extends BaseFragment {
    private final int summ;

    public TransactionHistoryFragment( int summ) {

        this.summ=summ;
    }
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
        actionBar.setTitle("Оплата");
        actionBar.setBackgroundColor(Color.parseColor("#50A7EA"));
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

        ConstraintLayout layout = new ConstraintLayout(context);
        layout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        layout.setId(View.generateViewId());
        layout.setPadding(AndroidUtilities.dp(24), AndroidUtilities.dp(24), AndroidUtilities.dp(24), 24);
        layout.setLayoutParams(new ViewGroup.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
        ));

        TextView historyTitle = new TextView(context);
        historyTitle.setId(View.generateViewId());
        historyTitle.setText("To’lov muvaffaqiyatli amalga oshdi");
        historyTitle.setTextSize(16);
        historyTitle.setGravity(Gravity.CENTER);
        historyTitle.setTextColor(Color.BLACK);
        layout.addView(historyTitle);

        ImageView imageView = new ImageView(context);
        imageView.setId(View.generateViewId());
        imageView.setImageResource(R.drawable.ic_succes);
        imageView.setPadding(0, AndroidUtilities.dp(12), 0, 24);
        layout.addView(imageView);

        TextView amountTitle = new TextView(context);
        amountTitle.setId(View.generateViewId());
        amountTitle.setPadding(0, AndroidUtilities.dp(12), 0, 24);
        amountTitle.setText(formatWithSpaces(summ)+" UZS");
        amountTitle.setGravity(Gravity.CENTER);
        amountTitle.setTextSize(28);
        amountTitle.setTextColor(Color.BLACK);
        layout.addView(amountTitle);

        LinearLayout itemLayout = new LinearLayout(context);
        itemLayout.setId(View.generateViewId());
        itemLayout.setPadding(AndroidUtilities.dp(24),0,AndroidUtilities.dp(24),0);
        itemLayout.setLayoutParams(new ViewGroup.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setGravity(Gravity.CENTER);
        itemLayout.setBackgroundResource(R.drawable.payment_card_border);

        LinearLayout.LayoutParams rowLayoutParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        );

        LinearLayout savedTransation = createRow(context, R.drawable.ic_oplata_save, "To'lovni\nsaqlash");
        LinearLayout detail = createRow(context, R.drawable.ic_detail, "To’lov\ntafsilotlari");
        LinearLayout repetition = createRow(context, R.drawable.ic_repetition, "To’lovni\ntakrorlash");
        savedTransation.setLayoutParams(rowLayoutParams);
        detail.setLayoutParams(rowLayoutParams);
        repetition.setLayoutParams(rowLayoutParams);
        itemLayout.addView(savedTransation);
        itemLayout.addView(detail);
        itemLayout.addView(repetition);
        LinearLayout layoutBottom = new LinearLayout(context);
        layoutBottom.setLayoutParams(new ViewGroup.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        layoutBottom.setId(View.generateViewId());
        layoutBottom.setOrientation(LinearLayout.VERTICAL);
        layoutBottom.setHorizontalGravity(Gravity.CENTER);
        layoutBottom.setGravity(Gravity.END);
        layoutBottom.addView(itemLayout);
        View spacer = new View(context);
        LinearLayout.LayoutParams spacerLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                AndroidUtilities.dp(24)
        );
        spacer.setLayoutParams(spacerLayoutParams);
        layoutBottom.addView(spacer);
        Button nextButton = new Button(context);
        nextButton.setId(View.generateViewId());
        nextButton.setText("Bosh sahifa");
        nextButton.setTextSize(16);
        nextButton.setGravity(Gravity.CENTER);
        nextButton.setTextColor(Color.parseColor("#F6F6F6"));
        nextButton.setBackground(context.getDrawable(R.drawable.rounded_button));
        nextButton.setOnClickListener(v -> {
            presentFragment(new HomeFragment("paymentsView"));
        });
        layoutBottom.addView(nextButton);

        layout.addView(layoutBottom);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        constraintSet.connect(historyTitle.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP, 24);
        constraintSet.connect(historyTitle.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
        constraintSet.connect(historyTitle.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);

        constraintSet.connect(imageView.getId(), ConstraintSet.TOP, historyTitle.getId(), ConstraintSet.BOTTOM, 12);
        constraintSet.connect(imageView.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
        constraintSet.connect(imageView.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);

        constraintSet.connect(amountTitle.getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.BOTTOM, 12);
        constraintSet.connect(amountTitle.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
        constraintSet.connect(amountTitle.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);

        constraintSet.connect(layoutBottom.getId(), ConstraintSet.BOTTOM, layout.getId(), ConstraintSet.BOTTOM, 24);
        constraintSet.connect(layoutBottom.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 24);
        constraintSet.connect(layoutBottom.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 24);

        constraintSet.applyTo(layout);
        layout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View view = layout.findFocus();
                if (view != null) {
                    view.clearFocus();
                }
            }
            return true;
        });
        return layout;
    }

    private static LinearLayout createRow(Context context, int iconResId, String text) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(16, 16, 16, 32);
        ImageView icon = new ImageView(context);
        icon.setImageResource(iconResId);
        icon.setLayoutParams(new ViewGroup.LayoutParams(
                AndroidUtilities.dp(48),
                AndroidUtilities.dp(48)
        ));
        row.addView(icon);
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(12);
        textView.setPadding(0, 0, 0, 0);
        row.addView(textView);
        return row;
    }
    public static String formatWithSpaces(int number) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        String formattedNumber = numberFormat.format(number);
        return formattedNumber.replace(",", " "); // Replace commas with spaces
    }
}
