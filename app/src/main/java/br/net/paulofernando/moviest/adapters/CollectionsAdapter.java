package br.net.paulofernando.moviest.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.communication.MovieDB;
import br.net.paulofernando.moviest.communication.entities.Collection;
import br.net.paulofernando.moviest.ui.CollectionActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Provide views to RecyclerView with data from movies.
 */
public class CollectionsAdapter extends BaseAdapter<CollectionsAdapter.ViewHolder> {

    private List<Collection> collections;
    private Context context;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        /** The index of the poster size in the themoviedb configuration*/
        private static final int POSTER_SIZE_INDEX = 3;

        @BindView(R.id.title_collection_tv)
        TextView titleTextView;

        @BindView(R.id.bg_collection_iv)
        ImageView bgCollection;

        private Collection collection;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CollectionsAdapter.this.context, CollectionActivity.class);
                    intent.putExtra(MovieDB.COLLECTION_DETAILS, collection);

                    View sharedView = bgCollection;
                    String transitionName = context.getString(R.string.collection_name);

                    ActivityOptions transitionActivityOptions = ActivityOptions.
                            makeSceneTransitionAnimation((Activity) CollectionsAdapter.this.context, sharedView, transitionName);

                    CollectionsAdapter.this.context.startActivity(intent, transitionActivityOptions.toBundle());
                }
            });
        }

        public void setCollection(Collection _collection) {
            this.collection = _collection;

            titleTextView.setText(collection.title);

            try {
                Picasso.with(context).load(collection.backgroundImageURL).into(bgCollection);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    }

    public CollectionsAdapter(List<Collection> listOfCollections, Context context) {
        this.collections = listOfCollections;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_collection, viewGroup, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.setCollection(collections.get(position));
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    @Override
    public void addMovies(List _collections){
        this.collections.addAll(_collections);
        notifyItemInserted(_collections.size() - 1);
    }
}