package com.randomappsinc.studentpicker.home;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;

public class DeleteListDialog extends DialogFragment {

    public interface Listener {
        void onDeleteListConfirmed(int position);
    }

    private FragmentManager fragmentManager;
    private Listener listener;
    private int position;

    public DeleteListDialog(FragmentManager fragmentManager, Listener listener) {
        this.fragmentManager = fragmentManager;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new MaterialDialog.Builder(getActivity())
                .title(R.string.confirm_deletion_title)
                .content(R.string.confirm_deletion_message)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.cancel)
                .onPositive((dialog, which) ->
                    listener.onDeleteListConfirmed(position)
                ).build();
    }

    public void show(int position) {
        this.position = position;
        this.show(fragmentManager, null);
    }
}
