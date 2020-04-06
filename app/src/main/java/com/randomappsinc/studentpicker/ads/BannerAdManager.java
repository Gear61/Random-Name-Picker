package com.randomappsinc.studentpicker.ads;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.randomappsinc.studentpicker.BuildConfig;
import com.randomappsinc.studentpicker.R;

public class BannerAdManager extends AdListener {

    public static final String DEBUG_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";
    public static final String PROD_BANNER_AD_UNIT_ID = "ca-app-pub-3903394384020535/3516424944";

    private static @Nullable AdSize sAdSize;

    private AdView adView;

    public BannerAdManager(ViewGroup rootView) {
        adView = new AdView(rootView.getContext());
        adView.setAdUnitId(BuildConfig.DEBUG ? DEBUG_BANNER_AD_UNIT_ID : PROD_BANNER_AD_UNIT_ID);
        adView.setVisibility(View.VISIBLE);
        adView.setBackgroundColor(ContextCompat.getColor(rootView.getContext(), R.color.app_blue));

        int screenWidthPixels = Resources.getSystem().getDisplayMetrics().widthPixels;

        rootView.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    public void onAdLoaded() {
        // Fade ad in
        System.out.println("FADING THE AD IN!!!");
        adView.setVisibility(View.VISIBLE);
    }

    // Refresh the ad to account for ads being turned off and orientation changes
    public void refreshAd() {

    }
}
