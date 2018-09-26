package com.randomappsinc.studentpicker.fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.activities.MainActivity;
import com.randomappsinc.studentpicker.activities.PresentationActivity;
import com.randomappsinc.studentpicker.adapters.NameChoosingAdapter;
import com.randomappsinc.studentpicker.dialogs.ChoicesDisplayDialog;
import com.randomappsinc.studentpicker.models.ChoosingSettings;
import com.randomappsinc.studentpicker.models.ChoosingSettingsViewHolder;
import com.randomappsinc.studentpicker.utils.NameUtils;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class NameChoosingFragment extends Fragment
        implements TextToSpeech.OnInitListener, ChoicesDisplayDialog.Listener {

    @BindView(R.id.no_content) TextView noContent;
    @BindView(R.id.num_names) TextView numNames;
    @BindView(R.id.names_list) ListView namesList;

    private NameChoosingAdapter nameChoosingAdapter;
    private ChoosingSettings settings;
    private ChoosingSettingsViewHolder settingsHolder;
    private MaterialDialog settingsDialog;

    private ChoicesDisplayDialog choicesDisplayDialog;
    private TextToSpeech textToSpeech;
    private boolean textToSpeechEnabled;
    private boolean canShowPresentationScreen;
    private String listName;
    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        settingsDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.name_choosing_settings)
                .customView(R.layout.name_choosing_settings, true)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        settingsHolder.applySettings();
                        UIUtils.showShortToast(R.string.settings_applied, getContext());
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        settingsHolder.revertSettings();
                    }
                })
                .cancelable(false)
                .build();
        choicesDisplayDialog = new ChoicesDisplayDialog(this, getActivity());

        textToSpeech = new TextToSpeech(getActivity(), this);
        textToSpeech.setLanguage(Locale.getDefault());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.name_choosing, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        listName = getArguments().getString(MainActivity.LIST_NAME_KEY, "");
        nameChoosingAdapter = new NameChoosingAdapter(noContent, numNames, listName);
        namesList.setAdapter(nameChoosingAdapter);

        settings = (new PreferencesManager(getContext())).getChoosingSettings(listName);
        settingsHolder = new ChoosingSettingsViewHolder(settingsDialog.getCustomView(), settings);

        return rootView;
    }

    public NameChoosingAdapter getNameChoosingAdapter() {
        return nameChoosingAdapter;
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
            getActivity().startActivity(intent);
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
    public void sayNames(String names) {
        if (textToSpeechEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sayTextPostL(names);
            } else {
                sayTextPreL(names);
            }
        } else {
            UIUtils.showLongToast(R.string.text_to_speech_fail, getContext());
        }
    }

    @SuppressWarnings("deprecation")
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

    @Override
    public void copyNamesToClipboard(String chosenNames, int numNames) {
        NameUtils.copyNamesToClipboard(chosenNames, null, numNames, false, getContext());
    }

    @Override
    public void onInit(int status) {
        textToSpeechEnabled = (status == TextToSpeech.SUCCESS);
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

    public void showNamesHistory() {
        final String namesHistory = nameChoosingAdapter.getNamesHistory();
        if (!namesHistory.isEmpty()) {
            new MaterialDialog.Builder(getActivity())
                    .title(R.string.chosen_names_history)
                    .content(namesHistory)
                    .positiveText(android.R.string.yes)
                    .neutralText(R.string.clear)
                    .negativeText(R.string.copy_text)
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            nameChoosingAdapter.clearNameHistory();
                            UIUtils.showShortToast(R.string.name_history_cleared, getContext());
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            NameUtils.copyNamesToClipboard(
                                    namesHistory,
                                    null,
                                    0,
                                    true,
                                    getContext());
                        }
                    })
                    .show();
        } else {
            UIUtils.showLongToast(R.string.empty_names_history, getContext());
        }
    }

    public void cacheListState() {
        nameChoosingAdapter.cacheState(settings);
    }

    @Override
    public void onResume() {
        super.onResume();
        canShowPresentationScreen = true;
        nameChoosingAdapter.resync();
    }

    @Override
    public void onPause() {
        super.onPause();
        cacheListState();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
                return true;
            case android.R.id.home:
                getActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
