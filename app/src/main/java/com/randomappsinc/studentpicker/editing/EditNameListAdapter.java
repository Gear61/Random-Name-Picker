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
import com.randomappsinc.studentpicker.utils.NameUtils;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditNameListAdapter extends RecyclerView.Adapter<EditNameListAdapter.NameViewHolder> {

    public interface Listener {
        void showNameOptions(NameDO nameDO);

        void showPhotoOptions(NameDO nameDO);
    }

    private List<NameDO> names;
    private final TextView noContent;
    private final TextView numNames;
    private final Listener listener;
    private int lastClickedPosition;

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

    NameDO getCurrentlySelectedName() {
        return names.get(lastClickedPosition);
    }

    void refreshSelectedItem() {
        notifyItemChanged(lastClickedPosition);
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
            NameDO nameDO = names.get(position);
            boolean nameHasPhoto = !TextUtils.isEmpty(nameDO.getPhotoUri());
            noImageIcon.setVisibility(nameHasPhoto ? View.GONE : View.VISIBLE);

            personImageView.setVisibility(nameHasPhoto ? View.VISIBLE : View.GONE);
            if (nameHasPhoto) {
                Picasso.get()
                        .load(nameDO.getPhotoUri())
                        .fit()
                        .centerCrop()
                        .into(personImageView);
            }

            nameTextView.setText(NameUtils.getDisplayTextForName(nameDO));
        }

        @OnClick(R.id.person_image)
        void onImageClick() {
            lastClickedPosition = getAdapterPosition();
            listener.showPhotoOptions(names.get(lastClickedPosition));
        }

        @OnClick(R.id.no_image_icon)
        void onNoImageClick() {
            lastClickedPosition = getAdapterPosition();
            listener.showPhotoOptions(names.get(lastClickedPosition));
        }

        @OnClick(R.id.parent)
        void onClick() {
            lastClickedPosition = getAdapterPosition();
            listener.showNameOptions(names.get(lastClickedPosition));
        }
    }
}
