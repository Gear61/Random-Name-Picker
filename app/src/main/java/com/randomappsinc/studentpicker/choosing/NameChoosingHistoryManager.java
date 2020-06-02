package com.randomappsinc.studentpicker.choosing;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.utils.NameUtils;
import com.randomappsinc.studentpicker.utils.UIUtils;

import java.util.List;

class NameChoosingHistoryManager {

    interface Delegate {
        ListInfo getChoosingStateListInfo();
    }

    private Delegate delegate;
    private MaterialDialog dialog;

    NameChoosingHistoryManager(Delegate delegate, Context context) {
        this.delegate = delegate;
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.chosen_names_history)
                .positiveText(R.string.okay)
                .neutralText(R.string.clear)
                .negativeText(R.string.copy_text)
                .onNeutral((dialog, which) -> {
                    delegate.getChoosingStateListInfo().clearNameHistory();
                    UIUtils.showShortToast(R.string.name_history_cleared, dialog.getContext());
                })
                .onNegative((dialog, which) -> NameUtils.copyNamesToClipboard(
                        getFormattedNameHistory(),
                        0,
                        true,
                        dialog.getContext()))
                .build();
    }

    void maybeShowNamesHistory() {
        String namesHistory = getFormattedNameHistory();
        if (!namesHistory.isEmpty()) {
            dialog.setContent(namesHistory);
            dialog.show();
        } else {
            UIUtils.showLongToast(R.string.empty_names_history, dialog.getContext());
        }
    }

    private String getFormattedNameHistory() {
        List<String> nameHistory = delegate.getChoosingStateListInfo().getNameHistory();
        StringBuilder namesHistory = new StringBuilder();
        for (int i = 0; i < nameHistory.size(); i++) {
            if (i != 0) {
                namesHistory.append("\n");
            }
            namesHistory.append(nameHistory.get(i));
        }
        return namesHistory.toString();
    }
}
