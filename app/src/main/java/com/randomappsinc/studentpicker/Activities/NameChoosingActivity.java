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

import com.randomappsinc.studentpicker.Adapters.NameChoosingAdapter;
import com.randomappsinc.studentpicker.R;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class NameChoosingActivity extends ActionBarActivity
{
    public static final String NO_NAMES = "You currently do not have any names in this list. " +
            "Why are you even here?";
    public static final String NO_CHOICES = "There aren't any names to choose from.";
    public static final String CHOSEN_NAME_DIALOG_TITLE = "And the (un)lucky winner is...";

    private Context context;
    private ListView students;
    private NameChoosingAdapter nameChoosingAdapter;
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
        listName = intent.getStringExtra(NameListsActivity.LIST_NAME_KEY);
        setTitle("List: " + listName);

        students = (ListView) findViewById(R.id.students_list);
        noContent = (TextView) findViewById(R.id.no_content);
        noContent.setText(NO_NAMES);
        withReplacement = (CheckBox) findViewById(R.id.with_replacement);

        nameChoosingAdapter = new NameChoosingAdapter(context, noContent, listName, students);
        students.setAdapter(nameChoosingAdapter);
    }

    public void choose(View view)
    {
        if (nameChoosingAdapter.getCount() == 0)
        {
            Toast.makeText(context, NO_CHOICES, Toast.LENGTH_SHORT).show();
        }
        else
        {
            final String randomStudent = nameChoosingAdapter.chooseStudentAtRandom(withReplacement.isChecked());
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);
            alertDialogBuilder.setTitle(CHOSEN_NAME_DIALOG_TITLE);
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
        getMenuInflater().inflate(R.menu.name_choosing_menu, menu);
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
            nameChoosingAdapter.resetStudents();
        }
        return super.onOptionsItemSelected(item);
    }
}
