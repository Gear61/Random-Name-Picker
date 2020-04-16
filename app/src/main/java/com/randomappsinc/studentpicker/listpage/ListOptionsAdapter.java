package com.randomappsinc.studentpicker.listpage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.randomappsinc.studentpicker.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListOptionsAdapter extends RecyclerView.Adapter<ListOptionsAdapter.OptionViewHolder> {

    public interface ItemSelectionListener {
        void onItemClick(int position);
    }

    private ItemSelectionListener itemSelectionListener;
    private String[] options;
    private String[] icons;

    public ListOptionsAdapter(
            Context context,
            ItemSelectionListener itemSelectionListener,
            @ArrayRes int options,
            @ArrayRes int icons) {
        this.itemSelectionListener = itemSelectionListener;
        this.options = context.getResources().getStringArray(options);
        this.icons = context.getResources().getStringArray(icons);
    }

    @Override
    @NonNull
    public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_option_cell,
                parent,
                false);
        return new OptionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionViewHolder holder, int position) {
        holder.loadOption(position);
    }

    @Override
    public int getItemCount() {
        return options.length;
    }

    class OptionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.icon) TextView icon;
        @BindView(R.id.option) TextView option;

        OptionViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void loadOption(int position) {
            option.setText(options[position]);
            icon.setText(icons[position]);
        }

        @OnClick(R.id.parent)
        void onOptionSelected() {
            itemSelectionListener.onItemClick(getAdapterPosition());
        }
    }
}
