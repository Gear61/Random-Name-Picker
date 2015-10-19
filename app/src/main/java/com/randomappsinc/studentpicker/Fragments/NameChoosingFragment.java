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
import com.randomappsinc.studentpicker.Misc.Utils;
import com.randomappsinc.studentpicker.Models.EditListEvent;
import com.randomappsinc.studentpicker.R;
import com.rey.material.widget.CheckBox;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by alexanderchiou on 10/18/15.
 */
public class NameChoosingFragment extends Fragment
{
    @Bind(R.id.no_content) TextView noContent;
    @Bind(R.id.with_replacement) CheckBox withReplacement;
    @Bind(R.id.names_list) ListView namesList;
    @BindString(R.string.name_chosen) String nameChosenTitle;

    private NameChoosingAdapter nameChoosingAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.name_choosing, container, false);
        ButterKnife.bind(this, rootView);
        Bundle bundle = getArguments();
        String listName = bundle.getString(NameListsActivity.LIST_NAME_KEY, "");
        nameChoosingAdapter = new NameChoosingAdapter(getActivity(), noContent, listName, namesList);
        namesList.setAdapter(nameChoosingAdapter);
        return rootView;
    }

    @OnClick(R.id.choose)
    public void choose(View view)
    {
        if (nameChoosingAdapter.getCount() != 0)
        {
            String chosenName = nameChoosingAdapter.chooseStudentAtRandom(withReplacement.isChecked());
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
            Utils.hideKeyboard(getActivity());
            getActivity().invalidateOptionsMenu();
        }
    }

    public void onEvent(EditListEvent event) {
        if (event.getEventType().equals(EditListEvent.ADD)) {
            nameChoosingAdapter.addName(event.getName());
        }
        else if (event.getEventType().equals(EditListEvent.REMOVE)) {
            nameChoosingAdapter.removeName(event.getName());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
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
            nameChoosingAdapter.resetStudents();
        }
        else if (item.getItemId() == android.R.id.home)
        {
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
