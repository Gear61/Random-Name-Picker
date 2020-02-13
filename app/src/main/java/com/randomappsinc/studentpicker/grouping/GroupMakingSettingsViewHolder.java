package com.randomappsinc.studentpicker.grouping;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.randomappsinc.studentpicker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

class GroupMakingSettingsViewHolder implements TextWatcher {

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

        namesPerGroup.addTextChangedListener(this);
        numGroups.addTextChangedListener(this);
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable editable) {
        if (isEditableAndFocused(editable, namesPerGroup)) {
            numGroups.setText(getOffset(namesPerGroup));
        } else if (isEditableAndFocused(editable, numGroups)) {
            namesPerGroup.setText(getOffset(numGroups));
        }
    }

    private String getOffset(EditText editText) {
        String inputtedNumber = editText.getText().toString().trim();
        int newNumber = (inputtedNumber.isEmpty()) ? 0 :
                Integer.parseInt(inputtedNumber);
        if (newNumber <= 0) {
            newNumber = editText.getContext().getResources()
                    .getInteger(R.integer.default_number_of_names_per_group);
        }
        int offset = (int) Math.ceil((double) settings.getNameListSize() / newNumber);
        return String.valueOf(offset);
    }

    private boolean isEditableAndFocused(Editable editable, EditText editText) {
        return editable == editText.getEditableText() && editText.isFocused();
    }
}
