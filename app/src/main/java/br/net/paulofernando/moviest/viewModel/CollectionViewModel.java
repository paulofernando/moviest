package br.net.paulofernando.moviest.viewModel;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.data.entities.Collection;
import br.net.paulofernando.moviest.databinding.ItemCollectionBinding;
import br.net.paulofernando.moviest.view.activity.CollectionActivity;

public class CollectionViewModel {

    private Context context;
    private Collection collection;
    private ItemCollectionBinding binding;

    public CollectionViewModel(Context context, Collection collection, ItemCollectionBinding binding) {
        this.context = context;
        this.collection = collection;
        this.binding = binding;
    }

    public View.OnClickListener onClickCollection() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCollectionActivity();
            }
        };
    }

    private void launchCollectionActivity() {
        Intent intent = CollectionActivity.getStartIntent(context, collection);

        String transitionName = context.getString(R.string.collection_name);
        ActivityOptions transitionActivityOptions = ActivityOptions.
        makeSceneTransitionAnimation((Activity) context, binding.bgCollectionIv, transitionName);

        context.startActivity(intent, transitionActivityOptions.toBundle());

    }

    public String getCollectionTitle() {
        return collection.title;
    }

    public String getImageUrl() {
        return collection.backgroundImageURL;
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
