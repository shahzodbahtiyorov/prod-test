package org.finance.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.util.Log;
import org.finance.data.model.myId.MyIdInfoModel;
import org.finance.data.room.DataBaseClient;
import org.finance.data.room.SuperAppDatabase;
import org.finance.data.service.GetCardService;
import org.finance.data.service.MonitoringService;
import org.finance.data.service.MyIdService;
import org.finance.ui.myId.MyIdFragment;
import org.finance.ui.myId.MyIdInfoFragment;
import org.finance.ui.ui_helper.HomeHelper;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.DialogsActivity;


/** @noinspection ALL*/
public class FinanceFragment extends BaseFragment {

    private ProgressBar progressBar;
    private View rootView;
    private FinanceViewModel financeViewModel;
    private MyIdInfoModel myIdInfo;
    private TextView errorResult;
    TLRPC.User user;
    private   LinearLayout financeCardLayout;
    private RelativeLayout layout;


    @Override
    public boolean canBeginSlide() {
        return false;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View createView(Context context) {
    LocaleController.getString("");
        if (rootView == null) {
            rootView = viewCreate(context);
        }

        return rootView;
    }
    private void setupViews(Context context, RelativeLayout layout) {
        progressBar = new ProgressBar(context);
        progressBar.setId(View.generateViewId());
        progressBar.setVisibility(View.VISIBLE);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF1F1F1F, PorterDuff.Mode.SRC_IN);
        RelativeLayout.LayoutParams progressBarParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        progressBarParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        progressBar.setLayoutParams(progressBarParams);
        layout.addView(progressBar);
        errorResult = new TextView(context);
        errorResult.setId(View.generateViewId());
        RelativeLayout.LayoutParams resultTextParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        resultTextParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        errorResult.setLayoutParams(resultTextParams);
        errorResult.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        errorResult.setTextSize(16);
        errorResult.setVisibility(View.GONE);
        layout.addView(errorResult);
    }
    private void observeViewModel(RelativeLayout layout, BaseFragment baseFragment) {




        financeViewModel.getAllCards().observeForever( cards -> {
            Log.d("Cardsdatabase", "" + cards);
            progressBar.setVisibility(View.GONE);

            financeViewModel.isLoading().observeForever( isLoading -> {
                Log.d("Cards error",isLoading.toString());
                if (isLoading==false) {
                    try {
                        if (financeCardLayout != null && financeCardLayout.getParent() != null) {
                            ((ViewGroup) financeCardLayout.getParent()).removeView(financeCardLayout);
                        }
                        if (cards != null && !cards.cards.isEmpty()) {
                            financeViewModel.getCardMonitoringLiveData().observeForever( monitoringModel -> {
                                if (financeCardLayout != null && financeCardLayout.getParent() != null) {
                                    ((ViewGroup) financeCardLayout.getParent()).removeView(financeCardLayout);
                                }
                                Log.d("Cards monitoring",monitoringModel.toString());

                                financeCardLayout = HomeHelper.createFinanceLayout(getContext(), cards, baseFragment, monitoringModel,financeViewModel);
                                layout.addView(financeCardLayout);
                            });
                        }
                    } catch (Exception e) {

                        Log.d("Cards error", e.getMessage());
                    }
                }
            });
        });
    financeViewModel.getCardErrorMessage().observeForever(error -> {
        Log.d("CardsError",error);
            progressBar.setVisibility(View.GONE);

            if (error.equals("No card data available.")){
                HomeHelper.setupMainUI(layout, getContext(), baseFragment);
            }else {
                errorResult.setVisibility(View.VISIBLE);
                errorResult.setText(error);
            }

     });
financeViewModel.getMonitoringErrorMessage().observeForever(monitoringModel -> {
    Log.d("Monitoring error",monitoringModel);
});

        financeViewModel.getMyIdInfoLiveData().observeForever( response -> {
            if (response != null) {
                myIdInfo = response;
                Log.e("rb_105", response.getAddress().getPermanentAddress());
                ActionBarMenu actionBarMenu = actionBar.createMenu();
                actionBarMenu.clearItems();
                actionBarMenu.addItem(105, R.drawable.user_identifiel_icon);
            }
        });

        financeViewModel.getMyIdErrorMessage().observe(getViewLifecycleOwner(), error -> {
            progressBar.setVisibility(View.GONE);
            showIdentificationDialog(getContext());
        });


    }

    private void showIdentificationDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Чтобы воспользоваться сервисом, нужно сначала пройти идентификацию!")
                .setTitle("Идентификация")
                .setPositiveButton("Пройти идентификацию", (dialog, which) -> {
                    dialog.cancel();
                    presentFragment(new MyIdFragment());
                });
        showDialog(builder.create());
    }

    public View viewCreate(Context context) {


         layout = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        layout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        layout.setLayoutParams(layoutParams);
        actionBar.setBackgroundColor(Color.parseColor("#50A7EA"));
        actionBar.setBackButtonImage(R.drawable.finance_back_icon);
        actionBar.setTitle(LocaleController.getString(R.string.Finance));
        actionBar.setAllowOverlayTitle(true);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == 105) {
                    presentFragment(new MyIdInfoFragment(myIdInfo));
                }else {
                    navigationTelegram();
                }
            }
        });
        user = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
        GetCardService getCardHelper = new GetCardService(context);
        SuperAppDatabase database = DataBaseClient.getInstance(context, user.phone).getAppDatabase();
        MyIdService myIdService = new MyIdService(context);
        MonitoringService monitoringService = new MonitoringService(context);
        setupViews(context, layout);
        financeViewModel = new FinanceViewModel(getCardHelper, myIdService, monitoringService,database);
        financeViewModel.fetchCardBalance();
        financeViewModel.fetchMyIdInfo();
        financeViewModel.getMonitoring();
        observeViewModel(layout, this);
        layout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View view = layout.findFocus();
                if (view != null) {
                    view.clearFocus();
                }
            }
            return true;
        });

        return layout;
    }
    @SuppressLint("NewApi")
    @Override
    public boolean onBackPressed() {
        navigationTelegram();
        return false;
    }
    public void navigationTelegram(){
        INavigationLayout financeLayout = getParentLayout();
        financeLayout.removeAllFragments();
        financeLayout.presentFragment(new DialogsActivity(null));
        if (financeCardLayout != null && financeCardLayout.getParent() != null) {
            ((ViewGroup) financeCardLayout.getParent()).removeView(financeCardLayout);
        }
        if (layout != null && layout.getParent() != null) {
            ((ViewGroup) layout.getParent()).removeView(layout);
        }
        clearViews();
    }
}
