package com.randomappsinc.studentpicker.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.randomappsinc.studentpicker.Misc.PreferencesManager;
import com.randomappsinc.studentpicker.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class StudentListsAdapter extends BaseAdapter
{
    public static final String CONFIRM_DELETION = "Confirm Deletion";

    private Context context;
    private List<String> content;
    private TextView noContent;

    public StudentListsAdapter(Context context, TextView noContent)
    {
        this.context = context;
        content = new ArrayList<>();
        content.addAll(PreferencesManager.get().getStudentLists());
        this.noContent = noContent;
        setNoContent();
    }

    public void setNoContent()
    {
        int viewVisibility = content.isEmpty() ? View.VISIBLE : View.GONE;
        noContent.setVisibility(viewVisibility);
    }

    public void addItem(String itemName)
    {
        content.add(itemName);
        setNoContent();
        PreferencesManager.get().setStudentsList(new HashSet<>(content));
        notifyDataSetChanged();
    }

    public void removeItem(int index)
    {
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
        final int _position = position;
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                alertDialogBuilder.setTitle(CONFIRM_DELETION);
                alertDialogBuilder
                        .setMessage("Are you sure that you want to delete the student list \""
                                + content.get(_position) + "\"?")
                                // Back button cancel dialog
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                removeItem(_position);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();
            }
        });

        return v;
    }
}
