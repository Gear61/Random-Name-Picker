package com.randomappsinc.studentpicker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.randomappsinc.studentpicker.Database.DataSource;
import com.randomappsinc.studentpicker.Models.ChoosingSettings;
import com.randomappsinc.studentpicker.Models.ListInfo;
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
    private ListInfo currentState;
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

        ListInfo info = PreferencesManager.get().getCachedNameList(listName);
        this.currentState = info == null ? dataSource.getListInfo(listName) : info;
        this.alreadyChosenNames = PreferencesManager.get().getAlreadyChosenNames(listName);

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

    public void addNames(String name, int amount) {
        currentState.addNames(name, amount);
        notifyDataSetChanged();
        setViews();
    }

    public void removeNames(String name, int amount) {
        currentState.removeNames(name, amount);
        notifyDataSetChanged();
        setViews();
    }

    public void changeNames(String oldName, String newName, int amount) {
        currentState.renamePeople(oldName, newName, amount);
        notifyDataSetChanged();
    }

    public void setViews() {
        if (dataSource.getListInfo(listName).getNumPeople() == 0) {
            noContent.setText(noNames);
        } else {
            noContent.setText(outOfNames);
        }
        if (currentState.getNumPeople() == 0) {
            numNames.setVisibility(View.GONE);
            noContent.setVisibility(View.VISIBLE);
        } else {
            noContent.setVisibility(View.GONE);
            String names = currentState.getNumPeople() == 1
                    ? context.getString(R.string.single_name)
                    : context.getString(R.string.plural_names);
            String numNamesText = String.valueOf(currentState.getNumPeople()) + names;
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
            // chosenNames.append(names.get(indexes.get(i)));
            // alreadyChosenNames.add(names.get(indexes.get(i)));
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
            // names.remove(index);
        }
        notifyDataSetChanged();
        setViews();
    }

    public void removeNameAtPosition(int position) {
        // names.remove(position);
        notifyDataSetChanged();
        setViews();
    }

    public void resetNames() {
        currentState = dataSource.getListInfo(listName);
        setViews();
        notifyDataSetChanged();
    }

    public void cacheState(ChoosingSettings settings) {
        PreferencesManager.get().cacheNameChoosingList(listName, currentState, alreadyChosenNames, settings);
    }

    @Override
    public int getCount() {
        return currentState.getNumNames();
    }

    @Override
    public String getItem(int position) {
        return currentState.getName(position);
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
            this.name.setText(currentState.getNameText(position));
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