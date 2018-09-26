package com.randomappsinc.studentpicker.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.activities.ListActivity;
import com.randomappsinc.studentpicker.activities.MainActivity;
import com.randomappsinc.studentpicker.adapters.EditNameListAdapter;
import com.randomappsinc.studentpicker.adapters.NameCreationACAdapter;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.dialogs.NameChoicesDialog;
import com.randomappsinc.studentpicker.dialogs.RenameDialog;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;

public class EditNameListFragment extends Fragment implements NameChoicesDialog.Listener, RenameDialog.Listener {

    @BindView(R.id.parent) View parent;
    @BindView(R.id.item_name_input) AutoCompleteTextView newNameInput;
    @BindView(R.id.no_content) TextView noContent;
    @BindView(R.id.num_names) TextView numNames;
    @BindView(R.id.content_list) ListView namesList;
    @BindView(R.id.plus_icon) ImageView plus;

    private EditNameListAdapter namesAdapter;
    private DataSource datasource;
    private String listName;
    private NameChoicesDialog nameChoicesDialog;
    private RenameDialog renameDialog;
    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lists_with_add_content, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        datasource = new DataSource(getContext());

        newNameInput.setHint(R.string.name_hint);
        newNameInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        newNameInput.setAdapter(new NameCreationACAdapter(getActivity()));
        plus.setImageDrawable(new IconDrawable(
                getActivity(),
                IoniconsIcons.ion_android_add).colorRes(R.color.white));

        listName = getArguments().getString(MainActivity.LIST_NAME_KEY, "");
        noContent.setText(R.string.no_names);

        namesAdapter = new EditNameListAdapter((ListActivity) getActivity(), noContent, numNames, listName, parent);
        namesList.setAdapter(namesAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nameChoicesDialog = new NameChoicesDialog(getActivity(), this);
        renameDialog = new RenameDialog(getActivity(), this);
    }

    @OnClick(R.id.add_item)
    public void addItem() {
        String newName = newNameInput.getText().toString().trim();
        newNameInput.setText("");
        if (newName.isEmpty()) {
            UIUtils.showSnackbar(parent, getString(R.string.blank_name));
        } else {
            namesAdapter.addNames(newName, 1);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getActivity().invalidateOptionsMenu();
        }
    }

    @OnItemClick(R.id.content_list)
    public void showNameOptions(final int position) {
        nameChoicesDialog.showChoices(namesAdapter.getItem(position));
    }

    @Override
    public void onRenameChosen(String name) {
        ListInfo listInfo = namesAdapter.getListInfo();
        int currentAmount = listInfo.getInstancesOfName(name);
        renameDialog.startRenamingProcess(name, currentAmount);
    }

    @Override
    public void onDeleteChosen(String name) {

    }

    @Override
    public void onCloneChosen(String name) {

    }

    @Override
    public void onRenameSubmitted(String previousName, String newName, int amountToRename) {
        datasource.renamePeople(previousName, newName, listName, amountToRename);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_name_list_menu, menu);
        UIUtils.loadMenuIcon(menu, R.id.import_names, IoniconsIcons.ion_android_upload, getActivity());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.import_names:
                final String[] importCandidates = datasource.getAllNameLists(listName);
                if (importCandidates.length == 0) {
                    UIUtils.showSnackbar(parent, getString(R.string.no_name_lists_to_import));
                } else {
                    MaterialDialog importDialog = new MaterialDialog.Builder(getActivity())
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
                                    for (Integer index : indices) {
                                        listNames.add(importCandidates[index]);
                                    }
                                    namesAdapter.importNamesFromList(listNames);
                                    UIUtils.showSnackbar(parent, getString(R.string.names_successfully_imported));
                                }
                            })
                            .build();
                    importDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                    importDialog.show();
                }
                return true;
            case android.R.id.home:
                getActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
