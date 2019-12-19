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

        void onItemEditClick(int position, String listName);

        void onItemDeleteClick(int position);

        void setNoContent();
    }

    private List<String> nameLists = new ArrayList<>();
    private Delegate delegate;

    NameListsAdapter(Delegate delegate, Set<String> initialNameLists) {
        this.delegate = delegate;
        this.nameLists.addAll(initialNameLists);
        Collections.sort(nameLists);
    }

    void refresh(Set<String> newNameLists) {
        nameLists.clear();
        nameLists.addAll(newNameLists);
        Collections.sort(nameLists);
        notifyDataSetChanged();
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
        return nameLists.size();
    }

    void addList(String newList) {
        nameLists.add(newList);
        Collections.sort(nameLists);
        notifyDataSetChanged();
        delegate.setNoContent();
    }

    String getItem(int position) {
        return nameLists.get(position);
    }

    void renameItem(int position, String newName) {
        nameLists.set(position, newName);
        notifyItemChanged(position);
    }

    void deleteItem(int position) {
        nameLists.remove(position);
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
        void renameList() {
            String listName = nameLists.get(getAdapterPosition());
            delegate.onItemEditClick(getAdapterPosition(), listName);
        }

        @OnClick(R.id.delete_icon)
        void deleteList() {
            delegate.onItemDeleteClick(getAdapterPosition());
        }

        @Override
        public void onClick(View v) {
            delegate.onItemClick(getAdapterPosition());
        }
    }
}
