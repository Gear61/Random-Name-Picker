package com.randomappsinc.studentpicker.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;

import com.randomappsinc.studentpicker.R;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BottomNavigationView extends FrameLayout {

    private static final float ADD_BUTTON_ROTATION_ANGLE = 45.0f;

    public interface Listener {
        void onNavItemSelected(@IdRes int viewId);

        void onAddOptionsExpanded();

        void onAddOptionsContracted();
    }

    @BindView(R.id.home) TextView homeButton;
    @BindView(R.id.add) TextView addButton;
    @BindView(R.id.settings) TextView settingsButton;

    @BindColor(R.color.bottom_navigation_item_color) int itemColor;
    @BindColor(R.color.app_blue) int blue;

    private Listener listener;
    private TextView currentlySelected;
    private boolean isAddSheetExpanded = false;

    public BottomNavigationView(Context context) {
        this(context, null, 0);
    }

    public BottomNavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomNavigationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(getContext(), R.layout.bottom_navigation, this);
        ButterKnife.bind(this);
        currentlySelected = homeButton;
        homeButton.setTextColor(blue);
        settingsButton.setTextColor(itemColor);
    }

    public void setCurrentlySelected(@IdRes int currentlySelected) {
        if (currentlySelected == R.id.home) {
            this.currentlySelected = homeButton;
            homeButton.setTextColor(blue);
            settingsButton.setTextColor(itemColor);
        } else if (currentlySelected == R.id.settings) {
            this.currentlySelected.setTextColor(itemColor);
            this.currentlySelected = settingsButton;
            settingsButton.setTextColor(blue);
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @OnClick(R.id.home)
    public void onHomeClicked() {
        if (currentlySelected == homeButton) {
            return;
        }

        currentlySelected.setTextColor(itemColor);
        currentlySelected = homeButton;
        homeButton.setTextColor(blue);
        listener.onNavItemSelected(R.id.home);
    }

    @OnClick(R.id.add)
    public void onAddClicked() {
        if (isAddSheetExpanded) {
            listener.onAddOptionsContracted();
        } else {
            listener.onAddOptionsExpanded();
        }
        isAddSheetExpanded = !isAddSheetExpanded;
    }

    public void setIsAddSheetExpanded(boolean isExpanded) {
        isAddSheetExpanded = isExpanded;
    }

    public void onAddSheetSlideOffset(float offset) {
        // If the keyboard opens while the bottom sheet is closing, the height change makes the offset weird
        float rotation = (Float.isNaN(offset) || Float.isInfinite(offset))
                ? 0
                : ADD_BUTTON_ROTATION_ANGLE * offset;
        addButton.setRotation(rotation);

        if (offset == 0) {
            isAddSheetExpanded = false;
        }
    }

    @OnClick(R.id.settings)
    public void onSettingsClicked() {
        if (currentlySelected == settingsButton) {
            return;
        }

        currentlySelected.setTextColor(itemColor);
        currentlySelected = settingsButton;
        settingsButton.setTextColor(blue);
        listener.onNavItemSelected(R.id.settings);
    }
}
