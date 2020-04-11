package com.randomappsinc.studentpicker.payments;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BuyPremiumActivity extends AppCompatActivity implements PaymentManager.Listener {

    @BindView(R.id.buy) View buyButton;
    @BindView(R.id.buy_premium_intro) View buyPremiumIntro;
    @BindView(R.id.buy_premium_body) View buyPremiumBody;
    @BindView(R.id.post_payment_thank_you) View thankYouTitle;
    @BindView(R.id.post_payment_message) View thankYouMessage;

    private PaymentManager paymentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_premium_activity);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()
                .setHomeAsUpIndicator(new IconDrawable(this, IoniconsIcons.ion_android_close)
                        .colorRes(R.color.white)
                        .actionBarSize());

        paymentManager = new PaymentManager(this, this);
        paymentManager.setUpAndCheckForPremium();
    }

    private void showThankYou() {
        buyButton.setVisibility(View.GONE);
        buyPremiumIntro.setVisibility(View.GONE);
        buyPremiumBody.setVisibility(View.GONE);
        thankYouTitle.setVisibility(View.VISIBLE);
        thankYouMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPremiumPurchaseSuccessful() {
        showThankYou();
    }

    @Override
    public void onPremiumAlreadyOwned() {
        showThankYou();
        new MaterialDialog.Builder(this)
                .title(R.string.thank_you_for_support)
                .content(R.string.premium_detected_buy_page)
                .positiveText(R.string.okay)
                .show();
    }

    @Override
    public void onPaymentFailed() {
        UIUtils.showLongToast(R.string.payment_failed, this);
    }

    @Override
    public void onStartupFailed() {
        UIUtils.showLongToast(R.string.payment_flow_startup_failed, this);
    }

    @OnClick(R.id.buy)
    public void buy() {
        paymentManager.startPaymentFlow();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_from_top);
        paymentManager.cleanUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
