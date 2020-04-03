package com.randomappsinc.studentpicker.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.common.SpeechToTextManager;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.importdata.ImportFromTextFileActivity;
import com.randomappsinc.studentpicker.listpage.ListActivity;
import com.randomappsinc.studentpicker.models.ListDO;
import com.randomappsinc.studentpicker.utils.PermissionUtils;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;
import com.randomappsinc.studentpicker.views.SimpleDividerItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.randomappsinc.studentpicker.listpage.ListActivity.START_ON_EDIT_PAGE;

public class HomepageFragment extends Fragment implements
        NameListsAdapter.Delegate, RenameListDialog.Listener,
        DeleteListDialog.Listener, SpeechToTextManager.Listener {

    public static HomepageFragment getInstance() {
        return new HomepageFragment();
    }

    private static final int IMPORT_FILE_REQUEST_CODE = 1;
    private static final int SAVE_TXT_FILE_LIST_IMPORT_REQUEST_CODE = 2;

    private static final int READ_RECORD_AUDIO_PERMISSION_CODE = 2;

    @BindView(R.id.coordinator_layout) View parent;
    @BindView(R.id.focal_point) View focalPoint;
    @BindView(R.id.item_name_input) EditText newListInput;
    @BindView(R.id.user_lists) RecyclerView lists;
    @BindView(R.id.no_content) TextView noContent;
    @BindView(R.id.plus_icon) ImageView plus;
    @BindView(R.id.import_text_file) FloatingActionButton importFile;

    private PreferencesManager preferencesManager;
    private SpeechToTextManager speechToTextManager;
    private DataSource dataSource;
    private NameListsAdapter nameListsAdapter;
    private RenameListDialog renameListDialog;
    private DeleteListDialog deleteListDialog;
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.homepage_fragment,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        speechToTextManager = new SpeechToTextManager(getContext(), this);
        speechToTextManager.setListeningPrompt(R.string.list_name_speech_input_prompt);
        preferencesManager = new PreferencesManager(getContext());
        renameListDialog = new RenameListDialog(this, getContext());
        deleteListDialog = new DeleteListDialog(this, getContext());
        dataSource = new DataSource(getContext());
        plus.setImageDrawable(new IconDrawable(getContext(),
                IoniconsIcons.ion_android_add).colorRes(R.color.white));
        importFile.setImageDrawable(new IconDrawable(
                getContext(),
                IoniconsIcons.ion_android_upload).colorRes(R.color.white));

        nameListsAdapter = new NameListsAdapter(this, dataSource.getNameLists());
        lists.setAdapter(nameListsAdapter);
        lists.addItemDecoration(new SimpleDividerItemDecoration(getContext()));

        if (preferencesManager.rememberAppOpen() == 5) {
            showPleaseRateDialog();
        }

        setNoContent();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        speechToTextManager.cleanUp();
    }

    private void showPleaseRateDialog() {
        new MaterialDialog.Builder(getActivity())
                .content(R.string.please_rate)
                .negativeText(R.string.no_im_good)
                .positiveText(R.string.will_rate)
                .onPositive((dialog, which) -> {
                    Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (!(getContext().getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                        UIUtils.showSnackbar(parent, getString(R.string.play_store_error));
                        return;
                    }
                    startActivity(intent);
                })
                .show();
    }

    @Override
    public void onItemClick(ListDO listDO) {
        Intent intent = new Intent(getActivity(), ListActivity.class);
        intent.putExtra(Constants.LIST_ID_KEY, listDO.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    public void onItemEditClick(int position, ListDO listDO) {
        renameListDialog.show(position, listDO);
    }

    @Override
    public void onRenameListConfirmed(int position, ListDO updatedList) {
        dataSource.renameList(updatedList);
        preferencesManager.renameList(nameListsAdapter.getItem(position).getName(), updatedList.getName());
        nameListsAdapter.renameItem(position, updatedList.getName());
    }

    @Override
    public void onItemDeleteClick(int position, ListDO listDO) {
        deleteListDialog.presentForList(position, listDO);
    }

    @Override
    public void onDeleteListConfirmed(int position, ListDO listDO) {
        dataSource.deleteList(listDO.getId());
        preferencesManager.removeNameList(listDO.getName());
        nameListsAdapter.deleteItem(position);
    }

    @Override
    public void setNoContent() {
        if (nameListsAdapter.getItemCount() == 0) {
            lists.setVisibility(View.GONE);
            noContent.setVisibility(View.VISIBLE);
        } else {
            noContent.setVisibility(View.GONE);
            lists.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.add_item)
    public void addItem() {
        String newList = newListInput.getText().toString().trim();
        if (newList.isEmpty()) {
            UIUtils.showSnackbar(parent, getString(R.string.blank_list_name));
        } else {
            newListInput.setText("");

            ListDO newListDO = dataSource.addNameList(newList);
            nameListsAdapter.addList(newListDO);

            Intent intent = new Intent(getActivity(), ListActivity.class);
            intent.putExtra(Constants.LIST_ID_KEY, newListDO.getId());
            intent.putExtra(START_ON_EDIT_PAGE, true);
            startActivity(intent);
        }
    }

    @OnClick(R.id.voice_entry_icon)
    public void voiceEntry() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.RECORD_AUDIO, getContext())) {
            speechToTextManager.startSpeechToTextFlow();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    getActivity(),
                    Manifest.permission.RECORD_AUDIO)) {
                new MaterialDialog.Builder(getActivity())
                        .content(R.string.need_record_audio)
                        .positiveText(R.string.okay)
                        .negativeText(R.string.cancel)
                        .onPositive((dialog, which) -> requestRecordAudio())
                        .show();
            } else {
                requestRecordAudio();
            }
        }
    }

    @Override
    public void onTextSpoken(String spokenText) {
        newListInput.setText(spokenText);
        newListInput.setSelection(spokenText.length());
    }

    @OnClick(R.id.import_text_file)
    public void importTextFile() {
        Intent txtFileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        txtFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        txtFileIntent.setType("text/*");
        startActivityForResult(txtFileIntent, IMPORT_FILE_REQUEST_CODE);
    }

    private void requestRecordAudio() {
        PermissionUtils.requestPermission(
                getActivity(), Manifest.permission.RECORD_AUDIO, READ_RECORD_AUDIO_PERMISSION_CODE);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMPORT_FILE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                    Uri uri = data.getData();

                    // Persist ability to read from this file
                    int takeFlags = data.getFlags()
                            & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    getContext().getContentResolver().takePersistableUriPermission(uri, takeFlags);

                    String uriString = uri.toString();
                    Intent intent = new Intent(getActivity(), ImportFromTextFileActivity.class);
                    intent.putExtra(Constants.FILE_URI_KEY, uriString);
                    startActivityForResult(intent, SAVE_TXT_FILE_LIST_IMPORT_REQUEST_CODE);
                }
                break;
            case SAVE_TXT_FILE_LIST_IMPORT_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    nameListsAdapter.refresh(dataSource.getNameLists());
                }
                break;
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        focalPoint.requestFocus();
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
