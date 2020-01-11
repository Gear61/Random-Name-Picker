package com.randomappsinc.studentpicker.grouping;

import android.view.View;
import android.widget.EditText;

import com.randomappsinc.studentpicker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

class GroupingSettingsViewHolder {

    @BindView(R.id.num_of_names_in_group) EditText namesInGroup;
    @BindView(R.id.num_of_groups) EditText numGroups;

    private GroupingSettings settings;

    GroupingSettingsViewHolder(View view, GroupingSettings settings) {
        ButterKnife.bind(this, view);
        this.settings = settings;
    }

    void revertSettings() {
        namesInGroup.setText(String.valueOf(settings.getNumOfNamesInGroup()));
        namesInGroup.clearFocus();
        numGroups.setText(String.valueOf(settings.getNumOfGroups()));
        numGroups.clearFocus();
    }

    void applySettings() {
        applyNumberOfNames(namesInGroup);
        applyNumberOfGroups(numGroups);
    }

    private void applyNumberOfNames(EditText namesInGroup) {
        String numOfNamesInGroup = namesInGroup.getText().toString();
        if (numOfNamesInGroup.isEmpty()) {
            namesInGroup.setText("2");
            settings.setNumOfNamesInGroup(2);
        } else {
            int userNumNames = Integer.parseInt(namesInGroup.getText().toString());
            if (userNumNames <= 0) {
                namesInGroup.setText("1");
                settings.setNumOfNamesInGroup(1);
            } else {
                settings.setNumOfNamesInGroup(userNumNames);
            }
        }
        namesInGroup.clearFocus();
    }

    private void applyNumberOfGroups(EditText numGroups) {
        String numOfGroup = numGroups.getText().toString();
        if (numOfGroup.isEmpty()) {
            numGroups.setText("1");
            settings.setNumOfGroups(1);
        } else {
            int userNumGroup = Integer.parseInt(numGroups.getText().toString());
            if (userNumGroup <= 0) {
                numGroups.setText("1");
                settings.setNumOfGroups(1);
            } else {
                settings.setNumOfGroups(userNumGroup);
            }
        }
        numGroups.clearFocus();
    }
}
