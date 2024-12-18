package org.finance.ui.adapters;

import static org.finance.ui.adapters.CardAdapter.formatWithSpaces;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.finance.data.model.get_card.GetCardBalanceModel;
import org.finance.helpers.SuperAppFormatters;
import org.telegram.messenger.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * @noinspection ALL
 */
public class MonitoringCardAdapter extends RecyclerView.Adapter<MonitoringCardAdapter.CardViewHolder> {

    private final Context context;
    private final List<GetCardBalanceModel.Cards> cardList;
    private List<Boolean> selectedPositions;
    private final Consumer<List<String>> selectionCallback;


    public MonitoringCardAdapter(Context context, List<GetCardBalanceModel.Cards> cardList, Consumer<List<String>> selectionCallback, @Nullable List<String> previousCards) {
        this.context = context;
        this.cardList = cardList;
        // Initialize selectedPositions with false for each card
        this.selectedPositions = new ArrayList<>(Collections.nCopies(cardList.size(), false));
        if (previousCards != null) {
            for (int i = 0; i < cardList.size(); i++) {
                if (previousCards.contains(cardList.get(i).getCardNumber())) {
                    this.selectedPositions.set(i, true);
                }
            }
        }
        this.selectionCallback = selectionCallback;

    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.monitoring_card_item, parent, false);
        return new CardViewHolder(view);
    }

    private void notifySelectedCards() {
        List<String> selectedCards = new ArrayList<>();
        for (int i = 0; i < cardList.size(); i++) {
            if (selectedPositions.get(i)) {
                selectedCards.add(cardList.get(i).getCardNumber());
            }
        }
        // Trigger the callback
        selectionCallback.accept(selectedCards);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, @SuppressLint("RecyclerView") int position) {
        GetCardBalanceModel.Cards card = cardList.get(position);
        holder.cardAmount.setText(String.format("%s", card.getBalance()));
        holder.cardDetails.setText(SuperAppFormatters.getCardLastFourDigits(card.getCardNumber()) + " | " + card.getCardOwnerName());

        if (card.getPcType() == 3) {
            holder.cardAmount.setText(formatWithSpaces(card.getBalance()));
            holder.cardIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.humo_card));
        } else if (card.getPcType() == 1) {
            holder.cardAmount.setText(formatWithSpaces(card.getBalance()));
            holder.cardIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_uzcard));
        } else if (card.getPcType() == 2) {
            holder.cardAmount.setText("***");
            holder.cardIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_tkb_card));
        }

        // Set checkIcon visibility based on the selection status
        holder.checkIcon.setVisibility(selectedPositions.get(position) ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            // Toggle the selection status for the clicked item
            selectedPositions.set(position, !selectedPositions.get(position));
            notifyItemChanged(position);
            notifySelectedCards();
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView cardIcon, checkIcon;
        TextView cardAmount, cardDetails;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardIcon = itemView.findViewById(R.id.cardIcon);
            checkIcon = itemView.findViewById(R.id.checkIcon);
            cardAmount = itemView.findViewById(R.id.cardAmount);
            cardDetails = itemView.findViewById(R.id.cardDetails);
        }
    }

    public void updateAdapter() {
        selectedPositions = new ArrayList<>(Collections.nCopies(cardList.size(), false));;
        notifyDataSetChanged();
    }
}
