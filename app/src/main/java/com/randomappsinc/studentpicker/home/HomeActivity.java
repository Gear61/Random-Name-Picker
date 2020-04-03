package com.randomappsinc.studentpicker.home;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.common.StandardActivity;
import com.randomappsinc.studentpicker.utils.UIUtils;
import com.randomappsinc.studentpicker.views.BottomNavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends StandardActivity implements BottomNavigationView.Listener {

    @BindView(R.id.bottom_navigation) BottomNavigationView bottomNavigation;
    @BindView(R.id.bottom_sheet) View bottomSheet;

    private HomepageFragmentController navigationController;
    protected BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        navigationController = new HomepageFragmentController(getSupportFragmentManager(), R.id.container);
        navigationController.loadHomeInitially();

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {}

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Set state to HIDDEN on slideOffset being 0 (fully hidden),
                // because if you expand/collapse it super fast, the state machine is broken
                if (slideOffset == 0) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
                bottomNavigation.onAddSheetSlideOffset(slideOffset);
            }
        });
        bottomNavigation.setListener(this);
    }

    @Override
    public void onNavItemSelected(int viewId) {
        UIUtils.hideKeyboard(this);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        navigationController.onNavItemSelected(viewId);

        switch (viewId) {
            case R.id.home:
                setTitle(R.string.app_name);
                break;
            case R.id.settings:
                setTitle(R.string.settings);
                break;
        }
    }

    @Override
    public void onAddOptionsExpanded() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onAddOptionsContracted() {
        hideBottomSheet();
    }

    @OnClick(R.id.sheet_create_name_list)
    public void createNameList() {
        hideBottomSheet();
    }

    @OnClick(R.id.sheet_import_from_txt)
    public void importFromTxt() {
        hideBottomSheet();
    }

    private void hideBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomNavigation.setIsAddSheetExpanded(false);
    }
}
