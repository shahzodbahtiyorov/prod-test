package org.finance.ui.ui_helper;

import android.app.Dialog;
import android.content.Context;

import org.telegram.messenger.R;

import java.util.Objects;

public class ProgresBarDialog {
   private final Context context;
  public   ProgresBarDialog(Context context){
      this.context=context;
  }
    private Dialog progressDialog;
    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(context);
            progressDialog.setContentView(R.layout.progresbar_dialog);
            progressDialog.setCancelable(false);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        }

        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
