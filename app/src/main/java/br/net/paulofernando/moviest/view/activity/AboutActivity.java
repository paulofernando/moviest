package br.net.paulofernando.moviest.view.activity;
/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AlignmentSpan;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import java.security.InvalidParameterException;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.data.AnalyticsApplication;
import br.net.paulofernando.moviest.view.widget.CircleTransform;
import butterknife.BindView;
import butterknife.ButterKnife;
import in.uncod.android.bypass.Bypass;
import io.plaidapp.ui.widget.ElasticDragDismissFrameLayout;
import io.plaidapp.ui.widget.InkPageIndicator;
import io.plaidapp.util.HtmlUtils;

/**
 * About screen. This displays 2 pages in a ViewPager:
 *  – About Plaid
 *  – Credit libraries
 */
public class AboutActivity extends Activity {

    private static final String TAG = "AboutActivity";
    private static final String ANALYTICS_TAG = TAG;

    @BindView(R.id.draggable_frame)
    ElasticDragDismissFrameLayout draggableFrame;
    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.indicator)
    InkPageIndicator pageIndicator;

    private static String version;

    private Tracker mTracker;
    private static final int[] PAGE_NAMES = {
            R.string.name_page_about_info, R.string.name_page_about_libs
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = "v. " + pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        pager.setAdapter(new AboutActivity.AboutPagerAdapter(this));
        pager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.spacing_normal));
        pageIndicator.setViewPager(pager);

        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                sendAnalyticsInfo();
            }
        });


        draggableFrame.addListener(
                new ElasticDragDismissFrameLayout.SystemChromeFader(this) {
                    @Override
                    public void onDragDismissed() {
                        // if we drag dismiss downward then the default reversal of the enter
                        // transition would slide content upward which looks weird. So reverse it.
                        if (draggableFrame.getTranslationY() > 0) {
                            getWindow().setReturnTransition(
                                    TransitionInflater.from(AboutActivity.this)
                                            .inflateTransition(R.transition.about_return_downward));
                        }
                        finishAfterTransition();
                    }
                });
    }


    private void sendAnalyticsInfo() {
        mTracker.setScreenName(getCurrentPageName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private String getCurrentPageName() {
        return getString(PAGE_NAMES[pager.getCurrentItem()]);
    }

    static class AboutPagerAdapter extends PagerAdapter {

        private View aboutPlaid;
        @Nullable @BindView(R.id.about_description) TextView plaidDescription;
        private View aboutLibs;
        @Nullable @BindView(R.id.libs_list) RecyclerView libsList;
        @Nullable @BindView(R.id.about_version) TextView versionTv;

        private final LayoutInflater layoutInflater;
        private final Bypass markdown;

        public AboutPagerAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
            markdown = new Bypass(context, new Bypass.Options());
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            View layout = getPage(position, collection);
            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        private View getPage(int position, ViewGroup parent) {
            switch (position) {
                case 0:
                    if (aboutPlaid == null) {
                        aboutPlaid = layoutInflater.inflate(R.layout.about_moviest, parent, false);
                        ButterKnife.bind(this, aboutPlaid);
                        versionTv.setText(AboutActivity.version);

                        // fun with spans & markdown
                        SpannableString about1 = new SpannableString(
                                parent.getResources().getString(R.string.about_moviest_1));
                        about1.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                0, about1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        SpannableString about2 = new SpannableString(markdown.markdownToSpannable
                                (parent.getResources().getString(R.string.about_moviest_2),
                                        plaidDescription, null));
                        about2.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                0, about2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        SpannableString about3 = new SpannableString(markdown.markdownToSpannable
                                (parent.getResources().getString(R.string.about_moviest_3),
                                        plaidDescription, null));
                        about3.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                0, about3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        CharSequence desc = TextUtils.concat(about1, "\n", about2, "\n\n", about3);
                        HtmlUtils.setTextWithNiceLinks(plaidDescription, desc);
                    }
                    return aboutPlaid;
                case 1:
                    if (aboutLibs == null) {
                        aboutLibs = layoutInflater.inflate(R.layout.about_libs, parent, false);
                        ButterKnife.bind(this, aboutLibs);
                        libsList.setAdapter(new LibraryAdapter(parent.getContext()));
                    }
                    return aboutLibs;
            }
            throw new InvalidParameterException();
        }
    }

    private static class LibraryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int VIEW_TYPE_INTRO = 0;
        private static final int VIEW_TYPE_LIBRARY = 1;
        private static final Library[] libs = {
                new Library("ButterKnife",
                        "http://jakewharton.github.io/butterknife/",
                        "https://avatars.githubusercontent.com/u/66577"),
                new Library("Retrofit",
                        "https://github.com/square/retrofit",
                        "https://avatars2.githubusercontent.com/u/82592"),
                new Library("Picasso",
                        "https://github.com/square/picasso",
                        "https://avatars2.githubusercontent.com/u/82592"),
                new Library("Bypass",
                        "https://github.com/Uncodin/bypass",
                        "https://avatars.githubusercontent.com/u/1072254"),
                new Library("Android support libs",
                        "https://android.googlesource.com/platform/frameworks/support/",
                        "http://developer.android.com/assets/images/android_logo@2x.png"),
                new Library("RxAndroid",
                        "https://github.com/ReactiveX/RxJava",
                        "https://avatars2.githubusercontent.com/u/66577"),
                new Library("Sweet Alert Dialog",
                        "https://github.com/pedant/sweet-alert-dialog",
                        "https://avatars0.githubusercontent.com/u/5111206"),
                new Library("Reservoir",
                        "https://github.com/anupcowkur/Reservoir",
                        "https://avatars2.githubusercontent.com/u/1691740"),
                new Library("AVLoadingIndicatorView",
                        "https://github.com/81813780/AVLoadingIndicatorView",
                        "https://avatars0.githubusercontent.com/u/2297803"),
                new Library("google-gson",
                        "https://github.com/google/gson",
                        "https://avatars0.githubusercontent.com/u/1342004"),
                new Library("AndroidImageSlider",
                        "https://github.com/daimajia/AndroidImageSlider",
                        "https://avatars1.githubusercontent.com/u/2503423"),
                new Library("PhotoView",
                        "https://github.com/chrisbanes/PhotoView",
                        "https://avatars3.githubusercontent.com/u/227486"),
                new Library("Plaid",
                        "https://github.com/nickbutcher/plaid",
                        "https://avatars1.githubusercontent.com/u/352556")};



        private final CircleTransform circleCrop;

        public LibraryAdapter(Context context) {
            circleCrop = new CircleTransform();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case VIEW_TYPE_INTRO:
                    return new LibraryIntroHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.about_lib_intro, parent, false));
                case VIEW_TYPE_LIBRARY:
                    return new LibraryHolder(LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.library, parent, false));
            }
            throw new InvalidParameterException();
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == VIEW_TYPE_LIBRARY) {
                bindLibrary((LibraryHolder) holder, libs[position - 1]); // adjust for intro
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? VIEW_TYPE_INTRO : VIEW_TYPE_LIBRARY;
        }

        @Override
        public int getItemCount() {
            return libs.length + 1; // + 1 for the static intro view
        }

        private void bindLibrary(final LibraryHolder holder, final Library lib) {
            holder.name.setText(lib.name);
            Picasso.with(holder.image.getContext())
                    .load(lib.imageUrl)
                    .placeholder(R.drawable.avatar_placeholder)
                    .transform(circleCrop)
                    .into(holder.image);
            final View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                holder.image.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(lib.link)));
                }
            };
            holder.itemView.setOnClickListener(clickListener);
        }
    }

    static class LibraryHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.library_image) ImageView image;
        @BindView(R.id.library_name) TextView name;

        public LibraryHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class LibraryIntroHolder extends RecyclerView.ViewHolder {

        TextView intro;

        public LibraryIntroHolder(View itemView) {
            super(itemView);
            intro = (TextView) itemView;
        }
    }

    /**
     * Models an open source library we want to credit
     */
    private static class Library {
        public final String name;
        public final String link;
        public final String imageUrl;

        public Library(String name, String link, String imageUrl) {
            this.name = name;
            this.link = link;
            this.imageUrl = imageUrl;
        }
    }


}
