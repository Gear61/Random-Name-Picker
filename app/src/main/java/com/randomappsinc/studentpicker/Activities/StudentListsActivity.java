package com.randomappsinc.studentpicker.Activities;

import android.content.Context;
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

import com.randomappsinc.studentpicker.Adapters.ListAdapter;
import com.randomappsinc.studentpicker.Misc.Constants;
import com.randomappsinc.studentpicker.Misc.PreferencesManager;
import com.randomappsinc.studentpicker.R;

public class StudentListsActivity extends ActionBarActivity
{
    public static final String EMPTY_STUDENT_LIST_MESSAGE = "Student list names cannot be blank.";
    public static final String NO_STUDENT_LISTS = "You currently do not have any student lists.";

    private Context context;
    private ListView studentLists;
    private EditText newListInput;
    private ListAdapter studentListsAdapter;
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
        setTitle(Constants.STUDENT_LISTS);

        studentLists = (ListView) findViewById(R.id.content_list);
        newListInput = (EditText) findViewById(R.id.item_name);
        noContent = (TextView) findViewById(R.id.no_content);
        noContent.setText(NO_STUDENT_LISTS);

        studentListsAdapter = new ListAdapter(context, false, false, noContent);
        studentLists.setAdapter(studentListsAdapter);
    }

    public void addItem(View view)
    {
        hideKeyboard();
        String newList = newListInput.getText().toString().trim();
        newListInput.setText("");
        if (newList.isEmpty())
        {
            Toast.makeText(context, EMPTY_STUDENT_LIST_MESSAGE, Toast.LENGTH_SHORT).show();
        }
        else if (PreferencesManager.get().getStudentLists().contains(newList))
        {
            Toast.makeText(context, "You already have a student list named \"" + newList + "\".",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            studentListsAdapter.addItem(newList);
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
