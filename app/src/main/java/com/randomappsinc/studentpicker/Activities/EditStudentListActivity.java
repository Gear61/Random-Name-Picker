package com.randomappsinc.studentpicker.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.randomappsinc.studentpicker.Adapters.StudentsAdapter;
import com.randomappsinc.studentpicker.R;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class EditStudentListActivity extends ActionBarActivity
{
    public static final String EMPTY_STUDENT_NAME_MESSAGE = "Student names cannot be blank.";
    public static final String NO_STUDENTS = "You currently do not have any students in this list.";
    public static final String STUDENT_NAME = "Student Name";

    private Context context;
    private ListView students;
    private EditText newStudentInput;
    private StudentsAdapter studentsAdapter;
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
        Intent intent = getIntent();
        listName = intent.getStringExtra(StudentListsActivity.LIST_NAME_KEY);
        setTitle("Editing: " + listName);

        students = (ListView) findViewById(R.id.content_list);
        newStudentInput = (EditText) findViewById(R.id.item_name);
        newStudentInput.setHint(STUDENT_NAME);
        noContent = (TextView) findViewById(R.id.no_content);
        noContent.setText(NO_STUDENTS);

        studentsAdapter = new StudentsAdapter(context, noContent, listName);
        students.setAdapter(studentsAdapter);
    }

    public void addItem(View view)
    {
        hideKeyboard();
        String newList = newStudentInput.getText().toString().trim();
        newStudentInput.setText("");
        if (newList.isEmpty())
        {
            Toast.makeText(context, EMPTY_STUDENT_NAME_MESSAGE, Toast.LENGTH_SHORT).show();
        }
        else
        {
            studentsAdapter.addItem(newList);
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
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}

