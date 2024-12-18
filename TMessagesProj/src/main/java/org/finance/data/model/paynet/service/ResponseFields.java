package org.finance.data.model.paynet.service;

import com.google.gson.annotations.SerializedName;

/** @noinspection ALL*/
public class ResponseFields {
    @SerializedName("fieldName")
    private String fieldName;
    @SerializedName("labelRu")
    private String labelRu;
    @SerializedName("labelUz")
    private String labelUz;
    @SerializedName("order")
    private Integer order;

    public ResponseFields() {}

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getLabelRu() {
        return labelRu;
    }

    public void setLabelRu(String labelRu) {
        this.labelRu = labelRu;
    }

    public String getLabelUz() {
        return labelUz;
    }

    public void setLabelUz(String labelUz) {
        this.labelUz = labelUz;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}

