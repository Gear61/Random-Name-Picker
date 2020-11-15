package com.randomappsinc.studentpicker.settings;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.backupandrestore.BackupAndRestoreActivity;
import com.randomappsinc.studentpicker.premium.BuyPremiumActivity;
import com.randomappsinc.studentpicker.premium.PremiumFeatureOpener;
import com.randomappsinc.studentpicker.theme.ThemeManager;
import com.randomappsinc.studentpicker.theme.ThemeMode;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SettingsFragment extends Fragment implements SettingsAdapter.ItemSelectionListener {

    public static SettingsFragment getInstance() {
        return new SettingsFragment();
    }

    private static final String SUPPORT_EMAIL = "RandomAppsInc61@gmail.com";
    private static final String OTHER_APPS_URL = "https://play.google.com/store/apps/dev?id=9093438553713389916";
    private static final String REPO_URL = "https://github.com/Gear61/Random-Name-Picker";

    @BindView(R.id.settings_options) RecyclerView settingsOptions;
    @BindString(R.string.feedback_subject) String feedbackSubject;
    @BindString(R.string.premium_feedback_subject) String premiumFeedbackSubject;
    @BindString(R.string.send_email) String sendEmail;

    @BindDrawable(R.drawable.line_divider) Drawable lineDivider;

    private PreferencesManager preferencesManager;
    private SettingsAdapter settingsAdapter;
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.settings,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        preferencesManager = new PreferencesManager(getContext());
        DividerItemDecoration itemDecorator =
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(lineDivider);
        settingsOptions.addItemDecoration(itemDecorator);
        settingsAdapter = new SettingsAdapter(getContext(), this);
        settingsOptions.setAdapter(settingsAdapter);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = null;
        // Everything is moved up by 1 if the user has already bought premium
        int delta = preferencesManager.isOnFreeVersion() ? 0 : 1;
        switch (position + delta) {
            case 0:
                intent = new Intent(getActivity(), BuyPremiumActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
                break;
            case 1:
                PremiumFeatureOpener.openFeature(R.string.backup_and_restore_feature_name, requireActivity(), () -> {
                    Intent backupRestoreIntent = new Intent(getActivity(), BackupAndRestoreActivity.class);
                    startActivity(backupRestoreIntent);
                    requireActivity().overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in);
                });
                return;
            case 2:
                int darkModePosition = preferencesManager.isOnFreeVersion() ? 2 : 1;
                View secondCell = settingsOptions.getChildAt(darkModePosition);
                Switch darkModeToggle = secondCell.findViewById(R.id.toggle);
                darkModeToggle.setChecked(!darkModeToggle.isChecked());
                int themeMode = darkModeToggle.isChecked() ? ThemeMode.DARK : ThemeMode.LIGHT;
                preferencesManager.setThemeMode(themeMode);
                ThemeManager.applyTheme(themeMode);
                return;
            case 3:
                int shakePosition = preferencesManager.isOnFreeVersion() ? 3 : 2;
                View firstCell = settingsOptions.getChildAt(shakePosition);
                Switch shakeToggle = firstCell.findViewById(R.id.toggle);
                boolean currentState = shakeToggle.isChecked();
                shakeToggle.setChecked(!currentState);
                preferencesManager.setShakeEnabled(!currentState);
                return;
            case 4:
                String uriText = "mailto:" + SUPPORT_EMAIL
                        + "?subject=" + Uri.encode(preferencesManager.isOnFreeVersion()
                        ? feedbackSubject
                        : premiumFeedbackSubject);
                Uri mailUri = Uri.parse(uriText);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO, mailUri);
                sendIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(Intent.createChooser(sendIntent, sendEmail));
                return;
            case 5:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(OTHER_APPS_URL));
                break;
            case 6:
                Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
                intent = new Intent(Intent.ACTION_VIEW, uri);
                if (!(getContext().getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                    UIUtils.showLongToast(R.string.play_store_error, getContext());
                    return;
                }
                break;
            case 7:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(REPO_URL));
                break;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        settingsAdapter.maybeRefreshList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
