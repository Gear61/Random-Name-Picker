package com.randomappsinc.studentpicker.Fragments;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
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
import com.randomappsinc.studentpicker.Activities.MainActivity;
import com.randomappsinc.studentpicker.Activities.PresentationActivity;
import com.randomappsinc.studentpicker.Adapters.NameChoosingAdapter;
import com.randomappsinc.studentpicker.Models.ChoosingSettings;
import com.randomappsinc.studentpicker.Models.ChoosingSettingsViewHolder;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.Utils.NameUtils;
import com.randomappsinc.studentpicker.Utils.PreferencesManager;
import com.randomappsinc.studentpicker.Utils.UIUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NameChoosingFragment extends Fragment implements TextToSpeech.OnInitListener {

    public static final String SCREEN_NAME = "Name Choosing Page";

    @Bind(R.id.parent) View parent;
    @Bind(R.id.no_content) TextView noContent;
    @Bind(R.id.num_names) TextView numNames;
    @Bind(R.id.names_list) ListView namesList;

    private NameChoosingAdapter nameChoosingAdapter;
    private ChoosingSettings settings;
    private ChoosingSettingsViewHolder settingsHolder;
    private MaterialDialog settingsDialog;
    private TextToSpeech textToSpeech;
    private boolean textToSpeechEnabled;
    private boolean canShow;
    private String listName;

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
                        UIUtils.showSnackbar(parent, getString(R.string.settings_applied));
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

        textToSpeech = new TextToSpeech(getActivity(), this);
        textToSpeech.setLanguage(Locale.US);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.name_choosing, container, false);
        ButterKnife.bind(this, rootView);

        listName = getArguments().getString(MainActivity.LIST_NAME_KEY, "");
        nameChoosingAdapter = new NameChoosingAdapter(getActivity(), noContent, numNames, listName);
        namesList.setAdapter(nameChoosingAdapter);

        settings = PreferencesManager.get().getChoosingSettings(listName);
        settingsHolder = new ChoosingSettingsViewHolder(settingsDialog.getCustomView(), settings);

        return rootView;
    }

    public NameChoosingAdapter getNameChoosingAdapter() {
        return nameChoosingAdapter;
    }

    @OnClick(R.id.choose)
    public void choose() {
        if (nameChoosingAdapter.getCount() > 0 && canShow) {
            canShow = false;
            if (settings.getPresentationMode()) {
                cacheListState();
                Intent intent = new Intent(getActivity(), PresentationActivity.class);
                intent.putExtra(PresentationActivity.LIST_NAME_KEY, listName);
                getActivity().startActivity(intent);
            } else {
                final List<Integer> chosenIndexes = NameUtils.getRandomNumsInRange(settings.getNumNamesToChoose(),
                        nameChoosingAdapter.getNumInstances() - 1);
                final String chosenNames = nameChoosingAdapter.chooseNamesAtRandom(chosenIndexes, settings);

                new MaterialDialog.Builder(getActivity())
                        .title(chosenIndexes.size() == 1 ? R.string.name_chosen : R.string.names_chosen)
                        .content(chosenNames)
                        .positiveText(android.R.string.yes)
                        .neutralText(chosenIndexes.size() == 1 ? R.string.say_name : R.string.say_names)
                        .negativeText(R.string.copy_text)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                canShow = true;
                            }
                        })
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
                        .autoDismiss(false)
                        .cancelable(false)
                        .show();
                if (settings.getAutomaticTts()) {
                    sayNames(chosenNames);
                }
            }
        }
    }

    public void sayNames(String names) {
        if (textToSpeechEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sayTextPostL(names);
            } else {
                sayTextPreL(names);
            }
        } else {
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

    public void cacheListState() {
        nameChoosingAdapter.cacheState(settings);
    }

    @Override
    public void onResume() {
        super.onResume();
        canShow = true;
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
        ButterKnife.unbind(this);
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
