package com.randomappsinc.studentpicker.home;

import android.content.Context;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.utils.PreferencesManager;

public class RenameListDialog {

    public interface Listener {
        void onRenameListConfirmed(int position, String newListName);
    }

    private MaterialDialog dialog;
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
                        listener.onRenameListConfirmed(position, newListName);
                    }
                    dialog.getInputEditText().setText("");
                })
                .build();
    }

    public void show(int position, String currentName) {
        this.position = position;
        dialog.getInputEditText().setText(currentName);
        dialog.show();
    }
}
