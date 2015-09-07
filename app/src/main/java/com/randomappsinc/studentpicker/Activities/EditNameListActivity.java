package com.randomappsinc.studentpicker.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.randomappsinc.studentpicker.Adapters.NamesAdapter;
import com.randomappsinc.studentpicker.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class EditNameListActivity extends AppCompatActivity
{
    public static final String EMPTY_NAME_MESSAGE = "Names cannot be blank.";
    public static final String NO_NAMES = "You currently do not have any names in this list.";
    public static final String NAME_HINT = "Name";

    @Bind(R.id.item_name_input) EditText newStudentInput;
    @Bind(R.id.no_content) TextView noContent;
    @Bind(R.id.content_list) ListView namesList;

    private NamesAdapter NamesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lists_with_add_content);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String listName = intent.getStringExtra(NameListsActivity.LIST_NAME_KEY);
        setTitle("Editing: " + listName);

        newStudentInput.setHint(NAME_HINT);
        newStudentInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        noContent.setText(NO_NAMES);

        NamesAdapter = new NamesAdapter(this, noContent, listName);
        namesList.setAdapter(NamesAdapter);
    }

    @OnClick(R.id.add_item)
    public void addItem(View view)
    {
        String newStudent = newStudentInput.getText().toString().trim();
        newStudentInput.setText("");
        if (newStudent.isEmpty())
        {
            Toast.makeText(this, EMPTY_NAME_MESSAGE, Toast.LENGTH_SHORT).show();
        }
        else
        {
            NamesAdapter.addStudent(newStudent);
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