package com.randomappsinc.studentpicker.grouping;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.randomappsinc.studentpicker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

class GroupMakingSettingsViewHolder {

    @BindView(R.id.number_of_names_in_list) TextView numberOfNamesInList;
    @BindView(R.id.num_of_names_per_group) EditText namesPerGroup;
    @BindView(R.id.num_of_groups) EditText numGroups;

    private GroupMakingSettings settings;

    GroupMakingSettingsViewHolder(View view, GroupMakingSettings settings) {
        ButterKnife.bind(this, view);
        this.settings = settings;
        numberOfNamesInList.setText(numberOfNamesInList.getContext().getResources()
                .getString(R.string.grouping_settings_number_of_names_in_list, settings.getNameListSize()));
        namesPerGroup.setText(String.valueOf(settings.getNumOfNamesPerGroup()));
        numGroups.setText(String.valueOf(settings.getNumOfGroups()));
    }

    void refreshListSizeSetting() {
        numberOfNamesInList.setText(numberOfNamesInList.getContext()
                .getString(R.string.grouping_settings_number_of_names_in_list, settings.getNameListSize()));
    }

    void revertSettings() {
        namesPerGroup.setText(String.valueOf(settings.getNumOfNamesPerGroup()));
        namesPerGroup.clearFocus();
        numGroups.setText(String.valueOf(settings.getNumOfGroups()));
        numGroups.clearFocus();
    }

    void applySettings() {
        applyNumberOfNames();
        applyNumberOfGroups();
    }

    private void applyNumberOfNames() {
        String numOfNamesPerGroup = namesPerGroup.getText().toString();
        int inputtedNumber = numOfNamesPerGroup.isEmpty() ? 0 :
                Integer.parseInt(namesPerGroup.getText().toString());
        if (inputtedNumber <= 0) {
            namesPerGroup.setText(R.string.default_number_of_names_per_group);
            settings.setNumOfNamesPerGroup(settings.getNumOfNamesPerGroup());
        } else {
            namesPerGroup.setText(String.valueOf(inputtedNumber));
            settings.setNumOfNamesPerGroup(inputtedNumber);
        }
        namesPerGroup.clearFocus();
    }

    private void applyNumberOfGroups() {
        String numOfGroup = numGroups.getText().toString();
        int inputtedNumber = numOfGroup.isEmpty() ? 0 :
                Integer.parseInt(numGroups.getText().toString().trim());
        if (inputtedNumber <= 0) {
            numGroups.setText(R.string.default_number_of_groups);
            settings.setNumOfGroups(settings.getNumOfGroups());
        } else {
            numGroups.setText(String.valueOf(inputtedNumber));
            settings.setNumOfGroups(inputtedNumber);
        }
        numGroups.clearFocus();
    }
}
