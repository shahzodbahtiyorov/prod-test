package org.finance.ui.paynet.provider;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
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
import com.google.gson.JsonObject;

import org.finance.data.model.paynet.provider.ProviderModel;
import org.finance.data.model.paynet.service.FieldValues;
import org.finance.data.model.paynet.service.PaymentServiceModel;
import org.finance.data.model.paynet.service.Services;
import org.finance.data.service.ApiCallback;
import org.finance.data.service.PaynetService;
import org.finance.ui.paynet.service.CommunalFragment;
import org.finance.ui.paynet.service.PaymentDynamicFragment;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/** @noinspection ALL*/
public class PaynetProviderAdapter extends RecyclerView.Adapter<PaynetProviderAdapter.ProviderViewHolder> {

    private final Context context;
    private final ArrayList<ProviderModel> providerModels;
    private final BaseFragment baseFragment;
    private  final PaynetService paynetService;
    private Dialog progressDialog;
    public PaynetProviderAdapter(Context context, ArrayList<ProviderModel> providerModels,BaseFragment baseFragment,PaynetService payment) {
        this.context = context;
        this.providerModels = providerModels;
        this.baseFragment=baseFragment;
        this.paynetService=payment;
    }

    @NonNull
    @Override
    public ProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = new CardView(context);
        CardView.LayoutParams cardParams = new CardView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(8);
        cardView.setBackgroundResource(R.drawable.payment_card_border);
        cardView.setContentPadding(8, 8, 8, 8);

        LinearLayout itemLayout = new LinearLayout(context);
        itemLayout.setPadding(12,0,12,12);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        cardView.addView(itemLayout);

        ImageView imageView = new ImageView(context);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                AndroidUtilities.dp(48),
                AndroidUtilities.dp(48)

        );
        imageParams.setMargins(0, 0, 0, 8);
        imageView.setLayoutParams(imageParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setBackgroundColor(context.getResources().getColor(R.color.cardview_light_background));
        itemLayout.addView(imageView);

        TextView titleUz = new TextView(context);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        titleUz.setLayoutParams(textParams);
        titleUz.setTextSize(12);
        titleUz.setTextColor(context.getResources().getColor(R.color.design_default_color_on_secondary));
        titleUz.setGravity(Gravity.START);
        itemLayout.addView(titleUz);

        return new ProviderViewHolder(cardView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProviderViewHolder holder, int position) {
        ProviderModel providerModel = providerModels.get(position);

        String titleUz = providerModel.getTitleShort();
        String imageUrl = BuildConfig.STATIC_URL+ "app-superapp/image/provider/"+providerModel.getProviderId()+".png";

        LinearLayout itemLayout = (LinearLayout) ((CardView) holder.itemView).getChildAt(0);
        ImageView imageView = (ImageView) itemLayout.getChildAt(0);
        TextView titleUzTextView = (TextView) itemLayout.getChildAt(1);

        if (titleUz != null && !titleUz.isEmpty()) {
            titleUzTextView.setText(titleUz);
        } else {
            titleUzTextView.setText("Default Title");
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Log.d("GlideDebug", position+"Attempting to load image: " + imageUrl);

            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(imageView);

        } else {
            Log.d("GlideDebug", "Image URL is null or empty. Setting placeholder image.");
            imageView.setImageResource(R.drawable.placeholder_image);
        }
        holder.itemView.setOnClickListener(v -> {
            if (baseFragment != null) {
                onCategoryClick(providerModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return providerModels.size();
    }

    public static class ProviderViewHolder extends RecyclerView.ViewHolder {
        public ProviderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    private void onCategoryClick(ProviderModel category) {
        showProgressDialog();
        JsonObject requestObject = new JsonObject();
        requestObject.addProperty("provider_id",category.getProviderId());


        paynetService.getPaymentService(new ApiCallback<PaymentServiceModel>() {
            @Override
            public void onSuccess(PaymentServiceModel response) {
                dismissProgressDialog();
                if (response != null && response.getServices() != null) {

                   try {
                       List<Services> services=response.getServices();
                       List<FieldValues> fieldValues = services.get(0).getFields().get(0).getFieldValues();
                       if (services.size()==1 &&("MONEY".equals(services.get(0).getFields().get(0).getFieldType()))){
                           Toast.makeText(context,"Ushbu xizmat xaqtincha ishlamayapti",Toast.LENGTH_SHORT).show();
                       }else{
                           if (!fieldValues.isEmpty()) {
                               baseFragment.presentFragment(new CommunalFragment(
                                       response,
                                       BuildConfig.STATIC_URL+ "app-superapp/image/provider/"+category.getProviderId()+".png"
                               ));

                           } if (fieldValues.isEmpty()) {
                               baseFragment.presentFragment(new PaymentDynamicFragment(
                                       response, category.getImageUrl()
                               ));
                           }

                       }
                   }catch (Exception e) {
                       Toast.makeText(context,response.getServices().toString(),Toast.LENGTH_SHORT).show();
                       ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                       if (clipboard != null) {
                           ClipData clip = ClipData.newPlainText("Error", e.toString());
                           clipboard.setPrimaryClip(clip);
                       }
                   }
                } else {
                    Toast.makeText(context,"No services found",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                dismissProgressDialog();
                Toast.makeText(context,errorMessage,Toast.LENGTH_SHORT).show();
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard != null) {
                    ClipData clip = ClipData.newPlainText("Error", errorMessage);
                    clipboard.setPrimaryClip(clip);

                }}
        },requestObject);
    }
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(context);
            progressDialog.setContentView(R.layout.progresbar_dialog);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}



