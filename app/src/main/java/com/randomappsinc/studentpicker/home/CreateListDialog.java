package com.randomappsinc.studentpicker.home;

import android.content.Context;
import android.text.InputType;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;

public class CreateListDialog {

    public interface Listener {
        void onCreateNewListConfirmed(String newListName);
    }

    private MaterialDialog adderDialog;
    protected Listener listener;

    CreateListDialog(Context context, Listener listener) {
        this.listener = listener;

        adderDialog = new MaterialDialog.Builder(context)
                .title(R.string.create_new_list_title)
                .alwaysCallInputCallback()
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .input(context.getString(R.string.create_list_hint),
                        "",
                        (dialog, input) -> {
                            String setName = input.toString();
                            boolean notEmpty = !setName.trim().isEmpty();
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(notEmpty);
                        })
                .positiveText(R.string.create)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> {
                    String setName = dialog.getInputEditText().getText().toString().trim();
                    listener.onCreateNewListConfirmed(setName);
                })
                .build();
    }

    public void show() {
        adderDialog.getInputEditText().setText("");
        adderDialog.show();
    }

    public void cleanUp() {
        listener = null;
    }
}
