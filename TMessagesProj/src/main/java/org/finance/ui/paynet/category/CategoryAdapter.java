package org.finance.ui.paynet.category;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.PictureDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;

import org.finance.data.model.paynet.category.CategoryModel;
import org.finance.ui.paynet.provider.PaynetProviderFragment;
import org.finance.ui.paynet.src.SvgSoftwareLayerSetter;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/** @noinspection ALL*/
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final Context context;
    private final ArrayList<CategoryModel> categories;
    private final BaseFragment baseFragment;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> categories, BaseFragment baseFragment) {
        this.context = context;
        this.baseFragment = baseFragment;

        // Filter and sort categories
        this.categories = new ArrayList<>();
        for (CategoryModel category : categories) {
            if (category.getIsActive()) {
                this.categories.add(category);
            }
        }

        Collections.sort(this.categories, new Comparator<CategoryModel>() {
            @Override
            public int compare(CategoryModel c1, CategoryModel c2) {
                return Boolean.compare(!c1.getIsActive(), !c2.getIsActive());
            }
        });
    }


    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = new CardView(context);
        CardView.LayoutParams cardParams = new CardView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(8);
        cardView.setCardElevation(4);
        cardView.setBackgroundResource(R.drawable.payment_card_border);
        cardView.setContentPadding(8, 8, 8, 8);

        LinearLayout itemLayout = new LinearLayout(context);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(12, 0, 12, 12);
        cardView.addView(itemLayout);

        ImageView imageView = new ImageView(context);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                AndroidUtilities.dp(58),
                AndroidUtilities.dp(58)
        );
        imageParams.setMargins(0, 0, 0, 8);
        imageView.setLayoutParams(imageParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setBackgroundColor(context.getResources().getColor(R.color.cardview_light_background));
        itemLayout.addView(imageView);

        TextView titleUz = new TextView(context);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleUz.setLayoutParams(textParams);
        titleUz.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        titleUz.setTypeface(Typeface.create("SF Pro Text", Typeface.NORMAL));
        titleUz.setGravity(Gravity.START);
        titleUz.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        titleUz.setTextColor(Color.parseColor("#1F1F1F"));
        itemLayout.addView(titleUz);

        return new CategoryViewHolder(cardView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryModel category = categories.get(position);

        String titleUz = category.getTitleUz();
        String imageUrl = BuildConfig.STATIC_URL + "app-superapp/image/category/" + category.getCategoryId() + ".svg";

        LinearLayout itemLayout = (LinearLayout) ((CardView) holder.itemView).getChildAt(0);
        ImageView imageView = (ImageView) itemLayout.getChildAt(0);
        TextView titleUzTextView = (TextView) itemLayout.getChildAt(1);

        titleUzTextView.setText(titleUz);

        Log.d("GlideDebug", position + " Attempting to load SVG: " + imageUrl);
        RequestBuilder<PictureDrawable> requestBuilder = Glide.with(context)
                .as(PictureDrawable.class)
                .listener(new SvgSoftwareLayerSetter())
                .override(AndroidUtilities.dp(36), AndroidUtilities.dp(36))
                .error(R.drawable.placeholder_image);

        requestBuilder
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(imageView);

        holder.itemView.setOnClickListener(v -> {
           if(category.getIsActive()){
               if (baseFragment != null) {
                   onCategoryClick(category);
               }
           }else {
               Toast.makeText(context,"Xizmat vaqtinchalik ishlamayapti",Toast.LENGTH_LONG).show();
           }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private void onCategoryClick(CategoryModel category) {
        if (baseFragment != null) {
            baseFragment.presentFragment(new PaynetProviderFragment(category));
        }
    }
}
