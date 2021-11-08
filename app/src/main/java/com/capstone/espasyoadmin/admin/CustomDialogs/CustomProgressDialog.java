package com.capstone.espasyoadmin.admin.CustomDialogs;

import android.app.ProgressDialog;
import android.content.Context;

public class CustomProgressDialog {

    private Context context;
    private ProgressDialog fetchDataDialog;

    public CustomProgressDialog(Context context) {
        this.context = context;
    }

    public boolean isShowing() {
        return fetchDataDialog.isShowing();
    }

    public void showProgressDialog(String message, boolean isCancelable) {
        fetchDataDialog = new ProgressDialog(context);
        fetchDataDialog.setCancelable(isCancelable);
        fetchDataDialog.setMessage(message);
        fetchDataDialog.show();
    }

    public void dismissProgressDialog() {
        fetchDataDialog.dismiss();
    }

}
