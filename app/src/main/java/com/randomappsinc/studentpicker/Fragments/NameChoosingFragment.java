package com.randomappsinc.studentpicker.Fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.randomappsinc.studentpicker.Activities.MainActivity;
import com.randomappsinc.studentpicker.Adapters.NameChoosingAdapter;
import com.randomappsinc.studentpicker.Misc.Utils;
import com.randomappsinc.studentpicker.Models.EditListEvent;
import com.randomappsinc.studentpicker.Models.NameChoosingSettingsViewHolder;
import com.randomappsinc.studentpicker.R;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by alexanderchiou on 10/18/15.
 */
public class NameChoosingFragment extends Fragment implements TextToSpeech.OnInitListener {
    @Bind(R.id.no_content) TextView noContent;
    @Bind(R.id.names_list) ListView namesList;
    @Bind(R.id.parent) View parent;

    @BindString(R.string.name_chosen) String nameChosenTitle;
    @BindString(R.string.names_chosen) String namesChosenTitle;

    private NameChoosingAdapter nameChoosingAdapter;
    private boolean withReplacement;
    private int numNamesChosen;
    private MaterialDialog settingsDialog;
    private NameChoosingSettingsViewHolder settingsHolder;
    private TextToSpeech textToSpeech;
    private boolean textToSpeechEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
        withReplacement = false;
        numNamesChosen = 1;
        settingsDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.name_choosing_settings)
                .customView(R.layout.name_choosing_settings, true)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        applySettings();
                        Utils.showSnackbar(parent, getString(R.string.settings_applied));
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        revertSettings();
                    }
                })
                .build();
        settingsHolder = new NameChoosingSettingsViewHolder(settingsDialog.getCustomView());
        textToSpeech = new TextToSpeech(getActivity(), this);
        textToSpeech.setLanguage(Locale.US);
    }

    public void applySettings() {
        withReplacement = settingsHolder.withReplacement.isChecked();
        String numChosenText = settingsHolder.numChosen.getText().toString();
        if (numChosenText.isEmpty()) {
            settingsHolder.numChosen.setText("1");
            numNamesChosen = 1;
        }
        else {
            int userNumNames = Integer.parseInt(settingsHolder.numChosen.getText().toString());
            if (userNumNames == 0) {
                settingsHolder.numChosen.setText("1");
                numNamesChosen = 1;
            }
            else {
                numNamesChosen = userNumNames;
            }
        }
        settingsHolder.numChosen.clearFocus();
    }

    public void revertSettings() {
        settingsHolder.withReplacement.setChecked(withReplacement);
        settingsHolder.numChosen.setText(String.valueOf(numNamesChosen));
        settingsHolder.numChosen.clearFocus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.name_choosing, container, false);
        ButterKnife.bind(this, rootView);
        Bundle bundle = getArguments();
        String listName = bundle.getString(MainActivity.LIST_NAME_KEY, "");
        nameChoosingAdapter = new NameChoosingAdapter(getActivity(), noContent, listName);
        namesList.setAdapter(nameChoosingAdapter);
        return rootView;
    }

    @OnClick(R.id.choose)
    public void choose() {
        if (nameChoosingAdapter.getCount() > 0) {
            List<Integer> chosenIndexes = Utils.getRandomNumsInRange(numNamesChosen, nameChoosingAdapter.getCount() - 1);
            final String chosenNames = nameChoosingAdapter.chooseNamesAtRandom(chosenIndexes, withReplacement);
            String title = chosenIndexes.size() == 1 ? nameChosenTitle : namesChosenTitle;
            String sayNames = chosenIndexes.size() == 1 ? getString(R.string.say_name) : getString(R.string.say_names);
            new MaterialDialog.Builder(getActivity())
                    .title(title)
                    .content(chosenNames)
                    .positiveText(android.R.string.yes)
                    .neutralText(R.string.copy_text)
                    .negativeText(sayNames)
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            copyNamesToClipboard(chosenNames);
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            sayNames(chosenNames);
                        }
                    })
                    .show();
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
            Utils.showSnackbar(parent, getString(R.string.text_to_speech_fail));
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

    public void copyNamesToClipboard(String names) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.chosen_names), names);
        clipboard.setPrimaryClip(clip);
        Utils.showSnackbar(parent, getString(R.string.copy_confirmation));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Utils.hideKeyboard(getActivity());
            getActivity().invalidateOptionsMenu();
        }
    }

    public void onEvent(EditListEvent event) {
        switch (event.getEventType()) {
            case EditListEvent.ADD:
                nameChoosingAdapter.addName(event.getName());
                break;
            case EditListEvent.REMOVE:
                nameChoosingAdapter.removeName(event.getName());
                break;
            case EditListEvent.RENAME:
                nameChoosingAdapter.changeName(event.getName(), event.getNewName());
                break;
            case EditListEvent.RENAME_LIST:
                nameChoosingAdapter.processListNameChange(event.getNewName());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        nameChoosingAdapter.cacheNamesList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.name_choosing_menu, menu);
        menu.findItem(R.id.silence).setIcon(
                new IconDrawable(getActivity(), FontAwesomeIcons.fa_volume_off)
                        .colorRes(R.color.white)
                        .actionBarSize());
        menu.findItem(R.id.settings).setIcon(
                new IconDrawable(getActivity(), FontAwesomeIcons.fa_gear)
                        .colorRes(R.color.white)
                        .actionBarSize());
        menu.findItem(R.id.reset).setIcon(
                new IconDrawable(getActivity(), FontAwesomeIcons.fa_rotate_right)
                        .colorRes(R.color.white)
                        .actionBarSize());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.silence:
                textToSpeech.stop();
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
