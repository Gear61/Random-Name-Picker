package com.randomappsinc.studentpicker.models;

import android.view.View;
import android.widget.EditText;

import com.randomappsinc.studentpicker.R;
import com.rey.material.widget.CheckBox;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 11/9/15.
 */
public class ChoosingSettingsViewHolder {
    @Bind(R.id.presentation_mode) CheckBox presentationMode;
    @Bind(R.id.with_replacement) CheckBox withReplacement;
    @Bind(R.id.automatic_tts) CheckBox automaticTts;
    @Bind(R.id.show_as_list) CheckBox showAsList;
    @Bind(R.id.num_people_chosen) EditText numChosen;

    private ChoosingSettings settings;

    public ChoosingSettingsViewHolder(View view, ChoosingSettings settings) {
        ButterKnife.bind(this, view);

        this.settings = settings;
        presentationMode.setCheckedImmediately(settings.getPresentationMode());
        withReplacement.setCheckedImmediately(settings.getWithReplacement());
        automaticTts.setCheckedImmediately(settings.getAutomaticTts());
        showAsList.setCheckedImmediately(settings.getShowAsList());
        numChosen.setText(String.valueOf(settings.getNumNamesToChoose()));
    }

    public void revertSettings() {
        presentationMode.setCheckedImmediately(settings.getPresentationMode());
        withReplacement.setCheckedImmediately(settings.getWithReplacement());
        automaticTts.setCheckedImmediately(settings.getAutomaticTts());
        showAsList.setCheckedImmediately(settings.getShowAsList());
        numChosen.setText(String.valueOf(settings.getNumNamesToChoose()));
        numChosen.clearFocus();
    }

    public void applySettings() {
        settings.setPresentationMode(presentationMode.isChecked());
        settings.setWithReplacement(withReplacement.isChecked());
        settings.setAutomaticTts(automaticTts.isChecked());
        settings.setShowAsList(showAsList.isChecked());
        String numChosenText = numChosen.getText().toString();
        if (numChosenText.isEmpty()) {
            numChosen.setText("1");
            settings.setNumNamesToChoose(1);
        } else {
            int userNumNames = Integer.parseInt(numChosen.getText().toString());
            if (userNumNames <= 0) {
                numChosen.setText("1");
                settings.setNumNamesToChoose(1);
            } else {
                settings.setNumNamesToChoose(userNumNames);
            }
        }
        numChosen.clearFocus();
    }
}
