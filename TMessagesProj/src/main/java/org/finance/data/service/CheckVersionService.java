package org.finance.data.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.JsonObject;

import org.finance.data.model.paynet.category.CategoryResponse;
import org.finance.data.retrofit.ApiInterface;
import org.finance.data.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckVersionService {


    private final ApiInterface apiService;
    private final Context context;

    public CheckVersionService(Context context) {
        this.context = context;
        apiService = RetrofitClient.getClient(context, false).create(ApiInterface.class);
    }

    public void checkVersion(ApiCallback<Boolean> callback) {
        JsonObject body = new JsonObject();
        body.addProperty("version" , "v1");



        Call<JsonObject> call = apiService.checkVersion(body);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    callback.onSuccess(true);
                }
                else {
                    callback.onSuccess(false);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onSuccess(false);
            }
        });
    }
}
