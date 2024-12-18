package org.finance.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.Fragment;

import org.telegram.ui.ActionBar.BaseFragment;

/** @noinspection ALL*/
public class PreferencesFragment extends BaseFragment {
    private final Fragment fragment;

    public PreferencesFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public View createView(Context context) {
        return fragment.onCreateView(LayoutInflater.from(context), null, null);
    }
}
