package org.finance.data.service;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import org.finance.data.model.tkb.card_create.TkbCardCreadeModel;
import org.finance.data.model.tkb.receiver_info.TkbReceiverInfoModel;
import org.finance.data.model.tkb.tkb_service_info.TkbServiceInfoModel;
import org.finance.data.model.tkb.tranfer.TkbTranferCreateModel;
import org.finance.data.retrofit.ApiInterface;
import org.finance.data.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** @noinspection ALL*/
public class TkbService {

    private ApiInterface apiService;
    private Context context;

    public TkbService(Context context) {
        this.context = context;
        apiService = RetrofitClient.getClient(context,true).create(ApiInterface.class);
    }
    public void tkbServiseInfo(ApiCallback<TkbServiceInfoModel> callback) {
        Call<TkbServiceInfoModel> call = apiService.tkbServiceInfo();

        try {
            call.enqueue(new Callback<TkbServiceInfoModel>() {
                @Override
                public void onResponse(@NonNull Call<TkbServiceInfoModel> call, @NonNull Response<TkbServiceInfoModel> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<TkbServiceInfoModel> call, @NonNull Throwable t) {
                    callback.onFailure( t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure( e.getMessage());
        }
    }

    public void tkbtransferCreate(ApiCallback<TkbTranferCreateModel> callback,JsonObject jsonObject) {
        Call<TkbTranferCreateModel> call = apiService.tkbTransferCreate(jsonObject);

        try {
            call.enqueue(new Callback<TkbTranferCreateModel>() {
                @Override
                public void onResponse(@NonNull Call<TkbTranferCreateModel> call, @NonNull Response<TkbTranferCreateModel> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<TkbTranferCreateModel> call, @NonNull Throwable t) {
                    callback.onFailure( t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure( e.getMessage());
        }
    }
    public void tkbCardRegister(ApiCallback<TkbCardCreadeModel> callback) {
        Call<TkbCardCreadeModel> call = apiService.tkbCardRegister();

        try {
            call.enqueue(new Callback<TkbCardCreadeModel>() {
                @Override
                public void onResponse(@NonNull Call<TkbCardCreadeModel> call, @NonNull Response<TkbCardCreadeModel> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<TkbCardCreadeModel> call, @NonNull Throwable t) {
                    callback.onFailure( t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure( e.getMessage());
        }
    }

    //UZ  RF
    public void tkbReceiverInfo(ApiCallback<TkbReceiverInfoModel> callback, JsonObject jsonObject) {
        Call<TkbReceiverInfoModel> call = apiService.tkbReceiverInfo(jsonObject);

        try {
            call.enqueue(new Callback<TkbReceiverInfoModel>() {
                @Override
                public void onResponse(@NonNull Call<TkbReceiverInfoModel> call, @NonNull Response<TkbReceiverInfoModel> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<TkbReceiverInfoModel> call, @NonNull Throwable t) {
                    callback.onFailure( t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure( e.getMessage());
        }
    }

    public void tkbUzRfCreate(ApiCallback<JsonObject> callback, JsonObject jsonObject) {
        Call<JsonObject> call = apiService.tkbUzRfTransferCreate(jsonObject);

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
    public void tkbUzRfConfirm(ApiCallback<JsonObject> callback, JsonObject jsonObject) {
        Call<JsonObject> call = apiService.tkbUzRfTransferConfirm(jsonObject);

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
    public void tkbCardRegisterCallbek(ApiCallback<JsonObject> callback, JsonObject jsonObject) {
        Call<JsonObject> call = apiService.tkbCardRegisterCallbek(jsonObject);

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

}
