package com.randomappsinc.studentpicker.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.randomappsinc.studentpicker.Adapters.NameChoosingAdapter;
import com.randomappsinc.studentpicker.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class NameChoosingActivity extends AppCompatActivity
{
    public static final String NO_CHOICES = "There aren't any names to choose from.";
    public static final String CHOSEN_NAME_DIALOG_TITLE = "And the (un)lucky winner is...";

    @Bind(R.id.no_content) TextView noContent;
    @Bind(R.id.with_replacement) CheckBox withReplacement;
    @Bind(R.id.names_list) ListView namesList;

    private NameChoosingAdapter NameChoosingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name_choosing);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String listName = intent.getStringExtra(NameListsActivity.LIST_NAME_KEY);
        setTitle("List: " + listName);

        NameChoosingAdapter = new NameChoosingAdapter(this, noContent, listName, namesList);
        namesList.setAdapter(NameChoosingAdapter);
    }

    @OnClick(R.id.choose)
    public void choose(View view)
    {
        if (NameChoosingAdapter.getCount() == 0)
        {
            Toast.makeText(this, NO_CHOICES, Toast.LENGTH_SHORT).show();
        }
        else
        {
            final String randomStudent = NameChoosingAdapter.chooseStudentAtRandom(withReplacement.isChecked());
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(CHOSEN_NAME_DIALOG_TITLE);
            alertDialogBuilder
                    .setMessage(randomStudent)
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
        if (item.getItemId() == R.id.reset)
        {
            NameChoosingAdapter.resetStudents();
        }
        return super.onOptionsItemSelected(item);
    }
}
