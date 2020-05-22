package com.randomappsinc.studentpicker.editing;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.models.NameDO;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditNameListAdapter extends RecyclerView.Adapter<EditNameListAdapter.NameViewHolder> {

    public interface Listener {
        void showNameOptions(NameDO nameDO);
    }

    private List<NameDO> names;
    private TextView noContent;
    private TextView numNames;
    private Listener listener;

    EditNameListAdapter(TextView noContent, TextView numNames, List<NameDO> nameList, Listener listener) {
        this.names = nameList;
        this.noContent = noContent;
        this.numNames = numNames;
        this.listener = listener;
        setNameList(nameList);
    }

    private void setViews() {
        if (names.size() == 0) {
            numNames.setVisibility(View.GONE);
            noContent.setVisibility(View.VISIBLE);
        } else {
            int numInstances = 0;
            for (NameDO nameDO : names) {
                numInstances += nameDO.getAmount();
            }

            noContent.setVisibility(View.GONE);
            Context context = noContent.getContext();
            String names = numInstances == 1
                    ? context.getString(R.string.single_name)
                    : context.getString(R.string.plural_names);
            String numNamesText = numInstances + names;
            numNames.setText(numNamesText);
            numNames.setVisibility(View.VISIBLE);
        }
    }

    void setNameList(List<NameDO> nameList) {
        names = nameList;
        Collections.sort(nameList, (name1, name2) -> name1.getName().compareTo(name2.getName()));
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
        return names.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    class NameViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.person_image) ImageView personImageView;
        @BindView(R.id.no_image_icon) View noImageIcon;
        @BindView(R.id.person_name) TextView nameTextView;

        private NameViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void loadName(int position) {
            boolean nameHasPhoto = !TextUtils.isEmpty(names.get(position).getPhotoUri());
            personImageView.setVisibility(nameHasPhoto ? View.VISIBLE : View.GONE);
            noImageIcon.setVisibility(nameHasPhoto ? View.GONE : View.VISIBLE);

            String name = names.get(position).getName();
            int amount = names.get(position).getAmount();
            String nameText = amount == 1 ? name : name + " (" + amount + ")";
            nameTextView.setText(nameText);
        }

        @OnClick(R.id.no_image_icon)
        void onNoImageClick() {

        }

        @OnClick(R.id.parent)
        void onClick() {
            listener.showNameOptions(names.get(getAdapterPosition()));
        }
    }
}
