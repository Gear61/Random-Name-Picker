package com.randomappsinc.studentpicker.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.randomappsinc.studentpicker.Adapters.StudentChoosingAdapter;
import com.randomappsinc.studentpicker.R;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class StudentChoosingActivity extends ActionBarActivity
{
    public static final String NO_STUDENTS = "You currently do not have any students in this list. " +
            "Why are you even here?";
    public static final String NO_CHOICES = "There aren't any students to choose from.";
    public static final String CHOSEN_STUDENT_DIALOG_TITLE = "And the (un)lucky winner is...";

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
        if (studentChoosingAdapter.getCount() == 0)
        {
            Toast.makeText(context, NO_CHOICES, Toast.LENGTH_SHORT).show();
        }
        else
        {
            final String randomStudent = studentChoosingAdapter.chooseStudentAtRandom(withReplacement.isChecked());
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);
            alertDialogBuilder.setTitle(CHOSEN_STUDENT_DIALOG_TITLE);
            alertDialogBuilder
                    .setMessage(randomStudent)
                            // Back button cancel dialog
                    .setCancelable(true)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.show();
        }
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
