package br.net.paulofernando.moviest.view.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.view.fragments.CollectionsFragment;
import br.net.paulofernando.moviest.view.fragments.MovieListFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.net.paulofernando.moviest.data.remote.TMDB.Services.PopularService;
import static br.net.paulofernando.moviest.data.remote.TMDB.Services.TopRatedService;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static final int NUMBER_TABS = 3;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.container) ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(mViewPager);

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
