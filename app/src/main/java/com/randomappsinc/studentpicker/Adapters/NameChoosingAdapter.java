package com.randomappsinc.studentpicker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.randomappsinc.studentpicker.Database.DataSource;
import com.randomappsinc.studentpicker.R;

import java.util.Collections;
import java.util.List;

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

    public NameChoosingAdapter(Context context, TextView noContent, String listName)
    {
        this.outOfNames = context.getString(R.string.out_of_names);
        this.noNames = context.getString(R.string.no_names);
        this.context = context;
        this.listName = listName;
        this.dataSource = new DataSource(context);
        this.content = dataSource.getAllNamesInList(listName);
        this.noContent = noContent;
        setNoContent();
    }

    public void addName(String name)
    {
        content.add(name);
        notifyDataSetChanged();
        setNoContent();
    }

    public void removeName(String name)
    {
        for (int i = 0; i < content.size(); i++)
        {
            if (content.get(i).equals(name))
            {
                content.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
        setNoContent();
    }

    public void setNoContent()
    {
        if (dataSource.getAllNamesInList(listName).isEmpty()) {
            noContent.setText(noNames);
        }
        else {
            noContent.setText(outOfNames);
        }
        int viewVisibility = content.isEmpty() ? View.VISIBLE : View.GONE;
        noContent.setVisibility(viewVisibility);
    }

    public String chooseNamesAtRandom(List<Integer> indexes, boolean withReplacement) {
        StringBuilder chosenNames = new StringBuilder();
        for (int i = 0; i < indexes.size(); i++) {
            if (i != 0) {
                chosenNames.append("\n");
            }
            chosenNames.append(content.get(indexes.get(i)));
        }
        // If without replacement, remove the names
        if (!withReplacement) {
            removeNamesAtPositions(indexes);
        }
        return chosenNames.toString();
    }

    public void removeNamesAtPositions(List<Integer> indexes)
    {
        Collections.sort(indexes);
        Collections.reverse(indexes);
        for (int index : indexes) {
            content.remove(index);
        }
        notifyDataSetChanged();
        setNoContent();
    }

    public void resetStudents()
    {
        content.clear();
        content.addAll(this.dataSource.getAllNamesInList(listName));
        setNoContent();
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