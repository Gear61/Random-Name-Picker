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
import com.randomappsinc.studentpicker.editing.EditNameListActivity;
import com.randomappsinc.studentpicker.views.SimpleDividerItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EditOptionsFragment extends Fragment implements ListOptionsAdapter.ItemSelectionListener {

    public static EditOptionsFragment getInstance(int listId) {
        EditOptionsFragment fragment = new EditOptionsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.LIST_ID_KEY, listId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(R.id.edit_list_options) RecyclerView editListOptions;

    private int listId;
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
        editListOptions.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        editListOptions.setAdapter(new ListOptionsAdapter(
                getActivity(),
                this,
                R.array.edit_list_options,
                R.array.edit_list_icons));
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
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
