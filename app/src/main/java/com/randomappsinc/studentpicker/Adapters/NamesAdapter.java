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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class NamesAdapter extends BaseAdapter
{
    private Context context;
    private List<String> content;
    private TextView noContent;
    private String listName;
    private DataSource dataSource;

    public NamesAdapter(Context context, TextView noContent, String listName)
    {
        this.context = context;
        this.dataSource = new DataSource(context);
        this.content = dataSource.getAllNamesInList(listName);
        this.noContent = noContent;
        setNoContent();
        this.listName = listName;
    }

    public void setNoContent()
    {
        int viewVisibility = content.isEmpty() ? View.VISIBLE : View.GONE;
        noContent.setVisibility(viewVisibility);
    }

    public void addStudent(String name)
    {
        dataSource.addName(name, listName);
        content.add(name);
        setNoContent();
        notifyDataSetChanged();
    }

    public void removeStudent(int index)
    {
        dataSource.removeName(content.get(index), listName);
        content.remove(index);
        notifyDataSetChanged();
        setNoContent();
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
        @Bind(R.id.name) TextView name;
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

        holder.name.setText(content.get(position));
        final int _position = position;
        final View _v = view;
        holder.delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
                // Disable clicking of the -, so they don't spam it
                view.setEnabled(false);
                // Make item fade out smoothly as opposed to just vanishing
                _v.animate().setDuration(250).alpha(0).withEndAction(new Runnable()
                {
                    public void run()
                    {
                        removeStudent(_position);
                        _v.setAlpha(1);
                        // Re-enable it after the row disappears
                        view.setEnabled(true);
                    }
                });
            }
        });

        return view;
    }
}
