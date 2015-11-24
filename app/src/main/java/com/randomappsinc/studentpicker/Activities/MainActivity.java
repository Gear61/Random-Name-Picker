package com.randomappsinc.studentpicker.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.randomappsinc.studentpicker.Adapters.NameListsAdapter;
import com.randomappsinc.studentpicker.Misc.PreferencesManager;
import com.randomappsinc.studentpicker.Misc.Utils;
import com.randomappsinc.studentpicker.R;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;

public class MainActivity extends StandardActivity {
    public static final String LIST_NAME_KEY = "listName";

    @Bind(R.id.item_name_input) EditText newListInput;
    @Bind(R.id.content_list) ListView lists;
    @Bind(R.id.no_content) TextView noContent;
    @Bind(R.id.coordinator_layout) View parent;
    @Bind(R.id.plus_icon) ImageView plus;
    @Bind(R.id.import_text_file) FloatingActionButton importFile;

    @BindString(R.string.confirm_deletion_title) String confirmDeletionTitle;
    @BindString(R.string.confirm_deletion_message) String confirmDeletionMessage;
    @BindString(R.string.list_duplicate) String listDuplicate;
    @BindString(R.string.new_list_name) String newListName;

    private NameListsAdapter nameListsAdapter;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        this.activity = this;

        setTitle(R.string.name_lists_label);
        newListInput.setHint(R.string.add_list_hint);
        noContent.setText(R.string.no_lists_message);
        plus.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_plus).colorRes(R.color.white));
        importFile.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_upload).colorRes(R.color.white));

        nameListsAdapter = new NameListsAdapter(this, noContent);
        lists.setAdapter(nameListsAdapter);

        if (PreferencesManager.get().getFirstTimeUser()) {
            PreferencesManager.get().setFirstTimeUser(false);
            new MaterialDialog.Builder(this)
                    .title(R.string.welcome)
                    .content(R.string.ask_for_help)
                    .positiveText(android.R.string.yes)
                    .show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        nameListsAdapter.refreshList();
    }

    @OnItemClick(R.id.content_list)
    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
        Utils.hideKeyboard(this);
        Intent intent = new Intent(this, ListActivity.class);
        String listName = nameListsAdapter.getItem(position);
        intent.putExtra(LIST_NAME_KEY, listName);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @OnClick(R.id.add_item)
    public void addItem(View view) {
        String newList = newListInput.getText().toString().trim();
        newListInput.setText("");
        if (newList.isEmpty()) {
            Utils.showSnackbar(parent, getString(R.string.blank_list_name));
        }
        else if (PreferencesManager.get().getNameLists().contains(newList)) {
            String dupeMessage = listDuplicate + " \"" + newList + "\".";
            Utils.showSnackbar(parent, dupeMessage);
        }
        else {
            nameListsAdapter.addList(newList);
            Intent intent = new Intent(this, ListActivity.class);
            intent.putExtra(LIST_NAME_KEY, newList);
            startActivity(intent);
        }
    }

    @OnItemLongClick(R.id.content_list)
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        String title = getString(R.string.options_for) + nameListsAdapter.getItem(position);

        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(this);
        IconDrawable editIcon = new IconDrawable(this, FontAwesomeIcons.fa_edit).colorRes(R.color.dark_gray);
        IconDrawable deleteIcon = new IconDrawable(this, FontAwesomeIcons.fa_remove).colorRes(R.color.dark_gray);

        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.rename_list)
                .icon(editIcon).iconPaddingDp(5)
                .backgroundColor(Color.WHITE)
                .build());
        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.delete_list)
                .icon(deleteIcon).iconPaddingDp(5)
                .backgroundColor(Color.WHITE)
                .build());

        new MaterialDialog.Builder(this)
                .title(title)
                .adapter(adapter, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        dialog.dismiss();
                        MaterialSimpleListItem item = adapter.getItem(which);
                        if (item.getContent().equals(getString(R.string.rename_list))) {
                            showRenameDialog(position);
                        } else {
                            showDeleteDialog(position);
                        }
                    }
                })
                .show();
        return true;
    }

    public void showRenameDialog(final int listPosition) {
        new MaterialDialog.Builder(this)
                .title(R.string.rename_list)
                .input(newListName, "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
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
                        jankyCloseKeyboard();
                        if (which == DialogAction.POSITIVE) {
                            String newListName = dialog.getInputEditText().getText().toString();
                            nameListsAdapter.renameList(listPosition, newListName);
                        }
                    }
                })
                .show();
    }

    private void jankyCloseKeyboard() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.hideKeyboard(activity);
            }
        }, 200);
    }

    private void showDeleteDialog(final int listPosition) {
        new MaterialDialog.Builder(this)
                .title(confirmDeletionTitle)
                .content(confirmDeletionMessage + " \"" + nameListsAdapter.getItem(listPosition) + "\"?")
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        nameListsAdapter.removeList(listPosition);
                    }
                })
                .show();
    }

    @OnClick(R.id.import_text_file)
    public void importTextFile() {
        Intent intent = new Intent();
        intent.setType("file/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri selectedTextFile = data.getData();
            if (!selectedTextFile.toString().endsWith(".txt")) {
                Utils.showSnackbar(parent, getString(R.string.invalid_file));
            }
            else {
                Intent intent = new Intent(this, ImportFileActivity.class);
                intent.putExtra(ImportFileActivity.FILE_PATH_KEY, selectedTextFile.toString());
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the blank_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        menu.findItem(R.id.settings).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_gear)
                        .colorRes(R.color.white)
                        .actionBarSize());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
