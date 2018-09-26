package com.randomappsinc.studentpicker.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;

/** Shows the options for a name on the editing page */
public class NameChoicesDialog {

    public interface Listener {
        void onRenameChosen(String name);

        void onDeleteChosen(String name);

        void onCloneChosen(String name);
    }

    private String currentName;
    private String[] optionTemplates;
    private MaterialDialog dialog;

    public NameChoicesDialog(@NonNull final Listener listener, Context context) {
        optionTemplates = new String[]{
                context.getString(R.string.rename_person),
                context.getString(R.string.delete_name),
                context.getString(R.string.duplicate)
        };
        dialog = new MaterialDialog.Builder(context)
                // Need to set this here, because otherwise, we can't set it dynamically later on
                .items(new String[]{})
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                listener.onRenameChosen(currentName);
                                break;
                            case 1:
                                listener.onDeleteChosen(currentName);
                                break;
                            case 2:
                                listener.onCloneChosen(currentName);
                                break;
                        }
                    }
                })
                .build();
    }

    public void showChoices(String name) {
        currentName = name;
        String[] options = new String[optionTemplates.length];
        for (int i = 0; i < optionTemplates.length; i++) {
            options[i] = String.format(optionTemplates[i], currentName);
        }
        dialog.setItems(options);
        dialog.show();
    }
}
