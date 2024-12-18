package org.finance.data.service;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;


import org.finance.data.model.get_card.CardInfoModel;
import org.finance.data.model.get_card.TransferCardModel;
import org.finance.data.retrofit.ApiInterface;
import org.finance.data.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** @noinspection ALL*/
public class P2PService {
    private ApiInterface apiService;
    private Context context;

    public P2PService(Context context) {
        this.context = context;
        apiService = RetrofitClient.getClient(context,true).create(ApiInterface.class);
    }
    public void getreceiredCard(ApiCallback<List<TransferCardModel>> callback) {
        Call<List<TransferCardModel>> call = apiService.getReceiverCard();

        try {
            call.enqueue(new Callback<List<TransferCardModel>>() {
                @Override
                public void onResponse(@NonNull Call<List<TransferCardModel>> call, @NonNull Response<List<TransferCardModel>> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onFailure("Xatolik: " + response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<TransferCardModel>> call, @NonNull Throwable t) {
                    callback.onFailure("So'rovda xatolik: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure("So'rov yuborishda xatolik: " + e.getMessage());
        }
    }

    public void cardInfo(ApiCallback<CardInfoModel> callback, JsonObject object) {
        Call<CardInfoModel> call = apiService.cardInfo(object);

        try {
            call.enqueue(new Callback<CardInfoModel>() {
                @Override
                public void onResponse(@NonNull Call<CardInfoModel> call, @NonNull Response<CardInfoModel> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onFailure("Xatolik: " + response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CardInfoModel> call, @NonNull Throwable t) {
                    callback.onFailure("So'rovda xatolik: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure("So'rov yuborishda xatolik: " + e.getMessage());
        }
    }


    public void cardTransfer(ApiCallback<JsonObject> callback, JsonObject object) {
        Call<JsonObject> call = apiService.cardTransferOne(object);

        try {
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onFailure("Xatolik: " + response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    callback.onFailure("So'rovda xatolik: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure("So'rov yuborishda xatolik: " + e.getMessage());
        }
    }
    public void cardTransferTwo(ApiCallback<JsonObject> callback, JsonObject object) {
        Call<JsonObject> call = apiService.cardTransferTwo(object);

        try {
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onFailure("Xatolik: " + response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    callback.onFailure("So'rovda xatolik: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure("So'rov yuborishda xatolik: " + e.getMessage());
        }
    }
    public void saveCard(ApiCallback<JsonObject> callback, JsonObject object) {
        Call<JsonObject> call = apiService.addReceiverCard(object);

        try {
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onFailure("Xatolik: " + response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    callback.onFailure("So'rovda xatolik: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure("So'rov yuborishda xatolik: " + e.getMessage());
        }
    }
}
