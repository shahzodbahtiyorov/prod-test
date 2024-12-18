package org.finance.helpers;

import android.view.View;
import android.view.ViewGroup;

public class RemoveView {
    public static void  clearsView(View fragment){
        if (fragment!=null){
            ((ViewGroup) fragment.getParent()).removeView(fragment);
        }
    }
}
