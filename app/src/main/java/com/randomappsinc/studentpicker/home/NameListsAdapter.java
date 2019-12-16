package com.randomappsinc.studentpicker.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.randomappsinc.studentpicker.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NameListsAdapter extends RecyclerView.Adapter<NameListsAdapter.NameListViewHolder> {

    public interface Delegate {
        void onItemClick(int position);

        void onItemEditClick(int position);

        void onItemDeleteClick(int position);

        void setNoContent();
    }

    private List<String> content;
    private Delegate delegate;

    NameListsAdapter(Delegate delegate, Set<String> prefsNameSet) {
        this.delegate = delegate;
        this.content = new ArrayList<>();

        content.addAll(prefsNameSet);
        Collections.sort(content);
    }

    @NonNull
    @Override
    public NameListsAdapter.NameListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.names_list_item, parent, false);
        return new NameListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NameListsAdapter.NameListViewHolder holder, int position) {
        holder.loadList(position);
    }

    @Override
    public int getItemCount() {
        return content.size();
    }

    void addList(String newList) {
        content.add(newList);
        Collections.sort(content);
        notifyDataSetChanged();
        delegate.setNoContent();
    }

    String getItem(int position) {
        return content.get(position);
    }

    void renameItem(int position, String newName) {
        content.set(position, newName);
        notifyItemChanged(position);
    }

    void deleteItem(int position) {
        content.remove(position);
        notifyItemRemoved(position);
        delegate.setNoContent();

    }

    class NameListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.list_name) TextView listName;

        NameListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        void loadList(int position) {
            this.listName.setText(getItem(position));
        }

        @OnClick(R.id.edit_icon)
        public void renameList() {
            delegate.onItemEditClick(getAdapterPosition());
        }

        @OnClick(R.id.delete_icon)
        public void deleteList() {
            delegate.onItemDeleteClick(getAdapterPosition());
        }

        @Override
        public void onClick(View v) {
            delegate.onItemClick(getAdapterPosition());
        }
    }
}