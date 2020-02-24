package com.randomappsinc.studentpicker.home;

import android.content.Context;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.models.ListDO;
import com.randomappsinc.studentpicker.utils.PreferencesManager;

public class RenameListDialog {

    public interface Listener {
        void onRenameListConfirmed(int position, ListDO updatedList);
    }

    private MaterialDialog dialog;
    private ListDO list;
    private int position;

    RenameListDialog(@NonNull Listener listener, Context context, PreferencesManager preferencesManager) {
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.rename_list)
                .input(context.getString(R.string.new_list_name), "", (dialog, input) -> {
                    boolean submitEnabled = !(input.toString().trim().isEmpty() ||
                            preferencesManager.doesListExist(input.toString().trim()));
                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(submitEnabled);
                })
                .alwaysCallInputCallback()
                .negativeText(android.R.string.cancel)
                .onAny((dialog, which) -> {
                    if (which == DialogAction.POSITIVE) {
                        String newListName = dialog.getInputEditText().getText().toString().trim();
                        list.setName(newListName);
                        listener.onRenameListConfirmed(position, list);
                    }
                    dialog.getInputEditText().setText("");
                })
                .build();
    }

    public void show(int position, ListDO listDO) {
        this.list = listDO;
        this.position = position;
        dialog.getInputEditText().setText(listDO.getName());
        dialog.show();
    }
}
