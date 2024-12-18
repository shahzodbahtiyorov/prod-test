package org.finance.data.model.get_card;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "card_balance")
public class GetCardBalanceModel {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @SerializedName("total_balance")
    private double totalBalance;

    @Ignore
    @SerializedName("cards")
    private List<Cards> cards; // Ignored here because it's a separate entity

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(double totalBalance) {
        this.totalBalance = totalBalance;
    }

    public List<Cards> getCards() {
        return cards;
    }

    public void setCards(List<Cards> cards) {
        this.cards = cards;
    }

    @Entity(
            tableName = "cards",
            foreignKeys = @ForeignKey(
                    entity = GetCardBalanceModel.class,
                    parentColumns = "id",
                    childColumns = "balanceModelId",
                    onDelete = ForeignKey.CASCADE
            ),
            indices = {@Index(value = "balanceModelId")}
    )
    public static class Cards {

        @PrimaryKey(autoGenerate = true)
        private int id;

        @SerializedName("card_uuid")
        private String cardUuid;

        @SerializedName("card_name")
        private String cardName;

        @SerializedName("balance")
        private double balance;

        @SerializedName("card_owner_name")
        private String cardOwnerName;

        @SerializedName("bank")
        private String bank;

        @SerializedName("pc_type")
        private int pcType;

        @SerializedName("active")
        private boolean active;

        @SerializedName("images")
        private String images;

        @SerializedName("image_log")
        private String imageLog;

        @SerializedName("card_number")
        private String cardNumber;

        @SerializedName("blocked")
        private boolean blocked;

        // Foreign key to reference GetCardBalanceModel
        private int balanceModelId;

        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCardUuid() {
            return cardUuid;
        }

        public void setCardUuid(String cardUuid) {
            this.cardUuid = cardUuid;
        }

        public String getCardName() {
            return cardName;
        }

        public void setCardName(String cardName) {
            this.cardName = cardName;
        }

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        public String getCardOwnerName() {
            return cardOwnerName;
        }

        public void setCardOwnerName(String cardOwnerName) {
            this.cardOwnerName = cardOwnerName;
        }

        public String getBank() {
            return bank;
        }

        public void setBank(String bank) {
            this.bank = bank;
        }

        public int getPcType() {
            return pcType;
        }

        public void setPcType(int pcType) {
            this.pcType = pcType;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public String getImages() {
            return images;
        }

        public void setImages(String images) {
            this.images = images;
        }

        public String getImageLog() {
            return imageLog;
        }

        public void setImageLog(String imageLog) {
            this.imageLog = imageLog;
        }

        public String getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        public boolean isBlocked() {
            return blocked;
        }

        public void setBlocked(boolean blocked) {
            this.blocked = blocked;
        }

        public int getBalanceModelId() {
            return balanceModelId;
        }

        public void setBalanceModelId(int balanceModelId) {
            this.balanceModelId = balanceModelId;
        }
    }
}
