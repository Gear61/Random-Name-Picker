package com.randomappsinc.studentpicker.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
    @Bind(R.id.coordinator_layout) CoordinatorLayout parent;

    @BindString(R.string.name_lists_label) String label;
    @BindString(R.string.no_lists_message) String noListsMessage;
    @BindString(R.string.add_list_hint) String addListHint;
    @BindString(R.string.confirm_deletion_title) String confirmDeletionTitle;
    @BindString(R.string.confirm_deletion_message) String confirmDeletionMessage;
    @BindString(R.string.blank_list_name) String blankListName;
    @BindString(R.string.list_duplicate) String listDuplicate;

    private NameListsAdapter nameListsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lists_with_add_content);
        ButterKnife.bind(this);

        setTitle(label);
        newListInput.setHint(addListHint);
        noContent.setText(noListsMessage);

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
            Snackbar.make(parent, blankListName, Snackbar.LENGTH_LONG).show();
        }
        else if (PreferencesManager.get().getStudentLists().contains(newList)) {
            Snackbar.make(parent, listDuplicate + " \"" + newList + "\".", Snackbar.LENGTH_LONG).show();
        }
        else {
            Utils.hideKeyboard(this);
            nameListsAdapter.addList(newList);
            Intent intent = new Intent(this, ListActivity.class);
            intent.putExtra(LIST_NAME_KEY, newList);
            startActivity(intent);
        }
    }

    @OnItemLongClick(R.id.content_list)
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id)
    {
        new MaterialDialog.Builder(this)
                .title(confirmDeletionTitle)
                .content(confirmDeletionMessage + " \"" + nameListsAdapter.getItem(position) + "\"?")
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        nameListsAdapter.removeList(position);
                    }
                })
                .show();
        return true;
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
