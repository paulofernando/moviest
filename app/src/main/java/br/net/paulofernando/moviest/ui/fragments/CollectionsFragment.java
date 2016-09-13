package br.net.paulofernando.moviest.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.communication.TempCollectionService;
import br.net.paulofernando.moviest.Utils;
import br.net.paulofernando.moviest.adapters.CollectionsAdapter;
import br.net.paulofernando.moviest.communication.entities.Collection;

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

        if(Utils.isNetworkConnected(getContext())) {
            getData();
        } else {
            Log.e(TAG, getResources().getResourceName(R.string.no_internet));
            Utils.showAlert(getContext(), getResources().getResourceName(R.string.no_internet));
            final Handler handler = new Handler();
            final Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            if(Utils.isNetworkConnected(getContext())) {
                                getData();
                                timer.cancel();
                            }
                        }
                    });
                }
            };
            timer.schedule(timerTask, INTERNET_CHECK_TIME, INTERNET_CHECK_TIME);
        }

        return rootView;
    }

    @Override
    public void loadMoreData(int page) {}

    protected void getData() {
        fillCollections();
        if (CollectionsFragment.this.getActivity() != null) {//there are movies to list
            CollectionsFragment.this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter == null) {
                        mAdapter = new CollectionsAdapter(CollectionsFragment.this.getContext());
                        mAdapter.addMovies(collections);
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        mAdapter.addMovies(collections);
                    }
                    loadingTextView.setVisibility(View.GONE);
                }
            });

        }
    }

    private void fillCollections() {
        Gson gson = new Gson();
        Collection c1 = gson.fromJson(TempCollectionService.getCollection(1), Collection.class);
        collections.add(c1);

        Collection c2 = gson.fromJson(TempCollectionService.getCollection(2), Collection.class);
        collections.add(c2);

        Collection c3 = gson.fromJson(TempCollectionService.getCollection(3), Collection.class);
        collections.add(c3);
    }

}