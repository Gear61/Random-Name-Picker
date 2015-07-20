package com.randomappsinc.studentpicker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.randomappsinc.studentpicker.Database.DataSource;
import com.randomappsinc.studentpicker.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class StudentChoosingAdapter extends BaseAdapter
{
    public static final String OUT_OF_STUDENTS = "You have ran out of students to call on.";

    private Context context;
    private List<String> initialStudents;
    private List<String> content;
    private TextView noContent;
    private String listName;
    private DataSource dataSource;

    public StudentChoosingAdapter(Context context, TextView noContent, String listName)
    {
        this.context = context;
        this.dataSource = new DataSource(context);
        this.initialStudents = this.dataSource.getAllStudents(listName);
        this.content = this.dataSource.getAllStudents(listName);
        this.noContent = noContent;
        setNoContent(true);
        this.listName = listName;
    }

    public void setNoContent(boolean firstTime)
    {
        if (!firstTime)
        {
            noContent.setText(OUT_OF_STUDENTS);
        }
        int viewVisibility = content.isEmpty() ? View.VISIBLE : View.GONE;
        noContent.setVisibility(viewVisibility);
    }

    public String chooseStudentAtRandom(boolean withReplacement)
    {
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(getCount());
        String randomStudent = content.get(randomInt);
        // If without replacement, remove the student
        if (!withReplacement)
        {
            removeStudent(randomInt);
        }
        return randomStudent;
    }

    public void removeStudent(int index)
    {
        content.remove(index);
        notifyDataSetChanged();
        setNoContent(false);
    }

    public void resetStudents()
    {
        content.clear();
        content.addAll(initialStudents);
        setNoContent(true);
        notifyDataSetChanged();
    }

    public int getCount()
    {
        return content.size();
    }

    public String getItem(int position)
    {
        return content.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder
    {
        public TextView itemName;
        public ImageView delete;
    }

    // Renders the ListView item that the user has scrolled to or is about to scroll to
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        final ViewHolder holder;
        if (v == null)
        {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.itemName = (TextView) v.findViewById(R.id.item_name);
            holder.delete = (ImageView) v.findViewById(R.id.delete);
            v.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) v.getTag();
        }

        holder.itemName.setText(content.get(position));

        return v;
    }
}