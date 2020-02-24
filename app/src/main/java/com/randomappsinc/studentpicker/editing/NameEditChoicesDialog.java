package com.randomappsinc.studentpicker.editing;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.models.NameDO;

/** Shows the options for a name on the editing page */
public class NameEditChoicesDialog {

    public interface Listener {
        void onRenameChosen(NameDO nameDO);

        void onDeleteChosen(NameDO nameDO);

        void onDuplicationChosen(NameDO nameDO);
    }

    private NameDO currentName;
    private String[] optionTemplates;
    private MaterialDialog dialog;

    NameEditChoicesDialog(Context context, final Listener listener) {
        optionTemplates = new String[]{
                context.getString(R.string.rename_person),
                context.getString(R.string.delete_name),
                context.getString(R.string.duplicate)
        };
        dialog = new MaterialDialog.Builder(context)
                // Need to set this here, because otherwise, we can't set it dynamically later on
                .items(new String[]{})
                .itemsCallback((dialog, view, which, text) -> {
                    switch (which) {
                        case 0:
                            listener.onRenameChosen(currentName);
                            break;
                        case 1:
                            listener.onDeleteChosen(currentName);
                            break;
                        case 2:
                            listener.onDuplicationChosen(currentName);
                            break;
                    }
                })
                .build();
    }

    void showChoices(NameDO nameDO) {
        currentName = nameDO;
        String[] options = new String[optionTemplates.length];
        for (int i = 0; i < optionTemplates.length; i++) {
            options[i] = String.format(optionTemplates[i], currentName.getName());
        }
        dialog.setItems(options);
        dialog.show();
    }
}
