package com.randomappsinc.studentpicker.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.joanzapata.iconify.widget.IconTextView;
import com.randomappsinc.studentpicker.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VoiceRecognizerDialogFragment extends DialogFragment implements RecognitionListener {

    public interface VoiceRecognizerInterface {
        void spokenText(String spokenText);
    }

    @BindView(R.id.voice_icon) IconTextView voiceIcon;
    @BindView(R.id.message) IconTextView message;
    @BindView(R.id.try_again) TextView tryAgain;

    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private Context context;
    private VoiceRecognizerInterface signal;

    public VoiceRecognizerDialogFragment(Context context, VoiceRecognizerInterface signal) {
        this.context = context;
        this.signal = signal;
    }

    public VoiceRecognizerDialogFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        startListening();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.voice_recognizble_layout, container, false);
        ButterKnife.bind(this, view);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.SpeechDialogAnimation;

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en");
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
        mSpeechRecognizer.setRecognitionListener(this);
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        return view;
    }

    private void startListening() {
        mSpeechRecognizer.setRecognitionListener(this);
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        changeUIStateToListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.destroy();
        }
    }

    @Override
    public void onBeginningOfSpeech() {
        message.setText(getString(R.string.waiting));
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    @Override
    public void onEndOfSpeech() {
    }

    @Override
    public void onError(int error) {
        if (error == 7) {
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
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches == null) {
            return;
        }
        int i = 0;
        String first = "";
        for (String s : matches) {
            if (i == 0) {
                first = s;
            }
            i++;
        }
        signal.spokenText(first);
        this.dismiss();
    }

    @Override
    public void onRmsChanged(float rmsdB) {
    }

    private void changeUIStateToListening() {
        voiceIcon.setText(R.string.voice_icon_recognizer_white);
        voiceIcon.setBackgroundResource(R.drawable.recognizable_voice_blue);
        message.setText(R.string.list_name_input_speech_message);
        tryAgain.setVisibility(View.INVISIBLE);
    }

    private void changeUIStateToRetry() {
        voiceIcon.setText(R.string.voice_icon_recognizer_blue);
        voiceIcon.setBackgroundResource(R.drawable.recognizable_voice_error);
        message.setText(R.string.did_not_catch);
        tryAgain.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.try_again)
    void tryAgain() {
        startListening();
    }
}