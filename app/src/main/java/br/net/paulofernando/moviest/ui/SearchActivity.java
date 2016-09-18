package br.net.paulofernando.moviest.ui;

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
import br.net.paulofernando.moviest.Utils;
import br.net.paulofernando.moviest.adapters.SearchAdapter;
import br.net.paulofernando.moviest.communication.MovieDB;
import br.net.paulofernando.moviest.communication.entities.Movie;
import br.net.paulofernando.moviest.communication.entities.Page;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

                                            if(!Utils.isNetworkConnected(getApplicationContext())) {
                                                Log.e(TAG, getResources().getResourceName(R.string.no_internet));
                                            } else {

                                                Call<Page> callSearch = MovieDB.getInstance().moviesService().search(MovieDB.API_KEY, query, 1);
                                                callSearch.enqueue(new Callback<Page>() {
                                                    @Override
                                                    public void onResponse(Call<Page> call, Response<Page> response) {
                                                        if (response.isSuccessful()) {
                                                            searchResult = response.body().movies;
                                                            if(searchResult != null) {
                                                                mAdapter = new SearchAdapter(searchResult, SearchActivity.this);
                                                                searchRecyclerView.setAdapter(mAdapter);
                                                                loadingSearch.setVisibility(View.GONE);
                                                            }
                                                        }
                                                    }
                                                    @Override
                                                    public void onFailure(Call<Page> call, Throwable t) {
                                                        Log.d(TAG, t.getMessage());
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
