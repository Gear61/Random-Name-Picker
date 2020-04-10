package com.randomappsinc.studentpicker.payments;

import android.os.Bundle;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.common.StandardActivity;

import butterknife.ButterKnife;

public class BuyPremiumActivity extends StandardActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_premium_activity);
        ButterKnife.bind(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_from_top);
    }
}
