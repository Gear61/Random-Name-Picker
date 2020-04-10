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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingViewHolder> {

    private static final int NUM_SETTINGS_OPTIONS = 6;

    public interface ItemSelectionListener {
        void onItemClick(int position);
    }

    private ItemSelectionListener itemSelectionListener;
    private List<String> options;
    private List<String> icons;
    private PreferencesManager preferencesManager;

    SettingsAdapter(Context context, ItemSelectionListener itemSelectionListener) {
        this.itemSelectionListener = itemSelectionListener;
        this.options = new ArrayList<>(
                Arrays.asList(context.getResources().getStringArray(R.array.settings_options)));
        this.icons = new ArrayList<>(
                Arrays.asList(context.getResources().getStringArray(R.array.settings_icons)));
        this.preferencesManager = new PreferencesManager(context);
    }

    void maybeRefreshList() {
        if (options.size() == NUM_SETTINGS_OPTIONS && !preferencesManager.isOnFreeVersion()) {
            options.remove(0);
            icons.remove(0);
            notifyItemRemoved(0);
        }
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
        return options.size();
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
            option.setText(options.get(position));
            icon.setText(icons.get(position));

            int shakePosition = preferencesManager.isOnFreeVersion() ? 1 : 0;
            if (position == shakePosition) {
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
