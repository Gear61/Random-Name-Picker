package com.randomappsinc.studentpicker.home;

import android.content.Context;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;

public class DeleteListDialog {

    public interface Listener {
        void onDeleteListConfirmed(int position);
    }

    public MaterialDialog dialog;
    private int position;

    DeleteListDialog(@NonNull Listener listener, Context context) {
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.confirm_deletion_title)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.cancel)
                .onPositive((dialog, which) ->
                        listener.onDeleteListConfirmed(position)
                ).build();
    }

    // Instead of sending the content inside the show function, Defining the dialog object as public is better to get an access for it from the MainActivity
    // I think changing the content inside a function called "show" isn't a good idea.

    public void show(int position) {
        this.position = position;
        dialog.show();
    }
}
