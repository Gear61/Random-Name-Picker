package com.randomappsinc.studentpicker.editing;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;

public class DuplicationDialog {

    public interface Listener {
        void onDuplicationSubmitted(String name, int amountToAdd);
    }

    private MaterialDialog dialog;
    private String currentName;

    public DuplicationDialog(Context context, final Listener listener) {
        dialog = new MaterialDialog.Builder(context)
                .content(R.string.cloning_title)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(context.getString(R.string.num_copies), "", (dialog, input) -> {
                    boolean isValid = input.length() > 0 && (Integer.parseInt(input.toString()) > 0);
                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(isValid);
                })
                .alwaysCallInputCallback()
                .negativeText(android.R.string.no)
                .positiveText(R.string.add)
                .onPositive((dialog, which) -> {
                    int numCopies = Integer.parseInt(dialog.getInputEditText().getText().toString().trim());
                    listener.onDuplicationSubmitted(currentName, numCopies);
                })
                .build();
        // Cap the amount you can make at 999
        dialog.getInputEditText().setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});
    }

    public void show(String name) {
        currentName = name;
        dialog.getInputEditText().setText("");
        dialog.show();
    }
}
