package com.randomappsinc.studentpicker.listpage;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.choosing.NameChoosingActivity;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.grouping.GroupMakingActivity;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChoosingOptionsFragment extends Fragment implements ListOptionsAdapter.ItemSelectionListener {

    static ChoosingOptionsFragment getInstance(int listId) {
        ChoosingOptionsFragment fragment = new ChoosingOptionsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.LIST_ID_KEY, listId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(R.id.recycler_view) RecyclerView choosingOptions;
    @BindDrawable(R.drawable.line_divider) Drawable lineDivider;

    private int listId;
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.simple_vertical_recyclerview,
                container,
                false);
        listId = getArguments().getInt(Constants.LIST_ID_KEY);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DividerItemDecoration itemDecorator =
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(lineDivider);
        choosingOptions.addItemDecoration(itemDecorator);
        choosingOptions.setAdapter(new ListOptionsAdapter(
                getActivity(),
                this,
                R.array.choosing_options,
                R.array.choosing_options_icons));
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
                Intent choosingIntent = new Intent(getActivity(), NameChoosingActivity.class);
                choosingIntent.putExtra(Constants.LIST_ID_KEY, listId);
                getActivity().startActivity(choosingIntent);
                break;
            case 1:
                Intent groupsIntent = new Intent(getActivity(), GroupMakingActivity.class);
                groupsIntent.putExtra(Constants.LIST_ID_KEY, listId);
                getActivity().startActivity(groupsIntent);
                break;
        }

        getActivity().overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
