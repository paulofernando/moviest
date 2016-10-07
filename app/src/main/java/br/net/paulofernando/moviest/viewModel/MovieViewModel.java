package br.net.paulofernando.moviest.viewModel;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.data.entities.Movie;
import br.net.paulofernando.moviest.data.remote.TMDB;
import br.net.paulofernando.moviest.databinding.ItemMovieBinding;
import br.net.paulofernando.moviest.view.activity.MovieDetailsActivity;
import br.net.paulofernando.moviest.view.adapter.MovieAdapter;

public class MovieViewModel extends BaseObservable {

    private Context context;
    private Movie movie;
    private ItemMovieBinding binding;

    public MovieViewModel(Context context, Movie movie, ItemMovieBinding binding) {
        this.context = context;
        this.movie = movie;
        this.binding = binding;
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
        Intent intent = MovieDetailsActivity.getStartIntent(context, movie);

        String transitionName = context.getString(R.string.cover_name);
        ActivityOptions transitionActivityOptions = ActivityOptions.
                makeSceneTransitionAnimation((Activity) context, binding.coverIv, transitionName);

        context.startActivity(intent, transitionActivityOptions.toBundle());
    }

    public int getGenreVisibility() {
        return  movie.genreIds != null || movie.genresList != null ? View.VISIBLE : View.GONE;
    }

    public String getImageUrl() {
        return "http://image.tmdb.org/t/p/" + TMDB.SIZE_DEFAULT + movie.posterPath;
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        Picasso.with(view.getContext())
                .load(imageUrl)
                .into(view,
                        new com.squareup.picasso.Callback() {

                            @Override
                            public void onSuccess() {
                                //loading.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                //loading.setVisibility(View.GONE);
                            }

                        });
    }

}
