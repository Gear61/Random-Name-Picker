package com.randomappsinc.studentpicker.editing;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.models.NameDO;

public class RenameDialog {

    public interface Listener {
        void onRenameSubmitted(int nameId, String previousName, String newName, int amountToRename);
    }

    private MaterialDialog renameAmountDialog;
    private MaterialDialog renamingDialog;
    private NameDO currentName = new NameDO();
    private int currentMaxAmount;
    private int amountToRename;

    RenameDialog(Context context, final Listener listener) {
        renameAmountDialog = new MaterialDialog.Builder(context)
                .content(R.string.multiple_renames_title)
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
                .alwaysCallInputCallback()
                .negativeText(android.R.string.no)
                .positiveText(R.string.next)
                .onNeutral((dialog, which) -> {
                    dialog.getInputEditText().setText(String.valueOf(currentMaxAmount));
                    amountToRename = currentMaxAmount;
                    goIntoRenamingDialogFromAmountChoice();
                })
                .onPositive((dialog, which) -> {
                    amountToRename = Integer.parseInt(renameAmountDialog.getInputEditText().getText().toString().trim());
                    goIntoRenamingDialogFromAmountChoice();
                })
                .build();

        renamingDialog = new MaterialDialog.Builder(context)
                .title(R.string.change_name)
                .input(context.getString(R.string.new_name), currentName.getName(), (dialog, input) -> {
                    boolean submitEnabled = !(input.toString().trim().isEmpty() ||
                            input.toString().equals(currentName));
                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(submitEnabled);
                })
                .alwaysCallInputCallback()
                .negativeText(android.R.string.no)
                .onAny((dialog, which) -> {
                    if (which == DialogAction.POSITIVE) {
                        String newName = dialog.getInputEditText().getText().toString().trim();
                        listener.onRenameSubmitted(currentName.getId(), currentName.getName(), newName, amountToRename);
                    } else if (which == DialogAction.NEUTRAL) {
                        renameAmountDialog.show();
                    }
                })
                .build();
    }

    void startRenamingProcess(NameDO nameDO) {
        currentName = nameDO;
        currentMaxAmount = nameDO.getAmount();
        renamingDialog.getInputEditText().setText(nameDO.getName());
        if (currentMaxAmount > 1) {
            renameAmountDialog.setActionButton(DialogAction.NEUTRAL, R.string.all_of_them);
            renameAmountDialog.setContent(R.string.multiple_renames_title,
                    "\"" + nameDO.getName() + "\"", currentMaxAmount);
            EditText input = renameAmountDialog.getInputEditText();
            if (input != null) {
                input.setText(String.valueOf(currentMaxAmount));
                input.setFilters(new InputFilter[]
                        {new InputFilter.LengthFilter(String.valueOf(currentMaxAmount).length())});
            }
            renameAmountDialog.show();
        } else {
            amountToRename = 1;
            renamingDialog.setActionButton(DialogAction.NEUTRAL, null);
            renamingDialog.show();
        }
    }

    private void goIntoRenamingDialogFromAmountChoice() {
        renamingDialog.setActionButton(DialogAction.NEUTRAL, R.string.back);
        renamingDialog.show();
    }
}
