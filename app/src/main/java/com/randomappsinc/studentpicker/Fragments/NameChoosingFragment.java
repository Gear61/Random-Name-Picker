package com.randomappsinc.studentpicker.Fragments;

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
import com.randomappsinc.studentpicker.Activities.MainActivity;
import com.randomappsinc.studentpicker.Activities.PresentationActivity;
import com.randomappsinc.studentpicker.Adapters.NameChoosingAdapter;
import com.randomappsinc.studentpicker.Models.ChoosingSettings;
import com.randomappsinc.studentpicker.Models.ChoosingSettingsViewHolder;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.Utils.JSONUtils;
import com.randomappsinc.studentpicker.Utils.NameUtils;
import com.randomappsinc.studentpicker.Utils.PreferencesManager;
import com.randomappsinc.studentpicker.Utils.UIUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alexanderchiou on 10/18/15.
 */
public class NameChoosingFragment extends Fragment implements TextToSpeech.OnInitListener {
    @Bind(R.id.parent) View parent;
    @Bind(R.id.no_content) TextView noContent;
    @Bind(R.id.num_names) TextView numNames;
    @Bind(R.id.names_list) ListView namesList;

    @BindString(R.string.name_chosen) String nameChosenTitle;
    @BindString(R.string.names_chosen) String namesChosenTitle;

    private NameChoosingAdapter nameChoosingAdapter;

    // Settings
    private ChoosingSettings settings;
    private ChoosingSettingsViewHolder settingsHolder;

    private MaterialDialog settingsDialog;
    private TextToSpeech textToSpeech;
    private boolean textToSpeechEnabled;

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
                        applySettings();
                        UIUtils.showSnackbar(parent, getString(R.string.settings_applied));
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        revertSettings();
                    }
                })
                .build();

        textToSpeech = new TextToSpeech(getActivity(), this);
        textToSpeech.setLanguage(Locale.US);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.name_choosing, container, false);
        ButterKnife.bind(this, rootView);

        String listName = getArguments().getString(MainActivity.LIST_NAME_KEY, "");
        nameChoosingAdapter = new NameChoosingAdapter(getActivity(), noContent, numNames, listName);
        namesList.setAdapter(nameChoosingAdapter);

        settings = PreferencesManager.get().getChoosingSetings(listName);
        settingsHolder = new ChoosingSettingsViewHolder(settingsDialog.getCustomView(), settings);

        return rootView;
    }

    public NameChoosingAdapter getNameChoosingAdapter() {
        return nameChoosingAdapter;
    }

    public void applySettings() {
        settings.setPresentationMode(settingsHolder.presentationMode.isChecked());
        settings.setWithReplacement(settingsHolder.withReplacement.isChecked());
        String numChosenText = settingsHolder.numChosen.getText().toString();
        if (numChosenText.isEmpty()) {
            settingsHolder.numChosen.setText("1");
            settings.setNumNamesToChoose(1);
        }
        else {
            int userNumNames = Integer.parseInt(settingsHolder.numChosen.getText().toString());
            if (userNumNames <= 0) {
                settingsHolder.numChosen.setText("1");
                settings.setNumNamesToChoose(1);
            }
            else {
                settings.setNumNamesToChoose(userNumNames);
            }
        }
        settingsHolder.numChosen.clearFocus();
    }

    public void revertSettings() {
        settingsHolder.presentationMode.setCheckedImmediately(settings.getPresentationMode());
        settingsHolder.withReplacement.setCheckedImmediately(settings.getWithReplacement());
        settingsHolder.numChosen.setText(String.valueOf(settings.getNumNamesToChoose()));
        settingsHolder.numChosen.clearFocus();
    }

    @OnClick(R.id.choose)
    public void choose() {
        if (nameChoosingAdapter.getCount() > 0) {
            final List<Integer> chosenIndexes = NameUtils.getRandomNumsInRange(settings.getNumNamesToChoose(),
                    nameChoosingAdapter.getCount() - 1);
            final String chosenNames = nameChoosingAdapter.chooseNamesAtRandom(chosenIndexes,
                    settings.getWithReplacement());
            String title = chosenIndexes.size() == 1 ? nameChosenTitle : namesChosenTitle;
            String sayNames = chosenIndexes.size() == 1 ? getString(R.string.say_name) : getString(R.string.say_names);
            if (settings.getPresentationMode()) {
                Intent intent = new Intent(getActivity(), PresentationActivity.class);
                intent.putExtra(PresentationActivity.NUM_NAMES_KEY, chosenIndexes.size());
                intent.putExtra(JSONUtils.NAMES_KEY, chosenNames);
                getActivity().startActivity(intent);
            }
            else {
                new MaterialDialog.Builder(getActivity())
                        .title(title)
                        .content(chosenNames)
                        .positiveText(android.R.string.yes)
                        .neutralText(sayNames)
                        .negativeText(R.string.copy_text)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                NameUtils.copyNamesToClipboard(chosenNames, parent, chosenIndexes.size(), false);
                            }
                        })
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                sayNames(chosenNames);
                            }
                        })
                        .show();
            }
        }
    }

    public void sayNames(String names) {
        if (textToSpeechEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sayTextPostL(names);
            }
            else {
                sayTextPreL(names);
            }
        }
        else {
            UIUtils.showSnackbar(parent, getString(R.string.text_to_speech_fail));
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
    public void onInit(int status) {
        textToSpeechEnabled = (status == TextToSpeech.SUCCESS);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            UIUtils.hideKeyboard(getActivity());
            getActivity().invalidateOptionsMenu();
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
                            UIUtils.showSnackbar(parent, getString(R.string.name_history_cleared));
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            NameUtils.copyNamesToClipboard(namesHistory, parent, 0, true);
                        }
                    })
                    .show();
        }
        else {
            UIUtils.showSnackbar(parent, getString(R.string.empty_names_history));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        nameChoosingAdapter.cacheState(settings);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.name_choosing_menu, menu);
        UIUtils.loadMenuIcon(menu, R.id.show_names_history, FontAwesomeIcons.fa_history, getActivity());
        UIUtils.loadMenuIcon(menu, R.id.settings, FontAwesomeIcons.fa_gear, getActivity());
        UIUtils.loadMenuIcon(menu, R.id.reset, FontAwesomeIcons.fa_rotate_right, getActivity());
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
