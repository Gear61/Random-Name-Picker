package com.randomappsinc.studentpicker.grouping;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.randomappsinc.studentpicker.R;

public class GroupsFragment extends Fragment {

    public static GroupsFragment getInstance() {
        return new GroupsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }
}
