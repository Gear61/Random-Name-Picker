package com.randomappsinc.studentpicker.editing;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;

public class DeleteNameDialog {

    public interface Listener {
        void onDeletionSubmitted(String name, int amountToDelete);
    }

    private MaterialDialog deleteManyDialog;
    private MaterialDialog confirmSingleDeletionDialog;
    private String currentName;
    private int currentMaxAmount;
    private int amountToDelete;
    private String confirmTemplate;

    public DeleteNameDialog(Context context, final Listener listener) {
        confirmTemplate = context.getString(R.string.confirm_name_delete);
        confirmSingleDeletionDialog = new MaterialDialog.Builder(context)
                .title(R.string.confirm_name_deletion)
                .content("")
                .negativeText(android.R.string.no)
                .positiveText(android.R.string.yes)
                .onPositive((dialog, which) -> listener.onDeletionSubmitted(currentName, amountToDelete))
                .build();
        deleteManyDialog = new MaterialDialog.Builder(context)
                .content(R.string.multiple_deletions_title)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(context.getString(R.string.num_copies), "", (dialog, input) -> {
                    if (input.length() > 0) {
                        int amount = Integer.parseInt(input.toString());
                        boolean validNumber = amount > 0 && amount <= currentMaxAmount;
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(validNumber);
                        return;
                    }
                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                })
                .onNeutral((dialog, which) -> {
                    listener.onDeletionSubmitted(currentName, currentMaxAmount);
                })
                .alwaysCallInputCallback()
                .negativeText(android.R.string.no)
                .positiveText(R.string.delete)
                .onPositive((dialog, which) -> {
                    int numCopies = Integer.parseInt(dialog.getInputEditText().getText().toString().trim());
                    listener.onDeletionSubmitted(currentName, numCopies);
                })
                .build();
    }

    public void startDeletionProcess(String name, int maxAmount) {
        currentName = name;
        currentMaxAmount = maxAmount;
        if (maxAmount > 1) {
            deleteManyDialog.setActionButton(DialogAction.NEUTRAL, R.string.all_of_them);
            deleteManyDialog.setContent(R.string.multiple_deletions_title, "\"" + name + "\"", maxAmount);
            EditText input = deleteManyDialog.getInputEditText();
            if (input != null) {
                input.setText(String.valueOf(maxAmount));
                input.setFilters(new InputFilter[]
                        {new InputFilter.LengthFilter(String.valueOf(maxAmount).length())});
            }
            deleteManyDialog.show();
        } else {
            amountToDelete = 1;
            confirmSingleDeletionDialog.setContent(String.format(confirmTemplate, currentName));
            confirmSingleDeletionDialog.show();
        }
    }
}
