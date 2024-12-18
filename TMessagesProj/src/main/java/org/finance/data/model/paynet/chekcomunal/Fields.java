package org.finance.data.model.paynet.chekcomunal;

import com.google.gson.annotations.SerializedName;

/** @noinspection ALL*/
public class Fields {

    @SerializedName("code")
    private String code;

    @SerializedName("licshet")
    private String licshet;

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLicshet() {
        return licshet;
    }

    public void setLicshet(String licshet) {
        this.licshet = licshet;
    }
}
