package com.randomappsinc.studentpicker.home;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;

public class DeleteListDialog {

    public interface Listener {
        void onDeleteListConfirmed(int position);
    }

    private MaterialDialog.Builder materialDialogBuilder;
    private Listener listener;
    private int position;

    DeleteListDialog(MaterialDialog.Builder materialDialogBuilder, Listener listener) {
        this.materialDialogBuilder = materialDialogBuilder;
        this.listener = listener;
        onCreateDialog();
    }

   private void onCreateDialog() {
        materialDialogBuilder
                .title(R.string.confirm_deletion_title)
                .content(R.string.confirm_deletion_message)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.cancel)
                .onPositive((dialog, which) ->
                        listener.onDeleteListConfirmed(position)
                ).build();
    }

    public void show(int position) {
        this.position = position;
        materialDialogBuilder.show();
    }
}
