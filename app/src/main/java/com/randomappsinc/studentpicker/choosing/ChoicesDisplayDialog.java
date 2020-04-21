package com.randomappsinc.studentpicker.choosing;

import android.content.Context;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.utils.NameUtils;

/** Shows the chosen names and their corresponding options in a dialog */
public class ChoicesDisplayDialog {

    public interface Listener {
        void sayNames(String chosenNames);

        void copyNamesToClipboard(String chosenNames, int numNames);
    }

    private String chosenNames;
    private int numNames;
    private int listId;
    private MaterialDialog dialog;

    ChoicesDisplayDialog(Listener listener, Context context, int listId) {
        this.listId = listId;
        dialog = new MaterialDialog.Builder(context)
                // Placeholder because otherwise, the view doesn't exist
                .title(R.string.name_chosen)
                .positiveText(android.R.string.yes)
                .negativeText(R.string.copy_text)
                .neutralText(R.string.say_name)
                .onPositive((dialog, which) -> dialog.dismiss())
                .onNegative((dialog, which) -> listener.copyNamesToClipboard(chosenNames, numNames))
                .onNeutral((dialog, which) -> listener.sayNames(chosenNames))
                .autoDismiss(false)
                .build();
    }

    void showChoices(String chosenNames, int numNames) {
        this.chosenNames = chosenNames;
        this.numNames = numNames;

        dialog.setTitle(NameUtils.getChoosingMessage(dialog.getContext(), listId, numNames));
        dialog.setContent(chosenNames);
        dialog.getActionButton(DialogAction.NEUTRAL)
                .setText(numNames == 1 ? R.string.say_name : R.string.say_names);
        dialog.getActionButton(DialogAction.NEUTRAL).setText(numNames == 1 ? R.string.say_name : R.string.say_names);
        dialog.show();
    }

    boolean isShowing() {
        return dialog.isShowing();
    }
}
