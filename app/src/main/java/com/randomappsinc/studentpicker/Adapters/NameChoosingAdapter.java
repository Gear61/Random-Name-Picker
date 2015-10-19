package com.randomappsinc.studentpicker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.randomappsinc.studentpicker.Database.DataSource;
import com.randomappsinc.studentpicker.Misc.Utils;
import com.randomappsinc.studentpicker.R;

import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class NameChoosingAdapter extends BaseAdapter
{
    private String outOfNames;
    private String noNames;

    private Context context;
    private String listName;
    private List<String> content;
    private TextView noContent;
    private DataSource dataSource;
    private ListView listView;

    public NameChoosingAdapter(Context context, TextView noContent, String listName, ListView listView)
    {
        this.outOfNames = context.getString(R.string.out_of_names);
        this.noNames = context.getString(R.string.no_names);
        this.context = context;
        this.listName = listName;
        this.dataSource = new DataSource(context);
        this.content = dataSource.getAllNamesInList(listName);
        this.noContent = noContent;
        setNoContent(true);
        this.listView = listView;
    }

    public void addName(String name)
    {
        content.add(name);
        notifyDataSetChanged();
        setNoContent(true);
    }

    public void removeName(String name)
    {
        for (int i = 0; i < content.size(); i++)
        {
            if (content.get(i).equals(name))
            {
                content.remove(i);
                notifyDataSetChanged();
                setNoContent(true);
                return;
            }
        }
    }

    public void setNoContent(boolean toggleNoContent)
    {
        if (!toggleNoContent) {
            noContent.setText(outOfNames);
        }
        else if (dataSource.getAllNamesInList(listName).isEmpty()) {
            noContent.setText(noNames);
        }
        int viewVisibility = content.isEmpty() ? View.VISIBLE : View.GONE;
        noContent.setVisibility(viewVisibility);
    }

    public String chooseNameAtRandom(boolean withReplacement)
    {
        Random randomGenerator = new Random();
        final int randomInt = randomGenerator.nextInt(getCount());
        String randomStudent = content.get(randomInt);
        // If without replacement, remove the student
        if (!withReplacement)
        {
            final View view = Utils.getViewByPosition(randomInt, listView);
            // Disable clicking of the -, so they don't spam it
            view.setEnabled(false);
            // Make item fade out smoothly as opposed to just vanishing
            view.animate().setDuration(250).alpha(0).withEndAction(new Runnable()
            {
                public void run()
                {
                    removeNameAtPosition(randomInt);
                    view.setAlpha(1);
                    // Re-enable it after the row disappears
                    view.setEnabled(true);
                }
            });
        }
        return randomStudent;
    }

    public void removeNameAtPosition(int index)
    {
        content.remove(index);
        notifyDataSetChanged();
        setNoContent(false);
    }

    public void resetStudents()
    {
        content.clear();
        content.addAll(this.dataSource.getAllNamesInList(listName));
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
        @Bind(R.id.name) TextView itemName;

        public ViewHolder(View view)
        {
            ButterKnife.bind(this, view);
        }
    }

    // Renders the ListView item that the user has scrolled to or is about to scroll to
    public View getView(int position, View view, ViewGroup parent)
    {
        final ViewHolder holder;
        if (view == null)
        {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.names_list_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }
        holder.itemName.setText(content.get(position));
        return view;
    }
}