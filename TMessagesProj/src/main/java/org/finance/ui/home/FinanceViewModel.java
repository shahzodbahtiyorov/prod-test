package org.finance.ui.home;

import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.gson.JsonObject;
import org.finance.data.model.get_card.GetCardBalanceModel;
import org.finance.data.model.get_card.GetCardBalanceWithCards;
import org.finance.data.model.monitoring.MonitoringModel;
import org.finance.data.model.myId.MyIdInfoModel;
import org.finance.data.room.CardsDao;
import org.finance.data.room.SuperAppDatabase;
import org.finance.data.service.ApiCallback;
import org.finance.data.service.GetCardService;
import org.finance.data.service.MonitoringService;
import org.finance.data.service.MyIdService;

import java.util.List;

/** @noinspection ALL*/
public class FinanceViewModel extends ViewModel {
    private final GetCardService getCardHelper;
    private final MyIdService myIdService;
    MonitoringService monitoringService;
    private final MutableLiveData<List<GetCardBalanceWithCards>> cardBalanceLiveData = new MutableLiveData<>();
    private final MutableLiveData<MyIdInfoModel> myIdInfoLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<MonitoringModel> monitorin = new MutableLiveData<>();
    private final MutableLiveData<String> cardError = new MutableLiveData<>();
    private final MutableLiveData<String> monitoringErrorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> myIdErrorMessage = new MutableLiveData<>();

    private final LiveData<GetCardBalanceWithCards>allCards;
    SuperAppDatabase database;

    public FinanceViewModel(GetCardService getCardHelper, MyIdService myIdService, MonitoringService monitoringService, SuperAppDatabase database) {
        this.getCardHelper = getCardHelper;
        this.myIdService = myIdService;
        this.database=database;
        this.monitoringService=monitoringService;
        this.allCards = database.cardsDao().getAllCardBalancesWithCards();;
    }

    public void fetchCardBalance() {

        loading.setValue(true);
        getCardHelper.fetchCardBalance(new ApiCallback<GetCardBalanceModel>() {
            @Override
            public void onSuccess(GetCardBalanceModel response) {
                if (response != null && !response.getCards().isEmpty()) {
                    saveDataToDatabase(response);
                    loading.setValue(false);
                } else {
                    loading.setValue(true);
                    cardError.setValue("No card data available.");
                }
            }

            @Override
            public void onFailure(String error) {
                cardError.setValue(error);
            }
        });
    }




    private void saveDataToDatabase(GetCardBalanceModel balanceModel) {
        new Thread(() -> {
            CardsDao cardsDao = database.cardsDao();

            cardsDao.clearAllBalances();
            cardsDao.clearAllCards();

            long balanceId = cardsDao.insertBalanceModel(balanceModel);

            List<GetCardBalanceModel.Cards> cards = balanceModel.getCards();
            if (cards != null) {
                for (GetCardBalanceModel.Cards card : cards) {
                    card.setBalanceModelId((int) balanceId);
                }
                cardsDao.insertCards(cards);
            }
        }).start();
    }
    public void clearDatabase() {
        new Thread(() -> {
            database.cardsDao().clearAllBalances();
            database.cardsDao().clearAllCards();
        }).start();
    }

    public void fetchMyIdInfo() {
        myIdService.getMyIdInfo(new ApiCallback<MyIdInfoModel>() {

            @Override
            public void onSuccess(MyIdInfoModel response) {
                myIdInfoLiveData.setValue(response);
            }

            @Override
            public void onFailure(String error) {
                if ("Not Found".equals(error)) {
                    myIdErrorMessage.setValue("MyID info not found");
                }
            }

        });
    }
    public void refreshData() {
        loading.setValue(true);
        new Thread(() -> {
            clearDatabase();
            new Handler(Looper.getMainLooper()).post(() -> {
                getCardHelper.fetchCardBalance(new ApiCallback<GetCardBalanceModel>() {
                    @Override
                    public void onSuccess(GetCardBalanceModel response) {
                        if (response != null && !response.getCards().isEmpty()) {
                            loading.setValue(false);
                            saveDataToDatabase(response);
                        } else {
                            loading.setValue(true);
                            cardError.setValue("No card data available.");
                        }
                    }
                    @Override
                    public void onFailure(String error) {
                        loading.setValue(true);
                        cardError.setValue(error);
                    }
                });
            });
        }).start();

        fetchMyIdInfo();
        getMonitoring();
    }





    public LiveData<GetCardBalanceWithCards> getAllCards() {
        return allCards;
    }
    public  void getMonitoring() {
        JsonObject requestObject = new JsonObject();
        monitoringService.getMonitoring(new ApiCallback<MonitoringModel>() {
            @Override
            public void onSuccess(MonitoringModel response) {
                monitorin.setValue(response);
            }
            @Override
            public void onFailure(String error) {
                monitoringErrorMessage.setValue(error);
            }
        }, requestObject);
    }


    public LiveData<MyIdInfoModel> getMyIdInfoLiveData() {
        return myIdInfoLiveData;
    }

    public LiveData<String> getCardErrorMessage() {
        return cardError;
    }
    public LiveData<String> getMyIdErrorMessage() {
        return myIdErrorMessage;
    }

    public LiveData<String> getMonitoringErrorMessage() {
        return monitoringErrorMessage;
    }
    public LiveData<MonitoringModel> getCardMonitoringLiveData() {
        return monitorin;
    }
    public LiveData<Boolean> isLoading() {
        return loading;
    }
}