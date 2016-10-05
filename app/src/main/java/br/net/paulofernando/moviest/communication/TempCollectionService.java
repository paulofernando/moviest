package br.net.paulofernando.moviest.communication;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class TempCollectionService {

    public static void getCollectionFromURL(String url, Callback callback) throws Exception {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(callback);
    }

}
