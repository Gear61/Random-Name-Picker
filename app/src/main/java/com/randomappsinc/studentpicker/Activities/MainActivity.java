package com.randomappsinc.studentpicker.Activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.randomappsinc.studentpicker.Adapters.NameListsAdapter;
import com.randomappsinc.studentpicker.Misc.PreferencesManager;
import com.randomappsinc.studentpicker.Misc.Utils;
import com.randomappsinc.studentpicker.R;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class MainActivity extends StandardActivity {
    public static final String LIST_NAME_KEY = "listName";

    @Bind(R.id.item_name_input) EditText newListInput;
    @Bind(R.id.content_list) ListView lists;
    @Bind(R.id.no_content) TextView noContent;
    @Bind(R.id.coordinator_layout) View parent;
    @Bind(R.id.add_item) View addItem;
    @Bind(R.id.plus_icon) ImageView plus;
    @Bind(R.id.import_text_file) FloatingActionButton importFile;

    @BindString(R.string.list_duplicate) String listDuplicate;

    private NameListsAdapter nameListsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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
                    .title(R.string.view_tutorial)
                    .content(R.string.tutorial_prompt)
                    .positiveText(R.string.accept_tutorial)
                    .negativeText(R.string.decline_tutorial)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            showTutorial(true);
                        }
                    })
                    .show();
        }
    }

    public void showTutorial(final boolean firstTime) {
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);
        MaterialShowcaseView addListExplanation = new MaterialShowcaseView.Builder(this)
                .setTarget(addItem)
                .setDismissText(R.string.got_it)
                .setContentText(R.string.add_name_list_explanation)
                .setUseAutoRadius(false)
                .setRadius(Utils.getDpInPixels(40))
                .build();
        sequence.addSequenceItem(addListExplanation);

        MaterialShowcaseView importFileExplanation = new MaterialShowcaseView.Builder(this)
                .setTarget(importFile)
                .setDismissText(R.string.got_it)
                .setContentText(R.string.import_explanation)
                .setUseAutoRadius(false)
                .setRadius(Utils.getDpInPixels(40))
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {}

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        if (firstTime) {
                            showWelcomeDialog();
                        }
                    }
                })
                .build();
        sequence.addSequenceItem(importFileExplanation);
        sequence.start();
    }

    public void showWelcomeDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.welcome)
                .content(R.string.welcome_string)
                .positiveText(android.R.string.yes)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nameListsAdapter.refreshList();
    }

    @OnItemClick(R.id.content_list)
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
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

    @OnClick(R.id.import_text_file)
    public void importTextFile() {
        Intent intent = new Intent(this, FilePickerActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            if (!filePath.endsWith(".txt")) {
                Utils.showSnackbar(parent, getString(R.string.invalid_file));
            }
            else {
                Intent intent = new Intent(this, ImportFileActivity.class);
                intent.putExtra(ImportFileActivity.FILE_PATH_KEY, filePath);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        menu.findItem(R.id.settings).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_gear)
                        .colorRes(R.color.white)
                        .actionBarSize());
        menu.findItem(R.id.view_tutorial).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_info_circle)
                        .colorRes(R.color.white)
                        .actionBarSize());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.view_tutorial:
                showTutorial(false);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
