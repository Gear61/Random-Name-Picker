package com.randomappsinc.studentpicker.payments;

import android.os.Bundle;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.common.StandardActivity;

import butterknife.ButterKnife;

public class BuyPremiumActivity extends StandardActivity {

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
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_from_top);
    }
}
