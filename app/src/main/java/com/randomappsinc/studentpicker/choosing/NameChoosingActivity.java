package com.randomappsinc.studentpicker.choosing;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.editing.EditNameListActivity;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.models.NameDO;
import com.randomappsinc.studentpicker.photo.PictureFullViewActivity;
import com.randomappsinc.studentpicker.presentation.PresentationActivity;
import com.randomappsinc.studentpicker.speech.TextToSpeechManager;
import com.randomappsinc.studentpicker.utils.NameUtils;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;
import com.squareup.seismic.ShakeDetector;

import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NameChoosingActivity extends AppCompatActivity
        implements ChoicesDisplayDialog.Listener, TextToSpeechManager.Listener,
        NameChoosingAdapter.Listener, NameChoosingHistoryManager.Delegate,
        ShakeDetector.Listener {

    private static final int PRESENTATION_MODE_REQUEST_CODE = 1;
    private static final int EDIT_LIST_REQUEST_CODE = 2;

    @BindView(R.id.empty_text_for_choosing) TextView noNamesToChoose;
    @BindView(R.id.num_names) TextView numNames;
    @BindView(R.id.names_list) RecyclerView namesList;

    @BindDrawable(R.drawable.line_divider) Drawable lineDivider;

    private NameChoosingAdapter nameChoosingAdapter;
    private ChoosingSettings settings;
    private ChoosingSettingsViewHolder settingsHolder;
    private MaterialDialog settingsDialog;
    private ChoicesDisplayDialog choicesDisplayDialog;
    private boolean canShowPresentationScreen;
    private int listId;
    private ListInfo choosingStateListInfo;
    private TextToSpeechManager textToSpeechManager;
    private NameChoosingHistoryManager nameChoosingHistoryManager;
    private DataSource dataSource;
    private PreferencesManager preferencesManager;
    private ShakeDetector shakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name_choosing);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()
                .setHomeAsUpIndicator(new IconDrawable(this, IoniconsIcons.ion_android_close)
                        .colorRes(R.color.white)
                        .actionBarSize());

        dataSource = new DataSource(this);
        preferencesManager = new PreferencesManager(this);
        shakeDetector = new ShakeDetector(this);
        if (preferencesManager.isShakeEnabled()) {
            shakeDetector.start((SensorManager) getSystemService(SENSOR_SERVICE));
        }

        listId = getIntent().getIntExtra(Constants.LIST_ID_KEY, 0);
        setTitle(dataSource.getListName(listId));

        DividerItemDecoration itemDecorator =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(lineDivider);
        namesList.addItemDecoration(itemDecorator);

        textToSpeechManager = new TextToSpeechManager(this, this);

        choosingStateListInfo = dataSource.getChoosingStateListInfo(listId);
        setViews();

        nameChoosingHistoryManager = new NameChoosingHistoryManager(this, this);
        nameChoosingAdapter = new NameChoosingAdapter(choosingStateListInfo, this);
        namesList.setAdapter(nameChoosingAdapter);

        settingsDialog = new MaterialDialog.Builder(this)
                .title(R.string.name_choosing_settings)
                .customView(R.layout.name_choosing_settings, true)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive((dialog, which) -> {
                    settingsHolder.applySettings();
                    UIUtils.showShortToast(R.string.settings_applied, this);
                })
                .onNegative((dialog, which) -> settingsHolder.revertSettings())
                .cancelable(false)
                .build();

        settings = dataSource.getChoosingSettings(listId);
        settingsHolder = new ChoosingSettingsViewHolder(settingsDialog.getCustomView(), settings);
        choicesDisplayDialog = new ChoicesDisplayDialog(this, this, listId, settings);
    }

    @Override
    public ListInfo getChoosingStateListInfo() {
        return choosingStateListInfo;
    }

    @Override
    public void onNameImageClicked(NameDO nameDO) {
        Intent intent = new Intent(this, PictureFullViewActivity.class)
                .putExtra(PictureFullViewActivity.IMAGE_URL_KEY, nameDO.getPhotoUri())
                .putExtra(PictureFullViewActivity.CAPTION_KEY, nameDO.getName());
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, 0);
    }

    @Override
    public void onNameRemoved() {
        setViews();
    }

    private void setViews() {
        if (dataSource.getListInfo(listId).getNumInstances() == 0) {
            noNamesToChoose.setText(R.string.no_names_for_choosing);
        } else {
            noNamesToChoose.setText(R.string.out_of_names);
        }
        if (choosingStateListInfo.getNumInstances() == 0) {
            numNames.setVisibility(View.GONE);
            noNamesToChoose.setVisibility(View.VISIBLE);
        } else {
            noNamesToChoose.setVisibility(View.GONE);
            Context context = numNames.getContext();
            String namesText = choosingStateListInfo.getNumInstances() == 1
                    ? context.getString(R.string.one_name)
                    : context.getString(R.string.x_names, choosingStateListInfo.getNumInstances());
            numNames.setText(namesText);
            numNames.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.choose)
    public void choose() {
        if (choosingStateListInfo.getNumNames() == 0) {
            return;
        }
        if (settings.isPresentationModeEnabled()) {
            if (!canShowPresentationScreen) {
                return;
            }
            canShowPresentationScreen = false;
            Intent intent = new Intent(this, PresentationActivity.class);
            intent.putExtra(PresentationActivity.LIST_ID_KEY, listId);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityForResult(intent, PRESENTATION_MODE_REQUEST_CODE);
        } else {
            if (choicesDisplayDialog.isShowing()) {
                return;
            }
            List<NameDO> chosenNames = choosingStateListInfo.chooseNames(settings);
            if (!settings.getWithReplacement()) {
                nameChoosingAdapter.notifyDataSetChanged();
                setViews();
            }
            choicesDisplayDialog.showChoices(chosenNames);
            if (settings.getAutomaticTts()) {
                sayNames(NameUtils.convertNameListToString(chosenNames, settings));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PRESENTATION_MODE_REQUEST_CODE:
                // Presentation mode mutates the choosing state, so trigger a refresh here
                choosingStateListInfo = dataSource.getChoosingStateListInfo(listId);
                nameChoosingAdapter.refreshList(choosingStateListInfo);
                setViews();
                break;
            case EDIT_LIST_REQUEST_CODE:
                // If list was changed on edit page, also trigger a refresh
                if (resultCode == Constants.LIST_UPDATED_RESULT_CODE) {
                    choosingStateListInfo = dataSource.getChoosingStateListInfo(listId);
                    nameChoosingAdapter.refreshList(choosingStateListInfo);
                    setViews();
                    break;
                }
                break;
        }
    }

    @Override
    public void sayNames(String names) {
        textToSpeechManager.speak(names, settings.getSpeechLanguage());
    }

    @Override
    public void onTextToSpeechFailure() {
        UIUtils.showLongToast(R.string.text_to_speech_fail, this);
    }

    @Override
    public void copyNamesToClipboard(String chosenNames, int numNames) {
        NameUtils.copyNamesToClipboard(chosenNames, numNames, false, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        dataSource.saveNameListState(listId, choosingStateListInfo, settings);
    }

    @Override
    public void onResume() {
        super.onResume();
        canShowPresentationScreen = true;
    }

    @Override
    public void hearShake() {
        choose();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        UIUtils.hideKeyboard(this);
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_from_top);
        textToSpeechManager.shutdown();
        if (preferencesManager.isShakeEnabled()) {
            shakeDetector.stop();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.name_choosing_menu, menu);
        UIUtils.loadMenuIcon(menu, R.id.settings, IoniconsIcons.ion_android_settings, this);
        UIUtils.loadMenuIcon(menu, R.id.reset, IoniconsIcons.ion_android_refresh, this);
        UIUtils.loadMenuIcon(menu, R.id.show_names_history, FontAwesomeIcons.fa_history, this);
        UIUtils.loadMenuIcon(menu, R.id.edit_name_list, IoniconsIcons.ion_android_add_circle, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.settings:
                settingsDialog.show();
                return true;
            case R.id.reset:
                choosingStateListInfo = dataSource.getListInfo(listId);
                nameChoosingAdapter.refreshList(choosingStateListInfo);
                setViews();
                UIUtils.showShortToast(R.string.list_reset_confirmation, this);
                return true;
            case R.id.show_names_history:
                nameChoosingHistoryManager.maybeShowNamesHistory();
                return true;
            case R.id.edit_name_list:
                Intent intent = new Intent(this, EditNameListActivity.class);
                intent.putExtra(Constants.LIST_ID_KEY, listId);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityForResult(intent, EDIT_LIST_REQUEST_CODE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
