package com.randomappsinc.studentpicker.views;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;

/** Shows the chosen names and their corresponding options in a dialog */
public class ChoicesDisplayDialog {

    public interface Listener {
        void sayNames(String chosenNames);

        void copyNamesToClipboard(String chosenNames, int numNames);
    }

    @NonNull private Listener mListener;
    private String mChosenNames;
    private int mNumNames;
    private MaterialDialog mDialog;

    public ChoicesDisplayDialog(@NonNull Listener listener, Context context) {
        mListener = listener;
        mDialog = new MaterialDialog.Builder(context)
                // Placeholder because otherwise, the view doesn't exist
                .title(R.string.name_chosen)
                .positiveText(android.R.string.yes)
                .negativeText(R.string.copy_text)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mListener.copyNamesToClipboard(mChosenNames, mNumNames);
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mListener.sayNames(mChosenNames);
                    }
                })
                .autoDismiss(false)
                .build();
    }

    public void showChoices(String chosenNames, int numNames) {
        mChosenNames = chosenNames;
        mNumNames = numNames;

        mDialog.setTitle(mNumNames == 1 ? R.string.name_chosen : R.string.names_chosen);
        mDialog.setContent(chosenNames);
        mDialog.getActionButton(DialogAction.NEUTRAL)
                .setText(mNumNames == 1 ? R.string.say_name : R.string.say_names);
        mDialog.show();
    }

    public boolean isShowing() {
        return mDialog.isShowing();
    }
}
