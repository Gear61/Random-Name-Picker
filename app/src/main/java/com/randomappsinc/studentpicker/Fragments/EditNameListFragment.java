package com.randomappsinc.studentpicker.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.randomappsinc.studentpicker.Activities.MainActivity;
import com.randomappsinc.studentpicker.Adapters.EditNameListAdapter;
import com.randomappsinc.studentpicker.Misc.Utils;
import com.randomappsinc.studentpicker.Models.EditListEvent;
import com.randomappsinc.studentpicker.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by alexanderchiou on 10/18/15.
 */
public class EditNameListFragment extends Fragment {
    @Bind(R.id.item_name_input) EditText newNameInput;
    @Bind(R.id.no_content) TextView noContent;
    @Bind(R.id.content_list) ListView namesList;
    @Bind(R.id.parent) View parent;
    @Bind(R.id.plus_icon) ImageView plus;

    private EditNameListAdapter EditNameListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lists_with_add_content, container, false);
        ButterKnife.bind(this, rootView);
        newNameInput.setHint(R.string.name_hint);
        newNameInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        plus.setImageDrawable(new IconDrawable(getActivity(), FontAwesomeIcons.fa_plus).colorRes(R.color.white));

        Bundle bundle = getArguments();
        String listName = bundle.getString(MainActivity.LIST_NAME_KEY, "");
        noContent.setText(R.string.no_names);

        EditNameListAdapter = new EditNameListAdapter(getActivity(), noContent, listName);
        namesList.setAdapter(EditNameListAdapter);
        return rootView;
    }

    @OnClick(R.id.add_item)
    public void addItem(View view) {
        String newName = newNameInput.getText().toString().trim();
        newNameInput.setText("");
        if (newName.isEmpty()) {
            Utils.showSnackbar(parent, getString(R.string.blank_name));
        }
        else {
            EditNameListAdapter.addStudent(newName);
            EventBus.getDefault().post(new EditListEvent(EditListEvent.ADD, newName));
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // If this fragment is becoming visible
        if (isVisibleToUser) {
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
        if (item.getItemId() == android.R.id.home) {
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
