package com.randomappsinc.studentpicker.editing;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.text.InputType;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.models.NameDO;

public class DeleteNameDialog {

    private static final long MILLIS_DELAY_FOR_KEYBOARD = 250L;

    public interface Listener {
        void onDeletionSubmitted(String name, int amountToDelete);
    }

    private final MaterialDialog deleteManyDialog;
    private final MaterialDialog confirmSingleDeletionDialog;
    private NameDO currentName;
    private int currentMaxAmount;
    private int amountToDelete;
    private final String confirmTemplate;
    private final Handler handler = new Handler(Looper.getMainLooper());

    DeleteNameDialog(Context context, final Listener listener) {
        confirmTemplate = context.getString(R.string.confirm_name_delete);
        confirmSingleDeletionDialog = new MaterialDialog.Builder(context)
                .title(R.string.confirm_name_deletion)
                .content("")
                .negativeText(android.R.string.no)
                .positiveText(android.R.string.yes)
                .onPositive((dialog, which) -> listener.onDeletionSubmitted(currentName.getName(), amountToDelete))
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
                    listener.onDeletionSubmitted(currentName.getName(), currentMaxAmount);
                })
                .alwaysCallInputCallback()
                .negativeText(android.R.string.no)
                .positiveText(R.string.delete)
                .onPositive((dialog, which) -> {
                    int numCopies = Integer.parseInt(dialog.getInputEditText().getText().toString().trim());
                    listener.onDeletionSubmitted(currentName.getName(), numCopies);
                })
                .build();
    }

    private void maybeRunShowKeyboardHack() {
        handler.postDelayed(() -> {
            if (deleteManyDialog.getInputEditText().requestFocus()) {
                InputMethodManager imm = (InputMethodManager) deleteManyDialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(deleteManyDialog.getInputEditText(), InputMethodManager.SHOW_IMPLICIT);
            }
        }, MILLIS_DELAY_FOR_KEYBOARD);
    }

    void startDeletionProcess(NameDO nameDO) {
        currentName = nameDO;
        currentMaxAmount = currentName.getAmount();
        if (currentMaxAmount > 1) {
            deleteManyDialog.setActionButton(DialogAction.NEUTRAL, R.string.all_of_them);
            deleteManyDialog.setContent(R.string.multiple_deletions_title,
                    "\"" + currentName.getName() + "\"", currentMaxAmount);
            EditText input = deleteManyDialog.getInputEditText();
            if (input != null) {
                input.setText(String.valueOf(currentMaxAmount));
                input.setFilters(new InputFilter[]
                        {new InputFilter.LengthFilter(String.valueOf(currentMaxAmount).length())});
            }
            deleteManyDialog.show();
            maybeRunShowKeyboardHack();
        } else {
            amountToDelete = 1;
            confirmSingleDeletionDialog.setContent(String.format(confirmTemplate, currentName.getName()));
            confirmSingleDeletionDialog.show();
        }
    }
}
