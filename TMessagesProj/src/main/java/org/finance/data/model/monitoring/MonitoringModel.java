package org.finance.data.model.monitoring;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MonitoringModel {

    @SerializedName("result")
    private List<Result> result;

    // Getter
    public List<Result> getResult() {
        return result;
    }

    public static class Result {

        @SerializedName("id")
        private int id;

        @SerializedName("sender")
        private String sender;

        @SerializedName("receiver")
        private String receiver;

        @SerializedName("total_amount")
        private double totalAmount;

        @SerializedName("transaction_type")
        private int transactionType;

        @SerializedName("description")
        private String description;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("dated_at")
        private String datedAt;

        @SerializedName("currency")
        private int currency;
        @SerializedName("state")
        private int state;

        @SerializedName("transaction_commission")
        private double transactionCommission;

        // Getters
        public int getId() {
            return id;
        }

        public String getSender() {
            return sender;
        }

        public String getReceiver() {
            return receiver;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public int getTransactionType() {
            return transactionType;
        }

        public String getDescription() {
            return description;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getDatedAt() {
            return datedAt;
        }

        public int getCurrency() {
            return currency;
        }

        public int getState() {
            return state;
        }

        public double getTransactionCommission() {
            return transactionCommission;
        }
    }

}
