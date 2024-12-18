package org.finance.data.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.google.gson.JsonObject;

import org.finance.data.model.monitoring.MonitoringInfoModel;
import org.finance.data.model.monitoring.MonitoringModel;
import org.finance.data.retrofit.ApiInterface;
import org.finance.data.retrofit.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** @noinspection ALL*/
public class MonitoringService {

    private final ApiInterface apiService;
    private  Context context;
    public MonitoringService(Context context) {
        this.context=context;
        apiService = RetrofitClient.getClient(context, true).create(ApiInterface.class);
    }

    public void getMonitoring(ApiCallback<MonitoringModel> callback, JsonObject jsonObject) {
        Call<MonitoringModel> call = apiService.getMonitoring(jsonObject);

        try {
            call.enqueue(new Callback<MonitoringModel>() {
                @Override
                public void onResponse(@NonNull Call<MonitoringModel> call, @NonNull Response<MonitoringModel> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onFailure("Xatolik: " + response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MonitoringModel> call, @NonNull Throwable t) {
                    callback.onFailure("So'rovda xatolik: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure("So'rov yuborishda xatolik: " + e.getMessage());
        }
    }

    public void filterMonitoring(ApiCallback<MonitoringModel> callback, JsonObject jsonObject) {
        Call<MonitoringModel> call = apiService.filterMonitoring(jsonObject);

        try {
            call.enqueue(new Callback<MonitoringModel>() {
                @Override
                public void onResponse(@NonNull Call<MonitoringModel> call, @NonNull Response<MonitoringModel> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onFailure("Xatolik: " + response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MonitoringModel> call, @NonNull Throwable t) {
                    callback.onFailure("So'rovda xatolik: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure("So'rov yuborishda xatolik: " + e.getMessage());
        }
    }
    public void pdfDownload(ApiCallback<ResponseBody> callback, JsonObject jsonObject) {
        Call<ResponseBody> call = apiService.chekDownload(jsonObject);

        try {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                        boolean isSaved = savePdfToFile(response.body(),context,"SuperApp_Transfer_"+generateRandomNumber());
                        if (isSaved) {
                            System.out.println("PDF muvaffaqiyatli yuklandi va saqlandi.");
                        } else {
                            System.err.println("PDF saqlashda xatolik yuz berdi.");
                        }
                    } else {
                        callback.onFailure("Xatolik: " + response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    callback.onFailure("So'rovda xatolik: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure("So'rov yuborishda xatolik: " + e.getMessage());
        }
    }

    private static boolean savePdfToFile(ResponseBody body, Context context,String filaName) {
        try {
            File file = new File(context.getExternalFilesDir(null), filaName + ".pdf");
            InputStream inputStream = body.byteStream();
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;

            // Faylni yozish
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            openPdfFile(context, file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    private static void sharePdfFile(Context context, File file) {
        try {
            Uri fileUri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".provider",
                    file
            );

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(Intent.createChooser(shareIntent, "PDF ulashish"));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "PDF faylni ulashishda xatolik yuz berdi", Toast.LENGTH_SHORT).show();
        }
    }
    private static void openPdfFile(Context context, File file) {
        try {
            Uri fileUri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".provider",
                    file
            );

            Intent openIntent = new Intent(Intent.ACTION_VIEW);
            openIntent.setDataAndType(fileUri, "application/pdf");
            openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (openIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(Intent.createChooser(openIntent, "PDF faylni ochish"));
            } else {
              sharePdfFile(context,file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "PDF faylni ochishda xatolik yuz berdi", Toast.LENGTH_SHORT).show();
        }
    }

    public static String generateRandomNumber() {
        Random random = new Random();
        StringBuilder number = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            number.append(random.nextInt(10));
        }

        return number.toString();
    }
}
