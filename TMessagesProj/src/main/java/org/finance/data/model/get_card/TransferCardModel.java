package org.finance.data.model.get_card;

import com.google.gson.annotations.SerializedName;

public class TransferCardModel {

    @SerializedName("id")
    private int id;

    @SerializedName("card_number")
    private String cardNumber;

    @SerializedName("type")
    private int type;

    @SerializedName("card_mask")
    private String cardMask;

    @SerializedName("pc_type")
    private int pcType;

    @SerializedName("card_name")
    private String cardName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCardMask() {
        return cardMask;
    }

    public void setCardMask(String cardMask) {
        this.cardMask = cardMask;
    }

    public int getPcType() {
        return pcType;
    }

    public void setPcType(int pcType) {
        this.pcType = pcType;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }


}

