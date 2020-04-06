package com.randomappsinc.studentpicker.ads;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.randomappsinc.studentpicker.BuildConfig;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.utils.PreferencesManager;

public class BannerAdManager extends AdListener {

    private static final String DEBUG_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";
    private static final String PROD_BANNER_AD_UNIT_ID = "ca-app-pub-3903394384020535/3516424944";

    private ViewGroup adContainerView;
    private PreferencesManager preferencesManager;
    private @Nullable AdView adView;

    public BannerAdManager(ViewGroup adContainerView) {
        this.adContainerView = adContainerView;
        this.preferencesManager = new PreferencesManager(adContainerView.getContext());
    }

    private void maybeSetUpAd() {
        if (adView != null) {
            adContainerView.removeView(adView);
        }

        if (preferencesManager.shouldShowAds()) {
            Context context = adContainerView.getContext();
            adView = new AdView(context);
            adView.setAlpha(0f);
            adView.setAdUnitId(BuildConfig.DEBUG ? DEBUG_BANNER_AD_UNIT_ID : PROD_BANNER_AD_UNIT_ID);

            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
            AdSize adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                    context, (int) screenWidthDp);
            adView.setAdSize(adSize);

            adContainerView.addView(adView);

            adView.setAdListener(this);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
    }

    @Override
    public void onAdLoaded() {
        if (adView != null) {
            adView.animate()
                    .alpha(1f)
                    .setDuration(adView.getResources().getInteger(R.integer.default_anim_length));
        }
    }

    // Try to load the ad if it hasn't already been loaded
    // We need to expose such an API because we shouldn't load ads upon the creation of fragments due to ViewPager
    public void maybeLoadAd() {
        if (adView == null) {
            maybeSetUpAd();
        }
    }

    // Refresh the ad to have a proper size for the orientation
    public void onOrientationChanged() {
        maybeSetUpAd();
    }
}
