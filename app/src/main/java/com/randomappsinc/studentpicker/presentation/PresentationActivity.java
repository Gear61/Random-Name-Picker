package com.randomappsinc.studentpicker.presentation;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.choosing.ChoosingSettings;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.models.NameDO;
import com.randomappsinc.studentpicker.speech.TextToSpeechManager;
import com.randomappsinc.studentpicker.utils.NameUtils;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PresentationActivity extends AppCompatActivity
        implements ColorChooserDialog.ColorCallback, TextToSpeechManager.Listener,
        PresentationManager.Listener {

    public static final String LIST_ID_KEY = "listId";
    public static final String DRUMROLL_FILE_NAME = "drumroll.mp3";

    @BindView(R.id.header) TextView header;
    @BindView(R.id.names_list) RecyclerView namesList;
    @BindColor(R.color.text_normal) int textNormalColor;
    @BindInt(R.integer.default_anim_length) int defaultAnimationLengthMs;
    @BindDrawable(R.drawable.line_divider) Drawable lineDivider;

    private PreferencesManager preferencesManager;
    private DataSource dataSource;
    private int listId;
    private ListInfo listState;
    private ChoosingSettings settings;
    private String chosenNamesText;
    private MaterialDialog setTextSizeDialog;
    private SetTextSizeViewHolder setTextViewHolder;
    private TextToSpeechManager textToSpeechManager;
    private PresentationManager presentationManager;
    private PresentationAdapter presentationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_activity);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()
                .setHomeAsUpIndicator(new IconDrawable(this, IoniconsIcons.ion_android_close)
                        .colorRes(R.color.white)
                        .actionBarSize());

        preferencesManager = new PreferencesManager(this);
        dataSource = new DataSource(this);
        listId = getIntent().getIntExtra(LIST_ID_KEY, 0);
        listState = dataSource.getChoosingStateListInfo(listId);
        settings = dataSource.getChoosingSettings(listId);
        textToSpeechManager = new TextToSpeechManager(this, this);
        presentationManager = new PresentationManager(this, namesList);

        presentationAdapter = new PresentationAdapter();
        presentationAdapter.setTextSize(preferencesManager.getPresentationTextSize() * 8);
        presentationAdapter.setTextColor(preferencesManager.getPresentationTextColor(textNormalColor));
        namesList.setAdapter(presentationAdapter);

        int numNames = settings.getNumNamesToChoose();
        header.setText(NameUtils.getChoosingMessage(this, listId, numNames));

        setTextSizeDialog = new MaterialDialog.Builder(this)
                .title(R.string.set_text_size_title)
                .customView(R.layout.set_text_size, true)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive((@NonNull MaterialDialog dialog, @NonNull DialogAction which) -> {
                    int newTextSize = setTextViewHolder.textSizeSlider.getProgress() + 1;
                    preferencesManager.setPresentationTextSize(newTextSize);
                    presentationAdapter.setTextSize(newTextSize * 8);
                })
                .onNegative((@NonNull MaterialDialog dialog, @NonNull DialogAction which) ->
                        setTextViewHolder.revertSetting()
                )
                .build();
        setTextViewHolder = new SetTextSizeViewHolder(setTextSizeDialog.getCustomView());

        chooseNames();
    }

    @OnClick(R.id.choose)
    public void choose() {
        chooseNames();
    }

    private void chooseNames() {
        textToSpeechManager.stopSpeaking();
        if (listState.getNumNames() > 0) {
            List<NameDO> chosenNames = listState.chooseNames(settings);
            chosenNamesText = NameUtils.convertNameListToString(chosenNames, settings);
            presentationAdapter.setChosenNames(chosenNames, settings.getShowAsList());
            presentationManager.presentChosenName();
        } else {
            UIUtils.showLongToast(R.string.no_names_left, this);
        }
    }

    @Override
    public void speakNames() {
        if (settings.getAutomaticTts()) {
            textToSpeechManager.speak(chosenNamesText, settings.getSpeechLanguage());
        }
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
                .preselect(preferencesManager.getPresentationTextColor(textNormalColor))
                .show(this);
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, int selectedColor) {
        preferencesManager.setPresentationTextColor(selectedColor);
        presentationAdapter.setTextColor(selectedColor);
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) {}

    @Override
    public void onTextToSpeechFailure() {
        UIUtils.showLongToast(R.string.text_to_speech_fail, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        dataSource.saveNameListState(listId, listState, settings);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_from_top);
        presentationManager.stopPlayer();
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
            case android.R.id.home:
                finish();
                return true;
            case R.id.settings:
                showSettingsDialog();
                return true;
            case R.id.say_names:
                textToSpeechManager.speak(chosenNamesText, settings.getSpeechLanguage());
                return true;
            case R.id.copy_names:
                NameUtils.copyNamesToClipboard(
                        chosenNamesText,
                        settings.getNumNamesToChoose(),
                        false,
                        this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
