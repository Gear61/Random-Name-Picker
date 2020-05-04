package com.randomappsinc.studentpicker.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.HashMap;
import java.util.Locale;

public class TextToSpeechManager implements TextToSpeech.OnInitListener {

    public interface Listener {
        void onTextToSpeechFailure();
    }

    protected Listener listener;
    private TextToSpeech textToSpeech;
    private boolean enabled;
    private AudioManager audioManager;

    // Oreo audio focus shenanigans
    private AudioFocusRequest audioFocusRequest;

    public TextToSpeechManager(Context context, Listener listener) {
        this.listener = listener;
        textToSpeech = new TextToSpeech(context, this);
        textToSpeech.setLanguage(Locale.getDefault());
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initializeOAudioFocusParams();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void initializeOAudioFocusParams() {
        AudioAttributes ttsAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANT)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();
        audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(ttsAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(audioFocusChangeListener, new Handler())
                .build();
    }

    public void speak(String text, @Language int language) {
        if (enabled) {
            textToSpeech.setLanguage(getLocaleFromLanguage(language));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestAudioFocusPostO(text);
            } else {
                requestAudioFocusPreO(text);
            }
        } else {
            listener.onTextToSpeechFailure();
        }
    }

    private Locale getLocaleFromLanguage(@Language int language) {
        switch (language) {
            case Language.ENGLISH:
                return new Locale("en");
            case Language.SPANISH:
                return new Locale("es");
            case Language.FRENCH:
                return new Locale("fr");
            case Language.JAPANESE:
                return new Locale("ja");
            case Language.PORTUGUESE:
                return new Locale("pt");
            case Language.CHINESE:
                return new Locale("zh");
            case Language.GERMAN:
                return new Locale("de");
            case Language.ITALIAN:
                return new Locale("it");
            case Language.KOREAN:
                return new Locale("ko");
            case Language.HINDI:
                return new Locale("hi");
            case Language.BENGALI:
                return new Locale("bn");
            case Language.RUSSIAN:
                return new Locale("ru");
            case Language.NORWEGIAN:
                return new Locale("nb");
            default:
                return Locale.getDefault();
        }
    }

    private void playTts(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sayTextPostL(text);
        } else {
            sayTextPreL(text);
        }
    }

    private void sayTextPreL(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, this.hashCode() + "");
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void sayTextPostL(String text) {
        String utteranceId = this.hashCode() + "";
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    private void requestAudioFocusPreO(String text) {
        int result = audioManager.requestAudioFocus(
                audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            playTts(text);
        } else {
            listener.onTextToSpeechFailure();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void requestAudioFocusPostO(String text) {
        int res = audioManager.requestAudioFocus(audioFocusRequest);
        if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            playTts(text);
        }
    }

    @Override
    public void onInit(int status) {
        enabled = (status == TextToSpeech.SUCCESS);
        if (enabled) {
            setUtteranceListener();
        }
    }

    private void setUtteranceListener() {
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {}

            @Override
            public void onDone(String utteranceId) {
                audioManager.abandonAudioFocus(audioFocusChangeListener);
            }

            @Override
            public void onError(String utteranceId) {
                listener.onTextToSpeechFailure();
            }
        });
    }

    private final AudioManager.OnAudioFocusChangeListener audioFocusChangeListener =
            focusChange -> {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS
                        || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                        || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                    stopSpeaking();
                }
            };

    public void stopSpeaking() {
        if (textToSpeech.isSpeaking()) {
            audioManager.abandonAudioFocus(audioFocusChangeListener);
            textToSpeech.stop();
        }
    }

    public void shutdown() {
        textToSpeech.shutdown();
        listener = null;
    }
}