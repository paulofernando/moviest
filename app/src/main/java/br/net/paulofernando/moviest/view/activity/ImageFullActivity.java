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
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.security.InvalidParameterException;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.data.AnalyticsApplication;
import br.net.paulofernando.moviest.data.remote.TMDB;
import br.net.paulofernando.moviest.view.widget.CircleTransform;
import butterknife.BindView;
import butterknife.ButterKnife;
import in.uncod.android.bypass.Bypass;
import io.plaidapp.ui.widget.ElasticDragDismissFrameLayout;
import io.plaidapp.ui.widget.InkPageIndicator;
import io.plaidapp.util.HtmlUtils;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.R.attr.version;
import static android.view.View.GONE;
import static br.net.paulofernando.moviest.R.id.pager;

/**
 * About screen. This displays 2 pages in a ViewPager:
 *  – About Plaid
 *  – Credit libraries
 */
public class ImageFullActivity extends Activity {

    private static final String TAG = "ImageFullActivity";
    private static final String ANALYTICS_TAG = TAG;
    private Tracker mTracker;
    PhotoViewAttacher mAttacher;

    @BindView(R.id.image_full_iv) ImageView imageFullView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full);
        ButterKnife.bind(this);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mAttacher = new PhotoViewAttacher(imageFullView);

        Bundle extras = getIntent().getExtras();
        loadImage(extras.getString(getResources().getString(R.string.cover_image)));
    }

    private void loadImage(String url) {
        Picasso.with(getApplicationContext()).load(url).into(imageFullView, new Callback() {
            @Override public void onSuccess() {
                mAttacher.update();
            }

            @Override public void onError() {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        sendAnalyticsInfo();
    }

    private void sendAnalyticsInfo() {
        mTracker.setScreenName(ANALYTICS_TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


}
