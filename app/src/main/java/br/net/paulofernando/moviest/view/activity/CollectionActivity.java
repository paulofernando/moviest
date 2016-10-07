package br.net.paulofernando.moviest.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import br.net.paulofernando.moviest.data.entities.Collection;
import br.net.paulofernando.moviest.data.entities.Movie;
import br.net.paulofernando.moviest.data.remote.TMDB;
import br.net.paulofernando.moviest.util.NetworkUtils;
import br.net.paulofernando.moviest.view.adapter.MovieAdapter;
import br.net.paulofernando.moviest.view.component.DividerItemDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static br.net.paulofernando.moviest.util.NetworkUtils.INTERNET_CHECK_TIME;

public class CollectionActivity extends AppCompatActivity {

    private static final String TAG = "CollectionActivity";

    @BindView(R.id.toolbar_collection) Toolbar toolbarCollection;
    @BindView(R.id.collapse_toolbar_collection) CollapsingToolbarLayout collapseToolbarCollection;
    @BindView(R.id.appbar_collection) AppBarLayout appbarCollection;
    @BindView(R.id.title_collection_tv) TextView titleCollection;
    @BindView(R.id.awards_list_rv) RecyclerView mRecyclerView;
    @BindView(R.id.loading_tv) TextView loadingTextView;
    @BindView(R.id.link_iv) ImageView link;
    @BindView(R.id.bg_collection_iv) ImageView bgCollection;

    protected MovieAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    private Collection collection;
    private List<Movie> movies = new ArrayList<>();

    private final AtomicLong counter = new AtomicLong();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        ButterKnife.bind(this);

        collection = getIntent().getParcelableExtra(TMDB.COLLECTION_DETAILS);

        setSupportActionBar(toolbarCollection);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapseToolbarCollection.setTitle(" ");
        titleCollection.setText(collection.title);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider));

        try {
            Picasso.with(this).load(collection.backgroundImageURL).into(bgCollection);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MovieAdapter(CollectionActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        if(NetworkUtils.isNetworkConnected(this)) {
            getData();
        } else {
            Log.e(TAG, getResources().getResourceName(R.string.no_internet));
            NetworkUtils.showAlert(this, getResources().getResourceName(R.string.no_internet));
            final Handler handler = new Handler();
            final Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            if(NetworkUtils.isNetworkConnected(CollectionActivity.this)) {
                                getData();
                                timer.cancel();
                            }
                        }
                    });
                }
            };
            timer.schedule(timerTask, INTERNET_CHECK_TIME, INTERNET_CHECK_TIME);
        }
        link.setVisibility(View.GONE);
    }

    public static Intent getStartIntent(Context context, Collection collection) {
        Intent intent = new Intent(context, CollectionActivity.class);
        intent.putExtra(TMDB.COLLECTION_DETAILS, collection);

        /*View sharedView = collectionRowContainer;
        String transitionName = context.getString(R.string.collection_name);

        ActivityOptions transitionActivityOptions = ActivityOptions.
        makeSceneTransitionAnimation((Activity) CollectionAdapter.this.context, sharedView, transitionName);*/

        return intent;
    }

    @OnClick(R.id.bg_collection_iv)
    public void linkClick() {
        Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(collection.sourceURL));
        startActivity(intent);
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
        if(counter.incrementAndGet() == collection.moviesIds.length) {
            updateList();
        }
    }

    private void updateList() {
        CollectionActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.setItems(movies);
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
