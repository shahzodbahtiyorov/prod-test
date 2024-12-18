package org.finance.data.model.paynet.provider;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ProvidersResponse {
    @SerializedName("providers")
    private ArrayList<ProviderModel> providers;

    public ArrayList<ProviderModel> getProviders() {
        return providers;
    }

    public void setProviders(ArrayList<ProviderModel> providers) {
        this.providers = providers;
    }
}
