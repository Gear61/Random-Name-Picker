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
import com.randomappsinc.studentpicker.Utils.PreferencesManager;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NameChoosingAdapter extends BaseAdapter {

    private Context context;
    private String listName;
    private ListInfo currentState;
    private TextView noContent;
    private TextView numNames;
    private DataSource dataSource;

    public NameChoosingAdapter(Context context, TextView noContent, TextView numNames, String listName) {
        this.context = context;
        this.listName = listName;
        this.dataSource = new DataSource();

        ListInfo info = PreferencesManager.get().getNameListState(listName);
        this.currentState = info == null ? dataSource.getListInfo(listName) : info;

        this.noContent = noContent;
        this.numNames = numNames;
        setViews();
    }

    public void resync() {
        ListInfo info = PreferencesManager.get().getNameListState(listName);
        currentState = info == null ? dataSource.getListInfo(listName) : info;
        notifyDataSetChanged();
        setViews();
    }

    public void clearNameHistory() {
        currentState.getNameHistory().clear();
    }

    public String getNamesHistory() {
        StringBuilder namesHistory = new StringBuilder();
        for (int i = 0; i < currentState.getNameHistory().size(); i++) {
            if (i != 0) {
                namesHistory.append("\n");
            }
            namesHistory.append(currentState.getNameHistory().get(i));
        }
        return namesHistory.toString();
    }

    public void addNames(String name, int amount) {
        currentState.addNames(name, amount);
        notifyDataSetChanged();
        setViews();
    }

    public void addNameMap(Map<String, Integer> nameAmounts) {
        for (String name : nameAmounts.keySet()) {
            currentState.addNames(name, nameAmounts.get(name));
        }
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
        if (dataSource.getListInfo(listName).getNumInstances() == 0) {
            noContent.setText(R.string.no_names);
        } else {
            noContent.setText(R.string.out_of_names);
        }
        if (currentState.getNumInstances() == 0) {
            numNames.setVisibility(View.GONE);
            noContent.setVisibility(View.VISIBLE);
        } else {
            noContent.setVisibility(View.GONE);
            String names = currentState.getNumInstances() == 1
                    ? context.getString(R.string.single_name)
                    : context.getString(R.string.plural_names);
            String numNamesText = String.valueOf(currentState.getNumInstances()) + names;
            numNames.setText(numNamesText);
            numNames.setVisibility(View.VISIBLE);
        }
    }

    // All name choosing goes through here
    public String chooseNamesAtRandom(List<Integer> indexes, ChoosingSettings settings) {
        String chosenNames = currentState.chooseNames(indexes, settings);
        if (!settings.getWithReplacement()) {
            notifyDataSetChanged();
            setViews();
        }
        return chosenNames;
    }

    private void removeNameAtPosition(int position) {
        currentState.removeAllInstancesOfName(position);
        notifyDataSetChanged();
        setViews();
    }

    public void resetNames() {
        currentState = dataSource.getListInfo(listName);
        notifyDataSetChanged();
        setViews();
    }

    public void cacheState(ChoosingSettings settings) {
        PreferencesManager.get().cacheNameChoosingList(listName, currentState, settings);
    }

    public int getNumInstances() {
        return currentState.getNumInstances();
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