package com.randomappsinc.studentpicker.presentation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.ads.BannerAdManager;
import com.randomappsinc.studentpicker.choosing.ChoosingSettings;
import com.randomappsinc.studentpicker.common.StandardActivity;
import com.randomappsinc.studentpicker.common.TextToSpeechManager;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.utils.NameUtils;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PresentationActivity extends StandardActivity
        implements ColorChooserDialog.ColorCallback, TextToSpeechManager.Listener {

    public static final String LIST_NAME_KEY = "listName";
    public static final String DRUMROLL_FILE_NAME = "drumroll.mp3";

    @BindView(R.id.header) TextView header;
    @BindView(R.id.names) TextView names;
    @BindView(R.id.bottom_ad_banner_container) FrameLayout bannerAdContainer;
    @BindColor(R.color.dark_gray) int darkGray;

    private PreferencesManager preferencesManager;
    private MediaPlayer player;
    private String listName;
    private ListInfo listState;
    private ChoosingSettings settings;
    private String chosenNamesText;
    private MaterialDialog setTextSizeDialog;
    private SetTextSizeViewHolder setTextViewHolder;
    private TextToSpeechManager textToSpeechManager;
    private BannerAdManager bannerAdManager;
    private Handler handler;
    private Runnable animateNamesTask = this::animateNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_activity);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferencesManager = new PreferencesManager(this);
        listName = getIntent().getStringExtra(LIST_NAME_KEY);
        listState = preferencesManager.getNameListState(listName);
        settings = preferencesManager.getChoosingSettings(listName);
        textToSpeechManager = new TextToSpeechManager(this, this);

        int numNames = settings.getNumNamesToChoose();
        header.setText(numNames > 1 ? R.string.names_chosen : R.string.name_chosen);

        if (!settings.getShowAsList()) {
            names.setGravity(Gravity.CENTER_HORIZONTAL);
        }

        names.setTextSize(TypedValue.COMPLEX_UNIT_SP, preferencesManager.getPresentationTextSize() * 8);
        names.setTextColor(preferencesManager.getPresentationTextColor(darkGray));

        handler = new Handler();
        player = new MediaPlayer();

        setTextSizeDialog = new MaterialDialog.Builder(this)
                .title(R.string.set_text_size_title)
                .customView(R.layout.set_text_size, true)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive((@NonNull MaterialDialog dialog, @NonNull DialogAction which) -> {
                    int newTextSize = setTextViewHolder.textSizeSlider.getProgress() + 1;
                    preferencesManager.setPresentationTextSize(newTextSize);
                    names.setTextSize(TypedValue.COMPLEX_UNIT_SP, newTextSize * 8);
                })
                .onNegative((@NonNull MaterialDialog dialog, @NonNull DialogAction which) -> {
                    setTextViewHolder.revertSetting();
                })
                .build();
        setTextViewHolder = new SetTextSizeViewHolder(setTextSizeDialog.getCustomView());
        bannerAdManager = new BannerAdManager(bannerAdContainer);
        bannerAdManager.maybeLoadAd();

        chooseNames();
    }

    @OnClick(R.id.choose)
    public void choose() {
        chooseNames();
    }

    private void chooseNames() {
        names.clearAnimation();
        textToSpeechManager.stopSpeaking();
        if (listState.getNumNames() > 0) {
            List<Integer> chosenIndexes = NameUtils.getRandomNumsInRange(
                    settings.getNumNamesToChoose(),
                    listState.getNumInstances() - 1);
            chosenNamesText = listState.chooseNames(chosenIndexes, settings);

            names.setAlpha(0.0f);
            names.setText(chosenNamesText);

            playSound();
        } else {
            UIUtils.showLongToast(R.string.no_names_left, this);
        }
    }

    private void playSound() {
        try {
            AssetFileDescriptor fileDescriptor = getAssets().openFd(DRUMROLL_FILE_NAME);
            player.reset();
            player.setDataSource(
                    fileDescriptor.getFileDescriptor(),
                    fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());
            player.prepare();
            player.start();
        } catch (Exception ex) {
            UIUtils.showLongToast(R.string.drumroll_error, this);
        }

        handler.removeCallbacks(animateNamesTask);
        handler.postDelayed(animateNamesTask, 2600);
    }

    private void animateNames() {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(names, "alpha", 1.0f).setDuration(250);
        fadeIn.setInterpolator(new AccelerateInterpolator());

        AnimatorSet scaleSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator
                .ofFloat(names, "scaleX", 1.0f, 3.0f, 1.0f)
                .setDuration(250);
        ObjectAnimator scaleY = ObjectAnimator
                .ofFloat(names, "scaleY", 1.0f, 3.0f, 1.0f)
                .setDuration(250);
        scaleSet.setInterpolator(new DecelerateInterpolator());
        scaleSet.playTogether(scaleX, scaleY);

        AnimatorSet fullSet = new AnimatorSet();
        fullSet.playSequentially(fadeIn, scaleSet);
        fullSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}


            @Override
            public void onAnimationEnd(Animator animator) {
                if (settings.getAutomaticTts()) {
                    textToSpeechManager.speak(chosenNamesText);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
        fullSet.start();
    }

    private void showSettingsDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.presentation_settings_title)
                .items(R.array.presentation_settings_options)
                .itemsCallback((dialog, view, which, text) -> {
                    switch (which) {
                        case 0:
                            setTextSizeDialog.show();
                            break;
                        case 1:
                            showColorChooserDialog();
                    }
                })
                .show();
    }

    private void showColorChooserDialog() {
        new ColorChooserDialog.Builder(this, R.string.set_text_color_title)
                .dynamicButtonColor(false)
                .preselect(preferencesManager.getPresentationTextColor(darkGray))
                .show(this);
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, int selectedColor) {
        preferencesManager.setPresentationTextColor(selectedColor);
        names.setTextColor(selectedColor);
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) {}

    @Override
    public void onTextToSpeechFailure() {
        UIUtils.showLongToast(R.string.text_to_speech_fail, this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        preferencesManager.setNameListState(listName, listState, settings);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        bannerAdManager.onOrientationChanged();
    }

    @Override
    public void finish() {
        // Make sure that we persist the choosing state, since name history will always change at least
        preferencesManager.setNameListState(listName, listState, settings);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
        textToSpeechManager.shutdown();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.presentation_menu, menu);
        UIUtils.loadMenuIcon(menu, R.id.settings, IoniconsIcons.ion_android_settings, this);
        UIUtils.loadMenuIcon(menu, R.id.say_names, IoniconsIcons.ion_android_microphone, this);
        if (settings.getNumNamesToChoose() > 1) {
            menu.findItem(R.id.say_names).setTitle(R.string.say_names);
        }
        UIUtils.loadMenuIcon(menu, R.id.copy_names, IoniconsIcons.ion_android_clipboard, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                showSettingsDialog();
                return true;
            case R.id.say_names:
                textToSpeechManager.speak(chosenNamesText);
                return true;
            case R.id.copy_names:
                NameUtils.copyNamesToClipboard(
                        chosenNamesText,
                        null,
                        settings.getNumNamesToChoose(),
                        false,
                        this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
