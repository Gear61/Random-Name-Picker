package com.randomappsinc.studentpicker.choosing;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.utils.NameUtils;
import com.randomappsinc.studentpicker.utils.UIUtils;

public class NameChoosingHistoryManager {

    private ListInfo listInfo;
    private MaterialDialog dialog;

    NameChoosingHistoryManager(ListInfo listInfo, Context context) {
        this.listInfo = listInfo;

        dialog = new MaterialDialog.Builder(context)
                .title(R.string.chosen_names_history)
                .positiveText(R.string.okay)
                .neutralText(R.string.clear)
                .negativeText(R.string.copy_text)
                .onNeutral((dialog, which) -> {
                    listInfo.clearNameHistory();
                    UIUtils.showShortToast(R.string.name_history_cleared, dialog.getContext());
                })
                .onNegative((dialog, which) -> NameUtils.copyNamesToClipboard(
                        listInfo.getNameHistoryFormatted(),
                        null,
                        0,
                        true,
                        dialog.getContext()))
                .build();
    }

    void maybeShowNamesHistory() {
        String namesHistory = listInfo.getNameHistoryFormatted();
        if (!namesHistory.isEmpty()) {
            dialog.setContent(namesHistory);
            dialog.show();
        } else {
            UIUtils.showLongToast(R.string.empty_names_history, dialog.getContext());
        }
    }
}