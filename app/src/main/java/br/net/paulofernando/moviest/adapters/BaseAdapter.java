package br.net.paulofernando.moviest.adapters;

import android.support.v7.widget.RecyclerView;

import java.util.List;

public abstract class BaseAdapter<T extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<T> {

    public abstract void addList(List<T> list);
}
