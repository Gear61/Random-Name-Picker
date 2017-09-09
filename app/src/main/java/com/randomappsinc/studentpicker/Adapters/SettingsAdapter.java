package com.randomappsinc.studentpicker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.Utils.PreferencesManager;
import com.rey.material.widget.Switch;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsAdapter extends BaseAdapter {

    private String[] options;
    private String[] icons;
    private Context context;

    public SettingsAdapter(Context context) {
        this.context = context;
        this.options = context.getResources().getStringArray(R.array.settings_options);
        this.icons = context.getResources().getStringArray(R.array.settings_icons);
    }

    @Override
    public int getCount() {
        return options.length;
    }

    @Override
    public String getItem(int position) {
        return options[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class SettingsViewHolder {

        @Bind(R.id.settings_icon) IconTextView icon;
        @Bind(R.id.settings_option) TextView option;
        @Bind(R.id.shake_toggle) Switch shakeToggle;

        public SettingsViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void loadSetting(int position) {
            option.setText(options[position]);
            icon.setText(icons[position]);

            if (position == 0) {
                shakeToggle.setCheckedImmediately(PreferencesManager.get().isShakeEnabled());
                shakeToggle.setVisibility(View.VISIBLE);
            } else {
                shakeToggle.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.shake_toggle)
        public void onSoundToggle() {
            PreferencesManager.get().setShakeEnabled(shakeToggle.isChecked());
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        SettingsViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.settings_list_item, parent, false);
            holder = new SettingsViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (SettingsViewHolder) view.getTag();
        }
        holder.loadSetting(position);
        return view;
    }
}
