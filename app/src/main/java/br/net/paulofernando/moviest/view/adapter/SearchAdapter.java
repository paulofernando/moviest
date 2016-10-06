package br.net.paulofernando.moviest.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.data.remote.TMDB;
import br.net.paulofernando.moviest.data.entities.Movie;
import br.net.paulofernando.moviest.view.activity.MovieDetailsActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
                    TMDB.getInstance().moviesService().summaryRx(movie.id, TMDB.API_KEY)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<Movie>() {
                                @Override
                                public void onCompleted() {}

                                @Override
                                public void onError(Throwable e) {}

                                @Override
                                public void onNext(Movie movie) {
                                    Intent intent = new Intent(SearchAdapter.this.context, MovieDetailsActivity.class);
                                    intent.putExtra(TMDB.MOVIE_DETAILS, movie);
                                    SearchAdapter.this.context.startActivity(intent);
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