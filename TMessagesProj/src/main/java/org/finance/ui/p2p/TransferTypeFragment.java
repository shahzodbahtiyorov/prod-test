package org.finance.ui.p2p;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import org.finance.ui.home.FinanceFragment;
import org.finance.ui.p2p.local.CardInfoFragment;
import org.finance.ui.p2p.visa.VisaTransferTypeFragment;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;

/** @noinspection ALL*/
public class TransferTypeFragment extends BaseFragment {
    private LinearLayout humoUzcard;
    private  LinearLayout tranferInternational;
    private  LinearLayout visa;
    private final int navigationId;

    public TransferTypeFragment(int navigationId) {
        this.navigationId = navigationId;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View createView(Context context) {
        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(context).inflate(R.layout.transfer_type_layout, null);
        actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
        actionBar.setTitle("Переводы");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
            if (navigationId==1){
                presentFragment(new FinanceFragment());
            }else {
                finishFragment();
            }}
        });
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

    @Override
    public boolean onBackPressed() {
        if (navigationId==1){
            presentFragment(new FinanceFragment());
        }else {
            finishFragment();
        }
        return false;
    }
}
