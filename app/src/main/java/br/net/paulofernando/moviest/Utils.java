package br.net.paulofernando.moviest;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;

import br.net.paulofernando.moviest.communication.TMDB;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class Utils {

    private static AlertDialog alertDialog;

    public static void showAlert(final Context context, String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(context.getResources().getString(R.string.no_internet_title))
                        .setContentText(context.getResources().getString(R.string.no_internet))
                        .show();
            }
        });

    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


}
