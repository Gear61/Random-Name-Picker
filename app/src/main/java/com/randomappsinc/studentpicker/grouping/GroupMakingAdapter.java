package com.randomappsinc.studentpicker.grouping;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.models.NameDO;
import com.randomappsinc.studentpicker.utils.NameUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupMakingAdapter extends RecyclerView.Adapter<GroupMakingAdapter.GroupViewHolder> {

    private final List<List<NameDO>> listOfGroups = new ArrayList<>();

    void setData(List<List<NameDO>> listOfGroups) {
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
        @BindView(R.id.group_names) LinearLayout groupNames;

        GroupViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void loadData() {
            groupNumber.setText(groupNumber.getContext()
                    .getString(R.string.group_number, getAdapterPosition() + 1));
            groupNames.removeAllViews();

            for (int i = 0; i < listOfGroups.get(getAdapterPosition()).size(); i++) {
                NameDO nameDO = listOfGroups.get(getAdapterPosition()).get(i);
                boolean nameHasPhoto = !TextUtils.isEmpty(nameDO.getPhotoUri());

                View view = LayoutInflater.from(itemView.getContext())
                        .inflate(R.layout.group_name_cell, null);
                ImageView personImageView = view.findViewById(R.id.person_image);
                TextView nameTextView = view.findViewById(R.id.person_name);

                personImageView.setVisibility(nameHasPhoto ? View.VISIBLE : View.GONE);
                if (nameHasPhoto) {
                   Picasso.get()
                           .load(nameDO.getPhotoUri())
                           .fit()
                           .centerCrop()
                           .into(personImageView);
                }
                nameTextView.append(NameUtils.getPrefix(i));
                nameTextView.append(nameDO.getName());
                groupNames.addView(view);
            }
        }
    }
}
