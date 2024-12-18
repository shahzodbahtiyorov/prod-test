package org.finance.data.model.get_card;

import com.google.gson.annotations.SerializedName;

/** @noinspection ALL*/
public class CardInfoModel {

    @SerializedName("result")
    private Result result;

    @SerializedName("image_back")
    private String imageBack;

    @SerializedName("image_icon")
    private String imageIcon;

    // Getters and Setters
    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getImageBack() {
        return imageBack;
    }

    public void setImageBack(String imageBack) {
        this.imageBack = imageBack;
    }

    public String getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(String imageIcon) {
        this.imageIcon = imageIcon;
    }

    // Inner class for the "result" field
    public static class Result {

        @SerializedName("is_credit")
        private boolean isCredit;

        @SerializedName("card_number")
        private String cardNumber;

        @SerializedName("owner")
        private String owner;

        @SerializedName("bank")
        private String bank;

        @SerializedName("state")
        private int state;

        @SerializedName("phone")
        private String phone;

        @SerializedName("limit")
        private Limit limit;

        @SerializedName("pc_type")
        private int pcType;

        // Getters and Setters for Result class
        public boolean isCredit() {
            return isCredit;
        }

        public void setCredit(boolean credit) {
            isCredit = credit;
        }

        public String getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getBank() {
            return bank;
        }

        public void setBank(String bank) {
            this.bank = bank;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public Limit getLimit() {
            return limit;
        }

        public void setLimit(Limit limit) {
            this.limit = limit;
        }

        public int getPcType() {
            return pcType;
        }

        public void setPcType(int pcType) {
            this.pcType = pcType;
        }
    }


    public static class Limit {

    }
}
