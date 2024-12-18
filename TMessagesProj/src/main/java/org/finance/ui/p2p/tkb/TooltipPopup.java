package org.finance.ui.p2p.tkb;

import static com.google.android.material.internal.ViewUtils.dpToPx;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;

public class TooltipPopup {

    private  PopupWindow popupWindow;
    private final Context context;
    private String title;
    public TooltipPopup(Context context,String title) {
        this.context=context;
        this.title=title;

    }

    public void show(View anchorView) {
        @SuppressLint("InflateParams") View tooltipLayout = LayoutInflater.from(context).inflate(R.layout.tooltip_layout, null);
        TextView titleView=tooltipLayout.findViewById(R.id.tooltipText);
        titleView.setText(title);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int tooltipWidth = screenWidth - AndroidUtilities.dp(32); // 16dp chap + 16dp o'ng
        popupWindow = new PopupWindow(tooltipLayout,tooltipWidth,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(anchorView);
    }
    public void dismiss() {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }
}
