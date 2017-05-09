package com.randomappsinc.studentpicker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.randomappsinc.studentpicker.Database.DataSource;
import com.randomappsinc.studentpicker.Models.ChoosingSettings;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.Utils.NameUtils;
import com.randomappsinc.studentpicker.Utils.PreferencesManager;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class NameChoosingAdapter extends BaseAdapter {
    private Context context;
    private String outOfNames;
    private String noNames;
    private String listName;
    private List<String> names;
    private List<String> alreadyChosenNames;
    private TextView noContent;
    private TextView numNames;
    private DataSource dataSource;

    public NameChoosingAdapter(Context context, TextView noContent, TextView numNames, String listName) {
        this.outOfNames = context.getString(R.string.out_of_names);
        this.noNames = context.getString(R.string.no_names);
        this.context = context;
        this.listName = listName;
        this.dataSource = new DataSource();

        List<String> cachedNames = PreferencesManager.get().getCachedNameList(listName);
        this.names = cachedNames.isEmpty() ? dataSource.getAllNamesInList(listName) : cachedNames;
        this.alreadyChosenNames = PreferencesManager.get().getAlreadyChosenNames(listName);

        Collections.sort(this.names);
        this.noContent = noContent;
        this.numNames = numNames;
        setViews();
    }

    public void clearNameHistory() {
        alreadyChosenNames.clear();
    }

    public String getNamesHistory() {
        StringBuilder namesHistory = new StringBuilder();
        for (int i = 0; i < alreadyChosenNames.size(); i++) {
            if (i != 0) {
                namesHistory.append("\n");
            }
            namesHistory.append(alreadyChosenNames.get(i));
        }
        return namesHistory.toString();
    }

    public void addName(String name) {
        names.add(name);
        Collections.sort(names);
        notifyDataSetChanged();
        setViews();
    }

    public void addNames(List<String> newNames) {
        names.addAll(newNames);
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
        } else {
            noContent.setText(outOfNames);
        }
        if (names.isEmpty()) {
            numNames.setVisibility(View.GONE);
            noContent.setVisibility(View.VISIBLE);
        } else {
            noContent.setVisibility(View.GONE);
            String names = getCount() == 1
                    ? context.getString(R.string.single_name)
                    : context.getString(R.string.plural_names);
            String numNamesText = String.valueOf(getCount()) + names;
            numNames.setText(numNamesText);
            numNames.setVisibility(View.VISIBLE);
        }
    }

    // All name choosing goes through here
    public String chooseNamesAtRandom(List<Integer> indexes, ChoosingSettings settings) {
        StringBuilder chosenNames = new StringBuilder();
        for (int i = 0; i < indexes.size(); i++) {
            if (i != 0) {
                chosenNames.append("\n");
            }
            if (settings.getShowAsList()) {
                chosenNames.append(NameUtils.getPrefix(i));
            }
            chosenNames.append(names.get(indexes.get(i)));
            alreadyChosenNames.add(names.get(indexes.get(i)));
        }
        // If without replacement, remove the names
        if (!settings.getWithReplacement()) {
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

    public void cacheState(ChoosingSettings settings) {
        PreferencesManager.get().cacheNameChoosingList(listName, names, alreadyChosenNames, settings);
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

        private int position;

        public NameViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void loadName(int position) {
            this.position = position;
            this.name.setText(getItem(position));
        }

        @OnClick(R.id.delete_icon)
        public void deleteName() {
            removeNameAtPosition(position);
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
        } else {
            holder = (NameViewHolder) view.getTag();
        }
        holder.loadName(position);
        return view;
    }
}