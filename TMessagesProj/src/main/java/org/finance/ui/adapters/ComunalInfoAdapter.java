package org.finance.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.finance.data.model.paynet.chekcomunal.ResponseItem;


import java.util.ArrayList;
import java.util.List;

/** @noinspection ALL*/
public class ComunalInfoAdapter extends RecyclerView.Adapter<ComunalInfoAdapter.CardViewHolder> {

    private List<ResponseItem> cardList;
    private final Context context;

    public ComunalInfoAdapter(List<ResponseItem> cardList, Context context) {
        this.cardList = cardList != null ? filterCardList(cardList) : new ArrayList<>();
        this.context = context;
        Log.d("CardList", cardList == null ? "List is null" : "List size: " + cardList.size());

    }


    private List<ResponseItem> filterCardList(List<ResponseItem> originalList) {
        List<ResponseItem> filteredList = new ArrayList<>();
        for (ResponseItem item : originalList) {
            if (item.getValue() != null && !item.getValue().trim().isEmpty()) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView textView = new TextView(context);
        textView.setPadding(24, 16, 24, 16);
        textView.setTextSize(16);
        textView.setTextColor(Color.BLACK);
        return new CardViewHolder(textView);

    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        ResponseItem cardItem = filterCardList(cardList).get(position);
        holder.bind(cardItem, position);
    }

    @Override
    public int getItemCount() {
        return filterCardList(cardList).size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateCardList(List<ResponseItem> newCardList) {
        if (newCardList != null) {
            this.cardList = filterCardList(newCardList);
        } else {
            this.cardList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }


    static class CardViewHolder extends RecyclerView.ViewHolder {
        final TextView cardBalanceText;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardBalanceText = (TextView) itemView;
        }

        @SuppressLint("DefaultLocale")
        public void bind(ResponseItem cardItem, int position) {
            cardBalanceText.setText(
                    String.format("%s\n%s", cardItem.getLabelUz(), cardItem.getValue()));


        }
    }
}
