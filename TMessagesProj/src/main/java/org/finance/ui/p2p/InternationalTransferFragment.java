package org.finance.ui.p2p;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;

import org.finance.ui.p2p.tkb.CountrySelectionPopup;
import org.finance.ui.p2p.tkb.TransferRFUZFragment;
import org.finance.ui.p2p.tkb.TransferUzRfFragment;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;

public class InternationalTransferFragment extends BaseFragment {

    private String senderCountry = "Rossiya";
    private String receiverCountry = "O'zbekiston";
    private TextView senderCountryText;
    private TextView receiverCountryText;
    private ImageView senderFlagIcon;
    private ImageView receiverFlagIcon;

    private CountrySelectionPopup senderCountryPopup;
    private CountrySelectionPopup receiverCountryPopup;
private View rootView;
    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility", "InflateParams"})
    @Nullable
    @Override
    public View createView(Context context) {
         rootView = LayoutInflater.from(context).inflate(R.layout.international_fragment_layout, null);

        actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
        actionBar.setTitle("Международные переводы");
        actionBar.setAllowOverlayTitle(true);
        actionBar.setBackgroundColor(Color.parseColor("#50A7EA"));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                    clearViews();
                    ((ViewGroup) rootView.getParent()).removeView(rootView);
                }
            }
        });

        senderCountryText = rootView.findViewById(R.id.senderCountryText);
        receiverCountryText = rootView.findViewById(R.id.receiverCountryText);
        senderFlagIcon = rootView.findViewById(R.id.senderFlagIcon);
        receiverFlagIcon = rootView.findViewById(R.id.receiverFlagIcon);
        LinearLayout senderCountryContainer = rootView.findViewById(R.id.senderCountryContainer);
        LinearLayout receiverCountryContainer = rootView.findViewById(R.id.receiverCountryContainer);
        Button nextButton = rootView.findViewById(R.id.nextButton);

        senderCountryPopup = new CountrySelectionPopup(context, senderCountryText, senderFlagIcon, this::updateCountriesRu);
        receiverCountryPopup = new CountrySelectionPopup(context, receiverCountryText, receiverFlagIcon, this::updateCountriesUz);

        senderCountryPopup.setSelectedCountry(senderCountry);
        receiverCountryPopup.setSelectedCountry(receiverCountry);

        setCountryDisplay(senderCountry, senderCountryText, senderFlagIcon);
        setCountryDisplay(receiverCountry, receiverCountryText, receiverFlagIcon);

        senderCountryContainer.setOnClickListener(v -> {
            senderCountryPopup.setSelectedCountry(senderCountry);
            senderCountryPopup.show(senderCountryContainer);
        });

        receiverCountryContainer.setOnClickListener(v -> {

            receiverCountryPopup.setSelectedCountry(receiverCountry);
            receiverCountryPopup.show(receiverCountryContainer);
        });


        nextButton.setOnClickListener(v -> {
            if (senderCountryText.getText().equals("Rossiya")){
                presentFragment(new TransferRFUZFragment());
            }else {
                presentFragment(new TransferUzRfFragment());
            }

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

    public void updateCountriesRu(String selectedCountry) {
        Log.d("InternationalTransfer", "Updating countries based on selected country: " + selectedCountry);

        if ("O'zbekiston".equals(selectedCountry)) {
            senderCountry = "O'zbekiston";
            receiverCountry = "Rossiya";
        } else  {
            senderCountry = "Rossiya";
            receiverCountry = "O'zbekiston";
        }


        setCountryDisplay(receiverCountry, receiverCountryText, receiverFlagIcon);
        setCountryDisplay(senderCountry, senderCountryText, senderFlagIcon);

        receiverCountryPopup.setSelectedCountry(receiverCountry);
        senderCountryPopup.setSelectedCountry(senderCountry);
        Log.d("InternationalTransfer", "Updated Sender: " + senderCountry);
        Log.d("InternationalTransfer", "Updated Receiver: " + receiverCountry);
    }

    public void updateCountriesUz(String selectedCountry) {
        Log.d("InternationalTransfer", "Updating countries based on selected country: " + selectedCountry);

        if ("O'zbekiston".equals(selectedCountry)) {
            senderCountry = "Rossiya";
            receiverCountry = "O'zbekiston";
        } else if ("Rossiya".equals(selectedCountry)) {
            senderCountry = "O'zbekiston";
            receiverCountry = "Rossiya";
        }

        setCountryDisplay(senderCountry, senderCountryText, senderFlagIcon);
        setCountryDisplay(receiverCountry, receiverCountryText, receiverFlagIcon);

        senderCountryPopup.setSelectedCountry(senderCountry);
        receiverCountryPopup.setSelectedCountry(receiverCountry);

        Log.d("InternationalTransfer", "Updated Sender: " + senderCountry);
        Log.d("InternationalTransfer", "Updated Receiver: " + receiverCountry);
    }


    private void setCountryDisplay(String country, TextView countryText, ImageView flagIcon) {
        countryText.setText(country);
        if (country.equals("Rossiya")) {
            flagIcon.setImageResource(R.drawable.ic_ru_flags);
        } else if (country.equals("O'zbekiston")) {
            flagIcon.setImageResource(R.drawable.ic_uz_flags);
        }
    }
    @Override
    public boolean onBackPressed() {
        finishFragment();
        ((ViewGroup) rootView.getParent()).removeView(rootView);
        return false;
    }
}
