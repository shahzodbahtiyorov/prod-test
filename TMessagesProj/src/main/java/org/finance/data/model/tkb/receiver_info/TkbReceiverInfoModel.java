package org.finance.data.model.tkb.receiver_info;

import com.google.gson.annotations.SerializedName;
/** @noinspection ALL*/
public class TkbReceiverInfoModel {

    @SerializedName("bank")
    private String bank;

    @SerializedName("bin")
    private String bin;

    @SerializedName("provider")
    private String provider;

    // Getter methods
    public String getBank() {
        return bank;
    }

    public String getBin() {
        return bin;
    }

    public String getProvider() {
        return provider;
    }
}
