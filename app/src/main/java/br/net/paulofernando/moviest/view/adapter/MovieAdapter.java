package br.net.paulofernando.moviest.view.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.data.entities.Movie;
import br.net.paulofernando.moviest.databinding.ItemMovieBinding;
import br.net.paulofernando.moviest.viewModel.MovieViewModel;

/**
 * Provide views to RecyclerView with data from mMovies.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.BindingHolder> {

    private List<Movie> mMovies;
    private Context mContext;

    public MovieAdapter(Context context) {
        mContext = context;
        mMovies = new ArrayList<>();
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemMovieBinding movieBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_movie,
                parent,
                false);
        return new BindingHolder(movieBinding);
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, int position) {
        ItemMovieBinding movieBinding = holder.binding;
        movieBinding.setViewModel(new MovieViewModel(mContext, mMovies.get(position), movieBinding));
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public void setItems(List<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    public void addItem(Movie post) {
        mMovies.add(post);
        notifyItemInserted(1);
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {
        private ItemMovieBinding binding;

        public BindingHolder(ItemMovieBinding binding) {
            super(binding.movieContainer);
            this.binding = binding;
        }
    }
}