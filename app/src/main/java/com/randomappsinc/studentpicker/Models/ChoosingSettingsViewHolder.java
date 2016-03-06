package com.randomappsinc.studentpicker.Models;

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
    @Bind(R.id.presentation_mode) public CheckBox presentationMode;
    @Bind(R.id.with_replacement) public CheckBox withReplacement;
    @Bind(R.id.num_people_chosen) public EditText numChosen;

    public ChoosingSettingsViewHolder(View view, ChoosingSettings settings) {
        ButterKnife.bind(this, view);

        presentationMode.setCheckedImmediately(settings.getPresentationMode());
        withReplacement.setCheckedImmediately(settings.getWithReplacement());
        numChosen.setText(String.valueOf(settings.getNumNamesToChoose()));
    }
}
