package org.finance.data.service;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


import org.finance.data.model.add_card.AddCardInfoModel;
import org.finance.data.retrofit.ApiInterface;
import org.finance.data.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** @noinspection ALL*/
public class AddCardService {
    private ApiInterface apiService;
    private Context context;

    public AddCardService(Context context) {
        this.context = context;
        apiService = RetrofitClient.getClient(context,true).create(ApiInterface.class);
    }
    public void addCardStepOne(ApiCallback<JsonObject> callback,JsonObject object) {
        Call<JsonObject> call = apiService.addCardStepOne(object);

        try {
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onFailure(response.message());
                    }
                }
                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    callback.onFailure( t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure( e.getMessage());
        }
    }
    public void addCardStepTwo(ApiCallback<JsonObject> callback,JsonObject object) {
        Call<JsonObject> call = apiService.addCardStepTwo(object);

        try {
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onFailure( response.message());
                    }
                }
                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    callback.onFailure(t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure( e.getMessage());
        }
    }
    public void addHumoCardStepTwo(ApiCallback<JsonObject> callback,JsonObject object) {
        Call<JsonObject> call = apiService.addHumoCardStepTwo(object);
        try {
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onFailure( response.message());
                    }
                }
                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    callback.onFailure( t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure( e.getMessage());
        }
    }
    public void addCardInfo(ApiCallback<AddCardInfoModel> callback,JsonObject object) {
        Call<AddCardInfoModel> call = apiService.addcardInfo(object);

        try {
            call.enqueue(new Callback<AddCardInfoModel>() {
                @Override
                public void onResponse(@NonNull Call<AddCardInfoModel> call, @NonNull Response<AddCardInfoModel> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());

                    }else if(response.code()==400){
                        callback.onFailure("Karta raqam oldin qo'shilgan");
                    } else {
                        callback.onFailure( response.message());
                    }
                }
                @Override
                public void onFailure(@NonNull Call<AddCardInfoModel> call, @NonNull Throwable t) {
                    callback.onFailure(t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure( e.getMessage());
        }
    }
}
