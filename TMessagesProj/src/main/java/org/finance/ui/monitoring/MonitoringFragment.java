package org.finance.ui.monitoring;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.finance.data.model.get_card.GetCardBalanceWithCards;
import org.finance.data.model.monitoring.MonitoringModel;
import org.finance.data.room.DataBaseClient;
import org.finance.data.room.SuperAppDatabase;
import org.finance.data.service.ApiCallback;
import org.finance.data.service.MonitoringService;
import org.finance.helpers.SuperAppFormatters;
import org.finance.ui.adapters.MonitoringAdapter;
import org.finance.ui.adapters.MonitoringCardAdapter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * @noinspection ALL
 */
public class MonitoringFragment extends BaseFragment {

    private ProgressBar progressBar;
    private TextView errorResult;
    private RecyclerView recyclerView;
    private TextView monthTitle;
    private ImageView nextButton;
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private List<MonitoringModel.Result> allTransactions = new ArrayList<>();
    private GetCardBalanceWithCards cardList;
    private SuperAppDatabase database;
    TLRPC.User user;


    private List<String> filteredCards = new ArrayList<>();
    Calendar startDate = Calendar.getInstance();
    Calendar endDate = Calendar.getInstance();
    private View view;

    @Override
    public View createView(Context context) {
        if (view == null) {
            view = viewCreate(context);
        }
        return view;
    }

    public View viewCreate(@NonNull Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.monitoring_fragment_layout, null);
        actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
        actionBar.setTitle("Мониторинг");
        actionBar.setAllowOverlayTitle(true);
        actionBar.setBackgroundColor(Color.parseColor("#50A7EA"));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment(true);
                }
            }
        });
        user = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
        database = DataBaseClient.getInstance(context, user.phone).getAppDatabase();
        ImageView backButton = rootView.findViewById(R.id.backButton);
        monthTitle = rootView.findViewById(R.id.monthTitle);
        nextButton = rootView.findViewById(R.id.nextButton);
        LinearLayout filterLayout = rootView.findViewById(R.id.filterLayout);



        updateMonthTitle();
        updateNextButtonIcon();

        backButton.setOnClickListener(v -> goToPreviousMonth());
        nextButton.setOnClickListener(v -> goToNextMonth());
        filterLayout.setOnClickListener(v -> {
            filterLayout.setEnabled(false);
            openFilterOptions();
            filterLayout.postDelayed(() -> filterLayout.setEnabled(true), 500);
        });

        recyclerView = rootView.findViewById(R.id.monitoringRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = 12;
                outRect.left = 12;
                outRect.right = 12;
                outRect.top = 12;
            }
        });

        progressBar = rootView.findViewById(R.id.progressBar);
        errorResult = rootView.findViewById(R.id.errorTextView);
        errorResult.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        errorResult.setTextSize(16);
        errorResult.setVisibility(View.GONE);

        getMonitoring();

        return rootView;
    }

    private void getMonitoring() {
        database.cardsDao().getAllCardBalancesWithCards().observe(getViewLifecycleOwner(), getCardBalanceWithCards -> {
            if (getCardBalanceWithCards != null && !getCardBalanceWithCards.cards.isEmpty()) {
                cardList = getCardBalanceWithCards;
            } else {
                Log.d("CardsList", "No cards found in the list");
            }
        });


        progressBar.setVisibility(View.VISIBLE);
        errorResult.setVisibility(View.GONE);
        JsonObject requestObject = new JsonObject();
        final MonitoringService monitoringService = new MonitoringService(getContext());


        monitoringService.getMonitoring(new ApiCallback<MonitoringModel>() {
            @Override
            public void onSuccess(MonitoringModel response) {
                progressBar.setVisibility(View.GONE);
                allTransactions = response.getResult();
                filterTransactionsByMonth();
            }

            @Override
            public void onFailure(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                errorResult.setVisibility(View.VISIBLE);
                errorResult.setText(errorMessage);
            }
        }, requestObject);
    }

    private void filterMonitoring(JsonObject jsonObject) {
        progressBar.setVisibility(View.VISIBLE);
        errorResult.setVisibility(View.GONE);
        final MonitoringService monitoringService = new MonitoringService(getContext());

        final BaseFragment current = this;

        monitoringService.filterMonitoring(new ApiCallback<MonitoringModel>() {
            @Override
            public void onSuccess(MonitoringModel response) {
                progressBar.setVisibility(View.GONE);
                allTransactions = response.getResult();
                MonitoringAdapter monitoringAdapter = new MonitoringAdapter(getContext(), allTransactions, current);
                recyclerView.setAdapter(monitoringAdapter);
            }

            @Override
            public void onFailure(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                errorResult.setVisibility(View.VISIBLE);
                errorResult.setText(errorMessage);
            }
        }, jsonObject);
    }

    private void goToPreviousMonth() {
        calendar.add(Calendar.MONTH, -1);
        updateMonthTitle();
        updateNextButtonIcon();
        filterTransactionsByMonth();
    }

    private void goToNextMonth() {
        Calendar currentCalendar = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) < currentCalendar.get(Calendar.YEAR) ||
                (calendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                        calendar.get(Calendar.MONTH) < currentCalendar.get(Calendar.MONTH))) {
            calendar.add(Calendar.MONTH, 1);
            updateMonthTitle();
            updateNextButtonIcon();
            filterTransactionsByMonth();
        }
    }

    private void updateMonthTitle() {
        monthTitle.setText(monthFormat.format(calendar.getTime()));
        startDate.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        endDate.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Log.e("start_rb_105_date", startDate.get(calendar.MONTH) + " " + startDate.get(calendar.DAY_OF_MONTH));
        Log.e("end_rb_105_date", endDate.get(calendar.MONTH) + " " + endDate.get(calendar.DAY_OF_MONTH));
    }

    private void updateNextButtonIcon() {
        Calendar currentCalendar = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {
            nextButton.setImageResource(R.drawable.ic_next_arrow);
        } else {
            nextButton.setImageResource(R.drawable.ic_next_disabled);

        }
    }


    private void filterTransactionsByMonth() {
        List<MonitoringModel.Result> filteredTransactions = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        int selectedYear = calendar.get(Calendar.YEAR);
        int selectedMonth = calendar.get(Calendar.MONTH);
        for (MonitoringModel.Result transaction : allTransactions) {
            try {
                Calendar transactionDate = Calendar.getInstance();
                transactionDate.setTime(dateFormat.parse(transaction.getCreatedAt()));

                int transactionYear = transactionDate.get(Calendar.YEAR);
                int transactionMonth = transactionDate.get(Calendar.MONTH);

                if (transactionYear == selectedYear && transactionMonth == selectedMonth) {
                    filteredTransactions.add(transaction);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        MonitoringAdapter monitoringAdapter = new MonitoringAdapter(getContext(), filteredTransactions, this);
        recyclerView.setAdapter(monitoringAdapter);
    }

    private void openFilterOptions() {
        JsonObject filterBody = new JsonObject();
        BottomSheetDialog filterDialog = new BottomSheetDialog(getContext(), R.style.RoundedBottomSheetDialog);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View filterView = inflater.inflate(R.layout.monitoring_filtr_bottom, null);

        filterDialog.setContentView(filterView);
        // Initialize views and set up listeners as you have already
        TextView startDateText = filterView.findViewById(R.id.startDate);
        TextView endDateText = filterView.findViewById(R.id.endDate);
        TextView cardFilterText = filterView.findViewById(R.id.cardFilter);
        Button resetButton = filterView.findViewById(R.id.resetButton);
        Button applyButton = filterView.findViewById(R.id.applyButton);
        ImageView closeButton = filterView.findViewById(R.id.closeButton);

        closeButton.setOnClickListener(v -> filterDialog.dismiss());


        if (!filteredCards.isEmpty()) {
            String result = "";
            for (String value : filteredCards) {
                String lastFourDigits = SuperAppFormatters.getCardLastFourDigits(value) + " ";
                result += lastFourDigits;
            }
            cardFilterText.setText(result);
        }

        endDateText.setText(String.format("%d.%d.%d", endDate.get(Calendar.DAY_OF_MONTH), endDate.get(Calendar.MONTH) + 1, endDate.get(Calendar.YEAR)));
        filterBody.addProperty("end_date", String.format("%d-%d-%d", endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH) + 1, endDate.get(Calendar.DAY_OF_MONTH)));

        if (startDate != null && startDate.before(endDate)) {
            startDateText.setText(String.format("%d.%d.%d", startDate.get(Calendar.DAY_OF_MONTH), startDate.get(Calendar.MONTH) + 1, startDate.get(Calendar.YEAR)));
        }
        startDateText.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            startDate.set(year, month, dayOfMonth);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                            startDateText.setText(dateFormat.format(startDate.getTime()));
                            Log.e("date-pick-format", String.format("%d-%d-%d", startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH)));
                            filterBody.addProperty("start_date", String.format("%d-%d-%d", startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH) + 1, startDate.get(Calendar.DAY_OF_MONTH)));
                        }
                    },

                    startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            datePickerDialog.getDatePicker().setMaxDate(endDate.getTimeInMillis());
            datePickerDialog.show();

        });
        endDateText.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            endDate.set(year, month, dayOfMonth);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                            endDateText.setText(dateFormat.format(endDate.getTime()));
                            Log.e("date-pick-format", String.format("%d-%d-%d", endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH) + 1, endDate.get(Calendar.DAY_OF_MONTH)));
                            filterBody.addProperty("end_date", String.format("%d-%d-%d", endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH) + 1, endDate.get(Calendar.DAY_OF_MONTH)));
                        }
                    },

                    endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.getDatePicker().setMinDate(startDate.getTimeInMillis());
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });


        cardFilterText.setOnClickListener(
                v ->{
                    cardFilterText.setEnabled(false);
                    openCardFilterBottomSheet(selectedCards -> {
                        JsonArray jsonArray = new JsonArray();
                        if (selectedCards.isEmpty()) {
                            cardFilterText.setText("Все карты");
                            filteredCards.clear();
                        } else {
                            filteredCards.clear();
                            filteredCards.addAll(selectedCards);
                            String result = "";
                            for (String value : filteredCards) {
                                String lastFourDigits = SuperAppFormatters.getCardLastFourDigits(value) + " ";
                                result += lastFourDigits;
                                jsonArray.add(value);
                            }
                            cardFilterText.setText(result);
                        }
                        filterBody.add("card_numbers", jsonArray);
                    }, filteredCards);
                    cardFilterText.postDelayed(() -> cardFilterText.setEnabled(true), 500);

                });

        resetButton.setOnClickListener(v -> {
            Calendar currentDate = Calendar.getInstance();
            filterBody.entrySet().clear();
            startDate.setTimeInMillis(currentDate.getTimeInMillis());
            endDate.setTimeInMillis(currentDate.getTimeInMillis());
            startDateText.setText("Не выбрано");
            endDateText.setText(String.format("%d.%d.%d", endDate.get(Calendar.DAY_OF_MONTH), endDate.get(Calendar.MONTH) + 1, endDate.get(Calendar.YEAR)));
            cardFilterText.setText("Все карты");
        });

        applyButton.setOnClickListener(v -> {
            filterDialog.dismiss();
            if (!filterBody.isEmpty()) {
                filterMonitoring(filterBody);
            }
        });

        filterDialog.show();
    }

    private void openCardFilterBottomSheet(Consumer<List<String>> selectionCallback, @Nullable List<String> previousCards) {
        BottomSheetDialog cardFilterDialog = new BottomSheetDialog(getContext(), R.style.RoundedBottomSheetDialog);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View cardFilterView = inflater.inflate(R.layout.monitoring_card_bottom, null);

        List<String> filteredCards = new ArrayList<>();
        if (!previousCards.isEmpty()) {
            filteredCards.clear();
            filteredCards.addAll(previousCards);
        }
        cardFilterView.post(() -> {
            View parent = (View) cardFilterView.getParent();
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);
            behavior.setPeekHeight(cardFilterView.getHeight());
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);
        });
        cardFilterView.setBackgroundResource(R.drawable.bottom_sheet_radius);
        cardFilterDialog.setContentView(cardFilterView);
        RecyclerView cardRecyclerView = cardFilterView.findViewById(R.id.cardRecyclerView);
        Button resetButton = cardFilterView.findViewById(R.id.resetButton);
        Button applyButton = cardFilterView.findViewById(R.id.applyButton);
        ImageView closeButton = cardFilterView.findViewById(R.id.closeButton);
        cardRecyclerView.setVisibility(View.VISIBLE);
        MonitoringCardAdapter cardAdapter = new MonitoringCardAdapter(getContext(), cardList.cards, selectedCards -> {
            filteredCards.clear();
            filteredCards.addAll(selectedCards);
        }, previousCards);
        cardRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cardRecyclerView.setAdapter(cardAdapter);
        closeButton.setOnClickListener(v -> cardFilterDialog.dismiss());

        resetButton.setOnClickListener(v -> {
            cardAdapter.updateAdapter();
            filteredCards.clear();
            cardFilterDialog.dismiss();
        });
        cardRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = 12;
                outRect.left = 12;
                outRect.right = 12;
                outRect.top = 12;
            }
        });
        applyButton.setOnClickListener(v -> {
            Log.i("filtered_cards", filteredCards.toString());
            selectionCallback.accept(filteredCards);
            cardFilterDialog.dismiss();
        });
        cardFilterDialog.show();
    }
}
