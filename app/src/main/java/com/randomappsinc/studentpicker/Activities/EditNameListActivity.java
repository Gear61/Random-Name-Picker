package com.randomappsinc.studentpicker.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.randomappsinc.studentpicker.Adapters.NamesAdapter;
import com.randomappsinc.studentpicker.Misc.Utils;
import com.randomappsinc.studentpicker.R;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class EditNameListActivity extends AppCompatActivity
{
    @Bind(R.id.item_name_input) EditText newStudentInput;
    @Bind(R.id.no_content) TextView noContent;
    @Bind(R.id.content_list) ListView namesList;
    @Bind(R.id.coordinator_layout) CoordinatorLayout parent;

    @BindString(R.string.blank_name) String blankName;
    @BindString(R.string.empty_list) String emptyList;
    @BindString(R.string.name_hint) String nameHint;
    @BindString(R.string.editing) String editing;

    private NamesAdapter NamesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lists_with_add_content);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String listName = intent.getStringExtra(NameListsActivity.LIST_NAME_KEY);
        setTitle(editing + " " + listName);

        newStudentInput.setHint(nameHint);
        newStudentInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        noContent.setText(emptyList);

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
            Snackbar.make(parent, blankName, Snackbar.LENGTH_LONG).show();
        }
        else
        {
            NamesAdapter.addStudent(newStudent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.blank_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            Utils.hideKeyboard(this);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}