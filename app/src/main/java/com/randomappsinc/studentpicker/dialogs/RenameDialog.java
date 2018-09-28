package com.randomappsinc.studentpicker.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;

public class RenameDialog {

    public interface Listener {
        void onRenameSubmitted(String previousName, String newName, int amountToRename);
    }

    private MaterialDialog renameAmountDialog;
    private MaterialDialog renamingDialog;
    private String currentName;
    private int currentMaxAmount;
    private int amountToRename;

    public RenameDialog(Context context, final Listener listener) {
        renameAmountDialog = new MaterialDialog.Builder(context)
                .content(R.string.multiple_renames_title)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(context.getString(R.string.num_copies), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (input.length() > 0) {
                            int amount = Integer.parseInt(input.toString());
                            boolean validNumber = amount > 0 && amount <= currentMaxAmount;
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(validNumber);
                            return;
                        }
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                    }
                })
                .alwaysCallInputCallback()
                .negativeText(android.R.string.no)
                .positiveText(R.string.next)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        amountToRename = Integer.parseInt(
                                dialog.getInputEditText().getText().toString().trim());
                        goIntoRenamingDialogFromAmountChoice();
                    }
                })
                .build();

        renamingDialog = new MaterialDialog.Builder(context)
                .title(R.string.change_name)
                .input(context.getString(R.string.new_name), currentName, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        boolean submitEnabled = !(input.toString().trim().isEmpty() ||
                                input.toString().equals(currentName));
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(submitEnabled);
                    }
                })
                .alwaysCallInputCallback()
                .negativeText(android.R.string.no)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            String newName = dialog.getInputEditText().getText().toString().trim();
                            listener.onRenameSubmitted(currentName, newName, amountToRename);
                        } else if (which == DialogAction.NEUTRAL) {
                            renameAmountDialog.show();
                        }
                    }
                })
                .build();
    }

    public void startRenamingProcess(String name, int maxAmount) {
        currentName = name;
        currentMaxAmount = maxAmount;
        renamingDialog.getInputEditText().setText("");
        if (maxAmount > 1) {
            EditText input = renameAmountDialog.getInputEditText();
            if (input != null) {
                input.setText("");
                input.setFilters(new InputFilter[]
                        {new InputFilter.LengthFilter(String.valueOf(maxAmount).length())});
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
