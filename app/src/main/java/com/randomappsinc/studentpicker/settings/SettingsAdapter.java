package com.randomappsinc.studentpicker.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingViewHolder> {

    public interface ItemSelectionListener {
        void onItemClick(int position);
    }

    private @NonNull ItemSelectionListener itemSelectionListener;
    private String[] options;
    private String[] icons;
    private PreferencesManager preferencesManager;

    SettingsAdapter(Context context, @NonNull ItemSelectionListener itemSelectionListener) {
        this.itemSelectionListener = itemSelectionListener;
        this.options = context.getResources().getStringArray(R.array.settings_options);
        this.icons = context.getResources().getStringArray(R.array.settings_icons);
        this.preferencesManager = new PreferencesManager(context);
    }

    @Override
    @NonNull
    public SettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.settings_list_item,
                parent,
                false);
        return new SettingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingViewHolder holder, int position) {
        holder.loadSetting(position);
    }

    @Override
    public int getItemCount() {
        return options.length;
    }

    class SettingViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.settings_icon) TextView icon;
        @BindView(R.id.settings_option) TextView option;
        @BindView(R.id.shake_toggle) Switch toggle;

        SettingViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void loadSetting(int position) {
            option.setText(options[position]);
            icon.setText(icons[position]);

            if (position == 0) {
                UIUtils.setCheckedImmediately(toggle, preferencesManager.isShakeEnabled());
                toggle.setVisibility(View.VISIBLE);
            } else {
                toggle.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.shake_toggle)
        void onToggle() {
            preferencesManager.setShakeEnabled(toggle.isChecked());
        }

        @OnClick(R.id.parent)
        void onSettingSelected() {
            itemSelectionListener.onItemClick(getAdapterPosition());
        }
    }
}
