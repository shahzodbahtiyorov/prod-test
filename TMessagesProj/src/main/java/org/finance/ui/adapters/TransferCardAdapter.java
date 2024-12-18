package org.finance.ui.adapters;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import org.finance.data.model.get_card.GetCardBalanceModel;
import org.finance.helpers.SuperAppFormatters;
import org.telegram.messenger.R;
import java.util.List;

/** @noinspection ALL*/
public  class TransferCardAdapter extends RecyclerView.Adapter<TransferCardAdapter.CardViewHolder> {
    private final Context context;
    private final List<GetCardBalanceModel.Cards> cardList;
    private int selectedPosition ;
    private final OnCardSelectedListener listener;
    public TransferCardAdapter(Context context, List<GetCardBalanceModel.Cards> cardList,TransferCardAdapter.OnCardSelectedListener listener,int id) {
        this.context = context;
        this.cardList = cardList;
        this.listener = listener;
        this.selectedPosition=id;
    }
    public interface OnCardSelectedListener {
        void onCardSelected(GetCardBalanceModel.Cards card);
    }
    @NonNull
    @Override
    public TransferCardAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.monitoring_card_item, parent, false);
        return new TransferCardAdapter.CardViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull TransferCardAdapter.CardViewHolder holder, @SuppressLint("RecyclerView") int position) {
        GetCardBalanceModel.Cards card = cardList.get(position);
        holder.cardAmount.setText(String.format("%s", card.getBalance()));
        holder.cardDetails.setText(SuperAppFormatters.formatCardInfo(card.getCardNumber(), card.getCardOwnerName()));
        if (card.getPcType() == 3) {
            holder.cardAmount.setText(SuperAppFormatters.formatWithSpaces(card.getBalance()));
            holder.cardIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.humo_card));
        } else if (card.getPcType() == 1) {
            holder.cardAmount.setText(SuperAppFormatters.formatWithSpaces(card.getBalance()));
            holder.cardIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_uzcard));
        } else if (card.getPcType() == 2) {
            holder.cardAmount.setText("***");
            holder.cardIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_tkb_card));
        }
       if (selectedPosition==card.getId()){
           holder.checkIcon.setVisibility(View.VISIBLE);
       }else {
           holder.checkIcon.setVisibility(View.GONE);
       }
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCardSelected(card);
            }
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
}

