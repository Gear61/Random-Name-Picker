package com.randomappsinc.studentpicker.payments;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the donation flow, including all success/error messaging.
 * Make sure to call cleanUp() to prevent leaks.
 */
public class PaymentManager implements PurchasesUpdatedListener, BillingClientStateListener,
        SkuDetailsResponseListener, ConsumeResponseListener {

    private static final String TAG = PaymentManager.class.getSimpleName();
    private static final String NINETY_NINE_CENT_PREMIUM = "99_cent_premium";

    private Activity activity;
    private BillingClient billingClient;
    private PreferencesManager preferencesManager;

    public PaymentManager(Activity activity) {
        this.activity = activity;
        this.billingClient = BillingClient
                .newBuilder(activity)
                .enablePendingPurchases()
                .setListener(this)
                .build();
        this.preferencesManager = new PreferencesManager(activity);
    }

    public void startPaymentFlow() {
        billingClient.startConnection(this);
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            UIUtils.showLongToast(R.string.payment_thank_you, activity);
            preferencesManager.setIsOnFreeVersion(false);

            for (Purchase purchase : purchases) {
                // This call only returns unconsumed purchases
                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    ConsumeParams consumeParams = ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
                    billingClient.consumeAsync(consumeParams, this);
                }
            }
        } else if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.USER_CANCELED) {
            UIUtils.showLongToast(R.string.payment_failed, activity);
        }
    }

    @Override
    public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            Log.d(TAG, "Purchase failed. Response code: " + billingResult.getResponseCode()
                    + " || Purchase token: " + purchaseToken);
        }
    }

    @Override
    public void onBillingSetupFinished(BillingResult billingResult) {
        // Consume any purchases that haven't been consumed already
        Purchase.PurchasesResult purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
        List<Purchase> purchasesList = purchases.getPurchasesList();
        for (Purchase purchase : purchasesList) {
            ConsumeParams consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(purchase.getPurchaseToken())
                    .build();
            billingClient.consumeAsync(consumeParams, this);
        }

        List<String> skuList = new ArrayList<>();
        skuList.add(NINETY_NINE_CENT_PREMIUM);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(), this);
    }

    @Override
    public void onBillingServiceDisconnected() {
        UIUtils.showLongToast(R.string.payment_flow_startup_failed, activity);
    }

    @Override
    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            UIUtils.showLongToast(R.string.payment_flow_startup_failed, activity);
            return;
        }

        for (SkuDetails skuDetails : skuDetailsList) {
            String sku = skuDetails.getSku();
            if (NINETY_NINE_CENT_PREMIUM.equals(sku)) {
                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .build();
                billingClient.launchBillingFlow(activity, flowParams);
            }
        }
    }

    public void cleanUp() {
        billingClient.endConnection();
        activity = null;
    }
}
