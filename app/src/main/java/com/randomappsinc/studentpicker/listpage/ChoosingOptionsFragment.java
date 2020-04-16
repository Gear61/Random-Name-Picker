package com.randomappsinc.studentpicker.listpage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.views.SimpleDividerItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChoosingOptionsFragment extends Fragment implements ListOptionsAdapter.ItemSelectionListener {

    public static ChoosingOptionsFragment getInstance(int setId) {
        ChoosingOptionsFragment fragment = new ChoosingOptionsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.LIST_ID_KEY, setId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(R.id.choosing_options) RecyclerView learnSetOptions;

    private int listId;
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.choosing_options_fragment,
                container,
                false);
        listId = getArguments().getInt(Constants.LIST_ID_KEY);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        learnSetOptions.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        learnSetOptions.setAdapter(new ListOptionsAdapter(
                getActivity(),
                this,
                R.array.choosing_options,
                R.array.choosing_options_icons));
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
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
