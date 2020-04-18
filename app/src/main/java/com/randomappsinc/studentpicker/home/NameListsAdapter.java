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

        void onChooseButtonClicked(ListDO listDO);

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

    class NameListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_name) TextView listName;

        NameListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void loadList(int position) {
            this.listName.setText(nameLists.get(position).getName());
        }

        @OnClick(R.id.list_cell_parent)
        void onEntireCellClicked() {
            delegate.onItemClick(nameLists.get(getAdapterPosition()));
        }

        @OnClick(R.id.choose_button)
        void onChooseClicked() {
            delegate.onChooseButtonClicked(nameLists.get(getAdapterPosition()));
        }
    }
}
