package org.finance.ui.monitoring;

import org.finance.data.model.monitoring.MonitoringModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TransactionGroupHelper {

    public static List<Object> groupTransactionsByDate(List<MonitoringModel.Result> transactions) {
        Map<String, List<MonitoringModel.Result>> groupedMap = new LinkedHashMap<>();

        for (MonitoringModel.Result transaction : transactions) {
            String date = transaction.getCreatedAt();
            if (!groupedMap.containsKey(date)) {
                groupedMap.put(date, new ArrayList<>());
            }
            Objects.requireNonNull(groupedMap.get(date)).add(transaction);
        }

        List<Object> groupedTransactions = new ArrayList<>();
        for (Map.Entry<String, List<MonitoringModel.Result>> entry : groupedMap.entrySet()) {
            groupedTransactions.add(entry.getKey());
            groupedTransactions.addAll(entry.getValue());
        }

        return groupedTransactions;
    }
}

