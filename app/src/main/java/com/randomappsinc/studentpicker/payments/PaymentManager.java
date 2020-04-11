package com.randomappsinc.studentpicker.payments;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
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
        SkuDetailsResponseListener, AcknowledgePurchaseResponseListener {

    private static final String TAG = PaymentManager.class.getSimpleName();
    private static final String NINETY_NINE_CENT_PREMIUM = "99_cent_premium";

    public interface Listener {
        void onPurchaseSuccess();
    }

    private Activity activity;
    private BillingClient billingClient;
    private PreferencesManager preferencesManager;
    private Listener listener;

    PaymentManager(Activity activity, Listener listener) {
        this.activity = activity;
        this.billingClient = BillingClient
                .newBuilder(activity)
                .enablePendingPurchases()
                .setListener(this)
                .build();
        this.preferencesManager = new PreferencesManager(activity);
        this.listener = listener;
    }

    void startPaymentFlow() {
        billingClient.startConnection(this);
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            preferencesManager.setIsOnFreeVersion(false);

            for (Purchase purchase : purchases) {
                // This call only returns unacknowledged purchases
                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    AcknowledgePurchaseParams ackParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
                    billingClient.acknowledgePurchase(ackParams, this);
                }
            }

            listener.onPurchaseSuccess();
        } else if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.USER_CANCELED) {
            UIUtils.showLongToast(R.string.payment_failed, activity);
        }
    }

    @Override
    public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            Log.d(TAG, "Purchase failed. Response code: " + billingResult.getResponseCode()
                    + " || Debug message: " + billingResult.getDebugMessage());
        }
    }

    @Override
    public void onBillingSetupFinished(BillingResult billingResult) {
        // Acknowledge any purchases that haven't been acknowledged already
        Purchase.PurchasesResult purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
        List<Purchase> purchasesList = purchases.getPurchasesList();
        for (Purchase purchase : purchasesList) {
            AcknowledgePurchaseParams ackParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.getPurchaseToken())
                    .build();
            billingClient.acknowledgePurchase(ackParams, this);
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

    void cleanUp() {
        billingClient.endConnection();
        activity = null;
    }
}
