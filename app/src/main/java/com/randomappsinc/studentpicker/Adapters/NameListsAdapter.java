package com.randomappsinc.studentpicker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.randomappsinc.studentpicker.Database.DataSource;
import com.randomappsinc.studentpicker.Misc.PreferencesManager;
import com.randomappsinc.studentpicker.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class NameListsAdapter extends BaseAdapter
{
    private Context context;
    private List<String> content;
    private TextView noContent;
    private DataSource dataSource;

    public NameListsAdapter(Context context, TextView noContent)
    {
        this.context = context;
        this.content = new ArrayList<>();
        this.content.addAll(PreferencesManager.get().getStudentLists());
        this.noContent = noContent;
        setNoContent();
        this.dataSource = new DataSource(context);
    }

    public void setNoContent()
    {
        int viewVisibility = content.isEmpty() ? View.VISIBLE : View.GONE;
        noContent.setVisibility(viewVisibility);
    }

    public void addList(String itemName)
    {
        content.add(itemName);
        setNoContent();
        PreferencesManager.get().setStudentsList(new HashSet<>(content));
        notifyDataSetChanged();
    }

    public void removeList(int index)
    {
        dataSource.deleteList(content.get(index));
        content.remove(index);
        PreferencesManager.get().setStudentsList(new HashSet<>(content));
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
        @Bind(R.id.name) TextView listName;

        public ViewHolder(View view)
        {
            ButterKnife.bind(this, view);
        }
    }

    // Renders the ListView item that the user has scrolled to or is about to scroll to
    public View getView(final int position, View view, ViewGroup parent)
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
        holder.listName.setText(content.get(position));
        return view;
    }
}
