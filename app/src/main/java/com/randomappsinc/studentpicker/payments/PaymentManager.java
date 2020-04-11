package com.randomappsinc.studentpicker.payments;

import android.app.Activity;

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
import com.randomappsinc.studentpicker.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the payment flow.
 * Make sure to call cleanUp() to prevent leaks.
 */
public class PaymentManager implements PurchasesUpdatedListener, BillingClientStateListener,
        SkuDetailsResponseListener, AcknowledgePurchaseResponseListener {

    private static final String NINETY_NINE_CENT_PREMIUM = "99_cent_premium";

    public interface Listener {
        void onPremiumPurchaseSuccessful();

        void onPremiumAlreadyOwned();

        void onPaymentFailed();

        void onStartupFailed();
    }

    private Activity activity;
    private BillingClient billingClient;
    private PreferencesManager preferencesManager;
    private Listener listener;
    private @Nullable BillingFlowParams billingFlowParams;

    public PaymentManager(Activity activity, Listener listener) {
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
        if (billingFlowParams != null) {
            billingClient.launchBillingFlow(activity, billingFlowParams);
        } else {
            listener.onStartupFailed();
        }
    }

    public void setUpAndCheckForPremium() {
        if (preferencesManager.isOnFreeVersion()) {
            billingClient.startConnection(this);
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                // This call only returns unacknowledged purchases
                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    AcknowledgePurchaseParams ackParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
                    billingClient.acknowledgePurchase(ackParams, this);
                }
            }
            preferencesManager.setIsOnFreeVersion(false);
            listener.onPremiumPurchaseSuccessful();
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            preferencesManager.setIsOnFreeVersion(false);
            listener.onPremiumAlreadyOwned();
        } else if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.USER_CANCELED) {
            listener.onPaymentFailed();
        }
    }

    @Override
    public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            preferencesManager.setIsOnFreeVersion(false);
            listener.onPremiumAlreadyOwned();
        }
    }

    // Need to get to this point to call APIs
    @Override
    public void onBillingSetupFinished(BillingResult billingResult) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            preferencesManager.setIsOnFreeVersion(false);
            listener.onPremiumAlreadyOwned();
            return;
        }

        // Acknowledge any purchases that haven't been acknowledged already
        Purchase.PurchasesResult purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
        List<Purchase> purchasesList = purchases.getPurchasesList();
        for (Purchase purchase : purchasesList) {
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                if (purchase.isAcknowledged()) {
                    AcknowledgePurchaseParams ackParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
                    billingClient.acknowledgePurchase(ackParams, this);
                }
                preferencesManager.setIsOnFreeVersion(false);
                listener.onPremiumAlreadyOwned();
            }
        }

        List<String> skuList = new ArrayList<>();
        skuList.add(NINETY_NINE_CENT_PREMIUM);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(), this);
    }

    @Override
    public void onBillingServiceDisconnected() {
        listener.onPaymentFailed();
    }

    @Override
    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            preferencesManager.setIsOnFreeVersion(false);
            listener.onPremiumAlreadyOwned();
        } else if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            listener.onStartupFailed();
            return;
        }

        for (SkuDetails skuDetails : skuDetailsList) {
            String sku = skuDetails.getSku();
            if (NINETY_NINE_CENT_PREMIUM.equals(sku)) {
                billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .build();
            }
        }
    }

    public void cleanUp() {
        billingClient.endConnection();
        activity = null;
    }
}
