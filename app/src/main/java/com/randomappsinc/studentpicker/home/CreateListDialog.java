package com.randomappsinc.studentpicker.home;

import android.content.Context;
import android.text.InputType;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.ads.PremiumBackDoor;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;

public class CreateListDialog {

    public interface Listener {
        void onCreateNewListConfirmed(String newListName);
    }

    private PreferencesManager preferencesManager;
    private MaterialDialog adderDialog;

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

    public void show() {
        adderDialog.getInputEditText().setText("");
        adderDialog.show();
    }
}
