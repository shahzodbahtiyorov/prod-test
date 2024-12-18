package org.finance.ui.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.Transition;

import org.finance.data.model.get_card.GetCardBalanceModel;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.bumptech.glide.request.target.SimpleTarget;

/** @noinspection ALL*/
public class HomeCardAdapter extends RecyclerView.Adapter<HomeCardAdapter.CardViewHolder> {
    private List<GetCardBalanceModel.Cards> cardList;
    private final Context context;
    private SharedPreferences sharedPreferences;
    private boolean isBalanceVisible;

    public HomeCardAdapter(List<GetCardBalanceModel.Cards> cardList, Context context) {
        this.context = context;
        this.cardList = new ArrayList<>();
        for (GetCardBalanceModel.Cards card : cardList) {
            if(card.getPcType() != 2 &&card.isActive()){
                this.cardList.add(card);
           }
        }
           if (context!=null){
               sharedPreferences = context.getSharedPreferences("SuperApp", Context.MODE_PRIVATE);

               isBalanceVisible = sharedPreferences.getBoolean("isBalanceVisible", false);
           }else {
               isBalanceVisible=false;
           }

    }

    @NonNull
    @Override
    public HomeCardAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_card_item, parent, false);

        return new HomeCardAdapter.CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeCardAdapter.CardViewHolder holder, int position) {
        GetCardBalanceModel.Cards cardItem = cardList.get(position);
        holder.bind(cardItem, context, isBalanceVisible);
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public void toggleBalanceVisibility() {
        isBalanceVisible = !isBalanceVisible;
        notifyDataSetChanged();
        sharedPreferences.edit().putBoolean("isBalanceVisible", isBalanceVisible).apply();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        private final TextView cardBalanceText;
        private final TextView cardMask;
        private final ImageView cardType;
        private final ConstraintLayout cardLayout;
        private  CardView cardView ;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.home_card);
            cardType = itemView.findViewById(R.id.home_card_type);
            cardBalanceText = itemView.findViewById(R.id.home_card_balance_text);
            cardMask = itemView.findViewById(R.id.home_card_mask_text);
            cardLayout = itemView.findViewById(R.id.home_card_layout);
        }
        public void bind(GetCardBalanceModel.Cards cardItem, Context context, boolean isBalanceVisible) {
            cardBalanceText.setText("");
            cardMask.setText("");
            cardType.setImageDrawable(null);
            cardLayout.setBackground(null);

            int cardWidth = AndroidUtilities.dp(300);
            int cardHeight = AndroidUtilities.dp(100);

            if (getItemCount() == 1) {
                cardWidth =ViewGroup.LayoutParams.MATCH_PARENT;
            }
            CardView.LayoutParams cardParams = new CardView.LayoutParams(
                    cardWidth,
                    cardHeight
            );
            cardView.setLayoutParams(cardParams);
            cardLayout.setLayoutParams(cardParams);
            String cardBackgroundUrl = cardItem.getImages();
            if (cardBackgroundUrl != null && !cardBackgroundUrl.isEmpty()) {
                Glide.with(context)
                        .load(BuildConfig.STATIC_URL + "app-superapp/static" + cardBackgroundUrl)
                        .apply(new RequestOptions().transform(new RoundedCorners(1)))
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                cardLayout.setBackground(resource);
                            }
                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                GradientDrawable background = new GradientDrawable(
                                        GradientDrawable.Orientation.LEFT_RIGHT,
                                        new int[]{Color.parseColor("#5A8FBB"), Color.parseColor("#5A8FBB")}
                                );
                                background.setCornerRadius(32f);
                                cardLayout.setBackground(background);
                            }
                        });
            } else {
                GradientDrawable background = new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[]{Color.parseColor("#5A8FBB"), Color.parseColor("#5A8FBB")}
                );
                background.setCornerRadius(32f);
                cardLayout.setBackground(background);
            }

            String balanceText = isBalanceVisible ? formatWithSpaces(cardItem.getBalance()) : "****** UZS";

            if (cardItem.getPcType() == 3) {
                animateRotateTextChangeVertical(cardBalanceText, balanceText); // Animate text change
                cardType.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.humo_card));
            } else if (cardItem.getPcType() == 1) {
                animateRotateTextChangeVertical(cardBalanceText, balanceText); // Animate text change
                cardType.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_uzcard));
            }
            ViewGroup.LayoutParams layoutParams = cardType.getLayoutParams();
            layoutParams.width = AndroidUtilities.dp(54);
            layoutParams.height = AndroidUtilities.dp(40);
            cardType.setLayoutParams(layoutParams);
            cardType.setScaleType(ImageView.ScaleType.FIT_CENTER);
            cardMask.setText(String.format(" %s | %s", maskCardNumber(cardItem.getCardNumber()), cardItem.getCardOwnerName()));
        }
    }

    public static String formatWithSpaces(double number) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        return numberFormat.format(number).replace(",", " ") + " UZS";
    }

    public static String maskCardNumber(String cardNumber) {
        String lastFourDigits = cardNumber.substring(cardNumber.length() - 4);
        return "•• " + lastFourDigits;
    }
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
