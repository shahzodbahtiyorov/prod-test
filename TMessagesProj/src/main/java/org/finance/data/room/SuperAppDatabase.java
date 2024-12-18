package org.finance.data.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import org.finance.data.model.get_card.GetCardBalanceModel;
import org.finance.data.model.get_card.GetCardBalanceModel.Cards;

@Database(entities = {GetCardBalanceModel.class, Cards.class}, version = 2, exportSchema = false)
public abstract class SuperAppDatabase extends RoomDatabase {

    public abstract CardsDao cardsDao();
}
