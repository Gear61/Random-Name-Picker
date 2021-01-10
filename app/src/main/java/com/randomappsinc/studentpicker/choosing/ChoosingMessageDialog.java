package com.randomappsinc.studentpicker.choosing;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.inputmethod.InputMethodManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.utils.NameUtils;

public class ChoosingMessageDialog {

    private static final long MILLIS_DELAY_FOR_KEYBOARD = 250L;

    public interface Listener {
        void onNewChoosingMessageConfirmed(String newMessage);
    }

    private final MaterialDialog dialog;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public ChoosingMessageDialog(Context context, Listener listener, int listId) {
        String prefill = NameUtils.getChoosingMessage(context, listId, 1);
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.customize_choosing_message_dialog_title)
                .alwaysCallInputCallback()
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
                .input(context.getString(R.string.create_list_hint),
                        prefill,
                        (dialog, input) -> {
                            String chooseMessage = input.toString();
                            boolean notEmpty = !chooseMessage.trim().isEmpty();
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(notEmpty);
                        })
                .positiveText(R.string.save)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> {
                    String newMessage = dialog.getInputEditText().getText().toString().trim();
                    listener.onNewChoosingMessageConfirmed(newMessage);
                })
                .build();
    }

    private void maybeRunShowKeyboardHack() {
        handler.postDelayed(() -> {
            if (dialog.getInputEditText().requestFocus()) {
                InputMethodManager imm = (InputMethodManager) dialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(dialog.getInputEditText(), InputMethodManager.SHOW_IMPLICIT);
            }
        }, MILLIS_DELAY_FOR_KEYBOARD);
    }

    public void show() {
        dialog.show();
        maybeRunShowKeyboardHack();
    }
}
