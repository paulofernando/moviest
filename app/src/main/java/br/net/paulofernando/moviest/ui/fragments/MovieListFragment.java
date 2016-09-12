package br.net.paulofernando.moviest.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;
import com.anupcowkur.reservoir.ReservoirPutCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.Utils;
import br.net.paulofernando.moviest.adapters.MovieListAdapter;
import br.net.paulofernando.moviest.communication.MovieDB;
import br.net.paulofernando.moviest.communication.entities.Movie;
import br.net.paulofernando.moviest.communication.entities.Page;
import br.net.paulofernando.moviest.storage.CacheManager;
import br.net.paulofernando.moviest.ui.component.DividerItemDecoration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListFragment extends BaseFragment {

    private static final String TAG = "MovieListFragment";
    private static final int DEFAULT_MAX_PAGE = 2;
    private static final int TOP_MAX_PAGE = 2;

    private MovieDB.Services serviceType;

    protected MovieListAdapter mAdapter;
    private List<Movie> movies = new ArrayList<>();

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MovieListFragment newInstance(MovieDB.Services serviceType) {
        MovieListFragment movieListFragment = new MovieListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SERVICE_TYPE, serviceType);
        movieListFragment.setArguments(args);
        return movieListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceType = (MovieDB.Services) getArguments().get(ARG_SERVICE_TYPE);
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
        Log.i(TAG, "Loading page " + page + "...");
        // Send an API request to retrieve appropriate data using the offset value as a parameter.
        if((serviceType == MovieDB.Services.NowPlaying) || (serviceType == MovieDB.Services.Popular)) {
            if(page < DEFAULT_MAX_PAGE) {
                loadingTextView.setVisibility(View.VISIBLE);
                fillMoviesList(serviceType, page + 1);
            }
        } else if(serviceType == MovieDB.Services.TopRated) {
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

    public void fillMoviesList(final MovieDB.Services serviceType, final int page) {
        String serviceCacheName = "";

        if(!CacheManager.hasExpired(serviceType)) {
            Reservoir.getAsync(serviceCacheName + page, Page.class, new ReservoirGetCallback<Page>() {
                @Override
                public void onSuccess(Page page) {
                    Log.d(TAG, "Getting data from cache");
                    final List<Movie> result = page.movies;
                    if ((result != null) && (MovieListFragment.this.getActivity() != null)) {//there are movies to list
                        MovieListFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                movies.addAll(result);
                                if (mAdapter == null) {
                                    mAdapter = new MovieListAdapter(movies, MovieListFragment.this.getContext());
                                    mRecyclerView.setAdapter(mAdapter);
                                    mAdapter.notifyDataSetChanged();
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                } else {
                                    mAdapter.addMovies(movies);
                                }
                                loadingTextView.setVisibility(View.GONE);
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    populateListFromAPI(serviceType, page);
                }
            });

        } else {
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
                                    populateListFromAPI(serviceType, page);
                                    timer.cancel();
                                }
                            }
                        });
                    }
                };
                timer.schedule(timerTask, INTERNET_CHECK_TIME, INTERNET_CHECK_TIME);
            } else {
                populateListFromAPI(serviceType, page);
            }
        }

    }

    private void populateListFromAPI(final MovieDB.Services serviceType, final int page) {
        Call<Page> callMovies = null;

        if(serviceType == MovieDB.Services.NowPlaying) {
            callMovies = MovieDB.getInstance().moviesService().nowPlaying(MovieDB.API_KEY, page);
        } else if(serviceType == MovieDB.Services.Popular) {
            callMovies = MovieDB.getInstance().moviesService().popular(MovieDB.API_KEY, page);
        } else if(serviceType == MovieDB.Services.TopRated) {
            callMovies = MovieDB.getInstance().moviesService().topRated(MovieDB.API_KEY, page);
        }

        if(callMovies != null) {
            callMovies.enqueue(new Callback<Page>() {
                @Override
                public void onResponse(Call<Page> call, Response<Page> response) {
                    if (response.isSuccessful()) {
                        final List<Movie> result = response.body().movies;
                        if ((result != null) && (MovieListFragment.this.getActivity() != null)) {//there are movies to list
                            MovieListFragment.this.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    movies.addAll(result);
                                    if (mAdapter == null) {
                                        mAdapter = new MovieListAdapter(movies, MovieListFragment.this.getContext());
                                        mRecyclerView.setAdapter(mAdapter);
                                        mAdapter.notifyDataSetChanged();
                                        mRecyclerView.setVisibility(View.VISIBLE);
                                    } else {
                                        mAdapter.addMovies(movies);
                                    }
                                    loadingTextView.setVisibility(View.GONE);
                                }
                            });
                        }

                        String serviceCacheName = null;
                        if(serviceType == MovieDB.Services.NowPlaying) {
                            serviceCacheName = CacheManager.CACHE_NOW_PLAYING;
                        } else if(serviceType == MovieDB.Services.NowPlaying) {
                            serviceCacheName = CacheManager.CACHE_POPULAR;
                        } else if(serviceType == MovieDB.Services.NowPlaying) {
                            serviceCacheName = CacheManager.CACHE_TOP;
                        }

                        if(serviceCacheName != null) {
                            Reservoir.putAsync(serviceCacheName + page, response.body(), new ReservoirPutCallback() {
                                @Override
                                public void onSuccess() {
                                    Log.d(TAG, "Page " + page + " cached in tab " + serviceType);
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Log.e(TAG, "Failure on saving data in cache");
                                    e.printStackTrace();
                                }
                            });

                            try {
                                Reservoir.put(serviceCacheName + CacheManager.CACHE_TIME, System.currentTimeMillis());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<Page> call, Throwable t) {
                    Log.d(TAG, t.getMessage());
                }
            });
        }
    }
}