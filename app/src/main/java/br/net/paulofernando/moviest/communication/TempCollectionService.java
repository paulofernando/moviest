package br.net.paulofernando.moviest.communication;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static okhttp3.Protocol.get;

public class TempCollectionService {

    private static JSONObject jsonCollections;

    public static String getCollection(int collectionId) {
        try {
            JSONArray collections = (JSONArray) jsonCollections.get("collections");
            if(collections.length() >= collectionId) {
                return collections.get(collectionId).toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void getCollectionFromURL(String url, Callback callback) throws Exception {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(callback);
    }

}
