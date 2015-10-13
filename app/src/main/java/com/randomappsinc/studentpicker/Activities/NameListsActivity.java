package com.randomappsinc.studentpicker.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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

public class NameListsActivity extends AppCompatActivity
{
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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lists_with_add_content);
        ButterKnife.bind(this);

        setTitle(label);
        newListInput.setHint(addListHint);
        noContent.setText(noListsMessage);

        nameListsAdapter = new NameListsAdapter(this, noContent);
        lists.setAdapter(nameListsAdapter);
    }


    @OnItemClick(R.id.content_list)
    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id)
    {
        Intent intent = new Intent(this, NameChoosingActivity.class);
        String listName = nameListsAdapter.getItem(position);
        intent.putExtra(LIST_NAME_KEY, listName);
        startActivity(intent);
    }

    @OnClick(R.id.add_item)
    public void addItem(View view)
    {
        String newList = newListInput.getText().toString().trim();
        newListInput.setText("");
        if (newList.isEmpty())
        {
            Snackbar.make(parent, blankListName, Snackbar.LENGTH_LONG).show();
        }
        else if (PreferencesManager.get().getStudentLists().contains(newList))
        {
            Snackbar.make(parent, listDuplicate + " \"" + newList + "\".", Snackbar.LENGTH_LONG).show();
        }
        else
        {
            Utils.hideKeyboard(this);
            nameListsAdapter.addList(newList);
            Intent intent = new Intent(this, EditNameListActivity.class);
            intent.putExtra(LIST_NAME_KEY, newList);
            startActivity(intent);
        }
    }

    @OnItemLongClick(R.id.content_list)
    public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, final long id)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(confirmDeletionTitle);
        alertDialogBuilder
                .setMessage(confirmDeletionMessage + " \""
                        + nameListsAdapter.getItem(position) + "\"?")
                        // Back button cancel dialog
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.dismiss();
                        // Disable clicking item they're removing, so they don't spam it
                        view.setEnabled(false);
                        // Make item fade out smoothly as opposed to just vanishing
                        view.animate().setDuration(250).alpha(0).withEndAction(new Runnable()
                        {
                            public void run()
                            {
                                nameListsAdapter.removeList(position);
                                view.setAlpha(1);
                                // Re-enable it after the row disappears
                                view.setEnabled(true);
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the blank_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.settings)
        {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
