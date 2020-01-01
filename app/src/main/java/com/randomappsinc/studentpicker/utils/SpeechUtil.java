package com.randomappsinc.studentpicker.utils;

import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;

import androidx.annotation.Nullable;

import com.randomappsinc.studentpicker.R;

import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class SpeechUtil {
    public static Intent openSpeechToTextDialog(Context context) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.list_name_input_speech_message));
        return intent;
    }

    @Nullable
    public static String processSpeechResults(int resultCode, Intent data, Context context) {
        if (resultCode != RESULT_OK || data == null) {
            return null;
        }

        List<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        if (result == null || result.isEmpty()) {
            UIUtils.showLongToast(R.string.speech_unrecognized, context);
            return null;
        }
        return result.get(0);
    }
}
