package org.finance.ui.paynet.category;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
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
import org.finance.data.model.paynet.category.CategoryResponse;
import org.finance.data.model.paynet.category.CategoryModel;
import org.finance.data.service.ApiCallback;
import org.finance.data.service.PaynetService;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import java.util.ArrayList;
import android.text.Editable;
import android.text.TextWatcher;
public class PaynetCategoryFragment extends BaseFragment {
    private ProgressBar progressBar;
    private TextView errorResult;
    private RecyclerView recyclerView;
    private PaynetService paynetService;
    private CategoryResponse categoryModel;
    private View rootView;
    private  RelativeLayout layout;
    @Override
    public View createView(Context context){
        if(rootView==null){
            rootView=viewCreate(context);
        }
        return rootView;
    }
  private  View viewCreate(Context context) {
       layout = new RelativeLayout(context);
      layout.setPadding(AndroidUtilities.dp(8),0,AndroidUtilities.dp(8),0);
      layout.setBackgroundColor(Color.parseColor("#F5F5F5"));

      setupActionBar();

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
      searchcategory.setPadding(0,12,0,0);
      searchcategory.setHint("Поиск сервиса");
      searchcategory.setTextSize(16);
      searchcategory.setHintTextColor(Color.parseColor("#A0A0A0"));
      searchcategory.setTextColor(Color.BLACK);
      searchcategory.setMaxLines(1);
      searchcategory.setLayoutParams(new ViewGroup.LayoutParams(
           AndroidUtilities.dp(120) ,
           AndroidUtilities.dp(36)
      ));
      searchcategory.setGravity(Gravity.CENTER_VERTICAL);
      searchcategory.setBackgroundResource(0); // Orqa fonni olib tashlash
      searchcategory.setLayoutParams(new LinearLayout.LayoutParams(
              0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
      ));
      searchLayout.addView(searchcategory);

      RelativeLayout.LayoutParams searchLayoutParams = new RelativeLayout.LayoutParams(
              RelativeLayout.LayoutParams.MATCH_PARENT,
              AndroidUtilities.dp(48)
      );
      int marginInDp = 8;
      int marginInPx = (int) TypedValue.applyDimension(
              TypedValue.COMPLEX_UNIT_DIP, marginInDp, context.getResources().getDisplayMetrics()
      );
      searchLayoutParams.setMargins(marginInPx, marginInPx, marginInPx, 0);
      searchLayout.setLayoutParams(searchLayoutParams);
      layout.addView(searchLayout);

      searchcategory.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
              if (s.length() >= 3) {
                  searchCategories(s.toString());
              } else {
                  if (categoryModel != null) {
                      setupRecyclerView(categoryModel.getCategories());
                      errorResult.setVisibility(View.GONE);
                  }
              }
          }

          @Override
          public void afterTextChanged(Editable s) {
          }
      });


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

      GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3); // 3 columns
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

        paynetService.getPaynetCategories(new ApiCallback<CategoryResponse>() {
            @Override
            public void onSuccess(CategoryResponse response) {
                progressBar.setVisibility(View.GONE);

                if (response != null && response.getCategories() != null && !response.getCategories().isEmpty()) {
                    categoryModel=response;
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

        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), categories, this);
        recyclerView.setAdapter(categoryAdapter);
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

    @Override
    public boolean onBackPressed() {
        if (layout != null && layout.getParent() != null) {
            ((ViewGroup) layout.getParent()).removeView(layout);
        }
        return super.onBackPressed();
    }
}
