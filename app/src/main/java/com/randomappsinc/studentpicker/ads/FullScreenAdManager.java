package com.randomappsinc.studentpicker.ads;

import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.randomappsinc.studentpicker.BuildConfig;

public class FullScreenAdManager {

    private static final String DEBUG_AD_ID = "ca-app-pub-3940256099942544/1033173712";
    private static final String PROD_AD_ID = "ca-app-pub-3903394384020535/9821415168";

    private InterstitialAd interstitialAd;

    public FullScreenAdManager(Context context) {
        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(BuildConfig.DEBUG ? DEBUG_AD_ID : PROD_AD_ID);
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load another ad when the ad is closed
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    public boolean showAd() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
            return true;
        }
        return false;
    }
}
