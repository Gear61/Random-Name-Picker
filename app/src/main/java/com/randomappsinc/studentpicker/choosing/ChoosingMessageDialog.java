package com.randomappsinc.studentpicker.choosing;

import android.content.Context;
import android.text.InputType;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.utils.NameUtils;

public class ChoosingMessageDialog {

    public interface Listener {
        void onNewChoosingMessageConfirmed(String newMessage);
    }

    private MaterialDialog adderDialog;

    public ChoosingMessageDialog(Context context, Listener listener, int listId) {
        String prefill = NameUtils.getChoosingMessage(context, listId, 1);
        adderDialog = new MaterialDialog.Builder(context)
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

    public void show() {
        adderDialog.show();
    }
}
