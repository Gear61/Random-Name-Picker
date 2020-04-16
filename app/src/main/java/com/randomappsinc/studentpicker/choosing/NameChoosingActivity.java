package com.randomappsinc.studentpicker.choosing;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.ads.BannerAdManager;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.common.StandardActivity;
import com.randomappsinc.studentpicker.common.TextToSpeechManager;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.presentation.PresentationActivity;
import com.randomappsinc.studentpicker.utils.NameUtils;
import com.randomappsinc.studentpicker.utils.UIUtils;
import com.randomappsinc.studentpicker.views.SimpleDividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NameChoosingActivity extends StandardActivity
        implements ChoicesDisplayDialog.Listener, TextToSpeechManager.Listener,
        NameChoosingAdapter.Listener, NameChoosingHistoryManager.Delegate {

    private static final int PRESENTATION_MODE_REQUEST_CODE = 1;

    @BindView(R.id.empty_text_for_choosing) TextView noNamesToChoose;
    @BindView(R.id.num_names) TextView numNames;
    @BindView(R.id.names_list) RecyclerView namesList;
    @BindView(R.id.bottom_ad_banner_container) FrameLayout bannerAdContainer;

    private NameChoosingAdapter nameChoosingAdapter;
    private ChoosingSettings settings;
    private ChoosingSettingsViewHolder settingsHolder;
    private MaterialDialog settingsDialog;

    private ChoicesDisplayDialog choicesDisplayDialog;
    private boolean canShowPresentationScreen;
    private int listId;
    private TextToSpeechManager textToSpeechManager;
    private NameChoosingHistoryManager nameChoosingHistoryManager;
    private DataSource dataSource;
    private ListInfo listInfo;
    private BannerAdManager bannerAdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name_choosing);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dataSource = new DataSource(this);

        listId = getIntent().getIntExtra(Constants.LIST_ID_KEY, 0);
        setTitle(dataSource.getListName(listId));
        namesList.addItemDecoration(new SimpleDividerItemDecoration(this));

        textToSpeechManager = new TextToSpeechManager(this, this);

        listInfo = dataSource.getChoosingStateListInfo(listId);
        setViews();

        nameChoosingHistoryManager = new NameChoosingHistoryManager(this, this);
        nameChoosingAdapter = new NameChoosingAdapter(listInfo, this);
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
        choicesDisplayDialog = new ChoicesDisplayDialog(this, this);

        settings = dataSource.getChoosingSettings(listId);
        settingsHolder = new ChoosingSettingsViewHolder(settingsDialog.getCustomView(), settings);
        bannerAdManager = new BannerAdManager(bannerAdContainer);
        bannerAdManager.loadOrRemoveAd();
    }

    @Override
    public ListInfo getListInfo() {
        return listInfo;
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
        if (listInfo.getNumInstances() == 0) {
            numNames.setVisibility(View.GONE);
            noNamesToChoose.setVisibility(View.VISIBLE);
        } else {
            noNamesToChoose.setVisibility(View.GONE);
            Context context = numNames.getContext();
            String namesText = listInfo.getNumInstances() == 1
                    ? context.getString(R.string.one_name)
                    : context.getString(R.string.x_names, listInfo.getNumInstances());
            numNames.setText(namesText);
            numNames.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.choose)
    public void choose() {
        if (listInfo.getNumNames() == 0) {
            return;
        }
        if (settings.isPresentationModeEnabled()) {
            if (!canShowPresentationScreen) {
                return;
            }
            canShowPresentationScreen = false;
            Intent intent = new Intent(this, PresentationActivity.class);
            intent.putExtra(PresentationActivity.LIST_ID_KEY, listId);
            startActivityForResult(intent, PRESENTATION_MODE_REQUEST_CODE);
        } else {
            if (choicesDisplayDialog.isShowing()) {
                return;
            }
            List<Integer> chosenIndexes = NameUtils.getRandomNumsInRange(settings.getNumNamesToChoose(),
                    listInfo.getNumInstances() - 1);
            String chosenNames = listInfo.chooseNames(chosenIndexes, settings);
            if (!settings.getWithReplacement()) {
                nameChoosingAdapter.notifyDataSetChanged();
                setViews();
            }
            choicesDisplayDialog.showChoices(chosenNames, chosenIndexes.size());
            if (settings.getAutomaticTts()) {
                sayNames(chosenNames);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Presentation mode mutates the choosing state, so trigger a refresh here
        if (requestCode == PRESENTATION_MODE_REQUEST_CODE) {
            listInfo = dataSource.getChoosingStateListInfo(listId);
            nameChoosingAdapter.refreshList(listInfo);
            setViews();
        }
    }

    @Override
    public void sayNames(String names) {
        textToSpeechManager.speak(names);
    }

    @Override
    public void onTextToSpeechFailure() {
        UIUtils.showLongToast(R.string.text_to_speech_fail, this);
    }

    @Override
    public void copyNamesToClipboard(String chosenNames, int numNames) {
        NameUtils.copyNamesToClipboard(chosenNames, null, numNames, false, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        dataSource.saveNameListState(listId, listInfo, settings);
    }

    @Override
    public void onResume() {
        super.onResume();
        canShowPresentationScreen = true;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        bannerAdManager.onOrientationChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        textToSpeechManager.shutdown();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.name_choosing_menu, menu);
        UIUtils.loadMenuIcon(menu, R.id.show_names_history, FontAwesomeIcons.fa_history, this);
        UIUtils.loadMenuIcon(menu, R.id.settings, IoniconsIcons.ion_android_settings, this);
        UIUtils.loadMenuIcon(menu, R.id.reset, IoniconsIcons.ion_android_refresh, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_names_history:
                nameChoosingHistoryManager.maybeShowNamesHistory();
                return true;
            case R.id.settings:
                settingsDialog.show();
                return true;
            case R.id.reset:
                listInfo = dataSource.getListInfo(listId);
                nameChoosingAdapter.refreshList(listInfo);
                setViews();
                UIUtils.showShortToast(R.string.list_reset_confirmation, this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
