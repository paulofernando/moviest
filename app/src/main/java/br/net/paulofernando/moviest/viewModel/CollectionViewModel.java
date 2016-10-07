package br.net.paulofernando.moviest.viewModel;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.data.entities.Collection;
import br.net.paulofernando.moviest.view.activity.CollectionActivity;

public class CollectionViewModel {

    private Context context;
    private Collection collection;

    public CollectionViewModel(Context context, Collection collection) {
        this.context = context;
        this.collection = collection;
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
        context.startActivity(CollectionActivity.getStartIntent(context, collection));
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
