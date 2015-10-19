package com.randomappsinc.studentpicker.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.Activities.NameListsActivity;
import com.randomappsinc.studentpicker.Adapters.NameChoosingAdapter;
import com.randomappsinc.studentpicker.R;
import com.rey.material.widget.CheckBox;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alexanderchiou on 10/18/15.
 */
public class NameChoosingFragment extends Fragment
{
    @Bind(R.id.no_content) TextView noContent;
    @Bind(R.id.with_replacement) CheckBox withReplacement;
    @Bind(R.id.names_list) ListView namesList;
    @BindString(R.string.name_chosen) String nameChosenTitle;

    private NameChoosingAdapter NameChoosingAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.name_choosing, container, false);
        ButterKnife.bind(this, rootView);
        Bundle bundle = getArguments();
        String listName = bundle.getString(NameListsActivity.LIST_NAME_KEY, "");
        NameChoosingAdapter = new NameChoosingAdapter(getActivity(), noContent, listName, namesList);
        namesList.setAdapter(NameChoosingAdapter);
        return rootView;
    }

    @OnClick(R.id.choose)
    public void choose(View view)
    {
        if (NameChoosingAdapter.getCount() != 0)
        {
            String chosenName = NameChoosingAdapter.chooseStudentAtRandom(withReplacement.isChecked());
            new MaterialDialog.Builder(getActivity())
                    .title(nameChosenTitle)
                    .content(chosenName)
                    .positiveText(android.R.string.yes)
                    .show();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // If this fragment is becoming visible
        if (isVisibleToUser)
        {
            getActivity().invalidateOptionsMenu();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.name_choosing_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
