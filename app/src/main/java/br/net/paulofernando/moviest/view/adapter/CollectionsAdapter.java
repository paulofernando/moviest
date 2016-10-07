package br.net.paulofernando.moviest.view.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.data.remote.TMDB;
import br.net.paulofernando.moviest.data.entities.Collection;
import br.net.paulofernando.moviest.view.activity.CollectionActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Provide views to RecyclerView with data from movies.
 */
public class CollectionsAdapter extends RecyclerView.Adapter<CollectionsAdapter.ViewHolder>
        implements BaseAdapter<CollectionsAdapter.ViewHolder> {

    private List<Collection> collections = new ArrayList<>();
    private Context context;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.collection_row_container) RelativeLayout collectionRowContainer;
        @BindView(R.id.title_collection_tv) TextView titleTextView;
        @BindView(R.id.bg_collection_iv) ImageView bgCollection;
        @BindView(R.id.loading_tv) TextView loading;
        @BindView(R.id.link_iv) ImageView link;

        private Collection collection;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CollectionsAdapter.this.context, CollectionActivity.class);
                    intent.putExtra(TMDB.COLLECTION_DETAILS, collection);

                    View sharedView = collectionRowContainer;
                    String transitionName = context.getString(R.string.collection_name);

                    ActivityOptions transitionActivityOptions = ActivityOptions.
                            makeSceneTransitionAnimation((Activity) CollectionsAdapter.this.context, sharedView, transitionName);

                    CollectionsAdapter.this.context.startActivity(intent, transitionActivityOptions.toBundle());
                }
            });
        }

        @OnClick(R.id.link_iv)
        public void linkClick() {
            Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(collection.sourceURL));
            CollectionsAdapter.this.context.startActivity(intent);
        }

        public void setCollection(Collection _collection) {
            this.collection = _collection;
            titleTextView.setText(collection.title);
            link.setVisibility(View.VISIBLE);
            try {
                loading.setVisibility(View.VISIBLE);
                Picasso.with(context).load(collection.backgroundImageURL).into(bgCollection,
                    new com.squareup.picasso.Callback() {

                        @Override
                        public void onSuccess() {
                            loading.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            loading.setVisibility(View.GONE);
                        }

                    });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    }

    public CollectionsAdapter(Context context) {
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
    public void addList(List _collections){
        this.collections.addAll(_collections);
        notifyItemInserted(_collections.size() - 1);
    }
}