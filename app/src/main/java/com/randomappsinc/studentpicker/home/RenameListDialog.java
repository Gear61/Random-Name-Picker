package com.randomappsinc.studentpicker.home;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.utils.PreferencesManager;

public class RenameListDialog extends DialogFragment {

    public interface Listener {
        void onRenameListConfirmed(int position, String newListName);
    }

    private FragmentManager fragmentManager;
    private Listener listener;
    private PreferencesManager preferencesManager;
    private int position;


    RenameListDialog(FragmentManager fragmentManager, Listener listener, PreferencesManager preferencesManager) {
        this.fragmentManager = fragmentManager;
        this.listener = listener;
        this.preferencesManager = preferencesManager;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new MaterialDialog.Builder(getActivity())
                .title(R.string.rename_list)
                .input(getString(R.string.new_list_name), "", (dialog, input) -> {
                    boolean submitEnabled = !(input.toString().trim().isEmpty() ||
                            preferencesManager.doesListExist(input.toString().trim()));
                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(submitEnabled);
                }).alwaysCallInputCallback()
                .negativeText(android.R.string.cancel)
                .onAny((dialog, which) -> {
                    if (which == DialogAction.POSITIVE) {
                        String newListName = dialog.getInputEditText().getText().toString().trim();
                        listener.onRenameListConfirmed(position, newListName);
                    }
                }).build();
    }

    public void show(int position) {
        this.position = position;
        this.show(fragmentManager, null);
    }
}
