package org.finance.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import org.finance.data.model.get_card.TransferCardModel;
import org.finance.ui.p2p.local.TransferAmountFragment;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;

import java.util.List;

/** @noinspection ALL*/
public class SaveCardAdapter extends RecyclerView.Adapter<SaveCardAdapter.SaveCardViewHolder> {

    private final Context context;
    private final List<TransferCardModel> cardList;
    private  final BaseFragment baseFragment;
    private final int id;


    public SaveCardAdapter(Context context, List<TransferCardModel> cardList ,BaseFragment baseFragment,int id) {
        this.baseFragment=baseFragment;
        this.context = context;
        this.cardList = cardList;
        this.id=id;

    }
    public void updateList(List<TransferCardModel> newCardList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return cardList.size();
            }

            @Override
            public int getNewListSize() {
                return newCardList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return cardList.get(oldItemPosition).getCardNumber().equals(newCardList.get(newItemPosition).getCardNumber());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return cardList.get(oldItemPosition).equals(newCardList.get(newItemPosition));
            }
        });


        cardList.clear();
        cardList.addAll(newCardList);
        Log.d("UpdateList", "New List Size: " + newCardList.size());

        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public SaveCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.save_card_item, parent, false);
        return new SaveCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SaveCardViewHolder holder, int position) {
        TransferCardModel card = cardList.get(position);

        holder.accountBalance.setText(card.getCardMask());
        holder.cardDetails.setText(card.getCardName());

        if (card.getType() == 1) {
            holder.cardLogo.setImageResource(R.drawable.ic_uzcard);
        } else if (card.getType() == 3) {
            holder.cardLogo.setImageResource(R.drawable.humo_card);
        }else if(card.getType()==2){
            holder.cardLogo.setImageResource(R.drawable.ic_tkb_card);

        }

        holder.accountBalanceContainer.setOnClickListener(v ->{
            baseFragment.presentFragment(new TransferAmountFragment(id,card));
        });
    }

    @Override
    public int getItemCount() {

        return cardList.size();
    }

    public static class SaveCardViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout accountBalanceContainer;
        private final ImageView cardLogo;
        private final TextView accountBalance;
        private final TextView cardDetails;
        private final ImageView arrowIcon;

        public SaveCardViewHolder(@NonNull View itemView) {
            super(itemView);
            accountBalanceContainer = itemView.findViewById(R.id.accountBalanceContainer);
            cardLogo = itemView.findViewById(R.id.cardLogo);
            accountBalance = itemView.findViewById(R.id.accountBalance);
            cardDetails = itemView.findViewById(R.id.cardDetails);
            arrowIcon = itemView.findViewById(R.id.arrowIcon);
        }
    }



}

