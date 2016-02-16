package com.randomappsinc.studentpicker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.Utils.PreferencesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class NameListsAdapter extends BaseAdapter {
    private Context context;
    private List<String> content;
    private TextView noContent;

    public NameListsAdapter(Context context, TextView noContent) {
        this.context = context;
        this.content = new ArrayList<>();
        this.noContent = noContent;
        refreshList();
    }

    public void refreshList() {
        content.clear();
        content.addAll(PreferencesManager.get().getNameLists());
        Collections.sort(content);
        setNoContent();
        notifyDataSetChanged();
    }

    public void setNoContent() {
        int viewVisibility = content.isEmpty() ? View.VISIBLE : View.GONE;
        noContent.setVisibility(viewVisibility);
    }

    public void addList(String itemName) {
        content.add(itemName);
        Collections.sort(content);
        setNoContent();
        PreferencesManager.get().addNameList(itemName);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return content.size();
    }

    @Override
    public String getItem(int position) {
        return content.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    public static class ViewHolder {
        @Bind(R.id.name) TextView listName;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.names_list_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }
        holder.listName.setText(content.get(position));
        return view;
    }
}
