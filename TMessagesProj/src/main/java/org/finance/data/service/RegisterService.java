package org.finance.data.service;

import org.finance.data.retrofit.ApiInterface;
import org.finance.data.retrofit.RetrofitClient;
import org.telegram.tgnet.TLRPC;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;


import org.telegram.messenger.UserConfig;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterService {

    private final ApiInterface apiService;
    private final Context context;
    private final String phone;

    TLRPC.User user;

    public RegisterService(Context context) {
        this.context = context;
        user = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
        this.phone = user.phone;
        apiService = RetrofitClient.getClient(context, false).create(ApiInterface.class);
    }

    /**
     * this method registers user to <b>SuperApp</b>
     */

    public void registerUser(String phoneNumber) {
        JsonObject phoneObject = new JsonObject();
        phoneObject.addProperty("phone", phoneNumber);

        Call<JsonObject> call = apiService.register(phoneObject);
        try {
            call.enqueue(new Callback<JsonObject>() {

                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {

                            JsonObject jsonObject = response.body();

                            JsonObject jwtTokenObject = jsonObject.getAsJsonObject("jwt_token");

                            String accessToken = jwtTokenObject.get("access").getAsString();
                            String refreshToken = jwtTokenObject.get("refresh").getAsString();


                            SharedPreferences sharedPreferences = context.getSharedPreferences("SuperApp", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(phone, accessToken);
                            editor.putString(phone + "_refresh", refreshToken);
                            editor.apply();

                        }
                    } catch (Exception ignored) {

                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {


                }
            });
        } catch (Exception ignored) {

        }
    }

    /**
     * this method used to get <b>REFRESHED</b> access token
     */
    public String getRefreshedToken() {

        SharedPreferences sharedPreferences = context.getSharedPreferences("SuperApp", Context.MODE_PRIVATE);
        String refreshToken = sharedPreferences.getString(phone + "_refresh", "");


        JsonObject refreshObject = new JsonObject();
        refreshObject.addProperty("refresh", refreshToken);

        // Create the call to the refresh endpoint
        Call<JsonObject> call = apiService.refresh(refreshObject);

        try {
            // Execute the request synchronously
            Response<JsonObject> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                JsonObject jsonObject = response.body();
                String newAccessToken = jsonObject.get("access").getAsString();
                refreshToken = jsonObject.get("refresh").getAsString();

                // Save the new access token to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(phone, newAccessToken);
                editor.putString(phone + "_refresh", refreshToken);
                editor.apply();

                // Return the new access token
                return newAccessToken;
            } else {
                return "";
            }
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * this method verifies <b>access token</b>
     */
    public void verifyToken() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SuperApp", Context.MODE_PRIVATE);
        String accessToken = sharedPreferences.getString(phone, "");

        JsonObject body = new JsonObject();
        body.addProperty("token", accessToken);

        Call<JsonObject> call = apiService.verify(body);

        try {
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.code() != 200) {
                        refreshToken();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    refreshToken();
                }
            });
        } catch (Exception e) {
            refreshToken();
        }

    }

    /**
     * this method refreshes <b>access token</b>
     */
    private void refreshToken() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SuperApp", Context.MODE_PRIVATE);
        String refreshToken = sharedPreferences.getString(phone + "_refresh", "");


        JsonObject refreshObject = new JsonObject();
        refreshObject.addProperty("refresh", refreshToken);

        // Create the call to the refresh endpoint
        Call<JsonObject> call = apiService.refresh(refreshObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                if (jsonObject != null) {
                    String newAccessToken = jsonObject.get("access").getAsString();
                    String refreshToken = jsonObject.get("refresh").getAsString();

                    // Save the new access token to SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(phone, newAccessToken);
                    editor.putString(phone + "_refresh", refreshToken);
                    editor.apply();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {}
        });

    }

}