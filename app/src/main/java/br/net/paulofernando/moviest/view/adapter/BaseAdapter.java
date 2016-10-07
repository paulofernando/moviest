package br.net.paulofernando.moviest.view.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.List;

public interface BaseAdapter<T extends RecyclerView.ViewHolder> {

    public abstract void addList(List<T> list);
}
