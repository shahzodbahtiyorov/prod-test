package org.finance.data.model.paynet.service;

import java.util.List;
import com.google.gson.annotations.SerializedName;

/** @noinspection ALL*/
public class Services {
    @SerializedName("id")
    private Integer id;
    @SerializedName("title_ru")
    private String titleRu;
    @SerializedName("title_uz")
    private String titleUz;
    @SerializedName("type_id")
    private Integer typeId;
    @SerializedName("min_amount")
    private double minAmount;
    @SerializedName("max_amount")
    private double maxAmount;
    @SerializedName("service_price")
    private double servicePrice;
    @SerializedName("service_commission")
    private double serviceCommission;
    @SerializedName("service_commission_sum")
    private double serviceCommissionSum;
    @SerializedName("paynet_commission_sum")
    private double paynetCommissionSum;
    @SerializedName("fields")
    private List<Fields> fields;
    @SerializedName("response_fields")
    private List<ResponseFields> responseFields;
    @SerializedName("services")
    private List<Services> services;

    public Services() {}

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

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Integer minAmount) {
        this.minAmount = minAmount;
    }

    public double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Integer maxAmount) {
        this.maxAmount = maxAmount;
    }

    public double getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(Integer servicePrice) {
        this.servicePrice = servicePrice;
    }

    public double getServiceCommission() {
        return serviceCommission;
    }

    public void setServiceCommission(Integer serviceCommission) {
        this.serviceCommission = serviceCommission;
    }

    public double getServiceCommissionSum() {
        return serviceCommissionSum;
    }

    public void setServiceCommissionSum(Integer serviceCommissionSum) {
        this.serviceCommissionSum = serviceCommissionSum;
    }

    public double getPaynetCommissionSum() {
        return paynetCommissionSum;
    }

    public void setPaynetCommissionSum(Integer paynetCommissionSum) {
        this.paynetCommissionSum = paynetCommissionSum;
    }

    public List<Fields> getFields() {
        return fields;
    }

    public void setFields(List<Fields> fields) {
        this.fields = fields;
    }

    public List<ResponseFields> getResponseFields() {
        return responseFields;
    }

    public void setResponseFields(List<ResponseFields> responseFields) {
        this.responseFields = responseFields;
    }

    public List<Services> getServices() {
        return services;
    }

    public void setServices(List<Services> services) {
        this.services = services;
    }
}
