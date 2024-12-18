package org.finance.data.model.get_card;

import androidx.room.Embedded;
import androidx.room.Relation;
import java.util.List;

public class GetCardBalanceWithCards {

    @Embedded
    public GetCardBalanceModel balance;

    @Relation(
            parentColumn = "id",
            entityColumn = "balanceModelId"
    )
    public List<GetCardBalanceModel.Cards> cards;
}
