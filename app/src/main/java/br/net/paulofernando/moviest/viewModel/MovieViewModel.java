package br.net.paulofernando.moviest.viewModel;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.data.entities.Movie;
import br.net.paulofernando.moviest.data.remote.TMDB;
import br.net.paulofernando.moviest.databinding.ItemMovieBinding;
import br.net.paulofernando.moviest.view.activity.MovieDetailsActivity;

public class MovieViewModel extends BaseObservable {

    private Context context;
    private Movie movie;
    private ItemMovieBinding binding;

    public ObservableField<Drawable> coverImage;
    private MovieViewModel.BindableFieldTarget bindableFieldTarget;

    public MovieViewModel(Context context, Movie movie, ItemMovieBinding binding) {
        this.context = context;
        this.movie = movie;
        this.binding = binding;

        coverImage = new ObservableField<>();
        // Picasso keeps a weak reference to the target so it needs to be stored in a field
        bindableFieldTarget = new MovieViewModel.BindableFieldTarget(coverImage, context.getResources());
        Picasso.with(context)
                .load(getImageUrl())
                .placeholder(R.drawable.cover_unloaded)
                .error(R.drawable.cover_unloaded)
                .into(bindableFieldTarget);
    }

    public String getMovieTitle() {
        return movie.title;
    }

    public String getMovieYear() {
        return movie.releaseDate.substring(0, 4);
    }

    public String getMovieGenre() {
        if((movie.genresList != null) && (movie.genresList.size() > 0)) {
            return movie.genresList.get(0).name;
        } else if((movie.genreIds != null) && (movie.genreIds.length > 0)) {
            return TMDB.getGenreNameByID(movie.genreIds[0]);
        }
        return "";
    }

    public View.OnClickListener onClickMovie() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMovieDetailsActivity();
            }
        };
    }

    private void launchMovieDetailsActivity() {
        Intent intent = MovieDetailsActivity.getStartIntent(context, movie, binding.coverIv);

        String transitionName = context.getString(R.string.cover_name);
        ActivityOptions transitionActivityOptions = ActivityOptions.
                makeSceneTransitionAnimation((Activity) context, binding.coverIv, transitionName);

        context.startActivity(intent, transitionActivityOptions.toBundle());
    }

    public int getGenreVisibility() {
        if(((movie.genreIds != null) && (movie.genreIds.length > 0)) ||
          ((movie.genresList != null) && (movie.genresList.size() > 0))){
            return View.VISIBLE;
        }
        return  View.GONE;
    }

    public String getImageUrl() {
        return "http://image.tmdb.org/t/p/" + TMDB.SIZE_DEFAULT + movie.posterPath;
    }

    public class BindableFieldTarget implements Target {

        private ObservableField<Drawable> observableField;
        private Resources resources;

        public BindableFieldTarget(ObservableField<Drawable> observableField, Resources resources) {
            this.observableField = observableField;
            this.resources = resources;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            observableField.set(new BitmapDrawable(resources, bitmap));
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            observableField.set(errorDrawable);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            observableField.set(placeHolderDrawable);
        }
    }

}
