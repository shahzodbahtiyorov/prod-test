package org.finance.ui.nav_bar;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;


/** @noinspection ALL*/
public class NavBarFragment extends BaseFragment {

    private FrameLayout fragmentContainer;
    private BottomNavigationView bottomNavigationView;

    @Override
    public View createView(Context context) {
        RelativeLayout layout = new RelativeLayout(context);
        layout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        ));

        fragmentContainer = new FrameLayout(context);
        fragmentContainer.setId(View.generateViewId());

        RelativeLayout.LayoutParams fragmentContainerParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        fragmentContainerParams.addRule(RelativeLayout.ABOVE, View.generateViewId());
        layout.addView(fragmentContainer, fragmentContainerParams);

        bottomNavigationView = new BottomNavigationView(context);
        bottomNavigationView.setId(View.generateViewId());

        RelativeLayout.LayoutParams bottomParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        bottomParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bottomNavigationView.setLayoutParams(bottomParams);

        // Add menu items
        bottomNavigationView.getMenu().add(0, 1, 0, "Home").setIcon(R.drawable.heart_confetti);
        bottomNavigationView.getMenu().add(0, 2, 1, "Profile").setIcon(R.drawable.finance);

        // Handle menu item clicks
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case 1:
                        return true;
                    case 2:
                        return true;
                }
                return false;
            }
        });

        layout.addView(bottomNavigationView, bottomParams);

        // Show default fragment (FinanceFragment)

        return layout;
    }

    // Replace the fragment in fragmentContainer while keeping NavBarFragment active

}
