package com.randomappsinc.studentpicker.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SpeechToTextManager implements RecognitionListener {
    public interface Listener {
        void onTextSpoken(String spokenText);
    }

    @BindView(R.id.voice_icon) TextView voiceIcon;
    @BindView(R.id.message) TextView message;
    @BindView(R.id.try_again) View tryAgain;

    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private Listener listener;
    private MaterialDialog dialog;

    public SpeechToTextManager(Context context, Listener listener) {
        this.listener = listener;
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().getLanguage());
        speechRecognizer.setRecognitionListener(this);

        dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.voice_recognizble_layout, false)
                .build();
        dialog.getWindow().getAttributes().windowAnimations = R.style.speech_dialog_animation;
        ButterKnife.bind(this, dialog);
    }

    public void startSpeechToTextFlow() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
        speechRecognizer.startListening(speechRecognizerIntent);
        changeUIStateToListening();
    }

    @Override
    public void onBeginningOfSpeech() {
        message.setText(R.string.spinning_icon_while_waiting);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    @Override
    public void onEndOfSpeech() {
    }

    @Override
    public void onError(int error) {
        if (error == SpeechRecognizer.ERROR_NO_MATCH) {
            changeUIStateToRetry();
        }
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
    }

    @Override
    public void onResults(Bundle results) {
        List<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches != null && !matches.isEmpty()) {
            listener.onTextSpoken(matches.get(0));
            dialog.dismiss();
        } else {
            changeUIStateToRetry();
        }
    }

    @Override
    public void onRmsChanged(float rmsdB) {
    }

    private void changeUIStateToListening() {
        voiceIcon.setText(R.string.voice_icon_recognizer_white);
        voiceIcon.setBackgroundResource(R.drawable.recognizable_voice_blue);
        message.setText(R.string.list_name_input_speech_message);
        tryAgain.setVisibility(View.GONE);
    }

    private void changeUIStateToRetry() {
        voiceIcon.setText(R.string.voice_icon_recognizer_blue);
        voiceIcon.setBackgroundResource(R.drawable.recognizable_voice_error);
        message.setText(R.string.did_not_catch_speech);
        tryAgain.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.try_again)
    void tryAgain() {
        startSpeechToTextFlow();
    }

    public void cleanUp() {
        speechRecognizer.destroy();
    }
}