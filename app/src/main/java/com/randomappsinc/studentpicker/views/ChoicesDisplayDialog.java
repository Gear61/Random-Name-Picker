package com.randomappsinc.studentpicker.views;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;

/** Shows the chosen names and their corresponding options in a dialog */
public class ChoicesDisplayDialog {

    public interface Listener {
        void sayNames(String chosenNames);

        void copyNamesToClipboard(String chosenNames, int numNames);
    }

    @NonNull private Listener listener;
    private String chosenNames;
    private int numNames;
    private MaterialDialog dialog;

    public ChoicesDisplayDialog(@NonNull Listener listener, Context context) {
        this.listener = listener;
        dialog = new MaterialDialog.Builder(context)
                // Placeholder because otherwise, the view doesn't exist
                .title(R.string.name_chosen)
                .positiveText(android.R.string.yes)
                .negativeText(R.string.copy_text)
                .neutralText(R.string.say_name)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ChoicesDisplayDialog.this.listener.copyNamesToClipboard(chosenNames, numNames);
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ChoicesDisplayDialog.this.listener.sayNames(chosenNames);
                    }
                })
                .autoDismiss(false)
                .build();
    }

    public void showChoices(String chosenNames, int numNames) {
        this.chosenNames = chosenNames;
        this.numNames = numNames;

        dialog.setTitle(this.numNames == 1 ? R.string.name_chosen : R.string.names_chosen);
        dialog.setContent(chosenNames);
        dialog.getActionButton(DialogAction.NEUTRAL)
                .setText(this.numNames == 1 ? R.string.say_name : R.string.say_names);
        dialog.getActionButton(DialogAction.NEUTRAL).setText(this.numNames == 1 ? R.string.say_name : R.string.say_names);
        dialog.show();
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }
}
