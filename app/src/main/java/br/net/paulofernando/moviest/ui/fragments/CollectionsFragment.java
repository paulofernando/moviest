package br.net.paulofernando.moviest.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.Utils;
import br.net.paulofernando.moviest.adapters.CollectionsAdapter;
import br.net.paulofernando.moviest.communication.TempCollectionService;
import br.net.paulofernando.moviest.communication.entities.Collection;
import br.net.paulofernando.moviest.communication.entities.Collections;
import br.net.paulofernando.moviest.storage.CacheManager;
import br.net.paulofernando.moviest.ui.component.DividerItemDecoration;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static br.net.paulofernando.moviest.Utils.INTERNET_CHECK_TIME;
import static br.net.paulofernando.moviest.communication.TMDB.Services.CollectionsService;
import static br.net.paulofernando.moviest.storage.CacheManager.getCacheExpiration;

public class CollectionsFragment extends BaseFragment {

    private static final String TAG = "CollectionsFragment";
    protected CollectionsAdapter mAdapter;
    private List<Collection> collections = new ArrayList<>();

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CollectionsFragment newInstance(int sectionNumber) {
        CollectionsFragment collectionsFragment = new CollectionsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SERVICE_TYPE, sectionNumber);
        collectionsFragment.setArguments(args);
        return collectionsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.divider));
        getData();
        return rootView;
    }

    @Override
    public void loadMoreData(int page) {}

    private void getData() {

        if(!CacheManager.hasExpired(CollectionsService.toString(), getCacheExpiration(CollectionsService))) {
            Log.d(TAG, "Getting collections from cache");
            Reservoir.getAsync(CollectionsService.toString(), Collections.class, new ReservoirGetCallback<Collections>() {
                @Override
                public void onSuccess(final Collections collections) {
                    if ((collections != null) && (CollectionsFragment.this.getActivity() != null)) {//there are movies to list
                        CollectionsFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateList(collections.colls);
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            if(Utils.isNetworkConnected(getContext())) {
                getCollectionFromServer();
            }
        }

        if(!Utils.isNetworkConnected(getContext())) {
            runInternetChecking();
        }

    }

    private void runInternetChecking() {
        Log.e(TAG, getResources().getResourceName(R.string.no_internet));
        Utils.showAlert(getContext(), getResources().getResourceName(R.string.no_internet));
        final Handler handler = new Handler();
        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if(Utils.isNetworkConnected(getContext())) {
                            getCollectionFromServer();
                            Utils.closeCurrentAlertDialog();
                            timer.cancel();
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, INTERNET_CHECK_TIME, INTERNET_CHECK_TIME);
    }

    private void getCollectionFromServer() {
        Log.d(TAG, "Getting collections from server");
        try {
            TempCollectionService.getCollectionFromURL("http://paulofernando.net.br/moviest/collections/collections.json",
                    new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String json = response.body().string();
                            if (!response.isSuccessful()) {
                                throw new IOException("Unexpected code " + response);
                            }
                            parseCollection(json);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseCollection(String JSONCollection) {
        try {
            JSONObject jsonCollections = new JSONObject(JSONCollection);
            JSONArray jsonCollection = (JSONArray) jsonCollections.get("collections");
            for (int i = 0; i < jsonCollection.length(); i++) {
                collections.add((new Gson()).fromJson(jsonCollection.get(i).toString(), Collection.class));
            }

            Collections c = new Collections();
            c.colls = collections;
            c.version = (Integer) jsonCollections.get("version");
            CacheManager.cacheCollection(c);

            if (CollectionsFragment.this.getActivity() != null) {//there are movies to list
                CollectionsFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateList(collections);
                    }
                });

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void updateList(List<Collection> collections) {
        if (mAdapter == null) {
            mAdapter = new CollectionsAdapter(CollectionsFragment.this.getContext());
            mAdapter.addList(collections);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mAdapter.addList(collections);
        }
        loadingTextView.setVisibility(View.GONE);
    }

}