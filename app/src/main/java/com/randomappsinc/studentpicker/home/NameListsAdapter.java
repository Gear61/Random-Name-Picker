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
            (listOne, listTwo) -> listOne.getName().toLowerCase().compareTo(listTwo.getName().toLowerCase());

    public interface Delegate {
        void onItemClick(ListDO listDO);

        void onItemEditClick(int position, ListDO listDO);

        void onItemDeleteClick(int position, ListDO listDO);

        void setNoContent();
    }

    private List<ListDO> allNameLists = new ArrayList<>();
    private List<ListDO> filteredLists = new ArrayList<>();
    private String searchTerm = "";
    private Delegate delegate;

    NameListsAdapter(Delegate delegate, List<ListDO> initialNameLists) {
        this.delegate = delegate;
        this.allNameLists.addAll(initialNameLists);
        this.filteredLists.addAll(initialNameLists);
        Collections.sort(filteredLists, LIST_SORTER);
    }

    void refresh(List<ListDO> newNameLists) {
        allNameLists.clear();
        allNameLists.addAll(newNameLists);
        filter(searchTerm);
        notifyDataSetChanged();
    }

    void filter(String searchTerm) {
        this.searchTerm = searchTerm;
        filteredLists.clear();
        if (searchTerm.isEmpty()) {
            filteredLists.addAll(allNameLists);
        } else {
            for (ListDO listDO : allNameLists) {
                if (listDO.getName().toLowerCase().contains(searchTerm.toLowerCase())) {
                    filteredLists.add(listDO);
                }
            }
        }
        Collections.sort(filteredLists, LIST_SORTER);
        notifyDataSetChanged();
    }

    ListDO getItem(int position) {
        return filteredLists.get(position);
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
        return filteredLists.size();
    }

    void renameItem(int position, String newName) {
        filteredLists.get(position).setName(newName);
        notifyItemChanged(position);
    }

    void deleteItem(int position) {
        ListDO removedList = filteredLists.remove(position);
        allNameLists.remove(removedList);
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
            this.listName.setText(filteredLists.get(position).getName());
        }

        @OnClick(R.id.edit_icon)
        void renameList() {
            ListDO listDO = filteredLists.get(getAdapterPosition());
            delegate.onItemEditClick(getAdapterPosition(), new ListDO(listDO));
        }

        @OnClick(R.id.delete_icon)
        void deleteList() {
            ListDO listDO = filteredLists.get(getAdapterPosition());
            delegate.onItemDeleteClick(getAdapterPosition(), new ListDO(listDO));
        }

        @Override
        public void onClick(View v) {
            delegate.onItemClick(filteredLists.get(getAdapterPosition()));
        }
    }
}
