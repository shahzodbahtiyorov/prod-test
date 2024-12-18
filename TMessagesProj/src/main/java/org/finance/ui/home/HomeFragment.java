package org.finance.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.util.Log;

import org.finance.data.model.myId.MyIdInfoModel;
import org.finance.data.model.paynet.category.CategoryModel;
import org.finance.data.model.paynet.category.CategoryResponse;
import org.finance.data.room.DataBaseClient;
import org.finance.data.room.SuperAppDatabase;
import org.finance.data.service.ApiCallback;
import org.finance.data.service.GetCardService;
import org.finance.data.service.MonitoringService;
import org.finance.data.service.MyIdService;
import org.finance.data.service.PaynetService;
import org.finance.ui.myId.MyIdFragment;
import org.finance.ui.myId.MyIdInfoFragment;
import org.finance.ui.p2p.InternationalTransferFragment;
import org.finance.ui.p2p.local.CardInfoFragment;
import org.finance.ui.p2p.visa.VisaTransferTypeFragment;
import org.finance.ui.paynet.category.CategoryAdapter;
import org.finance.ui.ui_helper.HomeHelper;
import org.telegram.messenger.AndroidUtilities;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** @noinspection ALL*/
public class HomeFragment extends BaseFragment {
    private LinearLayout firstLayout;
    private LinearLayout paymentsLayout;
    private LinearLayout homeLayout;

    private LinearLayout transferLayout;
    private LinearLayout servisesLayout;
    private ProgressBar progressBar;
   private LinearLayout rootView;
    private FinanceViewModel financeViewModel;
    private MyIdInfoModel myIdInfo;
    private TextView errorResult;
    TLRPC.User user;
    private   LinearLayout financeCardLayout;
    private RelativeLayout layout;
    private RecyclerView recyclerView;
    private PaynetService paynetService;
    private CategoryResponse categoryModel;
    private  RelativeLayout paymentslayout;
    private Map<String, View> cachedViews = new HashMap<>();
    private String savedView="financeView";
    private LinearLayout humoUzcard;
    private  LinearLayout tranferInternational;
    private  LinearLayout visa;
    private TextView homeTitle;
    private TextView paymentsTitle;
    private TextView p2pTitle;
    private  ImageView homeImage;
    private  ImageView paymentsImage;
    private  ImageView p2pImage;

public HomeFragment(String initView){
    this.savedView=initView;
}


    @Override
    public View createView(Context context) {
        if (actionBar != null) {
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
        }
        LayoutInflater inflater = LayoutInflater.from(context);
         rootView = (LinearLayout) inflater.inflate(R.layout.home_layout, null);
        firstLayout=rootView.findViewById(R.id.firstLayout);
        paynetService = new PaynetService(context);

        showView(context, savedView);
        //home
        homeTitle=rootView.findViewById(R.id.firstTextView);
        homeImage=rootView.findViewById(R.id.firstImageView);
        //payments
        paymentsTitle=rootView.findViewById(R.id.secondTextView );
        paymentsImage=rootView.findViewById(R.id.secondImageView);
        //P2p
       p2pTitle=rootView.findViewById(R.id.thirdTextView);
       p2pImage=rootView.findViewById(R.id.thirdImageView);

        paymentsLayout=rootView.findViewById(R.id.payments);
        homeLayout = rootView.findViewById(R.id.home_layout);
        paymentsLayout.setOnClickListener(v ->{
                    actionBar.setTitle("Платежи");
                    actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
                    savedView="paymentsView";
                    updatedView(savedView);
                    showView(context, "paymentsView");
                }
            );
        homeLayout.setOnClickListener(v -> {
            actionBar.setBackButtonImage(R.drawable.finance_back_icon);
            actionBar.setTitle(LocaleController.getString(R.string.Finance));
            savedView="financeView";
            updatedView(savedView);
            showView(context, "financeView");});
        transferLayout=rootView.findViewById(R.id.p2p_layout);
        transferLayout.setOnClickListener(v -> {
            actionBar.setTitle("Переводы");
            actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
            savedView="p2pView";
            updatedView(savedView);
            showView(context, "p2pView");});
        updatedView(savedView);
        return rootView;
    }


    //finance view
    public View financeView(Context context) {
        layout= new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        layout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        layout.setLayoutParams(layoutParams);

        user = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
        GetCardService getCardHelper = new GetCardService(context);
        SuperAppDatabase database = DataBaseClient.getInstance(context, user.phone).getAppDatabase();
        MyIdService myIdService = new MyIdService(context);
        MonitoringService monitoringService = new MonitoringService(context);
        setupViews(context, layout);
        layout = new RelativeLayout(context);
        layout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        financeViewModel = new FinanceViewModel(getCardHelper, myIdService, monitoringService, database);
        financeViewModel.fetchCardBalance();
        financeViewModel.fetchMyIdInfo();
        financeViewModel.getMonitoring();
        observeViewModel(layout, this,context);
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
    private int responseCount = 0; // Track the number of responses received
    private int totalResponses = 3; // Total number of responses to wait for (card data, monitoring, and MyIdInfo)

    private void observeViewModel(RelativeLayout layout, BaseFragment baseFragment,Context context) {
        financeViewModel.getAllCards().observeForever(cards -> {
            responseCount++;
            if (responseCount == totalResponses) {
                progressBar.setVisibility(View.GONE); // Hide progress bar once all responses are received
            }

            if (cards != null && !cards.cards.isEmpty()) {
                financeViewModel.getCardMonitoringLiveData().observeForever(monitoringModel -> {
                    if (monitoringModel != null) {
                        financeCardLayout = HomeHelper.createFinanceLayout(context, cards, baseFragment, monitoringModel, financeViewModel);
                        layout.addView(financeCardLayout);
                    }
                });
            }
        });

        financeViewModel.getMyIdInfoLiveData().observeForever(response -> {
            responseCount++;
            if (responseCount == totalResponses) {
                progressBar.setVisibility(View.GONE); // Hide progress bar once all responses are received
            }

            if (response != null) {
                myIdInfo = response;
                ActionBarMenu actionBarMenu = actionBar.createMenu();
                actionBarMenu.clearItems();
                actionBarMenu.addItem(105, R.drawable.user_identifiel_icon);
            }
        });

        financeViewModel.getCardErrorMessage().observeForever(error -> {
            progressBar.setVisibility(View.GONE); // Hide progress bar on error
            if (error.equals("No card data available.")) {
                HomeHelper.setupMainUI(layout, getContext(), baseFragment);
            } else {
                errorResult.setVisibility(View.VISIBLE);
                errorResult.setText(error);
            }
        });

        financeViewModel.getMonitoringErrorMessage().observeForever(monitoringModel -> {
            Log.d("Monitoring error", monitoringModel);
        });

        financeViewModel.getMyIdErrorMessage().observe(getViewLifecycleOwner(), error -> {
            progressBar.setVisibility(View.GONE); // Hide progress bar on error
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

    //payments view
public View paymentsView(Context context) {
    paymentslayout = new RelativeLayout(context);
    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
    );
    paymentslayout.setLayoutParams(layoutParams);
    paymentslayout.setPadding(AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8), 0);
    paymentslayout.setBackgroundColor(Color.parseColor("#F5F5F5"));
    LinearLayout searchLayout = new LinearLayout(context);
    searchLayout.setId(View.generateViewId());
    searchLayout.setOrientation(LinearLayout.HORIZONTAL);
    searchLayout.setPadding(0, 16, 0, 16);
    searchLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_edittext));
    ImageView searchIcon = new ImageView(context);
    searchIcon.setImageResource(R.drawable.ic_search);
    LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
            AndroidUtilities.dp(24), AndroidUtilities.dp(24)
    );
    iconParams.gravity = Gravity.CENTER_VERTICAL;
    searchIcon.setLayoutParams(iconParams);
    searchLayout.addView(searchIcon);
    EditText searchcategory = new EditText(context);
    searchcategory.setPadding(0, 12, 0, 0);
    searchcategory.setHint("Поиск сервиса");
    searchcategory.setTextSize(16);
    searchcategory.setHintTextColor(Color.parseColor("#A0A0A0"));
    searchcategory.setTextColor(Color.BLACK);
    searchcategory.setMaxLines(1);
    LinearLayout.LayoutParams searchCategoryParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
    );
    searchCategoryParams.setMargins(AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8), 0);
    searchcategory.setLayoutParams(searchCategoryParams);
    searchcategory.setGravity(Gravity.CENTER_VERTICAL);
    searchcategory.setBackgroundResource(0); // Orqa fonni olib tashlash
    searchcategory.setLayoutParams(new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
    ));
    searchLayout.addView(searchcategory);
    LinearLayout.LayoutParams searchLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            AndroidUtilities.dp(48)
    );
    int marginInDp = 8;
    int marginInPx = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, marginInDp, context.getResources().getDisplayMetrics()
    );
    searchLayoutParams.setMargins(marginInPx, marginInPx, marginInPx, 0);
    searchLayout.setLayoutParams(searchLayoutParams);
    paymentslayout.addView(searchLayout);
    TextView oplatiText = new TextView(context);
    oplatiText.setId(View.generateViewId());
    oplatiText.setText("Все услуги");
    oplatiText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    oplatiText.setTextColor(Color.parseColor("#1F1F1F"));

    RelativeLayout.LayoutParams oplatiTextParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
    );
    oplatiTextParams.addRule(RelativeLayout.BELOW, searchLayout.getId());
    oplatiTextParams.setMargins(marginInPx, marginInPx / 2, marginInPx, 0);
    oplatiText.setLayoutParams(oplatiTextParams);

    paymentslayout.addView(oplatiText);

    recyclerView = new RecyclerView(context);
    recyclerView.setId(View.generateViewId());
    RelativeLayout.LayoutParams recyclerViewParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
    );
    recyclerViewParams.addRule(RelativeLayout.BELOW, oplatiText.getId());
    recyclerView.setLayoutParams(recyclerViewParams);

    recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.bottom = AndroidUtilities.dp(8);
            outRect.left =  AndroidUtilities.dp(8);
            outRect.right =  AndroidUtilities.dp(8);
            outRect.top =  AndroidUtilities.dp(8);
        }
    });
    recyclerView.setVisibility(View.GONE);

    GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
    recyclerView.setLayoutManager(gridLayoutManager);
    paymentslayout.addView(recyclerView);

    progressBar = new ProgressBar(context);
    RelativeLayout.LayoutParams progressBarParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
    );
    progressBarParams.addRule(RelativeLayout.CENTER_IN_PARENT);
    progressBar.setLayoutParams(progressBarParams);
    paymentslayout.addView(progressBar);
    errorResult = new TextView(context);
    RelativeLayout.LayoutParams errorResultParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
    );
    errorResultParams.addRule(RelativeLayout.CENTER_IN_PARENT);
    errorResult.setLayoutParams(errorResultParams);
    errorResult.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
    errorResult.setTextSize(16);
    errorResult.setVisibility(View.GONE);
    paymentslayout.addView(errorResult);
    loadData();
    return paymentslayout;
}
    private void setupActionBar() {
        actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
        actionBar.setTitle("Платежи");
        actionBar.setBackgroundColor(Color.parseColor("#50A7EA"));
        actionBar.setAllowOverlayTitle(true);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment(true);
                    if (layout != null && layout.getParent() != null) {
                        ((ViewGroup) layout.getParent()).removeView(layout);
                    }
                }
            }
        });
    }
    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        errorResult.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        paynetService.getPaynetCategories(new ApiCallback<CategoryResponse>() {
            @Override
            public void onSuccess(CategoryResponse response) {
                progressBar.setVisibility(View.GONE);
                Log.d("Categories", "Data: " + response.getCategories().get(0).getTitleUz().toString());

                if (response != null && response.getCategories() != null && !response.getCategories().isEmpty()) {
                    categoryModel = response;
                    setupRecyclerView(response.getCategories());
                } else {
                    showError("Hech qanday ma'lumot topilmadi");
                }
            }
            @Override
            public void onFailure(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                showError(errorMessage);
            }
        });
    }
    private void showError(String message) {
        errorResult.setText(message);
        errorResult.setVisibility(View.VISIBLE);
    }

    private void setupRecyclerView(ArrayList<CategoryModel> categories) {
        recyclerView.setVisibility(View.VISIBLE);
        errorResult.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), categories, this);
        recyclerView.setAdapter(categoryAdapter);
        categoryAdapter.notifyDataSetChanged();
    }

    private void searchCategories(String query) {
        progressBar.setVisibility(View.VISIBLE);
        errorResult.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        paynetService.searchPaynetCategories(query, new ApiCallback<ArrayList<CategoryModel>>() {
            @Override
            public void onSuccess(ArrayList<CategoryModel> response) {
                progressBar.setVisibility(View.GONE);

                if (response != null && !response.isEmpty()) {
                    setupRecyclerView(response);
                } else {
                    showError("Topilmadi");
                }
            }
            @Override
            public void onFailure(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                showError(errorMessage);
            }
        });
    }
    //P2P view
    public View p2pView(Context context) {
        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(context).inflate(R.layout.transfer_type_layout, null);
        actionBar.setBackgroundColor(Color.parseColor("#50A7EA"));
        actionBar.setAllowOverlayTitle(true);
        humoUzcard=rootView.findViewById(R.id.humo_uzcard);
        tranferInternational=rootView.findViewById(R.id.international_transfer);
        visa=rootView.findViewById(R.id.transfer_visa);
        if (humoUzcard == null || tranferInternational == null || visa == null) {
            throw new NullPointerException("One or more required views are missing in the layout file.");
        }
        humoUzcard.setOnClickListener(v -> {
            presentFragment(new CardInfoFragment(0));
        });
        tranferInternational.setOnClickListener(v -> {
            presentFragment(new InternationalTransferFragment());
        });
        visa.setOnClickListener(v -> {
            presentFragment(new VisaTransferTypeFragment());
        });
        rootView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        rootView.setOnTouchListener((View v, @SuppressLint("ClickableViewAccessibility") MotionEvent event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                @SuppressLint("ClickableViewAccessibility") View view = rootView.findFocus();
                if (view != null) {
                    view.clearFocus();
                }
            }
            return true;
        });
        return rootView;
    }
    private void showView(Context context, String viewName) {
        View view = cachedViews.get(viewName);

        if (view == null) {
            switch (viewName) {
                case "financeView":
                    view = financeView(context);
                    break;
                case "paymentsView":
                    view = paymentsView(context);
                    break;
                case "p2pView":
                    view = p2pView(context);
                    break;
            }
            cachedViews.put(viewName, view);
        }

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }

            firstLayout.removeAllViews();
            view.setVisibility(View.VISIBLE);
            firstLayout.addView(view);
        }
        firstLayout.invalidate();
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
        if (rootView != null && rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        clearViews();
    }
    private void updatedView(String savedView) {
        setViewState(homeTitle, homeImage, "home", savedView);
        setViewState(p2pTitle, p2pImage, "p2p", savedView);
        setViewState(paymentsTitle, paymentsImage, "payments", savedView);
    }

    private void setViewState(TextView title, ImageView image, String viewName, String savedView) {
        int titleColor;
        int imageResId;
        switch (viewName) {
            case "home":
                titleColor = savedView.equals("financeView") ? Color.parseColor("#50A7EA") : Color.parseColor("#959595");
                imageResId = savedView.equals("financeView") ? R.drawable.ic_home : R.drawable.home_icon;
                break;
            case "p2p":

                titleColor = savedView.equals("p2pView") ? Color.parseColor("#50A7EA") : Color.parseColor("#959595");
                imageResId = savedView.equals("p2pView") ? R.drawable.transfer_icon : R.drawable.ic_transfers;
                break;
            case "payments":
                titleColor = savedView.equals("paymentsView") ? Color.parseColor("#50A7EA") : Color.parseColor("#959595");
                imageResId = savedView.equals("paymentsView") ? R.drawable.payments_icon : R.drawable.ic_payments;
                break;
            default:
                titleColor = Color.parseColor("#959595");
                imageResId = R.drawable.ic_transfers;
                break;
        }
        title.setTextColor(titleColor);
        image.setImageDrawable(ContextCompat.getDrawable(getContext(), imageResId));
    }

    @Override
    public boolean onBackPressed() {
        navigationTelegram();
        return super.onBackPressed();
    }
}
