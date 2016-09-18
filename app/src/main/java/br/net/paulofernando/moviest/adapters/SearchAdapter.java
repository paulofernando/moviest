package br.net.paulofernando.moviest.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.communication.MovieDB;
import br.net.paulofernando.moviest.communication.entities.Movie;
import br.net.paulofernando.moviest.ui.MovieDetailsActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private final static String TAG = "SearchAdapter";
    private List<Movie> movieList;
    private Context context;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.search_result_tv) TextView searchResultView;

        private Movie movie;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Call<Movie> callSummary = MovieDB.getInstance().moviesService().summary(movie.id, MovieDB.API_KEY);
                    callSummary.enqueue(new Callback<Movie>() {
                        @Override
                        public void onResponse(Call<Movie> call, Response<Movie> response) {
                            if (response.isSuccessful()) {
                                Intent intent = new Intent(SearchAdapter.this.context, MovieDetailsActivity.class);
                                intent.putExtra(MovieDB.MOVIE_DETAILS, response.body());
                                SearchAdapter.this.context.startActivity(intent);
                            }
                        }
                        @Override
                        public void onFailure(Call<Movie> call, Throwable t) {
                            Log.d(TAG, t.getMessage());
                        }
                    });
                }
            });
        }

        public void setSearchResult(Movie movie) {
            this.movie = movie;
            if(!movie.releaseDate.equals("")) {
                searchResultView.setText(movie.title + " (" + movie.releaseDate.substring(0,4) + ")");
            } else {
                searchResultView.setText(movie.title);
            }

        }
    }

    public SearchAdapter(List<Movie> movieList, Context context) {
        this.movieList = movieList;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_search_result, viewGroup, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.setSearchResult(movieList.get(position));
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }
}