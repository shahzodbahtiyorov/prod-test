package org.finance.ui.monitoring;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.gson.JsonObject;

import org.finance.data.model.monitoring.MonitoringModel;
import org.finance.data.service.ApiCallback;
import org.finance.data.service.MonitoringService;
import org.finance.helpers.SuperAppFormatters;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;

import okhttp3.ResponseBody;

public class MonitoringInfoFragment extends BaseFragment {
    private final MonitoringModel.Result result;
    private  View rootView;
    public MonitoringInfoFragment(MonitoringModel.Result result){
        this.result=result;
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
        actionBar.setTitle("Чек перевода");
        actionBar.setAllowOverlayTitle(true);
        actionBar.setBackgroundColor(Color.parseColor("#50A7EA"));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    if (rootView != null && rootView.getParent() != null) {
                        ((ViewGroup) rootView.getParent()).removeView(rootView);
                    }
                    finishFragment(true);
                }
            }
        });
         rootView = LayoutInflater.from(context).inflate(R.layout.monitoring_info_layout, null);
        TextView amount=rootView.findViewById(R.id.transferAmount);
        amount.setText(result.getTotalAmount()-result.getTotalAmount()*(result.getTransactionCommission()/100)+" "+SuperAppFormatters.getCurrencyName(result.getCurrency()));
        TextView receiverCardNumber=rootView.findViewById(R.id.receiverCardNumber);
        receiverCardNumber.setText(SuperAppFormatters.maskCardNumber(result.getReceiver()));
        TextView senderCardNumber=rootView.findViewById(R.id.senderCardNumber);
        senderCardNumber.setText(SuperAppFormatters.maskCardNumber(result.getSender()));
        TextView transactionDate=rootView.findViewById(R.id.transactionDate);
        transactionDate.setText(SuperAppFormatters.formatDate(result.getCreatedAt())+", "+result.getDatedAt());
        TextView transfer_fre=rootView.findViewById(R.id.transferFee);
        transfer_fre.setText(result.getTotalAmount()*result.getTransactionCommission()+" "+SuperAppFormatters.getCurrencyName(result.getCurrency()));
        TextView totalAmount=rootView.findViewById(R.id.totalAmount);
        totalAmount.setText(result.getTotalAmount()+" "+SuperAppFormatters.getCurrencyName(result.getCurrency()));
        ImageView status_icon=rootView.findViewById(R.id.status_icon);
        TextView status_title=rootView.findViewById(R.id.status_title);
        if (result.getState()==4){
            status_title.setText("Подтверждено");
            status_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.circle_chek_icon));
        }else if (result.getState() == 21) {
            status_title.setText("Отменено");

            status_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.circle_xmark_icon));
        } else if (result.getState() == 0){
            status_title.setText("Создано");
        }else if (result.getState() == 5){
            status_title.setText("Отклонено");
        }else if (result.getState() == 31){
            status_title.setText("В процессе");
        }
        LinearLayout pdfDownload=rootView.findViewById(R.id.headerContainerPdf);
        ProgressBar progressBar=rootView.findViewById(R.id.progress_bar);
        pdfDownload.setOnClickListener(v -> {
            JsonObject jsonObject=new JsonObject();
            progressBar.setVisibility(View.VISIBLE);
            jsonObject.addProperty("id",result.getId());
            MonitoringService monitoringService=new MonitoringService(context);
            monitoringService.pdfDownload(new ApiCallback<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody response) {
                    progressBar.setVisibility(View.GONE);
                }
                @Override
                public void onFailure(String errorMessage) {
                    progressBar.setVisibility(View.GONE);
                }
            },jsonObject);
        });



        return rootView;
    }
    @Override
    public boolean onBackPressed() {
        if (rootView != null && rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        return super.onBackPressed();
    }
}
