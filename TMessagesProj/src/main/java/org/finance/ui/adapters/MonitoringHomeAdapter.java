package org.finance.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import org.finance.data.model.monitoring.MonitoringModel;
import org.finance.ui.adapters.src.CardTransferChecker;
import org.finance.ui.monitoring.MonitoringInfoFragment;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;

import java.util.List;

/** @noinspection ALL */
public class MonitoringHomeAdapter extends RecyclerView.Adapter<MonitoringHomeAdapter.CardViewHolder> {
    private List<MonitoringModel.Result> result;
    private final Context context;
    private static BaseFragment baseFragment;


    public MonitoringHomeAdapter(Context context,List<MonitoringModel.Result> cardList,BaseFragment baseFragment) {
        this.result = cardList;
        this.context = context;
        this.baseFragment=baseFragment;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.monitoring_item_layout, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        MonitoringModel.Result cardItem = result.get(position);
        holder.bind(cardItem, context, position, getItemCount());
    }


    @Override
    public int getItemCount() {
        return result.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateCardList(List<MonitoringModel.Result> newCardList) {
        this.result = newCardList;
        notifyDataSetChanged();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        private final TextView transaction_name;
        private final TextView transaction_amount;
        private final TextView transaction_time;
        private final TextView transaction_status;
        private final ImageView status_icon;
        private View dashedLine;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            status_icon = itemView.findViewById(R.id.status_icon);
            dashedLine = itemView.findViewById(R.id.dashed_line);

            transaction_status = itemView.findViewById(R.id.transaction_status);
            transaction_name = itemView.findViewById(R.id.transaction_name);
            transaction_amount = itemView.findViewById(R.id.transaction_amount);
            transaction_time = itemView.findViewById(R.id.transaction_time);
        }

        @SuppressLint("DefaultLocale")
        public void bind(MonitoringModel.Result transaction, Context context, int position, int itemCount) {
            CardTransferChecker cardTransferChecker = new CardTransferChecker();
            transaction_time.setText(transaction.getDatedAt());
            transaction_name.setText(transaction.getDescription());

            if (transaction.getTransactionType() == 1) {
                transaction_amount.setText("-" + transaction.getTotalAmount()
                        + (transaction.getCurrency() == 860 ? " UZS" : " RUB"));
            } else if (transaction.getTransactionType() == 2) {
                transaction_amount.setText("+" + transaction.getTotalAmount()
                        + (transaction.getCurrency() == 860 ? " UZS" : " RUB"));
            }

            transaction_status.setText(cardTransferChecker.getTransferType(transaction.getDescription()));

            if (transaction.getState() == 4) {
                transaction_amount.setTextColor(Color.parseColor("#27AE60"));
                status_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.circle_chek_icon));
            } else if (transaction.getState() == 21) {
                transaction_amount.setTextColor(Color.parseColor("#D92D20"));
                status_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.circle_xmark_icon));
            } else {
                transaction_amount.setTextColor(Color.BLACK);
                status_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.circle_exclamation_icon));
            }

            if (position == itemCount - 1) {
                dashedLine.setVisibility(View.GONE);
            } else {
                dashedLine.setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(v -> {
                baseFragment.presentFragment(new MonitoringInfoFragment(transaction));
            });
        }


    }}
