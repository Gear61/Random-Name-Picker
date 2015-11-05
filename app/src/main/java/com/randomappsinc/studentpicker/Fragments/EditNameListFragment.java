package com.randomappsinc.studentpicker.Fragments;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.randomappsinc.studentpicker.Activities.NameListsActivity;
import com.randomappsinc.studentpicker.Adapters.NamesAdapter;
import com.randomappsinc.studentpicker.Misc.Utils;
import com.randomappsinc.studentpicker.Models.EditListEvent;
import com.randomappsinc.studentpicker.R;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by alexanderchiou on 10/18/15.
 */
public class EditNameListFragment extends Fragment
{
    @Bind(R.id.item_name_input) EditText newNameInput;
    @Bind(R.id.no_content) TextView noContent;
    @Bind(R.id.content_list) ListView namesList;
    @Bind(R.id.coordinator_layout) CoordinatorLayout parent;

    @BindString(R.string.blank_name) String blankName;
    @BindString(R.string.no_names) String emptyList;
    @BindString(R.string.name_hint) String nameHint;

    private NamesAdapter NamesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lists_with_add_content, container, false);
        ButterKnife.bind(this, rootView);
        newNameInput.setHint(nameHint);
        newNameInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        Bundle bundle = getArguments();
        String listName = bundle.getString(NameListsActivity.LIST_NAME_KEY, "");
        noContent.setText(emptyList);

        NamesAdapter = new NamesAdapter(getActivity(), noContent, listName);
        namesList.setAdapter(NamesAdapter);
        return rootView;
    }

    @OnClick(R.id.add_item)
    public void addItem(View view)
    {
        String newName = newNameInput.getText().toString().trim();
        newNameInput.setText("");
        if (newName.isEmpty())
        {
            Snackbar.make(parent, blankName, Snackbar.LENGTH_LONG).show();
        }
        else
        {
            NamesAdapter.addStudent(newName);
            EventBus.getDefault().post(new EditListEvent(EditListEvent.ADD, newName));
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
        inflater.inflate(R.menu.blank_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            Utils.hideKeyboard(getActivity());
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
