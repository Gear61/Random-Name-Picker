package com.randomappsinc.studentpicker.premium;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.ads.FullScreenAdManager;
import com.randomappsinc.studentpicker.common.PremiumFeature;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;

public class PremiumFeatureOpener {

    public interface Delegate {
        void launchBuyPremiumPage();
    }

    public interface FeatureHandler {
        void openFeature();
    }

    private FullScreenAdManager fullScreenAdManager;
    private PreferencesManager preferencesManager;
    private MaterialDialog unlockOptionsDialog;
    private @PremiumFeature String currentFeature;

    public PremiumFeatureOpener(Context context, Delegate delegate) {
        this.preferencesManager = new PreferencesManager(context);
        this.fullScreenAdManager = new FullScreenAdManager(context);
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
                                if (fullScreenAdManager.showAd()) {
                                    preferencesManager.unlockFeature(currentFeature);
                                } else {
                                    UIUtils.showLongToast(R.string.ad_not_ready_yet, dialog.getContext());
                                }
                                break;
                        }
                    })
                    .positiveText(R.string.cancel)
                    .build();
        }
    }

    public void openPremiumFeature(@PremiumFeature String feature, FeatureHandler featureHandler) {
        currentFeature = feature;
        if (preferencesManager.hasUnlockedFeature(feature)) {
            featureHandler.openFeature();
        } else {
            unlockOptionsDialog.show();
        }
    }
}
