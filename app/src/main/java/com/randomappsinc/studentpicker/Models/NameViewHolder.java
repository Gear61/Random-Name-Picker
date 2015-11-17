package com.randomappsinc.studentpicker.Models;

import android.view.View;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.randomappsinc.studentpicker.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 11/16/15.
 */
public class NameViewHolder {
    @Bind(R.id.person_name) public TextView name;
    @Bind(R.id.delete_icon) public IconTextView delete;

    public NameViewHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
