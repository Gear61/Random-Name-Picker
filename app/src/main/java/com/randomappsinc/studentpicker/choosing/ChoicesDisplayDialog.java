package com.randomappsinc.studentpicker.choosing;

import android.content.Context;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.utils.NameUtils;

import java.util.List;

/** Shows the chosen names and their corresponding options in a dialog */
public class ChoicesDisplayDialog {

    public interface Listener {
        void sayNames(String chosenNames);

        void copyNamesToClipboard(String chosenNames, int numNames);
    }

    private List<String> chosenNames;
    private ChoosingSettings settings;
    private int listId;
    private MaterialDialog dialog;
    private ChoicesDisplayViewHolder choicesDisplayViewHolder;

    ChoicesDisplayDialog(Listener listener, Context context, int listId, ListInfo listInfo, ChoosingSettings settings) {
        this.listId = listId;
        this.settings = settings;
        dialog = new MaterialDialog.Builder(context)
                // Placeholder because otherwise, the view doesn't exist
                .title(R.string.name_chosen)
                .customView(R.layout.name_choices_dialog, false)
                .positiveText(android.R.string.yes)
                .negativeText(R.string.copy_text)
                .neutralText(R.string.say_name)
                .onPositive((dialog, which) -> dialog.dismiss())
                .onNegative((dialog, which) -> listener.copyNamesToClipboard(
                        NameUtils.flattenListToString(chosenNames, settings),
                        chosenNames.size()))
                .onNeutral((dialog, which) -> listener.sayNames(
                        NameUtils.flattenListToString(chosenNames, settings)))
                .autoDismiss(false)
                .build();

        choicesDisplayViewHolder = new ChoicesDisplayViewHolder(dialog.getView(), listInfo);
    }

    void showChoices(List<String> chosenNames) {
        this.chosenNames = chosenNames;
        int numNames = chosenNames.size();

        dialog.setTitle(NameUtils.getChoosingMessage(dialog.getContext(), listId, numNames));
        choicesDisplayViewHolder.showChosenNames(chosenNames, settings.getShowAsList());
        dialog.getActionButton(DialogAction.NEUTRAL)
                .setText(numNames == 1 ? R.string.say_name : R.string.say_names);
        dialog.getActionButton(DialogAction.NEUTRAL).setText(numNames == 1 ? R.string.say_name : R.string.say_names);
        dialog.show();
    }

    boolean isShowing() {
        return dialog.isShowing();
    }
}
