package com.randomappsinc.studentpicker.models;

import android.util.TypedValue;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.utils.PreferencesManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SetTextSizeViewHolder {

    @BindView(R.id.text_size_slider) public SeekBar textSizeSlider;
    @BindView(R.id.sample_text) public TextView sampleText;

    public SetTextSizeViewHolder(View view) {
        ButterKnife.bind(this, view);
        textSizeSlider.setOnSeekBarChangeListener(sizeSetListener);
        textSizeSlider.setProgress(PreferencesManager.get().getPresentationTextSize() - 1);
        textSizeSlider.jumpDrawablesToCurrentState();
    }

    SeekBar.OnSeekBarChangeListener sizeSetListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            sampleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, (progress + 1) * 8);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    public void revertSetting() {
        textSizeSlider.setProgress(PreferencesManager.get().getPresentationTextSize() - 1);
        textSizeSlider.jumpDrawablesToCurrentState();
    }
}
