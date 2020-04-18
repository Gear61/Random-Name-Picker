package com.randomappsinc.studentpicker.choosing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.models.ListInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NameChoosingAdapter extends RecyclerView.Adapter<NameChoosingAdapter.NameViewHolder> {

    public interface Listener {
        void onNameRemoved();
    }

    private ListInfo currentState;
    private Listener listener;

    NameChoosingAdapter(ListInfo listInfo, Listener listener) {
        this.currentState = listInfo;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.choose_name_cell, parent, false);
        return new NameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NameViewHolder holder, int position) {
        holder.loadName();
    }

    @Override
    public int getItemCount() {
        return currentState.getNumNames();
    }

    void refreshList(ListInfo newState) {
        this.currentState = newState;
        notifyDataSetChanged();
    }

    class NameViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.person_name) TextView name;

        NameViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void loadName() {
            name.setText(currentState.getNameText(getAdapterPosition()));
        }

        @OnClick(R.id.delete_icon)
        void deleteName() {
            currentState.removeAllInstancesOfName(getAdapterPosition());
            notifyDataSetChanged();
            listener.onNameRemoved();
        }
    }
}
