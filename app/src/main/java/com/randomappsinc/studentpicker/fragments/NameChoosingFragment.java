package com.randomappsinc.studentpicker.fragments;

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
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.activities.MainActivity;
import com.randomappsinc.studentpicker.activities.PresentationActivity;
import com.randomappsinc.studentpicker.adapters.NameChoosingAdapter;
import com.randomappsinc.studentpicker.models.ChoosingSettings;
import com.randomappsinc.studentpicker.models.ChoosingSettingsViewHolder;
import com.randomappsinc.studentpicker.utils.NameUtils;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;
import com.randomappsinc.studentpicker.views.ChoicesDisplayDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class NameChoosingFragment extends Fragment
        implements TextToSpeech.OnInitListener, ChoicesDisplayDialog.Listener {

    public static final String SCREEN_NAME = "Name Choosing Page";

    @BindView(R.id.no_content) TextView noContent;
    @BindView(R.id.num_names) TextView numNames;
    @BindView(R.id.names_list) ListView namesList;

    private NameChoosingAdapter mNameChoosingAdapter;
    private ChoosingSettings mSettings;
    private ChoosingSettingsViewHolder mSettingsHolder;
    private MaterialDialog mSettingsDialog;

    private ChoicesDisplayDialog mChoicesDisplayDialog;
    private TextToSpeech mTextToSpeech;
    private boolean mTextToSpeechEnabled;
    private boolean mCanShowPresentationScreen;
    private String mListName;
    private Unbinder mUnbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mSettingsDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.name_choosing_settings)
                .customView(R.layout.name_choosing_settings, true)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mSettingsHolder.applySettings();
                        UIUtils.showShortToast(R.string.settings_applied);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mSettingsHolder.revertSettings();
                    }
                })
                .cancelable(false)
                .build();
        mChoicesDisplayDialog = new ChoicesDisplayDialog(this, getActivity());

        mTextToSpeech = new TextToSpeech(getActivity(), this);
        mTextToSpeech.setLanguage(Locale.getDefault());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.name_choosing, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        mListName = getArguments().getString(MainActivity.LIST_NAME_KEY, "");
        mNameChoosingAdapter = new NameChoosingAdapter(getActivity(), noContent, numNames, mListName);
        namesList.setAdapter(mNameChoosingAdapter);

        mSettings = PreferencesManager.get().getChoosingSettings(mListName);
        mSettingsHolder = new ChoosingSettingsViewHolder(mSettingsDialog.getCustomView(), mSettings);

        return rootView;
    }

    public NameChoosingAdapter getNameChoosingAdapter() {
        return mNameChoosingAdapter;
    }

    @OnClick(R.id.choose)
    public void choose() {
        if (mNameChoosingAdapter.getCount() == 0) {
            return;
        }

        if (mSettings.getPresentationMode()) {
            if (!mCanShowPresentationScreen) {
                return;
            }
            mCanShowPresentationScreen = false;
            cacheListState();
            Intent intent = new Intent(getActivity(), PresentationActivity.class);
            intent.putExtra(PresentationActivity.LIST_NAME_KEY, mListName);
            getActivity().startActivity(intent);
        } else {
            if (mChoicesDisplayDialog.isShowing()) {
                return;
            }
            List<Integer> chosenIndexes = NameUtils.getRandomNumsInRange(mSettings.getNumNamesToChoose(),
                    mNameChoosingAdapter.getNumInstances() - 1);
            String chosenNames = mNameChoosingAdapter.chooseNamesAtRandom(chosenIndexes, mSettings);
            mChoicesDisplayDialog.showChoices(chosenNames, chosenIndexes.size());
            if (mSettings.getAutomaticTts()) {
                sayNames(chosenNames);
            }
        }
    }

    @Override
    public void sayNames(String names) {
        if (mTextToSpeechEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sayTextPostL(names);
            } else {
                sayTextPreL(names);
            }
        } else {
            UIUtils.showLongToast(R.string.text_to_speech_fail);
        }
    }

    @SuppressWarnings("deprecation")
    private void sayTextPreL(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, this.hashCode() + "");
        mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void sayTextPostL(String text) {
        String utteranceId = this.hashCode() + "";
        mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    @Override
    public void copyNamesToClipboard(String chosenNames, int numNames) {
        NameUtils.copyNamesToClipboard(chosenNames, null, numNames, false);
    }

    @Override
    public void onInit(int status) {
        mTextToSpeechEnabled = (status == TextToSpeech.SUCCESS);
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
        final String namesHistory = mNameChoosingAdapter.getNamesHistory();
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
                            mNameChoosingAdapter.clearNameHistory();
                            UIUtils.showShortToast(R.string.name_history_cleared);
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            NameUtils.copyNamesToClipboard(namesHistory, null, 0, true);
                        }
                    })
                    .show();
        } else {
            UIUtils.showLongToast(R.string.empty_names_history);
        }
    }

    public void cacheListState() {
        mNameChoosingAdapter.cacheState(mSettings);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCanShowPresentationScreen = true;
        mNameChoosingAdapter.resync();
    }

    @Override
    public void onPause() {
        super.onPause();
        cacheListState();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
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
                mSettingsDialog.show();
                return true;
            case R.id.reset:
                mNameChoosingAdapter.resetNames();
                return true;
            case android.R.id.home:
                getActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
