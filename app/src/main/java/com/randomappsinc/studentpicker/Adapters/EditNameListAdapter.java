package com.randomappsinc.studentpicker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.randomappsinc.studentpicker.Database.DataSource;
import com.randomappsinc.studentpicker.Models.EditListEvent;
import com.randomappsinc.studentpicker.Models.NameViewHolder;
import com.randomappsinc.studentpicker.R;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class EditNameListAdapter extends BaseAdapter
{
    private Context context;
    private List<String> content;
    private TextView noContent;
    private String listName;
    private DataSource dataSource;

    public EditNameListAdapter(Context context, TextView noContent, String listName) {
        this.context = context;
        this.dataSource = new DataSource(context);
        this.content = dataSource.getAllNamesInList(listName);
        this.noContent = noContent;
        setNoContent();
        this.listName = listName;
    }

    public void setNoContent() {
        int viewVisibility = content.isEmpty() ? View.VISIBLE : View.GONE;
        noContent.setVisibility(viewVisibility);
    }

    public void addStudent(String name) {
        dataSource.addName(name, listName);
        content.add(name);
        setNoContent();
        notifyDataSetChanged();
    }

    public void removeStudent(int index) {
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

    // Renders the ListView item that the user has scrolled to or is about to scroll to
    public View getView(final int position, View view, ViewGroup parent) {
        NameViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.person_name_cell, parent, false);
            holder = new NameViewHolder(view);
            view.setTag(holder);
        }
        else {
            holder = (NameViewHolder) view.getTag();
        }

        holder.name.setText(content.get(position));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                dataSource.removeName(content.get(position), listName);
                EventBus.getDefault().post(new EditListEvent(EditListEvent.REMOVE, getItem(position)));
                removeStudent(position);
            }
        });
        return view;
    }
}
