package br.net.paulofernando.moviest.viewModel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.view.View;

import br.net.paulofernando.moviest.data.entities.Movie;
import br.net.paulofernando.moviest.data.remote.TMDB;
import br.net.paulofernando.moviest.view.activity.MovieDetailsActivity;

public class MovieViewModel extends BaseObservable {

    private Context context;
    private Movie movie;

    public MovieViewModel(Context context, Movie movie) {
        this.context = context;
        this.movie = movie;
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

    public int getGenreVisibility() {
        return  movie.genreIds != null || movie.genresList != null ? View.VISIBLE : View.GONE;
    }

    private void launchMovieDetailsActivity() {
        context.startActivity(MovieDetailsActivity.getStartIntent(context, movie));
    }
}
