package org.finance.data.model.paynet.chekcomunal;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Cheque {

    @SerializedName("transaction_id")
    private String transactionId;

    @SerializedName("status_text")
    private String statusText;

    @SerializedName("status")
    private String status;

    @SerializedName("time")
    private long time;

    @SerializedName("response")
    private List<ResponseItem> response;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public List<ResponseItem> getResponse() {
        return response;
    }

    public void setResponse(List<ResponseItem> response) {
        this.response = response;
    }
}
