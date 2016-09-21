package br.net.paulofernando.moviest.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.Utils;
import br.net.paulofernando.moviest.adapters.MovieListAdapter;
import br.net.paulofernando.moviest.communication.TMDB;
import br.net.paulofernando.moviest.communication.entities.Collection;
import br.net.paulofernando.moviest.communication.entities.Movie;
import br.net.paulofernando.moviest.ui.component.DividerItemDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CollectionActivity extends AppCompatActivity {

    private static final String TAG = "CollectionActivity";
    private static final int INTERNET_CHECK_TIME = 5000;

    @BindView(R.id.toolbar_collection) Toolbar toolbarCollection;
    @BindView(R.id.collapse_toolbar_collection) CollapsingToolbarLayout collapseToolbarCollection;
    @BindView(R.id.appbar_collection) AppBarLayout appbarCollection;
    @BindView(R.id.title_collection_tv) TextView titleCollection;
    @BindView(R.id.awards_list_rv) RecyclerView mRecyclerView;
    @BindView(R.id.loading_tv) TextView loadingTextView;
    @BindView(R.id.bg_collection_iv) ImageView bgCollection;

    protected MovieListAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    private Collection collection;
    private List<Movie> movies = new ArrayList<>();

    private final AtomicLong counter = new AtomicLong();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        ButterKnife.bind(this);

        collection = (Collection) getIntent().getSerializableExtra(TMDB.COLLECTION_DETAILS);

        setSupportActionBar(toolbarCollection);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapseToolbarCollection.setTitle(" ");
        titleCollection.setText(collection.title);

        try {
            Picasso.with(this).load(collection.backgroundImageURL).into(bgCollection);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MovieListAdapter(CollectionActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider));

        if(Utils.isNetworkConnected(this)) {
            getData();
        } else {
            Log.e(TAG, getResources().getResourceName(R.string.no_internet));
            Utils.showAlert(this, getResources().getResourceName(R.string.no_internet));
            final Handler handler = new Handler();
            final Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            if(Utils.isNetworkConnected(CollectionActivity.this)) {
                                getData();
                                timer.cancel();
                            }
                        }
                    });
                }
            };
            timer.schedule(timerTask, INTERNET_CHECK_TIME, INTERNET_CHECK_TIME);
        }
    }

    private void getData() {
        for (final Integer movieId : collection.moviesIds) {
            try {
                if (Reservoir.contains(String.valueOf(movieId))) {
                    Reservoir.getAsync(String.valueOf(movieId), Movie.class, new ReservoirGetCallback<Movie>() {
                        @Override
                        public void onSuccess(Movie movie) {
                            movies.add(movie);
                            checkForUpdateInRecyclerView();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            getMovieFromAPIRx(movieId);
                        }
                    });
                } else {
                    getMovieFromAPIRx(movieId);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getMovieFromAPI(final Integer movieId) {
        TMDB.getInstance().moviesService().summaryRx(movieId, TMDB.API_KEY)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<Movie>() {
                @Override
                public void onCompleted() {}

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, e.getMessage());
                }

                @Override
                public void onNext(Movie movie) {
                    try {
                        Reservoir.put(String.valueOf(movieId), movie);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    movies.add(movie);
                    checkForUpdateInRecyclerView();
                }
            });
    }

    private void getMovieFromAPIRx(final Integer movieId) {
        TMDB.getInstance().moviesService().summaryRx(movieId, TMDB.API_KEY)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Movie>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(Movie movie) {
                        try {
                            Reservoir.put(String.valueOf(movieId), movie);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        movies.add(movie);
                        checkForUpdateInRecyclerView();
                    }
                });

    }

    @OnClick(R.id.loading_tv)
    public void loadingClick() {
        updateList();
    }

    private void checkForUpdateInRecyclerView() {
        //only add in recycler when all data is loaded
        if(counter.incrementAndGet() == collection.moviesIds.size()) {
            updateList();
        }
    }

    private void updateList() {
        CollectionActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.addList(movies);
                loadingTextView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
