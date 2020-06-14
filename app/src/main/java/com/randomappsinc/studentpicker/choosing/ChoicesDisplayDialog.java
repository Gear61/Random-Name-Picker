package com.randomappsinc.studentpicker.choosing;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.common.ChooseNameAdapter;
import com.randomappsinc.studentpicker.models.NameDO;
import com.randomappsinc.studentpicker.utils.NameUtils;

import java.util.List;

/** Shows the chosen names and their corresponding options in a dialog */
public class ChoicesDisplayDialog {

    public interface Listener {
        void sayNames(String chosenNames);

        void copyNamesToClipboard(String chosenNames, int numNames);
    }

    private List<NameDO> chosenNames;
    private ChoosingSettings settings;
    private int listId;
    private MaterialDialog dialog;
    private ChooseNameAdapter chooseNameAdapter;

    ChoicesDisplayDialog(
            Listener listener, Context context, int listId, ChoosingSettings settings) {
        this.listId = listId;
        this.settings = settings;
        chooseNameAdapter = new ChooseNameAdapter();

        dialog = new MaterialDialog.Builder(context)
                // Placeholder because otherwise, the view doesn't exist
                .title(R.string.name_chosen)
                .adapter(chooseNameAdapter, new LinearLayoutManager(context))
                .positiveText(android.R.string.yes)
                .negativeText(R.string.copy_text)
                .neutralText(R.string.say_name)
                .onPositive((dialog, which) -> dialog.dismiss())
                .onNegative((dialog, which) -> listener.copyNamesToClipboard(
                        NameUtils.convertNameListToString(chosenNames, settings),
                        chosenNames.size()))
                .onNeutral((dialog, which) -> listener.sayNames(
                        NameUtils.convertNameListToString(chosenNames, settings)))
                .autoDismiss(false)
                .build();

        DividerItemDecoration itemDecorator =
                new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(context, R.drawable.line_divider));
        dialog.getRecyclerView().addItemDecoration(itemDecorator);
    }

    void showChoices(List<NameDO> chosenNames) {
        this.chosenNames = chosenNames;
        int numNames = chosenNames.size();

        dialog.setTitle(NameUtils.getChoosingMessage(dialog.getContext(), listId, numNames));
        chooseNameAdapter.setChosenNames(chosenNames, settings.getShowAsList());
        dialog.getActionButton(DialogAction.NEUTRAL)
                .setText(numNames == 1 ? R.string.say_name : R.string.say_names);
        dialog.getActionButton(DialogAction.NEUTRAL).setText(numNames == 1 ? R.string.say_name : R.string.say_names);
        dialog.show();
    }

    boolean isShowing() {
        return dialog.isShowing();
    }
}
