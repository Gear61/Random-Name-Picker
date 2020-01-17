package com.randomappsinc.studentpicker.grouping;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.utils.UIUtils;

class GroupingSettingsDialog {

    private MaterialDialog dialog;
    private GroupingSettingsViewHolder settingsHolder;

    GroupingSettingsDialog(Context context, GroupingSettings settings) {
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

        settingsHolder = new GroupingSettingsViewHolder(dialog.getCustomView(), context, settings);
    }

    void show() {
        dialog.show();
    }
}
