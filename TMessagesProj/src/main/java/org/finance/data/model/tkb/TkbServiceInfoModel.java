package org.finance.data.model.tkb;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/** @noinspection ALL*/
public class TkbServiceInfoModel {

    @SerializedName("rate")
    private Rate rate;

    @SerializedName("commission")
    private Commission commission;

    // Getters
    public Rate getRate() {
        return rate;
    }

    public Commission getCommission() {
        return commission;
    }

    public static class Rate {
        @SerializedName("sell")
        private double sell;

        @SerializedName("buy")
        private double buy;

        // Getters
        public double getSell() {
            return sell;
        }

        public double getBuy() {
            return buy;
        }
    }

    public static class Commission {
        @SerializedName("transfer")
        private List<Transfer> transfer;

        // Getters
        public List<Transfer> getTransfer() {
            return transfer;
        }
    }

    public static class Transfer {
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
}
