package com.randomappsinc.studentpicker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.randomappsinc.studentpicker.Database.DataSource;
import com.randomappsinc.studentpicker.Misc.PreferencesManager;
import com.randomappsinc.studentpicker.R;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class NameChoosingAdapter extends BaseAdapter {
    private Context context;
    private String outOfNames;
    private String noNames;
    private String listName;
    private List<String> names;
    private TextView noContent;
    private TextView numNames;
    private DataSource dataSource;

    public NameChoosingAdapter(Context context, TextView noContent, TextView numNames, String listName) {
        this.outOfNames = context.getString(R.string.out_of_names);
        this.noNames = context.getString(R.string.no_names);
        this.context = context;
        this.listName = listName;
        this.dataSource = new DataSource(context);
        List<String> cachedNames = PreferencesManager.get().getCachedNameList(listName);
        this.names = cachedNames.isEmpty() ? dataSource.getAllNamesInList(listName) : cachedNames;
        Collections.sort(this.names);
        this.noContent = noContent;
        this.numNames = numNames;
        setViews();
    }

    public void addName(String name) {
        names.add(name);
        Collections.sort(names);
        notifyDataSetChanged();
        setViews();
    }

    public void removeName(String name) {
        for (int i = 0; i < names.size(); i++) {
            if (names.get(i).equals(name)) {
                names.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
        setViews();
    }

    public void changeName(String oldName, String newName) {
        for (int i = 0; i < names.size(); i++) {
            if (names.get(i).equals(oldName)) {
                names.set(i, newName);
                Collections.sort(names);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void setViews() {
        if (dataSource.getAllNamesInList(listName).isEmpty()) {
            noContent.setText(noNames);
        }
        else {
            noContent.setText(outOfNames);
        }
        if (names.isEmpty()) {
            numNames.setVisibility(View.GONE);
            noContent.setVisibility(View.VISIBLE);
        }
        else {
            noContent.setVisibility(View.GONE);
            String names = getCount() == 1
                    ? context.getString(R.string.single_name)
                    : context.getString(R.string.plural_names);
            String numNamesText = String.valueOf(getCount()) + names;
            numNames.setText(numNamesText);
            numNames.setVisibility(View.VISIBLE);
        }
    }

    public String chooseNamesAtRandom(List<Integer> indexes, boolean withReplacement) {
        StringBuilder chosenNames = new StringBuilder();
        for (int i = 0; i < indexes.size(); i++) {
            if (i != 0) {
                chosenNames.append("\n");
            }
            chosenNames.append(names.get(indexes.get(i)));
        }
        // If without replacement, remove the names
        if (!withReplacement) {
            removeNamesAtPositions(indexes);
        }
        return chosenNames.toString();
    }

    public void removeNamesAtPositions(List<Integer> indexes) {
        Collections.sort(indexes);
        Collections.reverse(indexes);
        for (int index : indexes) {
            names.remove(index);
        }
        notifyDataSetChanged();
        setViews();
    }

    public void removeNameAtPosition(int position) {
        names.remove(position);
        notifyDataSetChanged();
        setViews();
    }

    public void resetNames() {
        names.clear();
        names.addAll(dataSource.getAllNamesInList(listName));
        Collections.sort(names);
        setViews();
        notifyDataSetChanged();
    }

    public void cacheNamesList() {
        if (PreferencesManager.get().doesListExist(listName)) {
            PreferencesManager.get().cacheNameChoosingList(listName, names);
        }
    }

    public void processListNameChange(String newListName) {
        PreferencesManager.get().moveNamesListCache(listName, newListName);
        listName = newListName;
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public String getItem(int position) {
        return names.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    public class NameViewHolder {
        @Bind(R.id.person_name) TextView name;
        @Bind(R.id.delete_icon) IconTextView delete;

        public NameViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        NameViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.choose_name_cell, parent, false);
            holder = new NameViewHolder(view);
            view.setTag(holder);
        }
        else {
            holder = (NameViewHolder) view.getTag();
        }
        holder.name.setText(names.get(position));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                removeNameAtPosition(position);
            }
        });
        return view;
    }
}