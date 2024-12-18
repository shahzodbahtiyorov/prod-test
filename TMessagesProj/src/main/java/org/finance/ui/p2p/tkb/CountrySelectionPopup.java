package org.finance.ui.p2p.tkb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.telegram.messenger.R;

public class CountrySelectionPopup {

    private final Context context;
    private PopupWindow popupWindow;
    private final TextView countryText;
    private final ImageView flagIcon;
    private String selectedCountry;
    private final CountrySelectionCallback callback;

    public CountrySelectionPopup(Context context, TextView countryText, ImageView flagIcon, CountrySelectionCallback callback) {
        this.context = context;
        this.countryText = countryText;
        this.flagIcon = flagIcon;
        this.callback = callback;
    }
    public void setSelectedCountry(String country) {
        this.selectedCountry = country;
        Log.d("CountrySelectionPopup", "Selected Country: " + country);
        if (popupWindow != null && popupWindow.isShowing()) {
            View popupView = popupWindow.getContentView();
            if (popupView != null) {
                ImageView uzbekistanCheckIcon = popupView.findViewById(R.id.uzbekistanCheckIcon);
                ImageView russiaCheckIcon = popupView.findViewById(R.id.russiaCheckIcon);

                // Correct visibility of check icons
                uzbekistanCheckIcon.setVisibility("O'zbekiston".equals(selectedCountry) ? View.VISIBLE : View.GONE);
                russiaCheckIcon.setVisibility("Rossiya".equals(selectedCountry) ? View.VISIBLE : View.GONE);
            }
        }
    }

    public void show(View anchorView) {
        Log.d("CountrySelectionPopup", "Showing popup for: " + selectedCountry);

        View popupView = LayoutInflater.from(context).inflate(R.layout.country_dropdown_menu, null);
        popupWindow = new PopupWindow(popupView, anchorView.getWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        LinearLayout uzbekistanOption = popupView.findViewById(R.id.uzbekistanOption);
        LinearLayout russiaOption = popupView.findViewById(R.id.russiaOption);
        ImageView uzbekistanCheckIcon = popupView.findViewById(R.id.uzbekistanCheckIcon);
        ImageView russiaCheckIcon = popupView.findViewById(R.id.russiaCheckIcon);

        updateCheckIcons(uzbekistanCheckIcon, russiaCheckIcon);

        uzbekistanOption.setOnClickListener(v -> {
            Log.d("CountrySelectionPopup", "Uzbekistan selected");
            selectedCountry = "O'zbekiston";
            updatePopupUI("O'zbekiston", uzbekistanCheckIcon, russiaCheckIcon);
        });

        russiaOption.setOnClickListener(v -> {
            Log.d("CountrySelectionPopup", "Russia selected");
            selectedCountry = "Rossiya";
            updatePopupUI("Rossiya", uzbekistanCheckIcon, russiaCheckIcon);
        });

        popupWindow.showAsDropDown(anchorView);
    }

    private void updatePopupUI(String country, ImageView uzbekistanCheckIcon, ImageView russiaCheckIcon) {
        countryText.setText(country);
        flagIcon.setImageResource("O'zbekiston".equals(country) ? R.drawable.ic_uz_flags : R.drawable.ic_ru_flags);
        updateCheckIcons(uzbekistanCheckIcon, russiaCheckIcon);
        popupWindow.dismiss();

        if (callback != null) {
            callback.onCountrySelected(country);
        }
    }


    private void updateCheckIcons(ImageView uzbekistanCheckIcon, ImageView russiaCheckIcon) {
        uzbekistanCheckIcon.setVisibility("O'zbekiston".equals(selectedCountry) ? View.VISIBLE : View.GONE);
        russiaCheckIcon.setVisibility("Rossiya".equals(selectedCountry) ? View.VISIBLE : View.GONE);
    }

    public interface CountrySelectionCallback {
        void onCountrySelected(String selectedCountry);
    }
}
