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

package io.plaidapp.ui;

import android.app.Activity;
import android.content.Context;
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
import android.widget.TextView;

import java.security.InvalidParameterException;

import br.net.paulofernando.moviest.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import in.uncod.android.bypass.Bypass;
import io.plaidapp.ui.widget.ElasticDragDismissFrameLayout;
import io.plaidapp.ui.widget.InkPageIndicator;
import io.plaidapp.util.HtmlUtils;


/**
 * About screen. This displays 3 pages in a ViewPager:
 *  – About Plaid
 *  – Credit Roman for the awesome icon
 *  – Credit libraries
 */
public class AboutActivity extends Activity {

    @BindView(R.id.draggable_frame) ElasticDragDismissFrameLayout draggableFrame;
    @BindView(R.id.pager) ViewPager pager;
    @BindView(R.id.indicator) InkPageIndicator pageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about2);
        ButterKnife.bind(this);

        pager.setAdapter(new AboutPagerAdapter(this));
        pager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.spacing_normal));
        pageIndicator.setViewPager(pager);

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

    static class AboutPagerAdapter extends PagerAdapter {

        private View aboutPlaid;
        @Nullable @BindView(R.id.about_description) TextView plaidDescription;
        private View aboutIcon;
        @Nullable @BindView(R.id.icon_description) TextView iconDescription;
        private View aboutLibs;
        @Nullable @BindView(R.id.libs_list) RecyclerView libsList;

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
            return 3;
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
                        // fun with spans & markdown
                        CharSequence about0 = markdown.markdownToSpannable(parent.getResources()
                                .getString(R.string.about_plaid_0), plaidDescription, null);
                        SpannableString about1 = new SpannableString(
                                parent.getResources().getString(R.string.about_plaid_1));
                        about1.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                0, about1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        SpannableString about2 = new SpannableString(markdown.markdownToSpannable
                                (parent.getResources().getString(R.string.about_plaid_2),
                                        plaidDescription, null));
                        about2.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                0, about2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        SpannableString about3 = new SpannableString(markdown.markdownToSpannable
                                (parent.getResources().getString(R.string.about_plaid_3),
                                        plaidDescription, null));
                        about3.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                0, about3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        CharSequence desc = TextUtils.concat(about0, "\n\n", about1, "\n", about2,
                                "\n\n", about3);
                        HtmlUtils.setTextWithNiceLinks(plaidDescription, desc);
                    }
                    return aboutPlaid;
                case 1:
                    if (aboutIcon == null) {
                        aboutIcon = layoutInflater.inflate(R.layout.about_icon, parent, false);
                        ButterKnife.bind(this, aboutIcon);
                    }
                    return aboutIcon;
                case 2:
                    if (aboutLibs == null) {
                        aboutLibs = layoutInflater.inflate(R.layout.about_libs, parent, false);
                        ButterKnife.bind(this, aboutLibs);
                    }
                    return aboutLibs;
            }
            throw new InvalidParameterException();
        }
    }

}
