package com.randomappsinc.studentpicker.Adapters;

/**
 * Created by alexanderchiou on 7/20/15.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.randomappsinc.studentpicker.R;

import java.util.ArrayList;

/**
 * Created by alexanderchiou on 5/15/15.
 */
public class StudentListChoicesAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<String> studentListChoices;

    public StudentListChoicesAdapter(Context context, String listName)
    {
        this.context = context;
        this.studentListChoices = new ArrayList<>();
        studentListChoices.add("Choose students from \"" + listName + "\"");
        studentListChoices.add("Add/remove students from \"" + listName + "\"");
    }

    public int getCount() {
        return studentListChoices.size();
    }

    public String getItem(int position) {
        return studentListChoices.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder
    {
        public TextView action;
    }

    // Renders the ListView item that the user has scrolled to or is about to scroll to
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        final ViewHolder holder;
        if (v == null)
        {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.student_list_choice, parent, false);
            holder = new ViewHolder();
            holder.action = (TextView) v.findViewById(R.id.action);
            v.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) v.getTag();
        }

        holder.action.setText(studentListChoices.get(position));

        return v;
    }
}
