package com.randomappsinc.studentpicker.editing;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.common.SpeechToTextManager;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.database.NameListDataManager;
import com.randomappsinc.studentpicker.home.MainActivity;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.utils.PermissionUtils;
import com.randomappsinc.studentpicker.utils.UIUtils;
import com.randomappsinc.studentpicker.views.SimpleDividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class EditNameListFragment extends Fragment implements
        NameEditChoicesDialog.Listener, RenameDialog.Listener, DeleteNameDialog.Listener,
        NameAmountAdjustmentDialog.Listener, MergeNameListsDialog.Listener, SpeechToTextManager.Listener{
        DuplicationDialog.Listener, EditNameListAdapter.Listener {

    private static final int RECORD_AUDIO_PERMISSION_CODE = 1;

    public static EditNameListFragment getInstance(String listName) {
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.LIST_NAME_KEY, listName);
        EditNameListFragment editNameListFragment = new EditNameListFragment();
        editNameListFragment.setArguments(bundle);
        return editNameListFragment;
    }

    @BindView(R.id.parent) View parent;
    @BindView(R.id.item_name_input) AutoCompleteTextView newNameInput;
    @BindView(R.id.no_content) TextView noContent;
    @BindView(R.id.num_names) TextView numNames;
    @BindView(R.id.content_list) RecyclerView namesList;
    @BindView(R.id.plus_icon) ImageView plus;

    private EditNameListAdapter namesAdapter;
    private NameListDataManager nameListDataManager = NameListDataManager.get();
    private String listName;
    private NameEditChoicesDialog nameEditChoicesDialog;
    private RenameDialog renameDialog;
    private DeleteNameDialog deleteNameDialog;
    private NameAmountAdjustmentDialog nameAmountAdjustmentDialog;
    private MergeNameListsDialog mergeNameListsDialog;
    private String[] importCandidates;
    private SpeechToTextManager speechToTextManager;
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

        newNameInput.setHint(R.string.name_hint);
        newNameInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        newNameInput.setAdapter(new NameCreationAutoCompleteAdapter(getActivity()));
        plus.setImageDrawable(new IconDrawable(
                getActivity(),
                IoniconsIcons.ion_android_add).colorRes(R.color.white));

        listName = getArguments().getString(MainActivity.LIST_NAME_KEY, "");
        DataSource dataSource = new DataSource(getContext());
        importCandidates = dataSource.getAllNameListsMinusCurrent(listName);
        noContent.setText(R.string.no_names_for_edit);

        speechToTextManager = new SpeechToTextManager(getContext(), this);
        speechToTextManager.setListeningPrompt(R.string.name_input_with_speech_prompt);

        namesAdapter = new EditNameListAdapter(noContent, numNames, listName, this);
        namesList.setAdapter(namesAdapter);
        namesList.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nameEditChoicesDialog = new NameEditChoicesDialog(getActivity(), this);
        renameDialog = new RenameDialog(getActivity(), this);
        deleteNameDialog = new DeleteNameDialog(getActivity(), this);
        nameAmountAdjustmentDialog = new NameAmountAdjustmentDialog(getActivity(), this);
        mergeNameListsDialog = new MergeNameListsDialog(getActivity(), this, importCandidates);
    }

    @OnClick(R.id.add_item)
    void addItem() {
        String newName = newNameInput.getText().toString().trim();
        newNameInput.setText("");
        if (newName.isEmpty()) {
            UIUtils.showSnackbar(parent, getString(R.string.blank_name));
        } else {
            nameListDataManager.addName(getContext(), newName, 1, listName);
            namesAdapter.addNames(newName, 1);
            String template = getString(R.string.added_name);
            UIUtils.showSnackbar(parent, String.format(template, newName));
        }
    }

    @Override
    public void showNameOptions(final String name) {
        nameEditChoicesDialog.showChoices(name);
    }

    @OnClick(R.id.voice_entry_icon)
    void addNameWithVoice() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.RECORD_AUDIO, getContext())) {
            speechToTextManager.startSpeechToTextFlow();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    getActivity(),
                    Manifest.permission.RECORD_AUDIO)) {
                new MaterialDialog.Builder(getContext())
                        .content(R.string.need_record_audio)
                        .positiveText(R.string.okay)
                        .negativeText(R.string.cancel)
                        .onPositive((dialog, which) ->
                                requestPermissions(
                                        new String[] {Manifest.permission.RECORD_AUDIO},
                                        RECORD_AUDIO_PERMISSION_CODE))
                        .show();
            } else {
                requestPermissions(
                        new String[] {Manifest.permission.RECORD_AUDIO},
                        RECORD_AUDIO_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRenameChosen(String name) {
        int currentAmount = namesAdapter.getListInfo().getInstancesOfName(name);
        renameDialog.startRenamingProcess(name, currentAmount);
    }

    @Override
    public void onDeleteChosen(String name) {
        int currentAmount = namesAdapter.getListInfo().getInstancesOfName(name);
        deleteNameDialog.startDeletionProcess(name, currentAmount);
    }

    @Override
    public void onNameAdjustmentChosen(String name) {
        int currentAmount = namesAdapter.getListInfo().getInstancesOfName(name);
        nameAmountAdjustmentDialog.show(name, currentAmount);
    }

    @Override
    public void onRenameSubmitted(String previousName, String newName, int amountToRename) {
        nameListDataManager.changeName(getContext(), previousName, newName, amountToRename, listName);
        namesAdapter.changeName(previousName, newName, amountToRename);
    }

    @Override
    public void onDeletionSubmitted(String name, int amountToDelete) {
        nameListDataManager.deleteName(getContext(), name, amountToDelete, listName);
        namesAdapter.removeNames(name, amountToDelete);
        if (amountToDelete == 1) {
            UIUtils.showSnackbar(parent, getString(R.string.deleted_name, name));
        } else {
            UIUtils.showSnackbar(parent, getString(R.string.names_deleted, amountToDelete, name));
        }
    }

    @Override
    public void onNameAmountAdjustmentSubmitted(String name, int newAmount, int currentAmount) {
        nameListDataManager.setNameAmount(name, newAmount, currentAmount, getContext(), listName);
        namesAdapter.setNameAmount(name, newAmount);
        UIUtils.showSnackbar(parent, R.string.name_amount_adjusted);
    }

    @Override
    public void onMergeSubmitted(List<String> listsToMergeIn) {
        ListInfo updatedListState = nameListDataManager.importNameLists(getContext(), listName, listsToMergeIn);
        namesAdapter.importNamesFromList(updatedListState);
        UIUtils.showSnackbar(parent, getString(R.string.names_successfully_imported));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            speechToTextManager.startSpeechToTextFlow();
        }
    }

    @Override
    public void onTextSpoken(String spokenText) {
        newNameInput.setText(spokenText);
        newNameInput.setSelection(spokenText.length());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_name_list_menu, menu);
        UIUtils.loadMenuIcon(menu, R.id.import_names, IoniconsIcons.ion_android_upload, getActivity());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.import_names:
                if (importCandidates.length == 0) {
                    UIUtils.showSnackbar(parent, getString(R.string.no_name_lists_to_import));
                } else {
                    mergeNameListsDialog.show();
                }
                return true;
            case android.R.id.home:
                getActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
