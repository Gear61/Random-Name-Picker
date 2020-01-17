package com.randomappsinc.studentpicker.grouping;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.randomappsinc.studentpicker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

class GroupingSettingsViewHolder {

    @BindView(R.id.number_of_names_in_list) TextView numberOfNamesInList;
    @BindView(R.id.num_of_names_per_group) EditText namesPerGroup;
    @BindView(R.id.num_of_groups) EditText numGroups;

    private Context context;
    private GroupingSettings settings;

    GroupingSettingsViewHolder(View view, Context context, GroupingSettings settings) {
        ButterKnife.bind(this, view);
        this.context = context;
        this.settings = settings;
        numberOfNamesInList.setText(
                context.getResources().getString(R.string.grouping_settings_number_of_names_in_list, 0));
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
            settings.setNumOfNamesPerGroup(context.getResources().getInteger(R.integer.default_number_of_names_per_group));
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
            settings.setNumOfGroups(context.getResources().getInteger(R.integer.default_number_of_groups));
        } else {
            numGroups.setText(String.valueOf(inputtedNumber));
            settings.setNumOfGroups(inputtedNumber);
        }
        numGroups.clearFocus();
    }
}
