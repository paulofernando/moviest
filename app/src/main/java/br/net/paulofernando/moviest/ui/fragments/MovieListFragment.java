package br.net.paulofernando.moviest.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.Utils;
import br.net.paulofernando.moviest.adapters.MovieListAdapter;
import br.net.paulofernando.moviest.communication.TMDB;
import br.net.paulofernando.moviest.communication.entities.Movie;
import br.net.paulofernando.moviest.communication.entities.Page;
import br.net.paulofernando.moviest.storage.CacheManager;
import br.net.paulofernando.moviest.ui.component.DividerItemDecoration;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static br.net.paulofernando.moviest.communication.TMDB.API_KEY;
import static br.net.paulofernando.moviest.communication.TMDB.Services.*;
import static br.net.paulofernando.moviest.storage.CacheManager.*;
import static br.net.paulofernando.moviest.Utils.INTERNET_CHECK_TIME;

public class MovieListFragment extends BaseFragment {

    private static final String TAG = "MovieListFragment";
    private static final int DEFAULT_MAX_PAGE = 5;
    private static final int TOP_MAX_PAGE = 5;

    private TMDB.Services serviceType;

    protected MovieListAdapter mAdapter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MovieListFragment newInstance(TMDB.Services serviceType) {
        MovieListFragment movieListFragment = new MovieListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SERVICE_TYPE, serviceType);
        movieListFragment.setArguments(args);
        return movieListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceType = (TMDB.Services) getArguments().get(ARG_SERVICE_TYPE);
        getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.divider));
        return view;
    }

    @Override
    public void loadMoreData(int page) {
        Log.i(TAG, "Loading page " + (page + 1) + "...");
        // Send an API request to retrieve appropriate data using the offset value as a parameter.
        if((serviceType == NowPlayingService) || (serviceType == PopularService)) {
            if(page < DEFAULT_MAX_PAGE) {
                loadingTextView.setVisibility(View.VISIBLE);
                fillMoviesList(serviceType, page + 1);
            }
        } else if(serviceType == TopRatedService) {
            if(page < TOP_MAX_PAGE) {
                loadingTextView.setVisibility(View.VISIBLE);
                fillMoviesList(serviceType, page + 1);
            }
        }
    }

    /** Retrieves the data from themoviedb */
    protected void getData() {
        fillMoviesList(serviceType, 1);
    }

    public void fillMoviesList(final TMDB.Services serviceType, final int pageNumber) {
        boolean isCached = false;
        try {
            isCached = Reservoir.contains(serviceType.toString() + pageNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if((!hasExpired(serviceType.toString() + "1", getCacheExpiration(serviceType))) //just the expiration time of page 1 matters
                && (isCached)){
            Log.d(TAG, "Getting " + serviceType.toString() + " page " + pageNumber + " from cache");
            Reservoir.getAsync(serviceType.toString() + pageNumber, Page.class, new ReservoirGetCallback<Page>() {
                @Override
                public void onSuccess(Page page) {
                    final List<Movie> result = page.movies;
                    if ((result != null) && (MovieListFragment.this.getActivity() != null)) {//there are movies to list
                        MovieListFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateList(result);
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    populateListFromAPI(serviceType, pageNumber);
                }
            });

        } else {
            Log.d(TAG, "Getting " + serviceType.toString() + " page " + pageNumber + " from server");
            if(!Utils.isNetworkConnected(getContext())) {
                Log.e(TAG, getResources().getResourceName(R.string.no_internet));
                Utils.showAlert(getContext(), getResources().getResourceName(R.string.no_internet));
                final Handler handler = new Handler();
                final Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    public void run() {
                        handler.post(new Runnable() {
                            public void run() {
                                if (Utils.isNetworkConnected(getContext())) {
                                    populateListFromAPI(serviceType, pageNumber);
                                    timer.cancel();
                                }
                            }
                        });
                    }
                };
                timer.schedule(timerTask, INTERNET_CHECK_TIME, INTERNET_CHECK_TIME);
            } else {
                populateListFromAPI(serviceType, pageNumber);
            }
        }
    }

    private void populateListFromAPI(TMDB.Services service, int pageNumber) {
        if(service == PopularService) {
            populatePopularListFromAPI(pageNumber);
        } else if(service == TopRatedService) {
            populateTopRatedListFromAPI(pageNumber);
        } else if(service == NowPlayingService) {
            populateNowPlayingListFromAPI(pageNumber);
        }
    }

    private void populatePopularListFromAPI(final int pageNumber) {
        TMDB.getInstance().moviesService().popularRx(API_KEY, pageNumber)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Page>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onNext(final Page page) {
                        updateList(page.movies);
                        CacheManager.cachePage(PopularService, page);
                    }
                });
    }

    private void populateTopRatedListFromAPI(final int page) {
        TMDB.getInstance().moviesService().topRatedRx(API_KEY, page)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Page>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onNext(final Page page) {
                        updateList(page.movies);
                        CacheManager.cachePage(TopRatedService, page);
                    }
                });
    }

    private void populateNowPlayingListFromAPI(final int pageNumber) {
        TMDB.getInstance().moviesService().nowPlayingRx(API_KEY, pageNumber)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Page>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onNext(final Page page) {
                        updateList(page.movies);
                        CacheManager.cachePage(NowPlayingService, page);
                    }
                });
    }

    private void updateList(List<Movie> result) {
        if (mAdapter == null) {
            mAdapter = new MovieListAdapter(result, MovieListFragment.this.getContext());
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mAdapter.addList(result);
        }
        loadingTextView.setVisibility(View.GONE);
    }


}