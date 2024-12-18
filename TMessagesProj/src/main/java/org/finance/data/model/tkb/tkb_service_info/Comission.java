package org.finance.data.model.tkb.tkb_service_info;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/** @noinspection ALL*/
public class Comission {

    @SerializedName("transfer")
    private List<Transfer> transfer;
    @SerializedName("uz-rf-transfer")
    private List<Transfer> uzRfTransfer;

    // Getter for 'transfer'
    public List<Transfer> getTransfer() {
        return transfer;
    }
    public List<Transfer> getUzRfTransferTransfer() {
        return uzRfTransfer;
    }
}

