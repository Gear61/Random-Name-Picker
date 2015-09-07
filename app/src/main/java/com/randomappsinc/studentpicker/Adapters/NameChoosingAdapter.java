package com.randomappsinc.studentpicker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
    public static final String OUT_OF_NAMES = "You have ran out of names to choose from.";

    private Context context;
    private List<String> initialStudents;
    private List<String> content;
    private TextView noContent;
    private DataSource dataSource;
    private ListView listView;

    public NameChoosingAdapter(Context context, TextView noContent, String listName, ListView listView)
    {
        this.context = context;
        this.dataSource = new DataSource(context);
        this.initialStudents = this.dataSource.getAllNamesInList(listName);
        this.content = this.dataSource.getAllNamesInList(listName);
        this.noContent = noContent;
        setNoContent(true);
        this.listView = listView;
    }

    public void setNoContent(boolean firstTime)
    {
        if (!firstTime)
        {
            noContent.setText(OUT_OF_NAMES);
        }
        int viewVisibility = content.isEmpty() ? View.VISIBLE : View.GONE;
        noContent.setVisibility(viewVisibility);
    }

    public String chooseStudentAtRandom(boolean withReplacement)
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
                    removeStudent(randomInt);
                    view.setAlpha(1);
                    // Re-enable it after the row disappears
                    view.setEnabled(true);
                }
            });
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
        @Bind(R.id.name) TextView itemName;
        @Bind(R.id.delete) ImageView delete;

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
        holder.delete.setVisibility(View.GONE);

        return view;
    }
}