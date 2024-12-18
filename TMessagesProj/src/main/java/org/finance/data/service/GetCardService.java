package org.finance.data.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;


import com.google.gson.JsonObject;

import org.finance.data.model.get_card.ChatUserCardModel;
import org.finance.data.model.get_card.GetCardBalanceModel;
import org.finance.data.retrofit.ApiInterface;
import org.finance.data.retrofit.RetrofitClient;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @noinspection ALL
 */
public class GetCardService {

    private final ApiInterface apiService;
    private final Context context;
    private final String phone;
    TLRPC.User user;


    public GetCardService(Context context) {
        this.context = context;
        user = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
        this.phone = user.phone;
        apiService = RetrofitClient.getClient(context, true).create(ApiInterface.class);
    }

    public void fetchCardBalance(ApiCallback<GetCardBalanceModel> callback) {
        Call<GetCardBalanceModel> call = apiService.getCardBalance();

        try {
            call.enqueue(new Callback<GetCardBalanceModel>() {
                @Override
                public void onResponse(@NonNull Call<GetCardBalanceModel> call, @NonNull Response<GetCardBalanceModel> response) {
                    if (response.isSuccessful() && response.body() != null) {


                        callback.onSuccess(response.body());
                    } else {
                        callback.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GetCardBalanceModel> call, @NonNull Throwable t) {
                    callback.onFailure(t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }


    public void delete(ApiCallback<String> callback, JsonObject jsonObject) {
        Call<Void> call = apiService.cardDelete(jsonObject);

        try {
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        callback.onSuccess("Karta muvaffaqiyatli o'chirildi");
                    } else {
                        String errorMsg = response.errorBody() != null ? response.errorBody().toString() : "Unknown error";
                        callback.onFailure(errorMsg);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    callback.onFailure(t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }

    public void cardBlocked(ApiCallback<String> callback, JsonObject cardBlokced) {
        Call<JsonObject> call = apiService.cardBlocked(cardBlokced);

        try {
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                         String message=response.body().get("message").getAsString();
                        Log.d("CardBlocced",message);

                        callback.onSuccess(message);
                    } else {
                        callback.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    callback.onFailure(t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }

    public void cardNameUpdated(ApiCallback<JsonObject> callback, JsonObject cardBlokced) {
        Call<JsonObject> call = apiService.updateCardName(cardBlokced);

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
                    callback.onFailure(t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }

    public void getChatUserCards(ApiCallback<ArrayList<ChatUserCardModel>> callback, JsonObject body) {
        Call<ArrayList<ChatUserCardModel>> call = apiService.getChatUserCards(body);

        try {
            call.enqueue(new Callback<ArrayList<ChatUserCardModel>>() {
                @Override
                public void onResponse(Call<ArrayList<ChatUserCardModel>> call, Response<ArrayList<ChatUserCardModel>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else if (response.code() == 500) {
                        callback.onFailure("Bu foydalanuvchi SuperAppdan ro'yhatdan o'tmagan");
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<ChatUserCardModel>> call, Throwable t) {
                    callback.onFailure(t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }
}
