package br.net.paulofernando.moviest.view.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.data.AnalyticsApplication;
import br.net.paulofernando.moviest.util.NetworkUtils;
import br.net.paulofernando.moviest.view.fragments.CollectionsFragment;
import br.net.paulofernando.moviest.view.fragments.MovieListFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.net.paulofernando.moviest.R.id.pager;
import static br.net.paulofernando.moviest.R.id.toolbar;
import static br.net.paulofernando.moviest.data.remote.TMDB.Services.PopularService;
import static br.net.paulofernando.moviest.data.remote.TMDB.Services.TopRatedService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static final int NUMBER_TABS = 3;
    private static final int RC_SEARCH = 0;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.container) ViewPager mViewPager;

    private Tracker mTracker;
    private static final int[] PAGE_NAMES = {
            R.string.name_page_main_collections, R.string.name_page_main_popular,
            R.string.name_page_main_top_rated
    };
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                sendAnalyticsInfo();
            }
        });
    }

    private void sendAnalyticsInfo() {
        mTracker.setScreenName(getCurrentPageName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private String getCurrentPageName() {
        return getString(PAGE_NAMES[mViewPager.getCurrentItem()]);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                if(NetworkUtils.isNetworkConnected(this)) {
                    View searchMenuView = toolbar.findViewById(R.id.menu_search);
                    Bundle options = ActivityOptions.makeSceneTransitionAnimation(this, searchMenuView,
                            getString(R.string.transition_search_back)).toBundle();
                    startActivityForResult(new Intent(this, SearchActivity.class), RC_SEARCH, options);
                } else {
                    Log.e(TAG, getResources().getResourceName(R.string.no_internet));
                    NetworkUtils.showAlert(this, getResources().getResourceName(R.string.no_internet));
                    return false;
                }
                return true;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(snackbar == null) {
            snackbar = Snackbar
                .make(mViewPager, getResources().getString(R.string.action_close_app), Snackbar.LENGTH_LONG)
                .setAction(getResources().getString(R.string.action_yes), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        closeApp();
                    }
                });
        }

        if(!snackbar.isShown()) {
            snackbar.show();
        } else {
            closeApp();
        }
    }

    public void closeApp() {
        super.onBackPressed();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0) return CollectionsFragment.newInstance(position);
            return MovieListFragment.newInstance((position == 1 ? PopularService : TopRatedService));
        }

        @Override
        public int getCount() {
            return NUMBER_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.tab_title_collections);
                case 1:
                    return getResources().getString(R.string.tab_title_popular);
                case 2:
                    return getResources().getString(R.string.tab_title_top_rated);
            }
            return null;
        }
    }
}
