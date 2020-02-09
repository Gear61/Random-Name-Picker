package com.randomappsinc.studentpicker.grouping;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.utils.NameUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupsMakingAdapter extends RecyclerView.Adapter<GroupsMakingAdapter.GroupViewHolder> {

    private List<List<String>> listOfGroups = new ArrayList<>();

    void setData(List<List<String>> listOfGroups) {
        this.listOfGroups.clear();
        this.listOfGroups.addAll(listOfGroups);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_list_item, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        holder.loadData();
    }

    @Override
    public int getItemCount() {
        return listOfGroups.size();
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.group_number) TextView groupNumber;
        @BindView(R.id.group_names) TextView groupNames;

        GroupViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void loadData() {
            groupNumber.setText(groupNumber.getContext()
                    .getString(R.string.group_number, getAdapterPosition() + 1));
            groupNames.setText("");
            for (int i = 0; i < listOfGroups.get(getAdapterPosition()).size(); i++) {
                if (i != 0) {
                    groupNames.append("\n");
                }
                groupNames.append(NameUtils.getPrefix(i));
                groupNames.append(listOfGroups.get(getAdapterPosition()).get(i));
            }
        }
    }
}
