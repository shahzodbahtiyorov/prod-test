package org.finance.data.retrofit;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.finance.data.service.RegisterService;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.UserConfig;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static String token = "";
    private static String refreshToken = "";

    public static Retrofit getClient(Context context, boolean isHeader ) {

        String phone ;
        // Recreate Retrofit instance for each call
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .addInterceptor(logging);


        if (isHeader) {
            phone =  UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().phone;
            // Retrieve token from SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("SuperApp", Context.MODE_PRIVATE);
            token = sharedPreferences.getString(phone, "");
            refreshToken = sharedPreferences.getString(phone + "_refresh", "");
            if (!token.isEmpty()) {
                // Authorization Interceptor
                Interceptor authInterceptor = chain -> {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", "Bearer " + token)
                            .method(original.method(), original.body());
                    Request originalRequest = requestBuilder.build();
                    Response response = chain.proceed(originalRequest);
                    if (response.code() == 401 && refreshToken != null && !refreshToken.isEmpty()) {
                        synchronized (RetrofitClient.class) {
                            // Double-check if the token was already refreshed
                            SharedPreferences prefs = context.getSharedPreferences("SuperApp", Context.MODE_PRIVATE);
                            String currentToken = prefs.getString(phone, "");

                            if (!currentToken.equals(token)) {
                                token = currentToken;
                                // Retry the request with the new token
                                Request retryRequest = originalRequest.newBuilder()
                                        .header("Authorization", "Bearer " + token)
                                        .build();
                                response.close(); // Close the original response
                                return chain.proceed(retryRequest);
                            }

                            // Perform token refresh
                            String newAccessToken = new RegisterService(context).getRefreshedToken();
                            if (!newAccessToken.isEmpty()) {
                                // Update SharedPreferences with the new token
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString(phone, newAccessToken);
                                editor.apply();

                                // Retry the original request with the new token
                                Request retryRequest = originalRequest.newBuilder()
                                        .header("Authorization", "Bearer " + newAccessToken)
                                        .build();
                                response.close(); // Close the original response
                                return chain.proceed(retryRequest);
                            } else {
                                Log.e("RetrofitClient", "Token refresh failed. Please log in again.");
                                // Optionally, trigger re-authentication flow
                            }
                        }
                    }

                    return response;
                };

                clientBuilder.addInterceptor(authInterceptor);
            } else {
                Toast.makeText(context, "No token found for phone: " + phone, Toast.LENGTH_LONG).show();
            }
        }

        OkHttpClient client = clientBuilder.build();

        // Build Retrofit client


        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }
}
