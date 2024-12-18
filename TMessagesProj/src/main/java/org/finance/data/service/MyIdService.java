package org.finance.data.service;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.util.Log;
import com.google.gson.JsonObject;

import org.finance.data.model.myId.MyIdInfoModel;
import org.finance.data.retrofit.ApiInterface;
import org.finance.data.retrofit.RetrofitClient;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyIdService {
    private final ApiInterface apiService;

    public MyIdService(Context context) {

        TLRPC.User user = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
        apiService = RetrofitClient.getClient(context, true).create(ApiInterface.class);
    }

        public void identifyUser(ApiCallback<JsonObject> callback, JsonObject object) {
        Call<JsonObject> call = apiService.myIdIdentification(object);

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

    public void getMyIdInfo(ApiCallback<MyIdInfoModel> callback) {
        try {
            Call<MyIdInfoModel> response = apiService.getMyIdInfo();
            response.enqueue( new Callback<MyIdInfoModel>(){
                @Override
                public void onResponse(@NonNull Call<MyIdInfoModel> call, @NonNull Response<MyIdInfoModel> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    }
                    else if(response.code()==404){
                        Log.d("myID error: ",response.message());
                        callback.onFailure("Not Found");
                    }else {
                        callback.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MyIdInfoModel> call, @NonNull Throwable t) {
                    Log.e("rb_105", t.getMessage());
                    callback.onFailure(t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }
}
