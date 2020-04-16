package com.randomappsinc.studentpicker.home;

import android.content.Context;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;

public class RenameListDialog {

    public interface Listener {
        void onRenameListConfirmed(String newName);
    }

    private MaterialDialog dialog;

    public RenameListDialog(Listener listener, Context context) {
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.rename_list)
                .input(context.getString(R.string.new_list_name), "", (dialog, input) -> {
                    boolean submitEnabled = !input.toString().trim().isEmpty();
                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(submitEnabled);
                })
                .alwaysCallInputCallback()
                .negativeText(android.R.string.cancel)
                .onAny((dialog, which) -> {
                    if (which == DialogAction.POSITIVE) {
                        String newListName = dialog.getInputEditText().getText().toString().trim();
                        listener.onRenameListConfirmed(newListName);
                    }
                    dialog.getInputEditText().setText("");
                })
                .build();
    }

    public void show(String currentName) {
        dialog.getInputEditText().setText(currentName);
        dialog.show();
    }
}
