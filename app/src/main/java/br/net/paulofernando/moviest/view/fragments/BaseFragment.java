package br.net.paulofernando.moviest.view.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.util.NetworkUtils;
import br.net.paulofernando.moviest.listeners.EndlessRecyclerViewScrollListener;
import br.net.paulofernando.moviest.view.activity.AboutActivity;
import br.net.paulofernando.moviest.view.activity.SearchActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.data;
import static br.net.paulofernando.moviest.R.id.toolbar;

public abstract class BaseFragment extends Fragment {

    protected static final String ARG_SERVICE_TYPE = "service_type";
    private static final String TAG = "BaseFragment";

    @BindView(R.id.list_rv) RecyclerView mRecyclerView;
    @BindView(R.id.loading_tv) TextView loadingTextView;

    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        if(!NetworkUtils.isNetworkConnected(getContext())) {
            loadingTextView.setVisibility(View.GONE);
        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        setRecyclerViewLayoutManager();
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                return false;
            case R.id.action_about:
                startActivity(new Intent(this.getContext(), AboutActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation(this.getActivity()).toBundle());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     */
    public void setRecyclerViewLayoutManager() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        positionScroll();
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMoreData(page);
            }
        });
    }

    private void positionScroll() {
        int scrollPosition = 0;
        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    public abstract void loadMoreData(int page);

}