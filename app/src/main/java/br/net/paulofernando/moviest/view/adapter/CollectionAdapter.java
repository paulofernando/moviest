package br.net.paulofernando.moviest.view.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.net.paulofernando.moviest.R;
import br.net.paulofernando.moviest.data.entities.Collection;
import br.net.paulofernando.moviest.databinding.ItemCollectionBinding;
import br.net.paulofernando.moviest.viewModel.CollectionViewModel;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.BindingHolder> {

    private List<Collection> mCollections;
    private Context mContext;

    public CollectionAdapter(Context context) {
        mContext = context;
        mCollections = new ArrayList<>();
    }

    @Override
    public CollectionAdapter.BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemCollectionBinding collectionBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_collection,
                parent,
                false);
        return new CollectionAdapter.BindingHolder(collectionBinding);
    }

    @Override
    public void onBindViewHolder(CollectionAdapter.BindingHolder holder, int position) {
        ItemCollectionBinding collectionBinding = holder.binding;
        collectionBinding.setViewModel(new CollectionViewModel(mContext, mCollections.get(position), collectionBinding));
    }

    @Override
    public int getItemCount() {
        return mCollections.size();
    }

    public void setItems(List<Collection> collections) {
        mCollections = collections;
        notifyDataSetChanged();
    }

    public void addItem(Collection collection) {
        mCollections.add(collection);
        notifyItemInserted(1);
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {
        private ItemCollectionBinding binding;

        public BindingHolder(ItemCollectionBinding binding) {
            super(binding.collectionContainer);
            this.binding = binding;
        }
    }
}