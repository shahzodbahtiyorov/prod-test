package org.finance.data.model.paynet.service;

import java.util.List;
import com.google.gson.annotations.SerializedName;

/** @noinspection ALL*/
public class PaymentServiceModel {
    @SerializedName("id")
    private Integer id;
    @SerializedName("title")
    private String title;
    @SerializedName("title_short")
    private String titleShort;
    @SerializedName("services")
    private List<Services> services;

    public PaymentServiceModel() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleShort() {
        return titleShort;
    }

    public void setTitleShort(String titleShort) {
        this.titleShort = titleShort;
    }

    public List<Services> getServices() {
        return services;
    }

    public void setServices(List<Services> services) {
        this.services = services;
    }
}
