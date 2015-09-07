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

import com.randomappsinc.studentpicker.Database.DataSource;
import com.randomappsinc.studentpicker.Misc.PreferencesManager;
import com.randomappsinc.studentpicker.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class NameListsAdapter extends BaseAdapter
{
    public static final String CONFIRM_DELETION = "Confirm Deletion";

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
        final View _v = v;
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                alertDialogBuilder.setTitle(CONFIRM_DELETION);
                alertDialogBuilder
                        .setMessage("Are you sure that you want to delete the name list \""
                                + content.get(_position) + "\"?")
                                // Back button cancel dialog
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.dismiss();
                                // Disable clicking item they're removing, so they don't spam it
                                _v.setEnabled(false);
                                // Make item fade out smoothly as opposed to just vanishing
                                _v.animate().setDuration(250).alpha(0).withEndAction(new Runnable()
                                {
                                    public void run()
                                    {
                                        removeList(_position);
                                        _v.setAlpha(1);
                                        // Re-enable it after the row disappears
                                        _v.setEnabled(true);
                                    }
                                });
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
