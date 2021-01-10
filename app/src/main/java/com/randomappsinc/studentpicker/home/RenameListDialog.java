package com.randomappsinc.studentpicker.home;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.inputmethod.InputMethodManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;

public class RenameListDialog {

    private static final long MILLIS_DELAY_FOR_KEYBOARD = 250L;

    public interface Listener {
        void onRenameListConfirmed(String newName);
    }

    private final MaterialDialog dialog;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public RenameListDialog(Listener listener, Context context) {
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.rename_list_dialog_title)
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

    private void maybeRunShowKeyboardHack() {
        handler.postDelayed(() -> {
            if (dialog.getInputEditText().requestFocus()) {
                InputMethodManager imm = (InputMethodManager) dialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(dialog.getInputEditText(), InputMethodManager.SHOW_IMPLICIT);
            }
        }, MILLIS_DELAY_FOR_KEYBOARD);
    }

    public void show(String currentName) {
        dialog.getInputEditText().setText(currentName);
        dialog.show();
        maybeRunShowKeyboardHack();
    }
}
