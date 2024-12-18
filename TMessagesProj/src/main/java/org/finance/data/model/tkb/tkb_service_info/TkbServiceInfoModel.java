package org.finance.data.model.tkb.tkb_service_info;

import com.google.gson.annotations.SerializedName;

/** @noinspection ALL*/
public class TkbServiceInfoModel {
    @SerializedName("rate")
    private Rate rate;

    @SerializedName("commission")
    private Comission commission;


    public Rate getRate() {
        return rate;
    }
    public Comission getCommission() {
        return commission;
    }
}
