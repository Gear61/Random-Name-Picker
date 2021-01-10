package com.randomappsinc.studentpicker.home;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.inputmethod.InputMethodManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.ads.PremiumBackDoor;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;

public class CreateListDialog {

    private static final long MILLIS_DELAY_FOR_KEYBOARD = 250L;

    public interface Listener {
        void onCreateNewListConfirmed(String newListName);
    }

    private final PreferencesManager preferencesManager;
    private final MaterialDialog adderDialog;
    private final Handler handler = new Handler(Looper.getMainLooper());

    CreateListDialog(Context context, Listener listener) {
        preferencesManager = new PreferencesManager(context);
        adderDialog = new MaterialDialog.Builder(context)
                .title(R.string.create_new_list_title)
                .alwaysCallInputCallback()
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .input(context.getString(R.string.create_list_hint),
                        "",
                        (dialog, input) -> {
                            String setName = input.toString();
                            boolean notEmpty = !setName.trim().isEmpty();
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(notEmpty);
                        })
                .positiveText(R.string.create)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> {
                    String listName = dialog.getInputEditText().getText().toString().trim();

                    if (preferencesManager.isOnFreeVersion()
                            && listName.toLowerCase().equals(PremiumBackDoor.PASSWORD.toLowerCase())) {
                        preferencesManager.onPremiumAcquired();
                        UIUtils.showLongToast(
                                R.string.premium_back_door_triggered, dialog.getContext());
                        return;
                    }

                    listener.onCreateNewListConfirmed(listName);
                })
                .build();
    }

    private void maybeRunShowKeyboardHack() {
        handler.postDelayed(() -> {
            if (adderDialog.getInputEditText().requestFocus()) {
                InputMethodManager imm = (InputMethodManager) adderDialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(adderDialog.getInputEditText(), InputMethodManager.SHOW_IMPLICIT);
            }
        }, MILLIS_DELAY_FOR_KEYBOARD);
    }

    public void show() {
        adderDialog.getInputEditText().setText("");
        adderDialog.show();
        maybeRunShowKeyboardHack();
    }
}
