package org.finance.data.model.paynet.chekcomunal;

import com.google.gson.annotations.SerializedName;

public class Host {

    @SerializedName("host")
    private String host;

    @SerializedName("timestamp")
    private String timestamp;

    // Getters and Setters
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
