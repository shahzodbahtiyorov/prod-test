package org.finance.data.retrofit;
import com.google.gson.JsonObject;

import org.finance.data.model.add_card.AddCardInfoModel;
import org.finance.data.model.get_card.CardInfoModel;
import org.finance.data.model.get_card.ChatUserCardModel;
import org.finance.data.model.get_card.TransferCardModel;
import org.finance.data.model.monitoring.MonitoringModel;
import org.finance.data.model.myId.MyIdInfoModel;
import org.finance.data.model.get_card.GetCardBalanceModel;
import org.finance.data.model.paynet.category.CategoryModel;
import org.finance.data.model.paynet.service.PaymentServiceModel;
import org.finance.data.model.paynet.category.CategoryResponse;
import org.finance.data.model.paynet.provider.ProvidersResponse;
import org.finance.data.model.tkb.card_create.TkbCardCreadeModel;
import org.finance.data.model.tkb.receiver_info.TkbReceiverInfoModel;
import org.finance.data.model.tkb.tkb_service_info.TkbServiceInfoModel;
import org.finance.data.model.tkb.tranfer.TkbTranferCreateModel;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/** @noinspection ALL*/
public interface ApiInterface {
    //Register
    @POST("api/accounts/register")
    Call<JsonObject> register(@Body JsonObject phone);
    @POST("account/token/refresh/")
    Call<JsonObject> refresh(@Body JsonObject refreshBody);
    @POST("/account/token/verify/")
    Call<JsonObject> verify(@Body JsonObject body);
    @POST("api/wallet/version")
    Call<JsonObject> checkVersion (@Body JsonObject body);

    @GET("api/wallet/card_balance")
    Call<GetCardBalanceModel> getCardBalance();
    @POST("api/wallet/card/name/update")
    Call<JsonObject> updateCardName(@Body JsonObject cardname);
    @POST("api/wallet/card_add_step_one")
    Call<JsonObject> addCardStepOne(@Body JsonObject addcardone);
    @POST("api/wallet/card_add_step_two")
    Call<JsonObject> addCardStepTwo(@Body JsonObject addcardtwo);
    @POST("api/wallet/card_add_humo")
    Call<JsonObject> addHumoCardStepTwo(@Body JsonObject addcardhumo);
    @POST("api/wallet/card_info")
    Call<CardInfoModel> cardInfo(@Body JsonObject cardinfo);
    @POST("api/wallet/card/info")
    Call<AddCardInfoModel> addcardInfo(@Body JsonObject addCardInfo);
    @POST("api/wallet/delete_card")
    Call<Void> cardDelete(@Body JsonObject cardinfo);
    @POST("api/wallet/card_blocked")
    Call<JsonObject> cardBlocked(@Body JsonObject cardBlocked);
    //P2P
    @POST("api/wallet/card_transfer")
    Call<JsonObject> cardTransferOne(@Body JsonObject cardinfo);

    @POST("api/wallet/card_transfer_confirme")
    Call<JsonObject> cardTransferTwo(@Body JsonObject cardinfo);

    @POST("api/wallet/chat/user")
    Call<ArrayList<ChatUserCardModel>> getChatUserCards(@Body JsonObject phone);

    @POST("api/wallet/categories")
    Call<CategoryResponse> paynetCategory();
    @POST("api/wallet/search_by_service")
    Call<ArrayList<CategoryModel>> searchPaynetCategory(@Body JsonObject categoryId);

    @POST("api/wallet/providers")
    Call<ProvidersResponse> paynetprovider(@Body JsonObject categoryId);

    @POST("api/wallet/services")
    Call<PaymentServiceModel> paymetservice(@Body JsonObject providerId);

    @POST("api/wallet/check_receiver/")
    Call<JsonObject> transactionHistory(@Body JsonObject jsonObject);

    // MyId
    @POST("api/wallet/identification")
    Call<JsonObject> myIdIdentification(@Body JsonObject config);

    @GET("api/wallet/my_id_info")
    Call<MyIdInfoModel> getMyIdInfo();

    // Monitoring
    @POST("api/wallet/card_monitoring")
    Call<MonitoringModel> getMonitoring(@Body JsonObject body);
    @POST("api/wallet/card/monitoring")
    Call<MonitoringModel> filterMonitoring(@Body JsonObject body);
    @POST("api/wallet/api/generate_pdf")
    Call<ResponseBody> chekDownload(@Body JsonObject body);
    @POST("api/wallet/check_receiver/")
    Call<JsonObject> checkReceiver(@Body JsonObject jsonObject);
    @POST("api/wallet/payment_create")
    Call<JsonObject>paymentCreate(@Body JsonObject jsonObject);
    @POST("api/wallet/payment_confirme")
    Call<JsonObject>paymentConfirme(@Body JsonObject jsonObject);

    //TKB
    @POST("api/wallet/tcb_service_info/")
    Call<TkbServiceInfoModel>tkbServiceInfo();
    @POST("api/wallet/transfer_create")
    Call<TkbTranferCreateModel>tkbTransferCreate(@Body JsonObject jsonObject);
    @POST("api/wallet/card_tcb_register")
    Call<TkbCardCreadeModel>tkbCardRegister();
    @POST("api/wallet/callbacks/card")
    Call<JsonObject>tkbCardRegisterCallbek(@Body JsonObject jsonObject);
    @POST("api/wallet/receiver/info")
    Call<TkbReceiverInfoModel>tkbReceiverInfo(@Body JsonObject jsonObject);
    @POST("api/wallet/transfer/create")
    Call<JsonObject>tkbUzRfTransferCreate(@Body JsonObject jsonObject);
    @POST("api/wallet/transfer/confirm")
    Call<JsonObject>tkbUzRfTransferConfirm(@Body JsonObject jsonObject);

    //ReceiverCard
    @GET("api/wallet/get_receiver/card")
    Call<List<TransferCardModel>>getReceiverCard();
    @POST("api/wallet/receiver/create/card")
    Call<JsonObject>addReceiverCard(@Body JsonObject jsonObject);
}
