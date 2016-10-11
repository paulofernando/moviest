package br.net.paulofernando.moviest.viewModel;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Timer;
import java.util.TimerTask;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.data.entities.Collection;
import br.net.paulofernando.moviest.databinding.ItemCollectionBinding;
import br.net.paulofernando.moviest.view.activity.CollectionActivity;

public class CollectionViewModel extends BaseObservable {

    Context context;
    private Collection collection;
    private ItemCollectionBinding binding;
    private boolean imageOnLoading;

    public ObservableField<Drawable> collectionImage;
    private CollectionViewModel.BindableFieldTarget bindableFieldTarget;

    public CollectionViewModel(final Context context, Collection collection, ItemCollectionBinding binding) {
        this.context = context;
        this.collection = collection;
        this.binding = binding;

        collectionImage = new ObservableField<>();
        // Picasso keeps a weak reference to the target so it needs to be stored in a field
        bindableFieldTarget = new CollectionViewModel.BindableFieldTarget(collectionImage, context.getResources());
        Picasso.with(context)
                .load(getImageUrl())
                .error(R.drawable.collection_unloaded)
                .into(bindableFieldTarget);
    }

    public View.OnClickListener onClickCollection() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCollectionActivity();
            }
        };
    }

    public View.OnClickListener onClickLink() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchLink();
            }
        };
    }

    public String getCollectionTitle() {
        return collection.title;
    }

    public String getImageUrl() {
        return collection.backgroundImageURL;
    }

    public int getLinkVisibility() {
        return ((collection.sourceURL != null) && (!collection.sourceURL.equals(""))) ? View.VISIBLE : View.GONE;
    }

    public String getThumbnailUrl() {
        return collection.backgroundThumbnailURL;
    }

    private void launchCollectionActivity() {
        Intent intent = CollectionActivity.getStartIntent(context, collection);

        String transitionName = context.getString(R.string.collection_name);
        ActivityOptions transitionActivityOptions = ActivityOptions.
                makeSceneTransitionAnimation((Activity) context, binding.bgCollectionIv, transitionName);

        context.startActivity(intent, transitionActivityOptions.toBundle());
    }

    private void launchLink() {
        Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(collection.sourceURL));
        context.startActivity(intent);
    }

    public class BindableFieldTarget implements Target {

        private ObservableField<Drawable> observableField;
        private Resources resources;

        public BindableFieldTarget(ObservableField<Drawable> observableField, Resources resources) {
            this.observableField = observableField;
            this.resources = resources;
            binding.loadingTv.setVisibility(View.VISIBLE);
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            observableField.set(new BitmapDrawable(resources, bitmap));
            binding.loadingTv.setVisibility(View.GONE);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            observableField.set(errorDrawable);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            observableField.set(placeHolderDrawable);
        }
    }

}
