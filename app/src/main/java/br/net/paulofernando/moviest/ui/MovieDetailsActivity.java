package br.net.paulofernando.moviest.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.Utils;
import br.net.paulofernando.moviest.communication.MovieDB;
import br.net.paulofernando.moviest.communication.entities.Configuration;
import br.net.paulofernando.moviest.communication.entities.Crew;
import br.net.paulofernando.moviest.communication.entities.Images;
import br.net.paulofernando.moviest.communication.entities.Movie;
import br.net.paulofernando.moviest.communication.entities.MovieWithCredits;
import br.net.paulofernando.moviest.communication.entities.Videos;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.androidpit.androidcolorthief.MMCQ;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbarMovieDetails);
        appbarMovieDetails.setExpanded(false);

        movie = (Movie) getIntent().getSerializableExtra(MovieDB.MOVIE_DETAILS);
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

        if ((movie.genres != null) && (movie.genres.size() > 0)) {
            genreTextView.setVisibility(View.VISIBLE);
            genreTextView.setText(MovieDB.getGenreNameByID(movie.genres.get(0)));
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

    private void loadImages() {
        Configuration config = MovieDB.getConfiguration();
        Picasso.with(getApplicationContext()).load("http://image.tmdb.org/t/p/" +
                (config.images != null ? config.images.posterSizes.get(DETAILS_POSTER_SIZE_INDEX) : "original") +
                movie.posterPath).into(new Target() {
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
                (config.images != null ? config.images.posterSizes.get(DETAILS_BACKDROP_SIZE_INDEX) : "original") +
                movie.backdropPath)
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

    public void retrieveMovieDetails(int movieID) {
        MovieDB.getInstance().moviesService().summaryWithAppendRx(movieID, MovieDB.API_KEY, "credits")
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
                        runtimeTextView.setVisibility(View.VISIBLE);

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

    public void retrieveImages(int movieID) {
        MovieDB.getInstance().moviesService().imagesRx(movieID, MovieDB.API_KEY)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Images>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(Images images) {
                        imagesResult = images;
                    }
                });
    }

    @Override
    public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
        if ((trailerID != null) && (Utils.isNetworkConnected(getApplicationContext()))) {
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
        MovieDB.getInstance().moviesService().videosRx(movieID, MovieDB.API_KEY)
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
                            youTubeThumbnailView.initialize(MovieDB.YOUTUBE_KEY, MovieDetailsActivity.this);
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
