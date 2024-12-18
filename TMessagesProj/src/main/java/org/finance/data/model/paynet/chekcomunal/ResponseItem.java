package org.finance.data.model.paynet.chekcomunal;

import com.google.gson.annotations.SerializedName;

/** @noinspection ALL*/
public class ResponseItem {

    @SerializedName("key")
    private String key;
    @SerializedName("label_ru")
    private String labelRu;
    @SerializedName("label_uz")
    private String labelUz;
    @SerializedName("value")
    private String value;
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getLabelUz() {
        return labelUz;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
