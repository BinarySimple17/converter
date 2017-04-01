package ru.binarysimple.sbrf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CurrencyAdapter extends BaseAdapter {

    private final Context context;
    private List<CurrencyNode> items;

    public CurrencyAdapter(Context context) {
        this.context = context;
        items = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public CurrencyNode getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        if (items.get(i) == null) {
            return -1;
        } else {
            return items.get(i).getNumCode();
        }
    }

    public void setItems(List<CurrencyNode> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context)
                    .inflate(android.R.layout.simple_expandable_list_item_1, null);
        }
        ((TextView) view.findViewById(android.R.id.text1))
                .setText(String.valueOf(items.get(i).getCharCode()));
        return view;
    }
}
