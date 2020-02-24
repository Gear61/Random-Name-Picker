package com.randomappsinc.studentpicker.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.models.ListDO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NameListsAdapter extends RecyclerView.Adapter<NameListsAdapter.NameListViewHolder> {

    private final Comparator<ListDO> LIST_SORTER =
            (listOne, listTwo) -> listOne.getName().compareTo(listTwo.getName());

    public interface Delegate {
        void onItemClick(ListDO listDO);

        void onItemEditClick(int position, ListDO listDO);

        void onItemDeleteClick(int position, ListDO listDO);

        void setNoContent();
    }

    private List<ListDO> nameLists = new ArrayList<>();
    private Delegate delegate;

    NameListsAdapter(Delegate delegate, List<ListDO> initialNameLists) {
        this.delegate = delegate;
        this.nameLists.addAll(initialNameLists);
        Collections.sort(nameLists, LIST_SORTER);
    }

    void refresh(List<ListDO> newNameLists) {
        nameLists.clear();
        nameLists.addAll(newNameLists);
        Collections.sort(nameLists, LIST_SORTER);
        notifyDataSetChanged();
    }

    ListDO getItem(int position) {
        return nameLists.get(position);
    }

    @NonNull
    @Override
    public NameListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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

    void addList(ListDO listDO) {
        nameLists.add(listDO);
        Collections.sort(nameLists, LIST_SORTER);
        notifyDataSetChanged();
        delegate.setNoContent();
    }

    void renameItem(int position, String newName) {
        nameLists.get(position).setName(newName);
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
            this.listName.setText(nameLists.get(position).getName());
        }

        @OnClick(R.id.edit_icon)
        void renameList() {
            ListDO listDO = nameLists.get(getAdapterPosition());
            delegate.onItemEditClick(getAdapterPosition(), listDO);
        }

        @OnClick(R.id.delete_icon)
        void deleteList() {
            ListDO listDO = nameLists.get(getAdapterPosition());
            delegate.onItemDeleteClick(getAdapterPosition(), listDO);
        }

        @Override
        public void onClick(View v) {
            delegate.onItemClick(nameLists.get(getAdapterPosition()));
        }
    }
}
