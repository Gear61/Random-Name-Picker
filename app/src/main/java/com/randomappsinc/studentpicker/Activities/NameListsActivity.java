package com.randomappsinc.studentpicker.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.randomappsinc.studentpicker.Adapters.NameListsAdapter;
import com.randomappsinc.studentpicker.Misc.PreferencesManager;
import com.randomappsinc.studentpicker.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class NameListsActivity extends AppCompatActivity
{
    public static final String CONFIRM_DELETION = "Confirm Deletion";
    public static final String EMPTY_NAME_LIST_MESSAGE = "List names cannot be blank.";
    public static final String NO_NAME_LISTS = "You currently do not have any name lists.";
    public static final String LIST_NAME_KEY = "listName";
    public static final String NAME_LISTS = "Name Lists";
    public static final String LIST_NAME = "List Name";

    @Bind(R.id.item_name_input) EditText newListInput;
    @Bind(R.id.content_list) ListView lists;
    @Bind(R.id.no_content) TextView noContent;

    private NameListsAdapter nameListsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lists_with_add_content);
        ButterKnife.bind(this);
        setTitle(NAME_LISTS);

        newListInput.setHint(LIST_NAME);
        noContent.setText(NO_NAME_LISTS);

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
            Toast.makeText(this, EMPTY_NAME_LIST_MESSAGE, Toast.LENGTH_SHORT).show();
        }
        else if (PreferencesManager.get().getStudentLists().contains(newList))
        {
            Toast.makeText(this, "You already have a name list named \"" + newList + "\".",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            nameListsAdapter.addList(newList);
            Intent intent = new Intent(this, EditNameListActivity.class);
            intent.putExtra(LIST_NAME_KEY, newList);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    /*
    holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                alertDialogBuilder.setTitle(CONFIRM_DELETION);
                alertDialogBuilder
                        .setMessage("Are you sure that you want to delete the name list \""
                                + content.get(_position) + "\"?")
                                // Back button cancel dialog
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.dismiss();
                                // Disable clicking item they're removing, so they don't spam it
                                _v.setEnabled(false);
                                // Make item fade out smoothly as opposed to just vanishing
                                _v.animate().setDuration(250).alpha(0).withEndAction(new Runnable()
                                {
                                    public void run()
                                    {
                                        removeList(_position);
                                        _v.setAlpha(1);
                                        // Re-enable it after the row disappears
                                        _v.setEnabled(true);
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
            }
        });
     */
}
