package org.finance.data.model.add_card;

import com.google.gson.annotations.SerializedName;

public class AddCardInfoModel {

    @SerializedName("result")
    private CardDetails result;

    @SerializedName("image_back")
    private String imageBack;

    @SerializedName("image_icon")
    private String imageIcon;

    public CardDetails getResult() {
        return result;
    }

    public void setResult(CardDetails result) {
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

    // CardDetails ichki klassi
    public static class CardDetails {

        @SerializedName("card_number")
        private String cardNumber;

        @SerializedName("owner")
        private String owner;

        @SerializedName("is_corporate")
        private boolean isCorporate;

        @SerializedName("bank")
        private String bank;

        @SerializedName("state")
        private int state;

        @SerializedName("pc_type")
        private int pcType;

        // Getter va setter metodlari
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

        public boolean isCorporate() {
            return isCorporate;
        }

        public void setCorporate(boolean corporate) {
            isCorporate = corporate;
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

        public int getPcType() {
            return pcType;
        }

        public void setPcType(int pcType) {
            this.pcType = pcType;
        }
    }
}

