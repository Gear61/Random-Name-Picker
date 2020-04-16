package com.randomappsinc.studentpicker.listpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.editing.EditNameListActivity;
import com.randomappsinc.studentpicker.home.RenameListDialog;
import com.randomappsinc.studentpicker.utils.UIUtils;
import com.randomappsinc.studentpicker.views.SimpleDividerItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EditOptionsFragment extends Fragment
        implements ListOptionsAdapter.ItemSelectionListener, RenameListDialog.Listener {

    public static EditOptionsFragment getInstance(int listId) {
        EditOptionsFragment fragment = new EditOptionsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.LIST_ID_KEY, listId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(R.id.edit_list_options) RecyclerView editListOptions;

    private int listId;
    private RenameListDialog renameListDialog;
    private DataSource dataSource;
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.edit_list_fragment,
                container,
                false);
        listId = getArguments().getInt(Constants.LIST_ID_KEY);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataSource = new DataSource(getContext());
        editListOptions.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        editListOptions.setAdapter(new ListOptionsAdapter(
                getActivity(),
                this,
                R.array.edit_list_options,
                R.array.edit_list_icons));
        renameListDialog = new RenameListDialog(this, getContext());
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
                Intent intent = new Intent(getActivity(), EditNameListActivity.class);
                intent.putExtra(Constants.LIST_ID_KEY, listId);
                getActivity().startActivity(intent);
                break;
            case 1:
                renameListDialog.show(dataSource.getListName(listId));
                break;
        }
    }

    @Override
    public void onRenameListConfirmed(String newName) {
        dataSource.renameList(listId, newName);
        getActivity().setTitle(newName);
        UIUtils.showShortToast(R.string.list_renamed, getContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
