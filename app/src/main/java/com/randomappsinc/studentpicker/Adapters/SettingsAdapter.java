package com.randomappsinc.studentpicker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.randomappsinc.studentpicker.Models.FontAwesomeViewHolder;
import com.randomappsinc.studentpicker.R;

/**
 * Created by alexanderchiou on 10/13/15.
 */
public class SettingsAdapter extends BaseAdapter
{
    private String[] optionNames;
    private Context context;

    @SuppressWarnings("unchecked")
    public SettingsAdapter(Context context)
    {
        this.context = context;
        this.optionNames = context.getResources().getStringArray(R.array.settings_options);
    }

    @Override
    public int getCount()
    {
        return optionNames.length;
    }

    @Override
    public String getItem(int position)
    {
        return optionNames[position];
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    public View getView(int position, View view, ViewGroup parent)
    {
        FontAwesomeViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.font_awesome_list_item, parent, false);
            holder = new FontAwesomeViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (FontAwesomeViewHolder) view.getTag();
        }

        String tabName = optionNames[position];
        holder.itemName.setText(tabName);

        if (tabName.equals(context.getString(R.string.send_feedback)))
        {
            holder.itemIcon.setIcon(context.getString(R.string.email_icon));
        }
        else if (tabName.equals(context.getString(R.string.rate_this_app)))
        {
            holder.itemIcon.setIcon(context.getString(R.string.rate_icon));
        }
        else if (tabName.equals(context.getString(R.string.about_the_developer)))
        {
            holder.itemIcon.setIcon(context.getString(R.string.info_icon));
        }
        else if (tabName.equals(context.getString(R.string.source_code)))
        {
            holder.itemIcon.setIcon(context.getString(R.string.github_icon));
        }
        return view;
    }
}
