package com.randomappsinc.studentpicker.choosing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.utils.PreferencesManager;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NameChoosingAdapter extends RecyclerView.Adapter<NameChoosingAdapter.NameViewHolder> {

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

    @NonNull
    @Override
    public NameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.choose_name_cell, parent, false);
        return new NameChoosingAdapter.NameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NameViewHolder holder, int position) {
        holder.loadName();
    }

    @Override
    public int getItemCount() {
        return currentState.getNumNames();
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
        if (currentState.getNumNames() >= 1) {
            notifyItemRemoved(position);
        } else {
            notifyDataSetChanged();
        }
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

    int getCount() {
        return currentState.getNumNames();
    }

    class NameViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.person_name) TextView name;

        NameViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void loadName() {
            name.setText(currentState.getNameText(getAdapterPosition()));
        }

        @OnClick(R.id.delete_icon)
        public void deleteName() {
            removeNameAtPosition(getAdapterPosition());
        }
    }
}
