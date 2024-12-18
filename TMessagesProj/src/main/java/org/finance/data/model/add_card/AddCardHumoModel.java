package org.finance.data.model.add_card;
import com.google.gson.annotations.SerializedName;

public class AddCardHumoModel {

    @SerializedName("succes")
    private Success success;

    @SerializedName("result")
    private Result result;

    // Getters and Setters
    public Success getSuccess() {
        return success;
    }

    public void setSuccess(Success success) {
        this.success = success;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public static class Success {
        @SerializedName("uz")
        private String uz;

        @SerializedName("ru")
        private String ru;

        @SerializedName("en")
        private String en;

        // Getters and Setters
        public String getUz() {
            return uz;
        }

        public void setUz(String uz) {
            this.uz = uz;
        }

        public String getRu() {
            return ru;
        }

        public void setRu(String ru) {
            this.ru = ru;
        }

        public String getEn() {
            return en;
        }

        public void setEn(String en) {
            this.en = en;
        }
    }

    public static class Result {
        @SerializedName("card_number")
        private String cardNumber;

        @SerializedName("expire")
        private String expire;

        @SerializedName("mask")
        private String mask;

        @SerializedName("status")
        private int status;

        @SerializedName("state")
        private int state;

        @SerializedName("owner")
        private String owner;

        @SerializedName("bank")
        private String bank;

        @SerializedName("account")
        private String account;

        @SerializedName("is_corporate")
        private boolean isCorporate;

        @SerializedName("phone")
        private String phone;

        @SerializedName("sms")
        private boolean sms;

        @SerializedName("balance")
        private int balance;

        @SerializedName("pc_type")
        private int pcType;

        // Getters and Setters
        public String getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        public String getExpire() {
            return expire;
        }

        public void setExpire(String expire) {
            this.expire = expire;
        }

        public String getMask() {
            return mask;
        }

        public void setMask(String mask) {
            this.mask = mask;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
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

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public boolean isCorporate() {
            return isCorporate;
        }

        public void setCorporate(boolean corporate) {
            isCorporate = corporate;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public boolean isSms() {
            return sms;
        }

        public void setSms(boolean sms) {
            this.sms = sms;
        }

        public int getBalance() {
            return balance;
        }

        public void setBalance(int balance) {
            this.balance = balance;
        }

        public int getPcType() {
            return pcType;
        }

        public void setPcType(int pcType) {
            this.pcType = pcType;
        }
    }
}

