package org.finance.data.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import org.finance.data.model.get_card.GetCardBalanceModel;
import org.finance.data.model.get_card.GetCardBalanceWithCards;

import java.util.List;

@Dao
public interface CardsDao {
    @Transaction
    @Query("SELECT * FROM card_balance")
    LiveData<GetCardBalanceWithCards> getAllCardBalancesWithCards();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertBalanceModel(GetCardBalanceModel balanceModel);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCards(List<GetCardBalanceModel.Cards> cards);
    @Query("SELECT * FROM cards")
    List<GetCardBalanceModel.Cards> getCardsList();
    @Query("DELETE FROM card_balance")
    void clearAllBalances();
    @Query("DELETE FROM cards")
    void clearAllCards();
    @Query("DELETE FROM cards WHERE id = :cardId")
    void deleteCardById(long cardId);
}
