package br.net.paulofernando.moviest.view.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SharedElementCallback;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ProgressBar;

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
import butterknife.OnClick;
import io.plaidapp.ui.recyclerview.SlideInItemAnimator;
import io.plaidapp.ui.transitions.CircularReveal;
import io.plaidapp.util.TransitionUtils;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchActivity extends Activity {

    private static final String TAG = "SearchActiviity";

    @BindView(R.id.search_rv) RecyclerView searchRecyclerView;
    @BindView(R.id.loading_search) com.wang.avi.AVLoadingIndicatorView loadingSearch;
    @BindView(R.id.searchback) ImageButton searchBack;
    @BindView(R.id.searchback_container) ViewGroup searchBackContainer;
    @BindView(R.id.search_view) android.widget.SearchView searchView;
    @BindView(R.id.search_background) View searchBackground;
    @BindView(android.R.id.empty) ProgressBar progress;

    protected List<Movie> searchResult = new ArrayList<Movie>();
    protected SearchAdapter mAdapter;

    private Timer timer = new Timer();
    private final long SEARCH_DELAY = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search2);
        ButterKnife.bind(this);
        setupSearchView();

        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SearchAdapter(searchResult, SearchActivity.this);
        searchRecyclerView.setAdapter(mAdapter);
        searchRecyclerView.setItemAnimator(new SlideInItemAnimator());

        setupTransitions();
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

    @OnClick(R.id.searchback)
    protected void dismiss() {
        // clear the background else the touch ripple moves with the translation which looks bad
        searchBack.setBackground(null);
        finishAfterTransition();
    }

    private void setupSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        searchView.setImeOptions(searchView.getImeOptions() | EditorInfo.IME_ACTION_SEARCH |
                EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);

        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                searchFor(query);
                return true;
            }
        });
    }

    private void setupTransitions() {
        // grab the position that the search icon transitions in *from*
        // & use it to configure the return transition
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onSharedElementStart(
                    List<String> sharedElementNames,
                    List<View> sharedElements,
                    List<View> sharedElementSnapshots) {
                if (sharedElements != null && !sharedElements.isEmpty()) {
                    View searchIcon = sharedElements.get(0);
                    if (searchIcon.getId() != R.id.searchback) return;
                    int centerX = (searchIcon.getLeft() + searchIcon.getRight()) / 2;
                    CircularReveal hideResults = (CircularReveal) TransitionUtils.findTransition(
                            (TransitionSet) getWindow().getReturnTransition(),
                            CircularReveal.class, R.id.results_container);
                    if (hideResults != null) {
                        hideResults.setCenter(new Point(centerX, 0));
                    }
                }
            }
        });
        // focus the search view once the transition finishes
        getWindow().getEnterTransition().addListener(
                new TransitionUtils.TransitionListenerAdapter() {
                    @Override
                    public void onTransitionEnd(Transition transition) {
                        searchView.requestFocus();
                    }
                });
    }

    /*@Override
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
                searchFor(query);
                return true;
            }
        });

        return true;
    }*/

    private void searchFor(final String query) {
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
    }
}
