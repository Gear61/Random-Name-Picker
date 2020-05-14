package com.randomappsinc.studentpicker.premium;

import android.content.Context;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.ads.FullScreenAdManager;
import com.randomappsinc.studentpicker.common.PremiumFeature;
import com.randomappsinc.studentpicker.utils.PreferencesManager;

public class PremiumFeatureOpener {

    public interface Delegate {
        void launchBuyPremiumPage();
    }

    public interface FeatureHandler {
        void openFeature();
    }

    private Delegate delegate;
    private FullScreenAdManager fullScreenAdManager;
    private PreferencesManager preferencesManager;
    private MaterialDialog unlockOptionsDialog;

    public PremiumFeatureOpener(Context context, Delegate delegate) {
        this.delegate = delegate;
        this.preferencesManager = new PreferencesManager(context);
        if (preferencesManager.isOnFreeVersion()) {
            unlockOptionsDialog = new MaterialDialog.Builder(context)
                    .title(R.string.unlock_premium_feature_options_prompt)
                    .items(R.array.unlock_premium_feature_options_list)
                    .itemsCallback((dialog, itemView, position, text) -> {
                        switch (position) {
                            case 0:
                                delegate.launchBuyPremiumPage();
                                break;
                            case 1:
                                break;
                        }
                    })
                    .positiveText(R.string.cancel)
                    .build();
        }
    }

    public void openPremiumFeature(@PremiumFeature String feature, FeatureHandler featureHandler) {
        if (preferencesManager.hasUnlockedFeature(feature)) {
            featureHandler.openFeature();
        } else {
            unlockOptionsDialog.show();
        }
    }
}
