package org.finance.ui.adapters.src;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;

import java.util.HashMap;
import java.util.Map;

public class CardTransferChecker {
    private static final Map<String, Integer> TRANSFER_TYPES = new HashMap<>();
    static {
        TRANSFER_TYPES.put("UZCARD TO UZCARD", 0);
        TRANSFER_TYPES.put("UZCARD TO HUMO", 1);
        TRANSFER_TYPES.put("HUMO TO UZCARD", 2);
        TRANSFER_TYPES.put("HUMO TO HUMO", 3);
        TRANSFER_TYPES.put("UZCARD TO PAYNET", 4);
        TRANSFER_TYPES.put("HUMO TO PAYNET", 5);
        TRANSFER_TYPES.put("RF TO UZB", 6);
        TRANSFER_TYPES.put("UZB TO RF", 7);
    }
    public static String getTransferType(String transfer) {
        Integer transferCode = TRANSFER_TYPES.get(transfer.toUpperCase());
        if (transferCode == null) {
            return "Noto'g'ri format!";
        }
        if (transferCode == 0 || transferCode == 1 || transferCode == 2 || transferCode == 3||transferCode == 6||transferCode == 7) {
            return LocaleController.getString(R.string.translation);
        } else if (transferCode == 4 || transferCode == 5) {
            return LocaleController.getString(R.string.payment);
        }
        return "Noma'lum tur!";
    }

}

