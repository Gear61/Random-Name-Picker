package com.randomappsinc.studentpicker.editing;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;

public class NameAmountAdjustmentDialog {

    public interface Listener {
        void onNameAmountAdjustmentSubmitted(String name, int newAmount, int currentAmount);
    }

    private MaterialDialog dialog;
    private String currentName;
    private int currentAmount;

    public NameAmountAdjustmentDialog(Context context, final Listener listener) {
        dialog = new MaterialDialog.Builder(context)
                .content(R.string.name_adjustment_message)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(context.getString(R.string.num_copies), "", (dialog, input) -> {
                    boolean isValid = input.length() > 0 && Integer.parseInt(input.toString()) != currentAmount;
                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(isValid);
                })
                .alwaysCallInputCallback()
                .negativeText(android.R.string.no)
                .positiveText(R.string.submit)
                .onPositive((dialog, which) -> {
                    int numCopies = Integer.parseInt(dialog.getInputEditText().getText().toString().trim());
                    listener.onNameAmountAdjustmentSubmitted(currentName, numCopies, currentAmount);
                })
                .build();
        // Cap the amount you can make at 999
        dialog.getInputEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
    }

    public void show(String name, int amount) {
        currentName = name;
        currentAmount = amount;
        dialog.setContent(R.string.name_adjustment_message, "\"" + name + "\"", currentAmount);
        dialog.getInputEditText().setText(String.valueOf(currentAmount));
        dialog.show();
    }
}
