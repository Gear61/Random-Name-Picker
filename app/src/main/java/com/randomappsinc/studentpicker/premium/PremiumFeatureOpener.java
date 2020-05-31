package com.randomappsinc.studentpicker.premium;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.StringRes;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;

public class PremiumFeatureOpener {

    public interface Delegate {
        void openFeature();
    }

    public static void openFeature(@StringRes int featureNameId, Activity activity, Delegate delegate) {
        PreferencesManager preferencesManager = new PreferencesManager(activity);
        if (preferencesManager.isOnFreeVersion()) {
            String featureName = activity.getString(featureNameId);
            String message = activity.getString(R.string.premium_needed_message, featureName);
            UIUtils.showLongToast(message, activity);
            Intent intent = new Intent(activity, BuyPremiumActivity.class);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
            return;
        }

        delegate.openFeature();
    }
}
