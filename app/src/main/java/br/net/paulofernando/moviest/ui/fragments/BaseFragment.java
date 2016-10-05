package br.net.paulofernando.moviest.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.Utils;
import br.net.paulofernando.moviest.listeners.EndlessRecyclerViewScrollListener;
import br.net.paulofernando.moviest.ui.AboutActivity;
import br.net.paulofernando.moviest.ui.SearchActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    protected static final String ARG_SERVICE_TYPE = "service_type";
    private static final String TAG = "BaseFragment";

    @BindView(R.id.list_rv)
    RecyclerView mRecyclerView;

    @BindView(R.id.loading_tv)
    TextView loadingTextView;

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

        if(!Utils.isNetworkConnected(getContext())) {
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
            case R.id.action_search:
                if(Utils.isNetworkConnected(getContext())) {
                    Intent searchIntent = new Intent(getContext(), SearchActivity.class);
                    getContext().startActivity(searchIntent);
                } else {
                    Log.e(TAG, getContext().getResources().getResourceName(R.string.no_internet));
                    Utils.showAlert(getContext(), getResources().getResourceName(R.string.no_internet));
                    return false;
                }
                return true;
            case R.id.action_about:
                Intent aboutIntent = new Intent(getContext(), AboutActivity.class);
                getContext().startActivity(aboutIntent);
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