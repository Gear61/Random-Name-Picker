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
import com.randomappsinc.studentpicker.payments.BuyPremiumActivity;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.views.SimpleDividerItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PremiumOptionsFragment extends Fragment implements ListOptionsAdapter.ItemSelectionListener {

    public static PremiumOptionsFragment getInstance(int listId) {
        PremiumOptionsFragment fragment = new PremiumOptionsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.LIST_ID_KEY, listId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(R.id.recycler_view) RecyclerView premiumOptions;

    private int listId;
    private PreferencesManager preferencesManager;
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
        preferencesManager = new PreferencesManager(getContext());
        premiumOptions.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        premiumOptions.setAdapter(new ListOptionsAdapter(
                getActivity(),
                this,
                R.array.premium_options,
                R.array.premium_options_icons));
    }

    @Override
    public void onItemClick(int position) {
        if (preferencesManager.isOnFreeVersion()) {
            Intent intent = new Intent(getActivity(), BuyPremiumActivity.class);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
        }

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
