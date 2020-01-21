package com.randomappsinc.studentpicker.choosing;

import android.content.Context;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.utils.NameUtils;
import com.randomappsinc.studentpicker.utils.UIUtils;

public class NameChoosingHistoryManager {

    public interface Listener {
        void onEmptyHistoryNames();
    }

    private ListInfo listInfo;
    private String namesHistory;
    private MaterialDialog dialog;
    private Listener listener;

    NameChoosingHistoryManager(ListInfo listInfo, Context context, @NonNull Listener listener) {
        this.listInfo = listInfo;
        this.listener = listener;

        dialog = new MaterialDialog.Builder(context)
                .title(R.string.chosen_names_history)
                .positiveText(android.R.string.yes)
                .neutralText(R.string.clear)
                .negativeText(R.string.copy_text)
                .onNeutral((dialog, which) -> {
                    listInfo.clearNameHistory();
                    UIUtils.showShortToast(R.string.name_history_cleared, context);
                })
                .onNegative((dialog, which) -> NameUtils.copyNamesToClipboard(
                        namesHistory,
                        null,
                        0,
                        true,
                        context))
                .build();
    }

    void showNamesHistory() {
        namesHistory = listInfo.getNameHistoryFormatted();
        if (!namesHistory.isEmpty()) {
            dialog.setContent(namesHistory);
            dialog.show();
        } else {
            listener.onEmptyHistoryNames();
        }
    }
}
