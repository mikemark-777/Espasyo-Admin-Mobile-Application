package com.capstone.espasyoadmin.connectivityUtil;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.capstone.espasyoadmin.R;

public class InternetConnectionUtil {

    private Context context;
    private ConnectivityManager connectivityManager;
    private NetworkInfo mobileConnection;
    private NetworkInfo wifiConnection;

    public InternetConnectionUtil(Context context, ConnectivityManager connectivityManager) {
        this.context = context;
        this.connectivityManager = connectivityManager;
        mobileConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    }

    public boolean isConnectedToInternet() {
        if(mobileConnection != null && mobileConnection.isConnected() || wifiConnection != null && wifiConnection.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public void showNoInternetConnectionDialog() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.admin_no_internet_connection_dialog, null);

        Button btnOkayInternetConnection = view.findViewById(R.id.btnOkayInternetConnection);
        AlertDialog noInternetDialog = new AlertDialog.Builder(context).setView(view).create();
        btnOkayInternetConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noInternetDialog.dismiss();
            }
        });
        noInternetDialog.show();
    }

}
