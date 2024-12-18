package org.finance.data.model.paynet.chekcomunal;

import com.google.gson.annotations.SerializedName;

/** @noinspection ALL*/
public class PaymentCommunalGasModel {

    @SerializedName("jsonrpc")
    private String jsonrpc;

    @SerializedName("result")
    private PaymentResult result;

    @SerializedName("id")
    private String id;

    @SerializedName("status")
    private boolean status;

    @SerializedName("origin")
    private String origin;

    @SerializedName("host")
    private Host host;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public PaymentResult getResult() {
        return result;
    }

    public void setResult(PaymentResult result) {
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }
}

