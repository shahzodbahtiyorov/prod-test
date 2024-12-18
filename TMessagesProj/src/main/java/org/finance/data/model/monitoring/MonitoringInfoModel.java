package org.finance.data.model.monitoring;

import com.google.gson.annotations.SerializedName;

public class MonitoringInfoModel {

    @SerializedName("id")
    private int id;

    @SerializedName("debit_tr_id")
    private String debitTrId;

    @SerializedName("db_amount")
    private double dbAmount;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("sender")
    private String sender;

    @SerializedName("db_state")
    private int dbState;

    @SerializedName("db_description")
    private String dbDescription;

    // Getters
    public int getId() {
        return id;
    }

    public String getDebitTrId() {
        return debitTrId;
    }

    public double getDbAmount() {
        return dbAmount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getSender() {
        return sender;
    }

    public int getDbState() {
        return dbState;
    }

    public String getDbDescription() {
        return dbDescription;
    }
}

