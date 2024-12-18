package org.finance.ui.ui_helper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import org.finance.data.model.get_card.GetCardBalanceWithCards;
import org.finance.data.model.monitoring.MonitoringModel;
import org.finance.ui.adapters.HomeCardAdapter;
import org.finance.ui.adapters.MonitoringHomeAdapter;
import org.finance.ui.card.mycard.CardTypeBottomSheet;
import org.finance.ui.card.mycard.MyCardFragment;
import org.finance.ui.home.FinanceViewModel;
import org.finance.ui.monitoring.MonitoringFragment;
import org.finance.ui.paynet.category.PaynetCategoryFragment;
import org.finance.ui.ui_helper.card_decaration.CardItemDecoration;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import java.text.NumberFormat;
import java.util.Locale;

/** @noinspection ALL*/
public class HomeHelper {
    @SuppressLint("UseCompatLoadingForDrawables")
    public static void setupMainUI(RelativeLayout layout,  Context context, BaseFragment baseFragment) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        int screenWidthPx = displayMetrics.widthPixels;
        int screenHeightPx = displayMetrics.heightPixels;
        ImageView imageView = new ImageView(context);
        int ivCardsId = View.generateViewId();
        imageView.setId(ivCardsId);
        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                (int) (screenWidthPx * 0.6),
                (int) (screenHeightPx * 0.3)
        );
        imageParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        imageParams.setMargins(0, (int) (screenHeightPx * 0.2), 0, 0);
        imageView.setLayoutParams(imageParams);
        imageView.setRotationX(2);
        imageView.setImageDrawable(context.getDrawable(R.drawable.ic_cards));
        TextView walletNotSetupText = new TextView(context);
        int tvWalletNotSetupId = View.generateViewId();
        walletNotSetupText.setId(tvWalletNotSetupId);
        RelativeLayout.LayoutParams walletTextParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        walletTextParams.addRule(RelativeLayout.BELOW, ivCardsId);
        walletTextParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        walletTextParams.setMargins(48, 16, 48, 0);
        walletNotSetupText.setLayoutParams(walletTextParams);
        walletNotSetupText.setText(LocaleController.getString(R.string.empty_card_title));
        walletNotSetupText.setTextColor(Color.parseColor("#1F1F1F"));
        walletNotSetupText.setTextSize(20);
        walletNotSetupText.setTypeface(null, Typeface.BOLD);
        TextView addCardInstructionText = new TextView(context);
        int tvAddCardInstructionId = View.generateViewId();
        addCardInstructionText.setId(tvAddCardInstructionId);
        RelativeLayout.LayoutParams addCardInstructionParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        addCardInstructionParams.addRule(RelativeLayout.BELOW, tvWalletNotSetupId);
        addCardInstructionParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        addCardInstructionParams.setMargins(48, 8, 48, 0);
        addCardInstructionText.setLayoutParams(addCardInstructionParams);
        addCardInstructionText.setText(LocaleController.getString(R.string.emtpy_card_subtitle));
        addCardInstructionText.setTextSize(16);
        addCardInstructionText.setGravity(Gravity.CENTER);
        addCardInstructionText.setTextColor(Color.DKGRAY);
        RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                AndroidUtilities.dp(48)
        );
        TextView btnAddCard = new TextView(context);
        int btnAddCardId = View.generateViewId();
        btnAddCard.setId(btnAddCardId);
        btnAddCard.setBackground(context.getDrawable(R.drawable.rounded_button));
        CardTypeBottomSheet bottomSheet = new CardTypeBottomSheet(context,baseFragment);
        btnParams.addRule(RelativeLayout.BELOW, tvAddCardInstructionId);
        btnParams.setMargins(34,  AndroidUtilities.dp((int) (screenHeightPx * 0.075)), 34, 0);
        btnAddCard.setLayoutParams(btnParams);
        btnAddCard.setGravity(Gravity.CENTER);
        btnAddCard.setText(LocaleController.getString(R.string.add_card));
        btnAddCard.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        btnAddCard.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        btnAddCard.setPadding(AndroidUtilities.dp(34), 0, AndroidUtilities.dp(34), 0);
        btnAddCard.setTypeface(AndroidUtilities.bold());
        btnAddCard.setOnClickListener(v -> bottomSheet.show());
        layout.addView(imageView);
        layout.addView(walletNotSetupText);
        layout.addView(addCardInstructionText);
        layout.addView(btnAddCard);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static LinearLayout createFinanceLayout(Context context, GetCardBalanceWithCards response, BaseFragment baseFragment, MonitoringModel monitoringModel, FinanceViewModel financeViewModel) {
        HomeCardAdapter cardAdapter = new HomeCardAdapter(response.cards, context);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(24, 16, 24, 0);
        mainLayout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        SwipeRefreshLayout swipeRefreshLayout = new SwipeRefreshLayout(context);
        LinearLayout.LayoutParams swipeParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,  AndroidUtilities.dp(80));
        swipeRefreshLayout.setLayoutParams(swipeParams);
        CardView cardView = new CardView(context);
        CardView.LayoutParams cardParams = new CardView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                AndroidUtilities.dp(80));
        cardView.setLayoutParams(cardParams);
        cardView.setCardBackgroundColor(Color.WHITE);
        cardView.setRadius(AndroidUtilities.dp(8));
        cardView.setCardElevation(0);
        ViewGroup parentLayout = (ViewGroup) cardView.getParent();
        if (parentLayout != null) {
            parentLayout.setClipChildren(false);
            parentLayout.setClipToPadding(false);
        }


        cardView.setLayoutParams(cardParams);

        cardView.addView(createRow(context, LocaleController.getString(R.string.Overall_balance), formatWithSpaces(response.balance.getTotalBalance()), cardAdapter));

        swipeRefreshLayout.addView(cardView);
        mainLayout.addView(swipeRefreshLayout);

        RelativeLayout row = new RelativeLayout(context);
        row.setPadding(0, AndroidUtilities.dp(16), 0, AndroidUtilities.dp(4));
        row.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        TextView valueTextView = new TextView(context);
        valueTextView.setText(LocaleController.getString(R.string.my_cards));
        valueTextView.setTextColor(Color.parseColor("#1F1F1F"));
        valueTextView.setTextSize(16);
        valueTextView.setTypeface(null, Typeface.BOLD);
        valueTextView.setGravity(Gravity.START);

        RelativeLayout.LayoutParams valueTextViewParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        valueTextViewParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        valueTextView.setLayoutParams(valueTextViewParams);
        row.addView(valueTextView);

        TextView hintTextView = new TextView(context);
        hintTextView.setText(LocaleController.getString(R.string.All_cards));
        hintTextView.setTextColor(Color.parseColor("#6C6C6C"));
        hintTextView.setTextSize(16);
        hintTextView.setGravity(Gravity.END);
        hintTextView.setOnClickListener(v -> {
            baseFragment.presentFragment(new MyCardFragment());
        });
        RelativeLayout.LayoutParams hintTextViewParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        hintTextViewParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        hintTextView.setLayoutParams(hintTextViewParams);
        row.addView(hintTextView);
        mainLayout.addView(row);
        mainLayout.addView(createCardInfoLayout(context, cardAdapter));
       // mainLayout.addView(createActionButtonsLayout(context, baseFragment));

        if (monitoringModel.getResult().isEmpty()) {
            mainLayout.addView(createTransactionHistoryLayout(context));
        } else {
            mainLayout.addView(createTransactionHistory( context, monitoringModel, baseFragment));
        }


        swipeRefreshLayout.setOnRefreshListener(() -> {
            financeViewModel.refreshData();
            swipeRefreshLayout.setRefreshing(false);
        });

        return mainLayout;
    }

    public static String formatWithSpaces(double number) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        String formattedNumber = numberFormat.format(number);
        return formattedNumber.replace(",", " ")+" UZS"; // Replace commas with spaces
    }
    public static LinearLayout createCardInfoLayout(Context context, HomeCardAdapter  cardAdapter) {
        LinearLayout cardInfoLayout = new LinearLayout(context);
        cardInfoLayout.setOrientation(LinearLayout.VERTICAL);
        cardInfoLayout.setPadding(16, 16, 16, AndroidUtilities.dp(12));
        LinearLayout.LayoutParams cardInfoParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        cardInfoParams.setMargins(0, 0, 0, 16);
        cardInfoLayout.setLayoutParams(cardInfoParams);

        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(cardAdapter);
        cardInfoLayout.addView(recyclerView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        int spaceInDp = AndroidUtilities.dp(8);
        CardItemDecoration itemDecoration = new CardItemDecoration(spaceInDp);
        recyclerView.addItemDecoration(itemDecoration);

        return cardInfoLayout;
    }
    private static LinearLayout createActionButtonLayout(Context context, int iconResId, String text, View.OnClickListener onClickListener) {
        LinearLayout buttonLayout = new LinearLayout(context);
        buttonLayout.setOrientation(LinearLayout.VERTICAL);
        buttonLayout.setGravity(Gravity.CENTER);
        buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        ));
        buttonLayout.setPadding(8, 8, 8, 8);

        ImageView icon = new ImageView(context);
        icon.setImageResource(iconResId);
        icon.setLayoutParams(new LinearLayout.LayoutParams(
                AndroidUtilities.dp(48),
                AndroidUtilities.dp(36)
        ));
        buttonLayout.addView(icon);

        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(12);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        textView.setTextColor(Color.parseColor("#5A8FBB"));

        buttonLayout.addView(textView);

        buttonLayout.setOnClickListener(onClickListener);

        return buttonLayout;
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private static LinearLayout createTransactionHistoryLayout(Context context) {
        LinearLayout transactionHistoryLayout = new LinearLayout(context);
        transactionHistoryLayout.setOrientation(LinearLayout.VERTICAL);
        transactionHistoryLayout.setGravity(LinearLayout.HORIZONTAL);
        TextView monitoringTextView = new TextView(context);
        monitoringTextView.setText("История транзакций");
        monitoringTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        monitoringTextView.setTextSize(18);
        monitoringTextView.setTextColor(Color.parseColor("#1F1F1F"));
        monitoringTextView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        monitoringTextView.setPadding(16, 16, 16, 16);
        transactionHistoryLayout.addView(monitoringTextView);

        ImageView transactionHistoryImageView = new ImageView(context);
        transactionHistoryImageView.setPadding(16, AndroidUtilities.dp(24), 16, 16);
        transactionHistoryImageView.setImageDrawable(context.getDrawable(R.drawable.ic_transaction_history));
        transactionHistoryLayout.addView(transactionHistoryImageView);

        TextView transactionHistoryDescriptionTextView = new TextView(context);
        transactionHistoryDescriptionTextView.setText("Вы еще не совершали никаких операций");
        transactionHistoryDescriptionTextView.setTextSize(14);
        transactionHistoryDescriptionTextView.setTextColor(Color.parseColor("#909092"));
        transactionHistoryDescriptionTextView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        transactionHistoryDescriptionTextView.setPadding(16, 16, 16, AndroidUtilities.dp(50));
        transactionHistoryLayout.addView(transactionHistoryDescriptionTextView);
        return transactionHistoryLayout;
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private static LinearLayout createTransactionHistory(Context context, MonitoringModel monitoringModel,BaseFragment baseFragment) {
        LinearLayout transactionHistoryLayout = new LinearLayout(context);
        transactionHistoryLayout.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout row = new RelativeLayout(context);
        row.setPadding(0,AndroidUtilities.dp(8),0,0);
        row.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        TextView valueTextView = new TextView(context);
        valueTextView.setText(LocaleController.getString(R.string.Operation_history));
        valueTextView.setTextColor(Color.parseColor("#1F1F1F"));
        valueTextView.setTextSize(16);
        valueTextView.setTypeface(null, Typeface.BOLD);
        valueTextView.setGravity(Gravity.START);

        RelativeLayout.LayoutParams valueTextViewParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        valueTextViewParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        valueTextView.setLayoutParams(valueTextViewParams);
        row.addView(valueTextView);

        TextView hintTextView = new TextView(context);
        hintTextView.setText(LocaleController.getString(R.string.All_operations));
        hintTextView.setTextColor(Color.GRAY);
        hintTextView.setTextSize(16);
        hintTextView.setGravity(Gravity.END);

        RelativeLayout.LayoutParams hintTextViewParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        hintTextViewParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        hintTextView.setLayoutParams(hintTextViewParams);
        row.addView(hintTextView);
       transactionHistoryLayout.addView(row);
        hintTextView.setOnClickListener(v -> {
            baseFragment.presentFragment(new MonitoringFragment());
        });
        CardView cardView = new CardView(context);
        cardView.setCardBackgroundColor(Color.WHITE);
        cardView.setRadius(AndroidUtilities.dp(8));

        CardView.LayoutParams cardParams = new CardView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(0);
        ViewGroup parentLayout = (ViewGroup) cardView.getParent();
        if (parentLayout != null) {
            parentLayout.setClipChildren(false);
            parentLayout.setClipToPadding(false);
        }


        RecyclerView recyclerView=new RecyclerView(context);
        RelativeLayout.LayoutParams recyclerViewParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        recyclerView.setLayoutParams(recyclerViewParams);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = 12;
                outRect.left = 12;
                outRect.right = 12;
                outRect.top = 12;
            }
        });
        MonitoringHomeAdapter monitoringAdapter = new MonitoringHomeAdapter(context, monitoringModel.getResult(),baseFragment);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(monitoringAdapter);
        cardView.addView(recyclerView);
        transactionHistoryLayout.addView(cardView);
        return transactionHistoryLayout;

    }

    private static LinearLayout createRow(Context context, String hint, String value, HomeCardAdapter adapter) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SuperApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        boolean isBalanceVisible = sharedPreferences.getBoolean("isBalanceVisible", false);
        boolean[] isBalanceCurrentlyVisible = {isBalanceVisible};

        LinearLayout column = new LinearLayout(context);
        column.setOrientation(LinearLayout.VERTICAL);
        column.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(12), AndroidUtilities.dp(16), AndroidUtilities.dp(4));

        TextView hintTextView = new TextView(context);
        hintTextView.setText(hint);
        hintTextView.setTextColor(Color.parseColor("#1F1F1F"));
        hintTextView.setTextSize(14);
        hintTextView.setGravity(Gravity.START);
        column.addView(hintTextView);

        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView valueTextView = new TextView(context);
        valueTextView.setText(isBalanceCurrentlyVisible[0] ? value : "******  UZS");
        valueTextView.setTextColor(Color.parseColor("#1F1F1F"));
        valueTextView.setTextSize(20);
        valueTextView.setTypeface(null, Typeface.BOLD);
        valueTextView.setGravity(Gravity.START);

        LinearLayout.LayoutParams valueParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        valueTextView.setLayoutParams(valueParams);
        row.addView(valueTextView);

        ImageView copyIcon = new ImageView(context);
        copyIcon.setImageResource(isBalanceCurrentlyVisible[0] ? R.drawable.ic_eye_slash : R.drawable.ic_eye);

        copyIcon.setOnClickListener(v -> {
            adapter.toggleBalanceVisibility();
            isBalanceCurrentlyVisible[0] = !isBalanceCurrentlyVisible[0];

            String newText = isBalanceCurrentlyVisible[0] ? value : "****** UZS";
            animateRotateTextChangeVertical(valueTextView, newText);

            int newIcon = isBalanceCurrentlyVisible[0] ? R.drawable.ic_eye_slash : R.drawable.ic_eye;
            copyIcon.setImageResource(newIcon);

            editor.putBoolean("isBalanceVisible", isBalanceCurrentlyVisible[0]);
            editor.apply();
        });
        row.addView(copyIcon);
        column.addView(row);

        return column;
    }


    private static LinearLayout createService(Context context) {
        LinearLayout linearLayout =new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout row = new RelativeLayout(context);
        row.setPadding(0,AndroidUtilities.dp(8),0,AndroidUtilities.dp(12));
        row.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        TextView valueTextView = new TextView(context);
        valueTextView.setText("Избранные");
        valueTextView.setTextColor(Color.parseColor("#1F1F1F"));
        valueTextView.setTextSize(16);
        valueTextView.setTypeface(null, Typeface.BOLD);
        valueTextView.setGravity(Gravity.START);

        RelativeLayout.LayoutParams valueTextViewParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        valueTextViewParams.addRule(RelativeLayout.ALIGN_PARENT_START); // Chap tarafga joylashtirish
        valueTextView.setLayoutParams(valueTextViewParams);
        row.addView(valueTextView);

        TextView hintTextView = new TextView(context);
        hintTextView.setText("Все");
        hintTextView.setTextColor(Color.GRAY);
        hintTextView.setTextSize(16);
        hintTextView.setGravity(Gravity.END);
        RelativeLayout.LayoutParams hintTextViewParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        hintTextViewParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        hintTextView.setLayoutParams(hintTextViewParams);
        row.addView(hintTextView);
        linearLayout.addView(row);
        return  linearLayout;
    }
//    private static LinearLayout createActionButtonsLayout(Context context, BaseFragment baseFragment) {
//        LinearLayout actionButtonsLayout = new LinearLayout(context);
//        actionButtonsLayout.setOrientation(LinearLayout.HORIZONTAL);
//        actionButtonsLayout.setGravity(Gravity.CENTER);
//        actionButtonsLayout.setLayoutParams(new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        ));
//        actionButtonsLayout.setWeightSum(4);
//        actionButtonsLayout.setPadding(8, 0, 8, 16);
//
//        LinearLayout withdrawButton = createActionButtonLayout(context, R.drawable.add_card, LocaleController.getString(R.string.my_cards), v -> baseFragment.presentFragment(new MyCardFragment()));
//
//        actionButtonsLayout.addView(withdrawButton);
//
//        LinearLayout depositButton = createActionButtonLayout(context, R.drawable.ic_deposit, LocaleController.getString(R.string.payments), v -> {
//            try {
//                baseFragment.presentFragment(new PaynetCategoryFragment());
//            }catch (Exception e){
//                Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show();
//                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//
//                ClipData clip = ClipData.newPlainText("Error", e.toString());
//
//                if (clipboard != null) {
//                    clipboard.setPrimaryClip(clip);
//
//                }
//            }
//        });
//        actionButtonsLayout.addView(depositButton);
//
//        LinearLayout sendFinanceButton = createActionButtonLayout(context, R.drawable.p2p, LocaleController.getString(R.string.transfers), v ->{
//            baseFragment.presentFragment(new TransferTypeFragment(0));
//        });
//        actionButtonsLayout.addView(sendFinanceButton);
//
//
//        LinearLayout requestButton = createActionButtonLayout(context, R.drawable.monitoring, LocaleController.getString(R.string.monitoring), v -> {
//            try {
//                baseFragment.presentFragment(new MonitoringFragment());
//            } catch (Exception e) {
//                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
//            }
//        });
//        actionButtonsLayout.addView(requestButton);
//        return actionButtonsLayout;
//    }
//
    public static void animateRotateTextChangeVertical(TextView textView, String newText) {
        ObjectAnimator rotateOut = ObjectAnimator.ofFloat(textView, "rotationX", 0f, 90f);
        rotateOut.setDuration(150);
        ObjectAnimator rotateIn = ObjectAnimator.ofFloat(textView, "rotationX", -90f, 0f);
        rotateIn.setDuration(150);
        rotateIn.setStartDelay(150);
        rotateOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                textView.setText(newText);
                rotateIn.start();
            }
        });
        rotateOut.start();
    }

}

