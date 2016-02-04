package com.randomappsinc.studentpicker.Fragments;

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
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.randomappsinc.studentpicker.Activities.MainActivity;
import com.randomappsinc.studentpicker.Adapters.EditNameListAdapter;
import com.randomappsinc.studentpicker.Adapters.NameCreationACAdapter;
import com.randomappsinc.studentpicker.Database.DataSource;
import com.randomappsinc.studentpicker.Misc.PreferencesManager;
import com.randomappsinc.studentpicker.Misc.Utils;
import com.randomappsinc.studentpicker.Models.EditListEvent;
import com.randomappsinc.studentpicker.R;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by alexanderchiou on 10/18/15.
 */
public class EditNameListFragment extends Fragment {
    @Bind(R.id.parent) View parent;
    @Bind(R.id.item_name_input) AutoCompleteTextView newNameInput;
    @Bind(R.id.no_content) TextView noContent;
    @Bind(R.id.num_names) TextView numNames;
    @Bind(R.id.content_list) ListView namesList;
    @Bind(R.id.plus_icon) ImageView plus;

    @BindString(R.string.new_list_name) String newListName;
    @BindString(R.string.confirm_deletion_title) String confirmDeletionTitle;
    @BindString(R.string.confirm_deletion_message) String confirmDeletionMessage;

    private EditNameListAdapter adapter;
    private DataSource dataSource;
    private String listName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lists_with_add_content, container, false);
        ButterKnife.bind(this, rootView);
        dataSource = new DataSource(getActivity());

        newNameInput.setHint(R.string.name_hint);
        newNameInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        newNameInput.setAdapter(new NameCreationACAdapter(getActivity()));
        plus.setImageDrawable(new IconDrawable(getActivity(), FontAwesomeIcons.fa_plus).colorRes(R.color.white));

        listName = getArguments().getString(MainActivity.LIST_NAME_KEY, "");
        noContent.setText(R.string.no_names);

        adapter = new EditNameListAdapter(getActivity(), noContent, numNames, listName, parent);
        namesList.setAdapter(adapter);
        return rootView;
    }

    @OnClick(R.id.add_item)
    public void addItem() {
        String newName = newNameInput.getText().toString().trim();
        newNameInput.setText("");
        if (newName.isEmpty()) {
            Utils.showSnackbar(parent, getString(R.string.blank_name));
        }
        else {
            adapter.addName(newName);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getActivity().invalidateOptionsMenu();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void showRenameDialog() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.rename_list)
                .input(newListName, "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        boolean submitEnabled = !(input.toString().trim().isEmpty() ||
                                PreferencesManager.get().doesListExist(input.toString()));
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(submitEnabled);
                    }
                })
                .alwaysCallInputCallback()
                .negativeText(android.R.string.no)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            String newListName = dialog.getInputEditText().getText().toString();
                            dataSource.renameList(listName, newListName);
                            PreferencesManager.get().renameList(listName, newListName);
                            listName = newListName;
                            getActivity().setTitle(listName);
                            EditListEvent editListEvent = new EditListEvent();
                            editListEvent.setEventType(EditListEvent.RENAME_LIST);
                            editListEvent.setNewName(newListName);
                            EventBus.getDefault().post(editListEvent);
                        }
                    }
                })
                .show();
    }

    private void showDeleteDialog() {
        new MaterialDialog.Builder(getActivity())
                .title(confirmDeletionTitle)
                .content(confirmDeletionMessage)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dataSource.deleteList(listName);
                        PreferencesManager.get().removeNameList(listName);
                        PreferencesManager.get().removeNamesListCache(listName);
                        getActivity().finish();
                    }
                })
                .show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_name_list_menu, menu);
        menu.findItem(R.id.import_names).setIcon(
                new IconDrawable(getActivity(), FontAwesomeIcons.fa_upload)
                        .colorRes(R.color.white)
                        .actionBarSize());
        menu.findItem(R.id.rename_list).setIcon(
                new IconDrawable(getActivity(), FontAwesomeIcons.fa_edit)
                        .colorRes(R.color.white)
                        .actionBarSize());
        menu.findItem(R.id.delete_list).setIcon(
                new IconDrawable(getActivity(), FontAwesomeIcons.fa_trash)
                        .colorRes(R.color.white)
                        .actionBarSize());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.import_names:
                String[] importCandidates = dataSource.getAllNameLists(listName);
                if (importCandidates.length == 0) {
                    Utils.showSnackbar(parent, getString(R.string.no_name_lists_to_import));
                }
                else {
                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.choose_list_to_import)
                            .items(importCandidates)
                            .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    adapter.importNamesFromList(text.toString());
                                    Utils.showSnackbar(parent, getString(R.string.names_successfully_imported));
                                    return true;
                                }
                            })
                            .negativeText(android.R.string.no)
                            .positiveText(R.string.choose)
                            .show();
                }
                return true;
            case R.id.rename_list:
                showRenameDialog();
                break;
            case R.id.delete_list:
                showDeleteDialog();
                break;
            case android.R.id.home:
                getActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
