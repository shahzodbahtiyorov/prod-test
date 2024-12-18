package org.finance.ui.paynet.provider;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.finance.data.model.paynet.category.CategoryModel;
import org.finance.data.model.paynet.provider.ProviderModel;
import org.finance.data.model.paynet.provider.ProvidersResponse;
import org.finance.data.service.ApiCallback;
import org.finance.data.service.PaynetService;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

import java.util.ArrayList;
public class PaynetProviderFragment extends BaseFragment {
    private ProgressBar progressBar;
    private TextView errorResult;
    private RecyclerView recyclerView;
    private  View rootView;
    private PaynetService paynetService;
    private final CategoryModel categoryModel;
    private  RelativeLayout layout;
  public PaynetProviderFragment(CategoryModel categoryModel){
      this.categoryModel = categoryModel;

  }

    @Override
    public View createView(Context context) {
        if(rootView==null){
            rootView=viewCreate(context);
        }
        return rootView;
    }

    public View viewCreate(Context context) {
         layout = new RelativeLayout(context);
        layout.setPadding(AndroidUtilities.dp(12),0,AndroidUtilities.dp(12),0);
        layout.setBackgroundColor(Color.parseColor("#F5F5F5"));

        setupActionBar();

        int marginInDp = 8;
        int marginInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, marginInDp, context.getResources().getDisplayMetrics()
        );


        TextView oplatiText = new TextView(context);
        oplatiText.setId(View.generateViewId());
        oplatiText.setText("Виды оплаты");
        oplatiText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        oplatiText.setTextColor(Color.parseColor("#1F1F1F"));

        RelativeLayout.LayoutParams oplatiTextParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        oplatiTextParams.setMargins(marginInPx, marginInPx / 2, marginInPx, 0);
        oplatiText.setLayoutParams(oplatiTextParams);

        layout.addView(oplatiText);
        paynetService = new PaynetService(context);

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

        layout.addView(recyclerView);

        progressBar = new ProgressBar(context);
        RelativeLayout.LayoutParams progressBarParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        progressBarParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        progressBar.setLayoutParams(progressBarParams);
        layout.addView(progressBar);

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
        layout.addView(errorResult);

        loadData();

        return layout;
    }

    private void setupActionBar() {
        actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
        actionBar.setTitle(categoryModel.getTitleRu());
        actionBar.setAllowOverlayTitle(true);
        actionBar.setBackgroundColor(Color.parseColor("#50A7EA"));
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
        JsonObject requestObject = new JsonObject();
        requestObject.addProperty("category_id",categoryModel.getCategoryId());
        progressBar.setVisibility(View.VISIBLE);
        errorResult.setVisibility(View.GONE);

        paynetService.getPaynetprovider(new ApiCallback<ProvidersResponse>() {
            @Override
            public void onSuccess(ProvidersResponse response) {
                progressBar.setVisibility(View.GONE);

                if (response != null && response.getProviders() != null && !response.getProviders().isEmpty()) {
                    setupRecyclerView(response.getProviders());
                } else {
                    showError("Hech qanday ma'lumot topilmadi");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                showError(errorMessage);
            }
        },requestObject);
    }

    private void showError(String message) {
        errorResult.setText(message);
        errorResult.setVisibility(View.VISIBLE);
    }

    private void setupRecyclerView(ArrayList<ProviderModel> categories) {
        recyclerView.setVisibility(View.VISIBLE);
        PaynetProviderAdapter paynetprovider = new PaynetProviderAdapter(getContext(), categories, this, paynetService);
        recyclerView.setAdapter(paynetprovider);
    }
    @Override
    public boolean onBackPressed() {
        if (layout != null && layout.getParent() != null) {
            ((ViewGroup) layout.getParent()).removeView(layout);
        }
        return super.onBackPressed();
    }
}

