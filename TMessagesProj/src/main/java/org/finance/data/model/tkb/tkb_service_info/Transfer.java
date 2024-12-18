package org.finance.data.model.tkb.tkb_service_info;

import com.google.gson.annotations.SerializedName;

/** @noinspection ALL*/
public class Transfer {

    @SerializedName("oper_type")
    private int operType;

    @SerializedName("min")
    private int min;

    @SerializedName("max")
    private int max;

    @SerializedName("max_verified")
    private int maxVerified;

    @SerializedName("com_ub_value")
    private double comUbValue;

    @SerializedName("com_ub_type")
    private String comUbType;

    @SerializedName("com_tc_value")
    private double comTcValue;

    @SerializedName("com_tc_type")
    private String comTcType;

    @SerializedName("com_partner_value")
    private double comPartnerValue;

    @SerializedName("com_partner_type")
    private String comPartnerType;

    // Getters
    public int getOperType() {
        return operType;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getMaxVerified() {
        return maxVerified;
    }

    public double getComUbValue() {
        return comUbValue;
    }

    public String getComUbType() {
        return comUbType;
    }

    public double getComTcValue() {
        return comTcValue;
    }

    public String getComTcType() {
        return comTcType;
    }

    public double getComPartnerValue() {
        return comPartnerValue;
    }

    public String getComPartnerType() {
        return comPartnerType;
    }
}

