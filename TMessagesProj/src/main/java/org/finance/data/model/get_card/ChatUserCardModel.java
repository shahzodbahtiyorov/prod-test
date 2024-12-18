package org.finance.data.model.get_card;

import com.google.gson.annotations.SerializedName;

/** @noinspection ALL*/
public class ChatUserCardModel {

    @SerializedName("id")
    private int id;

    @SerializedName("card_owner_name")
    private String cardOwnerName;

    @SerializedName("card_number")
    private String cardNumber;

    @SerializedName("pc_type")
    private int pcType;

    // getter & setters
    public int getId() {
        return id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardOwnerName() {
        return cardOwnerName;
    }

    public int getPcType() {
        return pcType;
    }

    public void setPcType(int pcType) {
        this.pcType = pcType;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setCardOwnerName(String cardOwnerName) {
        this.cardOwnerName = cardOwnerName;
    }
}
