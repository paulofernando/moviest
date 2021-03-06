package br.net.paulofernando.moviest.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;

import br.net.paulofernando.moviest.R;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class NetworkUtils {

    private static SweetAlertDialog alertDialog;
    public static final int INTERNET_CHECK_TIME = 5000;

    public static void showAlert(final Context context, String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                alertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(context.getResources().getString(R.string.title_warning))
                        .setContentText(context.getResources().getString(R.string.message_no_internet));
                alertDialog.show();
            }
        });
    }

    public static void closeCurrentAlertDialog() {
        if(alertDialog.isShowing()) {
            alertDialog.dismissWithAnimation();
        }
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


}