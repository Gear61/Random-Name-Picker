package com.randomappsinc.studentpicker.home;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.models.ListDO;

public class DeleteListDialog {

    public interface Listener {
        void onDeleteListConfirmed(int position, ListDO listDO);
    }

    private MaterialDialog dialog;
    private ListDO listDO;
    private int position;

    public DeleteListDialog(Listener listener, Context context) {
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.confirm_deletion_title)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.cancel)
                .onPositive((dialog, which) ->
                        listener.onDeleteListConfirmed(position, listDO)
                ).build();
    }

    void presentForList(int position, ListDO listDO) {
        this.position = position;
        this.listDO = listDO;
        String dialogContent = dialog.getContext().getString(
                R.string.confirm_deletion_message,
                " \"" + listDO.getName() + "\"");
        dialog.setContent(dialogContent);
        dialog.show();
    }
}
