package br.net.paulofernando.moviest.view.activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.util.NetworkUtils;
import br.net.paulofernando.moviest.view.adapter.SearchAdapter;
import br.net.paulofernando.moviest.data.remote.TMDB;
import br.net.paulofernando.moviest.data.entities.Movie;
import br.net.paulofernando.moviest.data.entities.Page;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActiviity";

    @BindView(R.id.search_rv) RecyclerView searchRecyclerView;
    @BindView(R.id.loading_search) com.wang.avi.AVLoadingIndicatorView loadingSearch;

    protected List<Movie> searchResult = new ArrayList<Movie>();
    protected SearchAdapter mAdapter;

    private Timer timer = new Timer();
    private final long SEARCH_DELAY = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.action_search));

        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SearchAdapter(searchResult, SearchActivity.this);
        searchRecyclerView.setAdapter(mAdapter);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String query) {
                System.out.println(query);
                if(query.length() > 1) {
                    loadingSearch.setVisibility(View.VISIBLE);
                } else {
                    loadingSearch.setVisibility(View.GONE);
                }

                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(query.length() > 1) {
                                            searchRecyclerView.setVisibility(View.VISIBLE);

                                            if(!NetworkUtils.isNetworkConnected(getApplicationContext())) {
                                                Log.e(TAG, getResources().getResourceName(R.string.no_internet));
                                            } else {

                                                TMDB.getInstance().moviesService().searchRx(TMDB.API_KEY, query, 1)
                                                        .subscribeOn(Schedulers.newThread())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(new Subscriber<Page>() {
                                                            @Override
                                                            public void onCompleted() {}

                                                            @Override
                                                            public void onError(Throwable e) {}

                                                            @Override
                                                            public void onNext(final Page page) {
                                                                searchResult = page.movies;
                                                                if(searchResult != null) {
                                                                    mAdapter = new SearchAdapter(searchResult, SearchActivity.this);
                                                                    searchRecyclerView.setAdapter(mAdapter);
                                                                    loadingSearch.setVisibility(View.GONE);
                                                                }
                                                            }
                                                        });
                                            }
                                        } else {
                                            searchRecyclerView.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                        },
                        SEARCH_DELAY
                );
                return true;
            }
        });

        return true;
    }
}
