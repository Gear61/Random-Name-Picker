package com.randomappsinc.studentpicker.editing;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditNameListAdapter extends RecyclerView.Adapter<EditNameListAdapter.NameViewHolder> {

    public interface Listener {
        void showNameOptions(String name);
    }

    private ListInfo content;
    private TextView noContent;
    private TextView numNames;
    private Listener listener;

    EditNameListAdapter(TextView noContent, TextView numNames, String listName, Listener listener) {
        Context context = noContent.getContext();
        DataSource dataSource = new DataSource(context);
        this.content = dataSource.getListInfo(listName);
        this.noContent = noContent;
        this.numNames = numNames;
        this.listener = listener;
        setViews();
    }

    ListInfo getListInfo() {
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
            String numNamesText = (content.getNumInstances()) + names;
            numNames.setText(numNamesText);
            numNames.setVisibility(View.VISIBLE);
        }
    }

    void addNames(String name, int amount) {
        content.addNames(name, amount);
        notifyDataSetChanged();
        setViews();
    }

    void removeNames(String name, int amount) {
        content.removeNames(name, amount);
        notifyDataSetChanged();
        setViews();
    }

    void changeName(String oldName, String newName, int amount) {
        content.renamePeople(oldName, newName, amount);
        notifyDataSetChanged();
    }

    void importNamesFromList(ListInfo updatedListInfo) {
        content = updatedListInfo;
        notifyDataSetChanged();
        setViews();
    }

    @NonNull
    @Override
    public NameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.edit_person_name_cell, parent, false);
        return new NameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NameViewHolder holder, int position) {
        holder.loadName(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return content.getNumNames();
    }

    private String getItem(int position) {
        return content.getName(position);
    }

    class NameViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.person_name) TextView name;

        private NameViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void loadName(int position) {
            name.setText(content.getNameText(position));
        }

        @OnClick(R.id.parent)
        public void onClick(View v) {
            listener.showNameOptions(getItem(getAdapterPosition()));
        }
    }
}
