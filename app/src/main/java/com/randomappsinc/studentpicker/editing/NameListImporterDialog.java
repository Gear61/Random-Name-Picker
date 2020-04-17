package com.randomappsinc.studentpicker.editing;

import android.content.Context;

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

        String[] importListArray = new String[importCandidates.size()];
        for (int i = 0; i < importCandidates.size(); i++) {
            importListArray[i] = importCandidates.get(i).getName();
            indexToList.put(i, importCandidates.get(i));
        }

        importDialog = new MaterialDialog.Builder(context)
                .title(R.string.name_list_importer)
                .content(R.string.name_list_importer_body)
                .items(importListArray)
                .itemsCallbackMultiChoice(null, (dialog, which, text) -> {
                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(which.length > 0);
                    return true;
                })
                .alwaysCallMultiChoiceCallback()
                .negativeText(R.string.cancel)
                .positiveText(R.string.choose)
                .onPositive((dialog, which) -> {
                    Integer[] indices = dialog.getSelectedIndices();
                    List<ListDO> selectedLists = new ArrayList<>();
                    for (Integer index : indices) {
                        selectedLists.add(indexToList.get(index));
                    }
                    listener.onNameListImportsConfirmed(selectedLists);
                })
                .build();
        importDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
    }

    public void show() {
        importDialog.show();
    }
}
