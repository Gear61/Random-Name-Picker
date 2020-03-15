package com.randomappsinc.studentpicker.grouping;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnTextChanged;

class GroupMakingSettingsViewHolder {

    @BindView(R.id.number_of_names_in_list) TextView numberOfNamesInList;
    @BindView(R.id.num_of_names_per_group) EditText namesPerGroup;
    @BindView(R.id.num_of_groups) EditText numGroups;
    @BindView(R.id.groups_warning_message) TextView warningMessage;
    @BindView(R.id.autocomplete) CheckBox autocomplete;

    private GroupMakingSettings settings;
    private MaterialDialog dialog;

    GroupMakingSettingsViewHolder(MaterialDialog dialog, GroupMakingSettings settings) {
        ButterKnife.bind(this, dialog.getCustomView());
        this.settings = settings;
        this.dialog = dialog;
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

    @OnCheckedChanged(R.id.autocomplete)
    void onAutocompleteSelected(boolean checked) {
        String numberOfGroupsInput = numGroups.getText().toString().trim();
        if (checked && isInputValid(numberOfGroupsInput)) {
            namesPerGroup.setText(getMatchingNumber(numberOfGroupsInput));
        }
    }

    @OnTextChanged(value = R.id.num_of_names_per_group, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterNamesPerGroupChanged() {
        String namesPerGroupInput = namesPerGroup.getText().toString().trim();
        boolean isInputValid = isInputValid(namesPerGroupInput);
        if (namesPerGroup.isFocused() && isInputValid && autocomplete.isChecked()) {
            numGroups.setText(getMatchingNumber(namesPerGroupInput));
        }
        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(isInputValid);
        maybeShowUnevenGroupsWarning();
    }

    @OnTextChanged(value = R.id.num_of_groups, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterGroupNumChanged() {
        String numberOfGroupsInput = numGroups.getText().toString().trim();
        boolean isInputValid = isInputValid(numberOfGroupsInput);
        if (numGroups.isFocused() && isInputValid && autocomplete.isChecked()) {
            namesPerGroup.setText(getMatchingNumber(numberOfGroupsInput));
        }
        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(isInputValid);
        maybeShowUnevenGroupsWarning();
    }

    // Given the number of names per group or number of groups, returns the corresponding number to use all the names
    private String getMatchingNumber(String inputtedNumber) {
        int newNumber = Integer.parseInt(inputtedNumber);
        double offset = (double) settings.getNameListSize() / newNumber;
        int matchingNumber = (int) Math.ceil(offset);
        return String.valueOf(matchingNumber);
    }

    private void maybeShowUnevenGroupsWarning() {
        if (!areInputsValid()) {
            return;
        }
        String namesPerGroupInput = namesPerGroup.getText().toString().trim();
        String numberOfGroupsInput = numGroups.getText().toString().trim();
        int namesNumber = Integer.parseInt(namesPerGroupInput);
        int groupsNumber = Integer.parseInt(numberOfGroupsInput);
        int totalNumber = namesNumber * groupsNumber;
        if (totalNumber > settings.getNameListSize()) {
            warningMessage.setVisibility(View.VISIBLE);
        } else {
            warningMessage.setVisibility(View.GONE);
        }
    }

    // Returns true if the inputted text is a positive number
    private boolean isInputValid(String inputText) {
        return areInputsValid() ||
                (autocomplete.isChecked() && inputText.length() > 0 && Integer.parseInt(inputText) > 0);
    }

    private boolean areInputsValid() {
        String namesPerGroupInput = namesPerGroup.getText().toString().trim();
        String numberOfGroupsInput = numGroups.getText().toString().trim();
        return ((namesPerGroupInput.length() > 0 && numberOfGroupsInput.length() > 0)
                && (Integer.parseInt(namesPerGroupInput) > 0 && Integer.parseInt(numberOfGroupsInput) > 0));
    }
}
