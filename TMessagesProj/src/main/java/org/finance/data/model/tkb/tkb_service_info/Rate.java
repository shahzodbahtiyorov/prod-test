package org.finance.data.model.tkb.tkb_service_info;

import com.google.gson.annotations.SerializedName;

/** @noinspection ALL*/
public class Rate {

    @SerializedName("sell")
    private double sell;

    @SerializedName("buy")
    private double buy;

    // Getter for 'sell'
    public double getSell() {
        return sell;
    }

    // Getter for 'buy'
    public double getBuy() {
        return buy;
    }
}
