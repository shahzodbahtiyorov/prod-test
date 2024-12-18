package org.finance.ui.p2p.visa;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;

/** @noinspection ALL*/
public class VisaTransferTypeFragment extends BaseFragment {
    private LinearLayout humoUzcard;
    private  LinearLayout tranferInternational;

    private  LinearLayout visa;
    TextView  header_text,humo_uzcard_text,international_transfer_text,transfer_visa_text;
    ImageView humo_uzcard_icon,international_transfer_icon,transfer_visa_icon;
    @SuppressLint("SetTextI18n")
    @Override
    public View createView(Context context) {
        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(context).inflate(R.layout.transfer_type_layout, null);
        actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
        actionBar.setTitle("Visa kartalariga o'tkazma");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                    clearViews();
                }
            }
        });
        actionBar.setBackgroundColor(Color.parseColor("#50A7EA"));
        actionBar.setAllowOverlayTitle(true);
        header_text=rootView.findViewById(R.id.header_text);
        humo_uzcard_icon=rootView.findViewById(R.id.humo_uzcard_icon);
        humo_uzcard_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.universalbank_visa_icon));
        humo_uzcard_text=rootView.findViewById(R.id.humo_uzcard_text);
        header_text.setText("Qanday qilib pul yuborish kerak?");
        humo_uzcard_text.setText("Universalbank Visa kartalari orasida");
        transfer_visa_icon=rootView.findViewById(R.id.transfer_visa_icon);
         transfer_visa_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.uz_visa_icon));

        transfer_visa_text=rootView.findViewById(R.id.transfer_visa_text);
        transfer_visa_text.setText("Uzcard | Humo dan istalgan Visaga");
        humoUzcard=rootView.findViewById(R.id.humo_uzcard);
        international_transfer_icon=rootView.findViewById(R.id.international_transfer_icon);
        international_transfer_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rf_visa_icon));
        international_transfer_text=rootView.findViewById(R.id.international_transfer_text);
        international_transfer_text.setText("Rossiyadan istalgan Visa kartasiga");
        tranferInternational=rootView.findViewById(R.id.international_transfer);
        visa=rootView.findViewById(R.id.transfer_visa);
        if (humoUzcard == null || tranferInternational == null || visa == null) {
            throw new NullPointerException("One or more required views are missing in the layout file.");
        }
        humoUzcard.setOnClickListener(v -> {
        });
        tranferInternational.setOnClickListener(v -> {
        });
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
}

