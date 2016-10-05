package br.net.paulofernando.moviest.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.communication.TMDB;
import br.net.paulofernando.moviest.communication.entities.Movie;
import br.net.paulofernando.moviest.ui.MovieDetailsActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Provide views to RecyclerView with data from movies.
 */
public class MovieListAdapter extends BaseAdapter<MovieListAdapter.ViewHolder> {

    private List<Movie> movies;
    private Context context;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        /** The index of the poster size in the themoviedb configuration*/
        private static final int POSTER_SIZE_INDEX = 3;

        @BindView(R.id.cover_iv) ImageView coverImageView;
        @BindView(R.id.title_tv) TextView titleTextView;
        @BindView(R.id.year_tv) TextView yearTextView;
        @BindView(R.id.genre_tv) TextView genreTextView;

        private Movie movie;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MovieListAdapter.this.context, MovieDetailsActivity.class);
                    intent.putExtra(TMDB.MOVIE_DETAILS, movie);


                    View sharedView = coverImageView;
                    String transitionName = context.getString(R.string.cover_name);

                    ActivityOptions transitionActivityOptions = ActivityOptions.
                            makeSceneTransitionAnimation((Activity) MovieListAdapter.this.context, sharedView, transitionName);

                    MovieListAdapter.this.context.startActivity(intent, transitionActivityOptions.toBundle());
                }
            });
        }

        public void setMovie(Movie movie) {
            this.movie = movie;

            if(movie.releaseDate != null) {
                yearTextView.setText(movie.releaseDate.substring(0, 4));
            }

            titleTextView.setText(movie.title);

            if((movie.genresList != null) && (movie.genresList.size() > 0)) {
                genreTextView.setVisibility(View.VISIBLE);
                genreTextView.setText(movie.genresList.get(0).name);
            } else if((movie.genreIds != null) && (movie.genreIds.length > 0)) {
                genreTextView.setVisibility(View.VISIBLE);
                genreTextView.setText(TMDB.getGenreNameByID(movie.genreIds[0]));
            }

            try {
                Picasso.with(context).load("http://image.tmdb.org/t/p/" + TMDB.getConfiguration().
                        images.posterSizes.get(POSTER_SIZE_INDEX) + movie.posterPath).
                        placeholder(R.drawable.cover_unloaded).
                        error(R.drawable.cover_unloaded).
                        into(coverImageView);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    }

    public MovieListAdapter(List<Movie> _listOfMovies, Context _context) {
        this.movies = _listOfMovies;
        this.context = _context;
    }

    public MovieListAdapter(Context _context) {
        this.movies = new ArrayList<>();
        this.context = _context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_movie, viewGroup, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.setMovie(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    @Override
    public void addList(List _movies){
        this.movies.addAll(_movies);
        notifyItemInserted(_movies.size() - 1);
    }

    public void setMovies(List _movies){
        this.movies = _movies;
        notifyDataSetChanged();
    }

    public void addMovie(Movie _movie){
        this.movies.add(_movie);
        notifyItemInserted(1);
        Log.i("Movies", "Size: " + this.movies.size());
    }
}