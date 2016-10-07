package br.net.paulofernando.moviest.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.util.NetworkUtils;
import br.net.paulofernando.moviest.data.remote.TMDB;
import br.net.paulofernando.moviest.data.entities.Configuration;
import br.net.paulofernando.moviest.data.entities.Crew;
import br.net.paulofernando.moviest.data.entities.Images;
import br.net.paulofernando.moviest.data.entities.Movie;
import br.net.paulofernando.moviest.data.entities.MovieWithCredits;
import br.net.paulofernando.moviest.data.entities.Videos;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.androidpit.androidcolorthief.MMCQ;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static br.net.paulofernando.moviest.util.NetworkUtils.INTERNET_CHECK_TIME;

public class MovieDetailsActivity extends AppCompatActivity implements YouTubeThumbnailView.OnInitializedListener {

    public static final int DETAILS_POSTER_SIZE_INDEX = 3;
    public static final int DETAILS_BACKDROP_SIZE_INDEX = 4;
    private static final String TAG = "MovieDetailsActivity";

    @BindView(R.id.movideDetailsContainer) CoordinatorLayout movideDetailsContainer;
    @BindView(R.id.toolbar_movie_details) Toolbar toolbarMovieDetails;
    @BindView(R.id.collapse_toolbar) CollapsingToolbarLayout collapseToolbar;
    @BindView(R.id.appbar_movie_details) AppBarLayout appbarMovieDetails;
    @BindView(R.id.cover_iv) ImageView coverImageView;
    @BindView(R.id.title_tv) TextView titleTextView;
    @BindView(R.id.star_iv) ImageView starIcon;
    @BindView(R.id.vote_average_tv) TextView voteAverageTextView;
    @BindView(R.id.vote_count_tv) TextView voteCountTextView;
    @BindView(R.id.genre_tv) TextView genreTextView;
    @BindView(R.id.runtime_tv) TextView runtimeTextView;
    @BindView(R.id.runtime_ll) LinearLayout runtimeLayout;
    @BindView(R.id.movie_overiew_tv) TextView movieOveriewView;
    @BindView(R.id.director_label_tv) TextView directorLabelView;
    @BindView(R.id.director_tv) TextView directorView;
    @BindView(R.id.release_label_tv) TextView releaseLabelView;
    @BindView(R.id.release_tv) TextView releaseView;
    @BindView(R.id.trailer_container) LinearLayout trailerContainer;
    @BindView(R.id.movie_trailer_title_tv) TextView movieTrailerTitle;
    @BindView(R.id.loading_trailer) com.wang.avi.AVLoadingIndicatorView loadingTrailer;
    @BindView(R.id.movie_backdrop) ImageView movieBackdrop;
    @BindView(R.id.movie_trailer_thumbnail) YouTubeThumbnailView youTubeThumbnailView;

    private Videos videosResult;
    private Images imagesResult;
    private String trailerID;
    private Movie movie;
    private MovieWithCredits movieWithCredits;

    private boolean showAlertNoConnection = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbarMovieDetails);
        appbarMovieDetails.setExpanded(false);

        movie = (Movie) getIntent().getParcelableExtra(TMDB.MOVIE_DETAILS);
        titleTextView.setText(movie.title);

        if (movie.voteAverage > 0) {
            starIcon.setVisibility(View.VISIBLE);
            voteAverageTextView.setVisibility(View.VISIBLE);
            voteAverageTextView.setText(String.valueOf(new DecimalFormat("#.#").format(movie.voteAverage)) + " /10");
            if (movie.voteCount > 0) {
                voteCountTextView.setVisibility(View.VISIBLE);
                voteCountTextView.setText(NumberFormat.getNumberInstance(Locale.US).format(movie.voteCount));
            }
        }

        if ((movie.genreIds != null) && (movie.genreIds.length > 0)) {
            genreTextView.setVisibility(View.VISIBLE);
            genreTextView.setText(TMDB.getGenreNameByID(movie.genreIds[0]));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapseToolbar.setTitle(" ");

        loadImages();

        appbarMovieDetails.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset < 20) {
                    collapseToolbar.setTitle(movie.title + (!movie.releaseDate.equals("") ?
                            " (" + movie.releaseDate.substring(0, 4) + ")" : ""));
                    isShow = true;
                } else if (isShow) {
                    collapseToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });

        movieOveriewView.setText(movie.overview);
        retrieveMovieDetails(movie.id);
        loadYoutubeThumbnail(movie.id);
    }

    public static Intent getStartIntent(Context context, Movie movie) {
        Intent intent = new Intent(context, MovieDetailsActivity.class);
        intent.putExtra(TMDB.MOVIE_DETAILS, movie);

        /*View sharedView = coverImageView;
        String transitionName = context.getString(R.string.cover_name);

        ActivityOptions transitionActivityOptions = ActivityOptions.
                makeSceneTransitionAnimation((Activity) context, sharedView, transitionName);

        context.startActivity(intent, transitionActivityOptions.toBundle());*/

        return intent;
    }

    private void loadImages() {
        Configuration config = TMDB.getConfiguration();
        Picasso.with(getApplicationContext()).load("http://image.tmdb.org/t/p/" +
                TMDB.SIZE_DEFAULT + movie.posterPath).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                coverImageView.setImageBitmap(bitmap);
                movieBackdrop.setVisibility(View.VISIBLE);
                changedScreenColors(bitmap);

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                movieBackdrop.setVisibility(View.GONE);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }

        });

        Picasso.with(getApplicationContext()).load("http://image.tmdb.org/t/p/" +
                TMDB.POSTER_W780 + movie.backdropPath)
                .into(movieBackdrop, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        appbarMovieDetails.setExpanded(true);
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    private void changedScreenColors(Bitmap bitmapSourceOfColors) {
        List<int[]> result;
        try {
            result = MMCQ.compute(bitmapSourceOfColors, 5);

            int[] color = result.get(0);
            int rgb = Color.rgb(color[0], color[1], color[2]);
            movideDetailsContainer.setBackgroundColor(rgb);

            color = result.get(1);
            rgb = Color.rgb(color[0], color[1], color[2]);
            trailerContainer.setBackgroundColor(rgb);
            collapseToolbar.setBackgroundColor(rgb);
            collapseToolbar.setContentScrimColor(rgb);
            collapseToolbar.setStatusBarScrimColor(rgb);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(rgb);
            }

            color = result.get(2);
            rgb = Color.rgb(color[0], color[1], color[2]);
            movieTrailerTitle.setTextColor(rgb);
            titleTextView.setTextColor(rgb);
            movieOveriewView.setTextColor(rgb);
            voteAverageTextView.setTextColor(rgb);
            voteCountTextView.setTextColor(rgb);
            directorLabelView.setTextColor(rgb);
            directorView.setTextColor(rgb);
            releaseLabelView.setTextColor(rgb);
            releaseView.setTextColor(rgb);
            loadingTrailer.setIndicatorColor(rgb);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void retrieveMovieDetails(final int movieID) {
        if(NetworkUtils.isNetworkConnected(this)) {
            retrieveMovieDetailsFromServer(movieID);
        } else {
            Log.e(TAG, getResources().getResourceName(R.string.no_internet));
            NetworkUtils.showAlert(this, getResources().getResourceName(R.string.no_internet));
            final Handler handler = new Handler();
            final Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            if(NetworkUtils.isNetworkConnected(MovieDetailsActivity.this)) {
                                retrieveMovieDetailsFromServer(movieID);
                                NetworkUtils.closeCurrentAlertDialog();
                                timer.cancel();
                            }
                        }
                    });
                }
            };
            timer.schedule(timerTask, INTERNET_CHECK_TIME, INTERNET_CHECK_TIME);
        }

    }

    private void retrieveMovieDetailsFromServer(int movieID) {
        TMDB.getInstance().moviesService().summaryWithAppendRx(movieID, TMDB.API_KEY, "credits")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MovieWithCredits>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(MovieWithCredits movieWithCredits) {
                        genreTextView.setText(movieWithCredits.genresList.get(0).name);
                        genreTextView.setVisibility(View.VISIBLE);
                        runtimeTextView.setText(String.valueOf(movieWithCredits.runtime) + " " + getResources().getString(R.string.minute));
                        runtimeLayout.setVisibility(View.VISIBLE);

                        String director = "";
                        for(Crew crew: movieWithCredits.credits.crew) {
                            if(crew.department.equals("Directing")) {
                                director = crew.name;
                                break;
                            }
                        }

                        if(!director.equals("")) {
                            directorLabelView.setVisibility(View.VISIBLE);
                            directorView.setVisibility(View.VISIBLE);
                            directorView.setText(director);
                        }

                        if(!movieWithCredits.releaseDate.equals("")) {
                            releaseLabelView.setVisibility(View.VISIBLE);
                            releaseView.setVisibility(View.VISIBLE);
                            releaseView.setText(movieWithCredits.releaseDate);
                        }
                    }
                });
    }


    @Override
    public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
        if ((trailerID != null) && (NetworkUtils.isNetworkConnected(getApplicationContext()))) {
            youTubeThumbnailLoader.setVideo(trailerID);
            trailerContainer.setVisibility(View.VISIBLE);
            youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                @Override
                public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                    loadingTrailer.setVisibility(View.GONE);
                }

                @Override
                public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView,
                                             YouTubeThumbnailLoader.ErrorReason errorReason) {
                }
            });
        }
    }

    @Override
    public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView,
                                        YouTubeInitializationResult youTubeInitializationResult) {
    }

    public void loadYoutubeThumbnail(int movieID) {
        TMDB.getInstance().moviesService().videosRx(movieID, TMDB.API_KEY)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Videos>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(Videos videos) {
                        for (Videos.Video v : videos.results) {
                            if (v.type.equals("Trailer")) {
                                trailerID = v.key;
                                break;
                            }
                        }

                        try {
                            youTubeThumbnailView.initialize(TMDB.YOUTUBE_KEY, MovieDetailsActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @OnClick(R.id.movie_trailer_thumbnail)
    public void trailerClick() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailerID)));
    }

}