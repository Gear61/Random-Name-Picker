package com.randomappsinc.studentpicker.editing;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.text.InputType;
import android.view.inputmethod.InputMethodManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.models.NameDO;

public class DuplicationDialog {

    private static final long MILLIS_DELAY_FOR_KEYBOARD = 250L;

    public interface Listener {
        void onDuplicationSubmitted(int nameId, String name, int amountToAdd);
    }

    private final MaterialDialog dialog;
    private NameDO currentName;
    private final Handler handler = new Handler(Looper.getMainLooper());

    DuplicationDialog(Context context, final Listener listener) {
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
                    listener.onDuplicationSubmitted(currentName.getId(), currentName.getName(), numCopies);
                })
                .build();
        // Cap the amount you can make at 999
        dialog.getInputEditText().setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});
    }

    private void maybeRunShowKeyboardHack() {
        handler.postDelayed(() -> {
            if (dialog.getInputEditText().requestFocus()) {
                InputMethodManager imm = (InputMethodManager) dialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(dialog.getInputEditText(), InputMethodManager.SHOW_IMPLICIT);
            }
        }, MILLIS_DELAY_FOR_KEYBOARD);
    }

    public void show(NameDO name) {
        currentName = name;
        dialog.getInputEditText().setText("");
        dialog.show();
        maybeRunShowKeyboardHack();
    }
}
