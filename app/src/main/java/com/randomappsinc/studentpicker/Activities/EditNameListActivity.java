package com.randomappsinc.studentpicker.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.randomappsinc.studentpicker.Adapters.NamesAdapter;
import com.randomappsinc.studentpicker.R;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class EditNameListActivity extends ActionBarActivity
{
    public static final String EMPTY_NAME_MESSAGE = "Names cannot be blank.";
    public static final String NO_NAMES = "You currently do not have any names in this list.";
    public static final String NAME_HINT = "Name";

    private Context context;
    private ListView students;
    private EditText newStudentInput;
    private NamesAdapter namesAdapter;
    private TextView noContent;
    private String listName;

    public void hideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(newStudentInput.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lists_with_add_content);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;
        // Make DONE button close keyboard
        InputMethodManager inputManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(0, 0);

        Intent intent = getIntent();
        listName = intent.getStringExtra(NameListsActivity.LIST_NAME_KEY);
        setTitle("Editing: " + listName);

        students = (ListView) findViewById(R.id.content_list);
        newStudentInput = (EditText) findViewById(R.id.item_name);
        newStudentInput.setHint(NAME_HINT);
        newStudentInput.setOnEditorActionListener(new EditText.OnEditorActionListener()
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
        newStudentInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        noContent = (TextView) findViewById(R.id.no_content);
        noContent.setText(NO_NAMES);

        namesAdapter = new NamesAdapter(context, noContent, listName);
        students.setAdapter(namesAdapter);
    }

    public void addItem(View view)
    {
        String newStudent = newStudentInput.getText().toString().trim();
        newStudentInput.setText("");
        if (newStudent.isEmpty())
        {
            Toast.makeText(context, EMPTY_NAME_MESSAGE, Toast.LENGTH_SHORT).show();
        }
        else
        {
            namesAdapter.addStudent(newStudent);
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
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            hideKeyboard();
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}