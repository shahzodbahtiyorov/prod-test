package org.finance.data.model.paynet.chekcomunal;

import com.google.gson.annotations.SerializedName;

/** @noinspection ALL*/
public class Receiver {

    @SerializedName("id")
    private long id;

    @SerializedName("service_id")
    private int serviceId;

    @SerializedName("time")
    private double time;

    @SerializedName("fields")
    private Fields fields;

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public Fields getFields() {
        return fields;
    }

    public void setFields(Fields fields) {
        this.fields = fields;
    }
}
