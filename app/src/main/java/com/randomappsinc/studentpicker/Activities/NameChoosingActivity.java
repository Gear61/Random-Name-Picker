package com.randomappsinc.studentpicker.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.Adapters.NameChoosingAdapter;
import com.randomappsinc.studentpicker.R;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class NameChoosingActivity extends AppCompatActivity
{
    @Bind(R.id.no_content) TextView noContent;
    @Bind(R.id.with_replacement) CheckBox withReplacement;
    @Bind(R.id.names_list) ListView namesList;
    @BindString(R.string.name_chosen) String nameChosenTitle;
    @BindString(R.string.list) String list;

    private NameChoosingAdapter NameChoosingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name_choosing);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String listName = intent.getStringExtra(NameListsActivity.LIST_NAME_KEY);
        setTitle(list + " "+ listName);

        NameChoosingAdapter = new NameChoosingAdapter(this, noContent, listName, namesList);
        namesList.setAdapter(NameChoosingAdapter);
    }

    @OnClick(R.id.choose)
    public void choose(View view)
    {
        if (NameChoosingAdapter.getCount() != 0)
        {
            String chosenName = NameChoosingAdapter.chooseStudentAtRandom(withReplacement.isChecked());
            new MaterialDialog.Builder(this)
                    .title(nameChosenTitle)
                    .content(chosenName)
                    .positiveText(android.R.string.yes)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the blank_menu; this adds items to the action bar if it is present.
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
        else if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
