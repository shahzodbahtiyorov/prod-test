package org.finance.data.model.paynet.service;

import com.google.gson.annotations.SerializedName;

/** @noinspection ALL*/
public class FieldValues {
    @SerializedName("id")
    private Integer id;
    @SerializedName("titleRu")
    private String titleRu;
    @SerializedName("titleUz")
    private String titleUz;

    public FieldValues() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitleRu() {
        return titleRu;
    }

    public void setTitleRu(String titleRu) {
        this.titleRu = titleRu;
    }

    public String getTitleUz() {
        return titleUz;
    }

    public void setTitleUz(String titleUz) {
        this.titleUz = titleUz;
    }
}

