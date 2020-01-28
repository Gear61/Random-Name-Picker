package com.randomappsinc.studentpicker.editing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.models.ListInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditNameListAdapter extends BaseAdapter {

    private ListInfo content;
    private TextView noContent;
    private TextView numNames;

    public EditNameListAdapter(
            TextView noContent,
            TextView numNames,
            String listName) {
        Context context = noContent.getContext();
        DataSource dataSource = new DataSource(context);
        this.content = dataSource.getListInfo(listName);
        this.noContent = noContent;
        this.numNames = numNames;
        setViews();
    }

    public ListInfo getListInfo() {
        return content;
    }

    private void setViews() {
        if (content.getNumInstances() == 0) {
            numNames.setVisibility(View.GONE);
            noContent.setVisibility(View.VISIBLE);
        } else {
            noContent.setVisibility(View.GONE);
            Context context = noContent.getContext();
            String names = content.getNumInstances() == 1
                    ? context.getString(R.string.single_name)
                    : context.getString(R.string.plural_names);
            String numNamesText = String.valueOf(content.getNumInstances()) + names;
            numNames.setText(numNamesText);
            numNames.setVisibility(View.VISIBLE);
        }
    }

    public void addNames(String name, int amount) {
        content.addNames(name, amount);
        notifyDataSetChanged();
        setViews();
    }

    public void removeNames(String name, int amount) {
        content.removeNames(name, amount);
        notifyDataSetChanged();
        setViews();
    }

    public void setNameAmount(String name, int amount) {
        content.setNameAmount(name, amount);
        notifyDataSetChanged();
        setViews();
    }

    public void changeName(String oldName, String newName, int amount) {
        content.renamePeople(oldName, newName, amount);
        notifyDataSetChanged();
    }

    public void importNamesFromList(ListInfo updatedListInfo) {
        content = updatedListInfo;
        notifyDataSetChanged();
        setViews();
    }

    @Override
    public int getCount() {
        return content.getNumNames();
    }

    @Override
    public String getItem(int position) {
        return content.getName(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    class NameViewHolder {
        @BindView(R.id.person_name) TextView name;

        private NameViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        private void loadName(int position) {
            name.setText(content.getNameText(position));
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        NameViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.edit_person_name_cell, parent, false);
            holder = new NameViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (NameViewHolder) view.getTag();
        }
        holder.loadName(position);
        return view;
    }
}
