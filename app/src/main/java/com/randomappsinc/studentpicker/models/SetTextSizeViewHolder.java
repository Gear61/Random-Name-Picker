package com.randomappsinc.studentpicker.models;

import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.rey.material.widget.Slider;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 3/9/16.
 */
public class SetTextSizeViewHolder {
    @Bind(R.id.text_size_slider) public Slider textSizeSlider;
    @Bind(R.id.sample_text) public TextView sampleText;

    public SetTextSizeViewHolder(View view) {
        ButterKnife.bind(this, view);
        textSizeSlider.setOnPositionChangeListener(sizeSetListener);
        textSizeSlider.setValue((float) PreferencesManager.get().getPresentationTextSize(), false);
    }

    Slider.OnPositionChangeListener sizeSetListener = new Slider.OnPositionChangeListener() {
        @Override
        public void onPositionChanged(Slider view, boolean fromUser, float oldPos,
                                      float newPos, int oldValue, int newValue) {
            sampleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, newValue * 8);
        }
    };

    public void revertSetting() {
        textSizeSlider.setValue((float) PreferencesManager.get().getPresentationTextSize(), false);
    }
}
