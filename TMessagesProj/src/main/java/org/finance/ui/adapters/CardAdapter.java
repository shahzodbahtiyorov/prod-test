package org.finance.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.finance.data.model.get_card.GetCardBalanceModel;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/** @noinspection ALL */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private List<GetCardBalanceModel.Cards> cardList;
    private final Context context;

    public CardAdapter(List<GetCardBalanceModel.Cards> cardList, Context context) {
        this.cardList = cardList;
        this.context = context;
    }

    @NonNull
    @Override
    public CardAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new CardAdapter.CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardAdapter.CardViewHolder holder, int position) {
        GetCardBalanceModel.Cards cardItem = cardList.get(position);
        holder.bind(cardItem, context);
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateCardList(List<GetCardBalanceModel.Cards> newCardList) {
        this.cardList = newCardList;
        notifyDataSetChanged();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        private final TextView cardBalanceText;
        private final TextView cardHolderNameText;
        private final TextView cardMask;
        private final ImageView cardType;
        private final ImageView banklogo;
        private final ConstraintLayout cardLayout;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            banklogo = itemView.findViewById(R.id.card_left_logo);
            cardType = itemView.findViewById(R.id.card_type);
            cardBalanceText = itemView.findViewById(R.id.card_balance_text);
            cardHolderNameText = itemView.findViewById(R.id.card_holder_name_text);
            cardMask = itemView.findViewById(R.id.card_mask_text);
            cardLayout = itemView.findViewById(R.id.card_layout);
        }

        @SuppressLint("DefaultLocale")
        public void bind(GetCardBalanceModel.Cards cardItem, Context context) {
            String bankLogoUrl = BuildConfig.STATIC_URL +"app-superapp/static" +cardItem.getImageLog();
            if (bankLogoUrl != null && !bankLogoUrl.isEmpty()) {
                Log.d("GlideDebug", "Attempting to load image: " + bankLogoUrl);
                Glide.with(context)
                        .load(bankLogoUrl)
                        .apply(new RequestOptions().override(32, 32).transform(new RoundedCorners(8)))  // Adjust size and apply rounded corners
                        .into(banklogo);
            } else {
                banklogo.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.error_in_my_id));
            }


            String cardBackgroundUrl = cardItem.getImages();
            if (cardBackgroundUrl != null && !cardBackgroundUrl.isEmpty()) {
                Glide.with(context)
                        .load(BuildConfig.STATIC_URL +"app-superapp/static"+ cardBackgroundUrl)
                        .apply(new RequestOptions().transform(new RoundedCorners(12)))  // Apply rounded corners
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

            if (cardItem.isActive()) {
                cardHolderNameText.setText(cardItem.getCardOwnerName());
                cardMask.setText(cardItem.getCardNumber());
                if (cardItem.getPcType() == 3) {
                    cardBalanceText.setText(formatWithSpaces(cardItem.getBalance()));
                    cardType.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.humo_card));
                } else if (cardItem.getPcType() == 1) {
                    cardBalanceText.setText(formatWithSpaces(cardItem.getBalance()));
                    cardType.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_uzcard));
                } else if (cardItem.getPcType() == 2) {
                    cardBalanceText.setText("***");
                    cardType.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_tkb_card));
                }
            } else {
                cardBalanceText.setText("");
                cardHolderNameText.setText("Karta bloklangan");
                cardMask.setText("");

                GradientDrawable blockbackground = new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[]{Color.parseColor("#AEB2B9"), Color.parseColor("#AEB2B9")}
                );
                blockbackground.setCornerRadius(32f);
                cardLayout.setBackground(blockbackground);
            }

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                    AndroidUtilities.dp(276),
                    AndroidUtilities.dp(172)
            );
            cardLayout.setLayoutParams(params);

            itemView.setOnClickListener(v -> {
                // Handle click event here
            });
        }
    }

    public static String formatWithSpaces(double number) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        String formattedNumber = numberFormat.format(number);
        return formattedNumber.replace(",", " ") + " UZS"; // Replace commas with spaces
    }
}
