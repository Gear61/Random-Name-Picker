package com.randomappsinc.studentpicker.choosing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.common.TextToSpeechManager;
import com.randomappsinc.studentpicker.database.NameListDataManager;
import com.randomappsinc.studentpicker.home.MainActivity;
import com.randomappsinc.studentpicker.presentation.PresentationActivity;
import com.randomappsinc.studentpicker.shake.ShakeManager;
import com.randomappsinc.studentpicker.utils.NameUtils;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;
import com.randomappsinc.studentpicker.views.SimpleDividerItemDecoration;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class NameChoosingFragment extends Fragment
        implements ChoicesDisplayDialog.Listener, NameListDataManager.Listener,
        ShakeManager.Listener, TextToSpeechManager.Listener {

    private static final int PRESENTATION_MODE_REQUEST_CODE = 1;

    public static NameChoosingFragment getInstance(String listName) {
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.LIST_NAME_KEY, listName);
        NameChoosingFragment nameChoosingFragment = new NameChoosingFragment();
        nameChoosingFragment.setArguments(bundle);
        return nameChoosingFragment;
    }

    @BindView(R.id.empty_text_for_choosing) TextView noNamesToChoose;
    @BindView(R.id.num_names) TextView numNames;
    @BindView(R.id.names_list) RecyclerView namesList;

    private NameChoosingAdapter nameChoosingAdapter;
    private ChoosingSettings settings;
    private ChoosingSettingsViewHolder settingsHolder;
    private MaterialDialog settingsDialog;

    private ChoicesDisplayDialog choicesDisplayDialog;
    private boolean canShowPresentationScreen;
    private String listName;
    private NameListDataManager nameListDataManager = NameListDataManager.get();
    private ShakeManager shakeManager = ShakeManager.get();
    private TextToSpeechManager textToSpeechManager;
    private PreferencesManager preferencesManager;
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

        listName = getArguments().getString(MainActivity.LIST_NAME_KEY, "");
        nameChoosingAdapter = new NameChoosingAdapter(noNamesToChoose, numNames, listName);
        namesList.setAdapter(nameChoosingAdapter);
        Context context = rootView.getContext();
        namesList.addItemDecoration(new SimpleDividerItemDecoration(context));

        nameListDataManager.registerListener(this);
        shakeManager.registerListener(this);

        textToSpeechManager = new TextToSpeechManager(context, this);
        preferencesManager = new PreferencesManager(context);

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
                    UIUtils.showShortToast(R.string.settings_applied, getContext());
                })
                .onNegative((dialog, which) -> settingsHolder.revertSettings())
                .cancelable(false)
                .build();
        choicesDisplayDialog = new ChoicesDisplayDialog(this, getActivity());

        settings = (new PreferencesManager(getContext())).getChoosingSettings(listName);
        settingsHolder = new ChoosingSettingsViewHolder(settingsDialog.getCustomView(), settings);
    }

    @Override
    public void onNameAdded(String name, int amount, String listName) {
        if (this.listName.equals(listName)) {
            nameChoosingAdapter.addNames(name, amount);
        }
    }

    @Override
    public void onNameDeleted(String name, int amount, String listName) {
        if (this.listName.equals(listName)) {
            nameChoosingAdapter.removeNames(name, amount);
        }
    }

    @Override
    public void onNameChanged(String oldName, String newName, int amount, String listName) {
        if (this.listName.equals(listName)) {
            nameChoosingAdapter.changeNames(oldName, newName, amount);
        }
    }

    @Override
    public void onNameListsImported(Map<String, Integer> nameAmounts, String listName) {
        if (this.listName.equals(listName)) {
            nameChoosingAdapter.addNameMap(nameAmounts);
        }
    }

    @Override
    public void onShakeDetected() {
        choose();
    }

    @OnClick(R.id.choose)
    public void choose() {
        if (nameChoosingAdapter.getCount() == 0) {
            return;
        }
        if (settings.getPresentationMode()) {
            if (!canShowPresentationScreen) {
                return;
            }
            canShowPresentationScreen = false;
            cacheListState();
            Intent intent = new Intent(getActivity(), PresentationActivity.class);
            intent.putExtra(PresentationActivity.LIST_NAME_KEY, listName);
            getActivity().overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in);
            startActivityForResult(intent, PRESENTATION_MODE_REQUEST_CODE);
        } else {
            if (choicesDisplayDialog.isShowing()) {
                return;
            }
            List<Integer> chosenIndexes = NameUtils.getRandomNumsInRange(settings.getNumNamesToChoose(),
                    nameChoosingAdapter.getNumInstances() - 1);
            String chosenNames = nameChoosingAdapter.chooseNamesAtRandom(chosenIndexes, settings);
            choicesDisplayDialog.showChoices(chosenNames, chosenIndexes.size());
            if (settings.getAutomaticTts()) {
                sayNames(chosenNames);
            }
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
        // If we are choosing without replacement, then presentation mode has mutated the choosing state
        // and we need to update the list
        if (requestCode == PRESENTATION_MODE_REQUEST_CODE && !settings.getWithReplacement()) {
            nameChoosingAdapter.refreshList(preferencesManager.getNameListState(listName));
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getActivity() != null) {
            if (isVisibleToUser) {
                UIUtils.hideKeyboard(getActivity());
                getActivity().invalidateOptionsMenu();
            }
        }
    }

    private void showNamesHistory() {
        final String namesHistory = nameChoosingAdapter.getNamesHistory();
        if (!namesHistory.isEmpty()) {
            new MaterialDialog.Builder(getActivity())
                    .title(R.string.chosen_names_history)
                    .content(namesHistory)
                    .positiveText(android.R.string.yes)
                    .neutralText(R.string.clear)
                    .negativeText(R.string.copy_text)
                    .onNeutral((dialog, which) -> {
                        nameChoosingAdapter.clearNameHistory();
                        UIUtils.showShortToast(R.string.name_history_cleared, getContext());
                    })
                    .onNegative((dialog, which) -> NameUtils.copyNamesToClipboard(
                            namesHistory,
                            null,
                            0,
                            true,
                            getContext()))
                    .show();
        } else {
            UIUtils.showLongToast(R.string.empty_names_history, getContext());
        }
    }

    private void cacheListState() {
        nameChoosingAdapter.cacheState(settings);
    }

    @Override
    public void onResume() {
        super.onResume();
        canShowPresentationScreen = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        cacheListState();
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
                showNamesHistory();
                return true;
            case R.id.settings:
                settingsDialog.show();
                return true;
            case R.id.reset:
                nameChoosingAdapter.resetNames();
                UIUtils.showShortToast(R.string.list_reset_confirmation, getContext());
                return true;
            case android.R.id.home:
                getActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
