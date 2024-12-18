package org.finance.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.finance.data.model.monitoring.MonitoringModel;
import org.finance.ui.adapters.src.CardTransferChecker;
import org.finance.ui.monitoring.MonitoringInfoFragment;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @noinspection ALL
 */
public class MonitoringAdapter extends RecyclerView.Adapter<MonitoringAdapter.DateViewHolder> {
    private final BaseFragment baseFragment;
    private final Context context;
    private final Map<String, List<MonitoringModel.Result>> groupedTransactions = new HashMap<>();
    private final List<String> dateHeaders = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public MonitoringAdapter(Context context, List<MonitoringModel.Result> transactions, BaseFragment baseFragment) {
        this.context = context;
        this.baseFragment = baseFragment;
        groupTransactionsByDate(transactions);
    }

    private void groupTransactionsByDate(List<MonitoringModel.Result> transactions) {
        for (MonitoringModel.Result transaction : transactions) {
            String date = transaction.getCreatedAt();
            if (!groupedTransactions.containsKey(date)) {
                groupedTransactions.put(date, new ArrayList<>());
                dateHeaders.add(date);
            }
            groupedTransactions.get(date).add(transaction);
        }
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction_group, parent, false);
        return new DateViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        String date = dateHeaders.get(position);
        List<MonitoringModel.Result> transactionsForDate = groupedTransactions.get(date);
        CardTransferChecker cardTransferChecker = new CardTransferChecker();
        holder.dateText.setText(date);
        holder.transactionContainer.removeAllViews();

        for (MonitoringModel.Result transaction : transactionsForDate) {
            View transactionView = LayoutInflater.from(context).inflate(R.layout.transaction_item, holder.transactionContainer, false);

            TextView trName = transactionView.findViewById(R.id.transaction_name);
            TextView trAmount = transactionView.findViewById(R.id.transaction_amount);
            TextView trTime = transactionView.findViewById(R.id.transaction_time);
            TextView trStatus = transactionView.findViewById(R.id.transaction_status);
            ImageView trState = transactionView.findViewById(R.id.status_icon);
            View dashLine = transactionView.findViewById(R.id.dash_line);
            if (transactionsForDate.indexOf(transaction) == transactionsForDate.size() - 1){
                dashLine.setVisibility(View.GONE);
            }

            trName.setText(transaction.getDescription());
            trAmount.setText((transaction.getTransactionType() == 1 ? "-" : "+") + transaction.getTotalAmount() + " UZS");
            trAmount.setTextColor(Color.parseColor(transaction.getTransactionType() == 1 ? "#D92D20" : "#27AE60"));
            trTime.setText(transaction.getDatedAt());
            trStatus.setText(cardTransferChecker.getTransferType(transaction.getDescription()));

            if (transaction.getState()==4){
                trAmount.setTextColor(Color.parseColor("#27AE60"));
                trState.setImageDrawable((ContextCompat.getDrawable(context,R.drawable.circle_chek_icon)));
            } else  if (transaction.getState()==21){
                trAmount.setTextColor(Color.parseColor("#D92D20"));
                trState.setImageDrawable((ContextCompat.getDrawable(context,R.drawable.circle_xmark_icon)));
            } else {
                trAmount.setTextColor(Color.BLACK);
                trState.setImageDrawable((ContextCompat.getDrawable(context,R.drawable.circle_exclamation_icon)));
            }

            transactionView.setOnClickListener(v -> {
                baseFragment.presentFragment(new MonitoringInfoFragment(transaction));
            });

            holder.transactionContainer.addView(transactionView);
        }
    }

    @Override
    public int getItemCount() {
        return dateHeaders.size();
    }

    static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;
        LinearLayout transactionContainer;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateText);
            transactionContainer = itemView.findViewById(R.id.transactionContainer);
        }
    }

}
