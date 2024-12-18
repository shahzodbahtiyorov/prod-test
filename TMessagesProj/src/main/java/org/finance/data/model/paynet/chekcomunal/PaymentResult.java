package org.finance.data.model.paynet.chekcomunal;

import com.google.gson.annotations.SerializedName;

/** @noinspection ALL*/
public class PaymentResult{

    @SerializedName("sender")
    private Object sender; // Nullable

    @SerializedName("receiver")
    private Receiver receiver;

    @SerializedName("support_info")
    private Object supportInfo; // Nullable

    @SerializedName("state")
    private int state;

    @SerializedName("amount")
    private double amount;

    @SerializedName("description")
    private String description;

    @SerializedName("tr_id")
    private long trId;

    @SerializedName("commission")
    private double commission;

    @SerializedName("cheque")
    private Cheque cheque;

    // Getters and Setters
    public Object getSender() {
        return sender;
    }

    public void setSender(Object sender) {
        this.sender = sender;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public Object getSupportInfo() {
        return supportInfo;
    }

    public void setSupportInfo(Object supportInfo) {
        this.supportInfo = supportInfo;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTrId() {
        return trId;
    }

    public void setTrId(long trId) {
        this.trId = trId;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }

    public Cheque getCheque() {
        return cheque;
    }

    public void setCheque(Cheque cheque) {
        this.cheque = cheque;
    }
}
