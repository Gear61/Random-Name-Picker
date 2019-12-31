package com.randomappsinc.studentpicker.choosing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.utils.PreferencesManager;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NameChoosingAdapter extends BaseAdapter {

    private String listName;
    private ListInfo currentState;
    private TextView noContent;
    private TextView numNames;
    private DataSource dataSource;
    private PreferencesManager preferencesManager;

    NameChoosingAdapter(TextView noContent, TextView numNames, String listName) {
        this.listName = listName;
        this.dataSource = new DataSource(noContent.getContext());

        this.preferencesManager = new PreferencesManager(noContent.getContext());
        ListInfo info = preferencesManager.getNameListState(listName);
        this.currentState = info == null ? dataSource.getListInfo(listName) : info;

        this.noContent = noContent;
        this.numNames = numNames;
        setViews();
    }

    void refreshList(ListInfo newState) {
        this.currentState = newState;
        notifyDataSetChanged();
        setViews();
    }

    void clearNameHistory() {
        currentState.getNameHistory().clear();
    }

    String getNamesHistory() {
        StringBuilder namesHistory = new StringBuilder();
        for (int i = 0; i < currentState.getNameHistory().size(); i++) {
            if (i != 0) {
                namesHistory.append("\n");
            }
            namesHistory.append(currentState.getNameHistory().get(i));
        }
        return namesHistory.toString();
    }

    void addNames(String name, int amount) {
        currentState.addNames(name, amount);
        notifyDataSetChanged();
        setViews();
    }

    void addNameMap(Map<String, Integer> nameAmounts) {
        for (String name : nameAmounts.keySet()) {
            currentState.addNames(name, nameAmounts.get(name));
        }
        notifyDataSetChanged();
        setViews();
    }

    void removeNames(String name, int amount) {
        currentState.removeNames(name, amount);
        notifyDataSetChanged();
        setViews();
    }

    void changeNames(String oldName, String newName, int amount) {
        currentState.renamePeople(oldName, newName, amount);
        notifyDataSetChanged();
    }

    private void setViews() {
        if (dataSource.getListInfo(listName).getNumInstances() == 0) {
            noContent.setText(R.string.no_names_for_choosing);
        } else {
            noContent.setText(R.string.out_of_names);
        }
        if (currentState.getNumInstances() == 0) {
            numNames.setVisibility(View.GONE);
            noContent.setVisibility(View.VISIBLE);
        } else {
            noContent.setVisibility(View.GONE);
            Context context = numNames.getContext();
            String namesText = currentState.getNumInstances() == 1
                    ? context.getString(R.string.one_name)
                    : context.getString(R.string.x_names, currentState.getNumInstances());
            numNames.setText(namesText);
            numNames.setVisibility(View.VISIBLE);
        }
    }

    // All name choosing goes through here
    String chooseNamesAtRandom(List<Integer> indexes, ChoosingSettings settings) {
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

    void resetNames() {
        currentState = dataSource.getListInfo(listName);
        notifyDataSetChanged();
        setViews();
    }

    void cacheState(ChoosingSettings settings) {
        preferencesManager.setNameListState(listName, currentState, settings);
    }

    int getNumInstances() {
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

    class NameViewHolder {

        @BindView(R.id.person_name) TextView name;

        private int position;

        NameViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        void loadName(int position) {
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
            LayoutInflater vi = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
