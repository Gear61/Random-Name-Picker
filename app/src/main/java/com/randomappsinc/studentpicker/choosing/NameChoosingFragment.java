package com.randomappsinc.studentpicker.choosing;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.ads.BannerAdManager;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.common.TextToSpeechManager;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.database.NameListDataManager;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.presentation.PresentationActivity;
import com.randomappsinc.studentpicker.shake.ShakeManager;
import com.randomappsinc.studentpicker.utils.NameUtils;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;
import com.randomappsinc.studentpicker.views.SimpleDividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class NameChoosingFragment extends Fragment
        implements ChoicesDisplayDialog.Listener, NameListDataManager.Listener,
        ShakeManager.Listener, TextToSpeechManager.Listener, NameChoosingAdapter.Listener,
        NameChoosingHistoryManager.Delegate {

    private static final int PRESENTATION_MODE_REQUEST_CODE = 1;

    public static NameChoosingFragment getInstance(int listId) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.LIST_ID_KEY, listId);
        NameChoosingFragment nameChoosingFragment = new NameChoosingFragment();
        nameChoosingFragment.setArguments(bundle);
        return nameChoosingFragment;
    }

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
    private String listName;
    private int listId;
    private NameListDataManager nameListDataManager = NameListDataManager.get();
    private ShakeManager shakeManager = ShakeManager.get();
    private TextToSpeechManager textToSpeechManager;
    private PreferencesManager preferencesManager;
    private NameChoosingHistoryManager nameChoosingHistoryManager;
    private DataSource dataSource;
    private ListInfo listInfo;
    private BannerAdManager bannerAdManager;
    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.name_choosing, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        Context context = rootView.getContext();
        dataSource = new DataSource(context);

        listId = getArguments().getInt(Constants.LIST_ID_KEY);
        listName = dataSource.getListName(listId);
        namesList.addItemDecoration(new SimpleDividerItemDecoration(context));

        nameListDataManager.registerListener(this);
        shakeManager.registerListener(this);

        textToSpeechManager = new TextToSpeechManager(context, this);
        preferencesManager = new PreferencesManager(context);

        listInfo = preferencesManager.getNameListState(listName);
        if (listInfo == null) {
            listInfo = dataSource.getListInfo(listId);
        }
        setViews();

        nameChoosingHistoryManager = new NameChoosingHistoryManager(this, context);
        nameChoosingAdapter = new NameChoosingAdapter(listInfo, this);
        namesList.setAdapter(nameChoosingAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        settingsDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.name_choosing_settings)
                .customView(R.layout.name_choosing_settings, true)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive((dialog, which) -> {
                    settingsHolder.applySettings();
                    cacheListState();
                    UIUtils.showShortToast(R.string.settings_applied, getContext());
                })
                .onNegative((dialog, which) -> settingsHolder.revertSettings())
                .cancelable(false)
                .build();
        choicesDisplayDialog = new ChoicesDisplayDialog(this, getActivity());

        settings = dataSource.getChoosingSettings(listId);
        settingsHolder = new ChoosingSettingsViewHolder(settingsDialog.getCustomView(), settings);
        bannerAdManager = new BannerAdManager(bannerAdContainer);
    }

    @Override
    public ListInfo getListInfo() {
        return listInfo;
    }

    @Override
    public void onNameRemoved() {
        setViews();
        cacheListState();
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

    @Override
    public void onNameAdded(String name, int amount, int listId) {
        nameChoosingAdapter.addNames(name, amount);
        setViews();
        cacheListState();
    }

    @Override
    public void onNameDeleted(String name, int amount, int listId) {
        nameChoosingAdapter.removeNames(name, amount);
        setViews();
        cacheListState();
    }

    @Override
    public void onNameChanged(String oldName, String newName, int amount, int listId) {
        nameChoosingAdapter.changeNames(oldName, newName, amount);
        cacheListState();
    }

    @Override
    public void onShakeDetected() {
        choose();
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
            Intent intent = new Intent(getActivity(), PresentationActivity.class);
            intent.putExtra(PresentationActivity.LIST_NAME_KEY, listName);
            intent.putExtra(PresentationActivity.LIST_ID_KEY, listId);
            getActivity().overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in);
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
        if (!settings.getWithReplacement()) {
            cacheListState();
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Presentation mode mutates the choosing state, so trigger a refresh here
        if (requestCode == PRESENTATION_MODE_REQUEST_CODE) {
            listInfo = preferencesManager.getNameListState(listName);
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
        UIUtils.showLongToast(R.string.text_to_speech_fail, getContext());
    }

    @Override
    public void copyNamesToClipboard(String chosenNames, int numNames) {
        NameUtils.copyNamesToClipboard(chosenNames, null, numNames, false, getContext());
    }

    private void cacheListState() {
        preferencesManager.setNameListState(listName, listInfo, settings);
    }

    @Override
    public void onResume() {
        super.onResume();
        canShowPresentationScreen = true;
        bannerAdManager.maybeLoadAd();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        bannerAdManager.onOrientationChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        nameListDataManager.unregisterListener(this);
        shakeManager.unregisterListener();
        textToSpeechManager.shutdown();
        unbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.name_choosing_menu, menu);
        UIUtils.loadMenuIcon(menu, R.id.show_names_history, FontAwesomeIcons.fa_history, getActivity());
        UIUtils.loadMenuIcon(menu, R.id.settings, IoniconsIcons.ion_android_settings, getActivity());
        UIUtils.loadMenuIcon(menu, R.id.reset, IoniconsIcons.ion_android_refresh, getActivity());
        super.onCreateOptionsMenu(menu, inflater);
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
                cacheListState();
                nameChoosingAdapter.refreshList(listInfo);
                setViews();
                UIUtils.showShortToast(R.string.list_reset_confirmation, getContext());
                return true;
            case android.R.id.home:
                getActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
