package com.randomappsinc.studentpicker.Models;

import android.view.View;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.FontAwesomeText;
import com.randomappsinc.studentpicker.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 10/13/15.
 */
public class FontAwesomeViewHolder
{
    @Bind(R.id.item_icon) public FontAwesomeText itemIcon;
    @Bind(R.id.item_name) public TextView itemName;

    public FontAwesomeViewHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
