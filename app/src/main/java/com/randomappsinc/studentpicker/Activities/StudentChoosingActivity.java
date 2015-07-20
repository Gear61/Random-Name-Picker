package com.randomappsinc.studentpicker.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.randomappsinc.studentpicker.Adapters.StudentChoosingAdapter;
import com.randomappsinc.studentpicker.R;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class StudentChoosingActivity extends ActionBarActivity
{
    public static final String NO_STUDENTS = "You currently do not have any students in this list. " +
            "Why are you even here?";

    private Context context;
    private ListView students;
    private StudentChoosingAdapter studentChoosingAdapter;
    private TextView noContent;
    private CheckBox withReplacement;
    private String listName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_choosing);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        Intent intent = getIntent();
        listName = intent.getStringExtra(StudentListsActivity.LIST_NAME_KEY);
        setTitle("List: " + listName);

        students = (ListView) findViewById(R.id.students_list);
        noContent = (TextView) findViewById(R.id.no_content);
        noContent.setText(NO_STUDENTS);
        withReplacement = (CheckBox) findViewById(R.id.with_replacement);

        studentChoosingAdapter = new StudentChoosingAdapter(context, noContent, listName);
        students.setAdapter(studentChoosingAdapter);
    }

    public void choose(View view)
    {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.student_choosing_menu, menu);
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
        else if (id == R.id.reset)
        {
            studentChoosingAdapter.resetStudents();
        }
        return super.onOptionsItemSelected(item);
    }
}
