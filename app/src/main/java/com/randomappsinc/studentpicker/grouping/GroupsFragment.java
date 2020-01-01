package com.randomappsinc.studentpicker.grouping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.randomappsinc.studentpicker.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GroupsFragment extends Fragment {

    public static GroupsFragment getInstance() {
        return new GroupsFragment();
    }

    @BindView(R.id.no_groups) TextView noGroups;

    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_groups, container, false);
        unbinder = ButterKnife.bind(this,rootView);

        noGroups.setVisibility(View.VISIBLE);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
