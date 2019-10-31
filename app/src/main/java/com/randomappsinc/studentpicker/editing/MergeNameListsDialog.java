package com.randomappsinc.studentpicker.editing;

import android.content.Context;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;

import java.util.ArrayList;
import java.util.List;

public class MergeNameListsDialog {

    public interface Listener {
        void onMergeSubmitted(List<String> listsToMergeIn);
    }

    private MaterialDialog dialog;

    public MergeNameListsDialog(Context context, final Listener listener, final String[] importCandidates) {
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.choose_list_to_import)
                .items(importCandidates)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(which.length > 0);
                        return true;
                    }
                })
                .alwaysCallMultiChoiceCallback()
                .negativeText(android.R.string.no)
                .positiveText(R.string.choose)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Integer[] indices = dialog.getSelectedIndices();
                        List<String> listNames = new ArrayList<>();
                        if (indices != null) {
                            for (Integer index : indices) {
                                listNames.add(importCandidates[index]);
                            }
                        }
                        listener.onMergeSubmitted(listNames);
                    }
                })
                .build();
    }

    public void show() {
        // Clear all previous selected lists
        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
        dialog.setSelectedIndices(new Integer[]{});
        dialog.show();
    }
}
