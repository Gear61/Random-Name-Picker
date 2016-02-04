package com.randomappsinc.studentpicker.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.widget.IconTextView;
import com.randomappsinc.studentpicker.Database.DataSource;
import com.randomappsinc.studentpicker.Models.EditListEvent;
import com.randomappsinc.studentpicker.R;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class EditNameListAdapter extends BaseAdapter {
    private Context context;
    private List<String> content;
    private TextView noContent;
    private TextView numNames;
    private String listName;
    private DataSource dataSource;
    private View parent;

    public EditNameListAdapter(Context context, TextView noContent, TextView numNames, String listName, View parent) {
        this.context = context;
        this.dataSource = new DataSource(context);
        this.content = dataSource.getAllNamesInList(listName);
        this.noContent = noContent;
        this.numNames = numNames;
        this.listName = listName;
        this.parent = parent;
        Collections.sort(this.content);
        setViews();
    }

    public void setViews() {
        if (content.isEmpty()) {
            numNames.setVisibility(View.GONE);
            noContent.setVisibility(View.VISIBLE);
        }
        else {
            noContent.setVisibility(View.GONE);
            String names = getCount() == 1
                    ? context.getString(R.string.single_name)
                    : context.getString(R.string.plural_names);
            String numNamesText = String.valueOf(getCount()) + names;
            numNames.setText(numNamesText);
            numNames.setVisibility(View.VISIBLE);
        }
    }

    public void addName(String name) {
        dataSource.addName(name, listName);
        content.add(name);
        Collections.sort(content);
        setViews();
        notifyDataSetChanged();

        EditListEvent event = new EditListEvent();
        event.setEventType(EditListEvent.ADD);
        event.setName(name);
        EventBus.getDefault().post(event);

        showConfirmationDialog(true, name);
    }

    public void removeName(int index) {
        String nameToRemove = getItem(index);
        dataSource.removeName(nameToRemove, listName);

        EditListEvent event = new EditListEvent();
        event.setEventType(EditListEvent.REMOVE);
        event.setName(nameToRemove);
        EventBus.getDefault().post(event);

        content.remove(index);
        notifyDataSetChanged();
        setViews();

        showConfirmationDialog(false, nameToRemove);
    }

    public void changeName(int position, String newName) {
        EditListEvent event = new EditListEvent();
        event.setEventType(EditListEvent.RENAME);
        event.setName(getItem(position));
        event.setNewName(newName);
        EventBus.getDefault().post(event);

        content.set(position, newName);
        Collections.sort(content);
        notifyDataSetChanged();
    }

    public void importNamesFromList(String listToAbsorb) {
        dataSource.importNamesIntoList(listName, listToAbsorb);
        content = dataSource.getAllNamesInList(listName);
        Collections.sort(content);
        setViews();
        notifyDataSetChanged();

        List<String> namesAdded = dataSource.getAllNamesInList(listToAbsorb);
        for (String name : namesAdded) {
            EditListEvent event = new EditListEvent();
            event.setEventType(EditListEvent.ADD);
            event.setName(name);
            EventBus.getDefault().post(event);
        }
    }

    public void showConfirmationDialog(final boolean addMode, final String name) {
        String prefix = addMode ? context.getString(R.string.added) : context.getString(R.string.removed);
        String confirmationMessage = prefix + "\"" + name + "\"";
        Snackbar snackbar = Snackbar.make(parent, confirmationMessage, Snackbar.LENGTH_LONG);
        View rootView = snackbar.getView();
        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.app_teal));
        TextView tv = (TextView) rootView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addMode) {
                    removeName(content.indexOf(name));
                }
                else {
                    addName(name);
                }
            }
        });
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.show();
    }

    @Override
    public int getCount() {
        return content.size();
    }

    @Override
    public String getItem(int position) {
        return content.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    public void showRenameDialog(final int position) {
        final String currentName = content.get(position);

        new MaterialDialog.Builder(context)
                .title(R.string.change_name)
                .input(context.getString(R.string.new_name), currentName, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        boolean submitEnabled = !(input.toString().trim().isEmpty() ||
                                input.toString().equals(currentName));
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(submitEnabled);
                    }
                })
                .alwaysCallInputCallback()
                .negativeText(android.R.string.no)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            String newName = dialog.getInputEditText().getText().toString();
                            dataSource.renamePerson(currentName, newName, listName);
                            changeName(position, newName);
                        }
                    }
                })
                .show();
    }

    public class NameViewHolder {
        @Bind(R.id.person_name) TextView name;
        @Bind(R.id.edit_icon) IconTextView edit;
        @Bind(R.id.delete_icon) IconTextView delete;

        public NameViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        NameViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.edit_person_name_cell, parent, false);
            holder = new NameViewHolder(view);
            view.setTag(holder);
        }
        else {
            holder = (NameViewHolder) view.getTag();
        }

        holder.name.setText(content.get(position));
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRenameDialog(position);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                removeName(position);
            }
        });
        return view;
    }
}
