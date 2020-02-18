package com.randomappsinc.studentpicker.grouping;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.utils.UIUtils;

class GroupMakingSettingsDialog {

    private MaterialDialog dialog;
    private GroupMakingSettingsViewHolder settingsHolder;

    GroupMakingSettingsDialog(Context context, GroupMakingSettings settings) {
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.group_settings_dialog_title)
                .customView(R.layout.group_making_settings, true)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive((dialog, which) -> {
                    settingsHolder.applySettings();
                    UIUtils.showShortToast(R.string.settings_applied, context);
                })
                .onNegative((dialog, which) -> settingsHolder.revertSettings())
                .cancelable(false)
                .build();

        settingsHolder = new GroupMakingSettingsViewHolder(dialog, settings);
    }

    void refreshSetting() {
        settingsHolder.refreshListSizeSetting();
    }

    void show() {
        dialog.show();
    }
}
