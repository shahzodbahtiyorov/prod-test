package org.finance.data.service;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import org.finance.data.model.paynet.category.CategoryModel;
import org.finance.data.model.paynet.service.PaymentServiceModel;
import org.finance.data.model.paynet.category.CategoryResponse;
import org.finance.data.model.paynet.provider.ProvidersResponse;
import org.finance.data.retrofit.ApiInterface;
import org.finance.data.retrofit.RetrofitClient;
import org.telegram.messenger.UserConfig;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaynetService {
    private final ApiInterface apiService;


    public PaynetService(Context context) {
        apiService = RetrofitClient.getClient(context, true).create(ApiInterface.class);
    }

    public void getPaynetCategories(ApiCallback<CategoryResponse> callback) {
        Call<CategoryResponse> call = apiService.paynetCategory();

        try {
            call.enqueue(new Callback<CategoryResponse>() {
                @Override
                public void onResponse(@NonNull Call<CategoryResponse> call, @NonNull Response<CategoryResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                       ;
                        callback.onSuccess(response.body());
                    } else {
                        callback.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CategoryResponse> call, @NonNull Throwable t) {
                    callback.onFailure( t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure( e.getMessage()); }
    }
    public void getPaynetprovider(ApiCallback<ProvidersResponse> callback, JsonObject categoryID) {
        Call<ProvidersResponse> call = apiService.paynetprovider(categoryID);

        try {
            call.enqueue(new Callback<ProvidersResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProvidersResponse> call, @NonNull Response<ProvidersResponse> response) {

                    if (response.isSuccessful() && response.body() != null) {

                        callback.onSuccess(response.body());
                    } else {
                        callback.onFailure( response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProvidersResponse> call, @NonNull Throwable t) {
                    callback.onFailure(t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure(   e.getMessage()); }
    }
    public void getPaymentService(ApiCallback<PaymentServiceModel> callback, JsonObject providerId) {
        Call<PaymentServiceModel> call = apiService.paymetservice(providerId);
        try {
            call.enqueue(new Callback<PaymentServiceModel>() {

                @Override
                public void onResponse(@NonNull Call<PaymentServiceModel> call, @NonNull Response<PaymentServiceModel> response) {
                    if (response.isSuccessful() && response.body() != null) {

                        callback.onSuccess(response.body());
                    } else {
                        callback.onFailure( response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PaymentServiceModel> call, @NonNull Throwable t) {
                    callback.onFailure(t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure( e.getMessage());
        }
    }
    public void paymentCommunalChek(ApiCallback<JsonObject> callback, JsonObject checkJson) {
        Call<JsonObject> call = apiService.checkReceiver(checkJson);

        try {
            call.enqueue(new Callback<JsonObject>() {

                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        JsonObject responseBody=response.body();
                       try {
                           callback.onSuccess(responseBody);
                       } catch (Exception e) {
                           callback.onFailure(responseBody.getAsString());
                       }
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
    public void paymentCreate(ApiCallback<JsonObject> callback, JsonObject paymentCreate) {
        Call<JsonObject> call = apiService.paymentCreate(paymentCreate);

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
    } public void paymentConfirm(ApiCallback<JsonObject> callback, JsonObject paymentConfirm) {
        Call<JsonObject> call = apiService.paymentConfirme(paymentConfirm);

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
    public void searchPaynetCategories(String query,ApiCallback<ArrayList<CategoryModel>> callback) {
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("search_text",query);
        Call<ArrayList<CategoryModel>> call = apiService.searchPaynetCategory(jsonObject);

        try {
            call.enqueue(new Callback<ArrayList<CategoryModel>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<CategoryModel>> call, @NonNull Response<ArrayList<CategoryModel>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ;
                        callback.onSuccess(response.body());
                    } else {
                        callback.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<CategoryModel>> call, @NonNull Throwable t) {
                    callback.onFailure( t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure( e.getMessage()); }
    }
}
