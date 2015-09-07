package com.randomappsinc.studentpicker.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.randomappsinc.studentpicker.Adapters.StudentListChoicesAdapter;
import com.randomappsinc.studentpicker.Adapters.NameListsAdapter;
import com.randomappsinc.studentpicker.Misc.PreferencesManager;
import com.randomappsinc.studentpicker.R;

public class NameListsActivity extends ActionBarActivity
{
    public static final String EMPTY_NAME_LIST_MESSAGE = "Name list names cannot be blank.";
    public static final String NO_NAME_LISTS = "You currently do not have any name lists.";
    public static final String LIST_NAME_KEY = "listName";
    public static final String NAME_LISTS = "Name Lists";
    public static final String LIST_NAME = "List Name";

    private Context context;
    private ListView studentLists;
    private EditText newListInput;
    private NameListsAdapter nameListsAdapter;
    private TextView noContent;

    public void hideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(newListInput.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        context = this;

        setContentView(R.layout.lists_with_add_content);
        setTitle(NAME_LISTS);

        studentLists = (ListView) findViewById(R.id.content_list);
        newListInput = (EditText) findViewById(R.id.item_name);
        newListInput.setHint(LIST_NAME);
        newListInput.setOnEditorActionListener(new EditText.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    hideKeyboard();
                }
                return false;
            }
        });
        noContent = (TextView) findViewById(R.id.no_content);
        noContent.setText(NO_NAME_LISTS);

        nameListsAdapter = new NameListsAdapter(context, noContent);
        studentLists.setAdapter(nameListsAdapter);

        studentLists.setOnItemClickListener(studentListListener);
    }

    // Student list item clicked
    AdapterView.OnItemClickListener studentListListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View convertView = inflater.inflate(R.layout.list_with_dividers, null);
            alertDialogBuilder.setView(convertView);
            final String listName = nameListsAdapter.getItem(position);
            ListView studentListChoices = (ListView) convertView.findViewById(R.id.listView1);
            final StudentListChoicesAdapter choicesAdapter = new StudentListChoicesAdapter(context, listName);
            studentListChoices.setAdapter(choicesAdapter);
            final AlertDialog studentListDialog = alertDialogBuilder.show();
            studentListChoices.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id)
                {
                    studentListDialog.dismiss();
                    String action = choicesAdapter.getItem(position);
                    if (action.startsWith("Choose"))
                    {
                        Intent intent = new Intent(context, NameChoosingActivity.class);
                        intent.putExtra(LIST_NAME_KEY, listName);
                        startActivity(intent);
                    }
                    else if (action.startsWith("Add/remove"))
                    {
                        Intent intent = new Intent(context, EditNameListActivity.class);
                        intent.putExtra(LIST_NAME_KEY, listName);
                        startActivity(intent);
                    }
                }
            });
            studentListDialog.setCanceledOnTouchOutside(true);
            studentListDialog.setCancelable(true);
        }
    };

    public void addItem(View view)
    {
        hideKeyboard();
        String newList = newListInput.getText().toString().trim();
        newListInput.setText("");
        if (newList.isEmpty())
        {
            Toast.makeText(context, EMPTY_NAME_LIST_MESSAGE, Toast.LENGTH_SHORT).show();
        }
        else if (PreferencesManager.get().getStudentLists().contains(newList))
        {
            Toast.makeText(context, "You already have a name list named \"" + newList + "\".",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            nameListsAdapter.addList(newList);
            Intent intent = new Intent(context, EditNameListActivity.class);
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
}
