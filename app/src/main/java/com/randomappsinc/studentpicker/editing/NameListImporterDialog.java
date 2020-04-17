package com.randomappsinc.studentpicker.editing;

import android.content.Context;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.models.ListDO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Dialog for letting users choose other name lists they want to import names from */
public class NameListImporterDialog {

    public interface Listener {
        void onNameListImportsConfirmed(List<ListDO> chosenLists);
    }

    private MaterialDialog importDialog;
    private Map<Integer, ListDO> indexToList = new HashMap<>();

    public NameListImporterDialog(Context context, int currentListId, Listener listener) {
        DataSource dataSource = new DataSource(context);
        List<ListDO> importCandidates = dataSource.getNonEmptyOtherLists(currentListId);

        importDialog = new MaterialDialog.Builder(context)
                .title(R.string.name_list_importer)
                .content(R.string.name_list_importer_body)
                .itemsCallbackMultiChoice(null, (dialog, which, text) -> {
                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(which.length > 0);
                    return true;
                })
                .alwaysCallMultiChoiceCallback()
                .negativeText(R.string.cancel)
                .positiveText(R.string.choose)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Integer[] indices = dialog.getSelectedIndices();
                        List<String> listNames = new ArrayList<>();
                        for (Integer index : indices) {
                        }
                        // Show TOAST here
                    }
                })
                .build();
        importDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
    }

    public void show() {

        importDialog.show();
    }
}
