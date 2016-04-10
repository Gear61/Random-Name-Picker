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
import com.randomappsinc.studentpicker.Activities.ListActivity;
import com.randomappsinc.studentpicker.Database.DataSource;
import com.randomappsinc.studentpicker.R;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class EditNameListAdapter extends BaseAdapter {
    private ListActivity listActivity;
    private List<String> content;
    private TextView noContent;
    private TextView numNames;
    private String listName;
    private DataSource dataSource;
    private View parent;

    public EditNameListAdapter(ListActivity listActivity, TextView noContent, TextView numNames, String listName, View parent) {
        this.listActivity = listActivity;
        this.dataSource = new DataSource();
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
                    ? listActivity.getString(R.string.single_name)
                    : listActivity.getString(R.string.plural_names);
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

        listActivity.getListTabsAdapter().getNameChoosingFragment().getNameChoosingAdapter().addName(name);

        showConfirmationDialog(true, name);
    }

    public void removeName(int index) {
        String nameToRemove = getItem(index);
        dataSource.removeName(nameToRemove, listName);

        listActivity.getListTabsAdapter().getNameChoosingFragment().getNameChoosingAdapter().removeName(nameToRemove);

        content.remove(index);
        notifyDataSetChanged();
        setViews();

        showConfirmationDialog(false, nameToRemove);
    }

    public void changeName(int position, String newName) {
        listActivity.getListTabsAdapter().getNameChoosingFragment()
                .getNameChoosingAdapter().changeName(getItem(position), newName);

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
            listActivity.getListTabsAdapter().getNameChoosingFragment().getNameChoosingAdapter().addName(name);
        }
    }

    public void showConfirmationDialog(final boolean addMode, final String name) {
        String prefix = addMode ? listActivity.getString(R.string.added) : listActivity.getString(R.string.removed);
        String confirmationMessage = prefix + "\"" + name + "\"";
        Snackbar snackbar = Snackbar.make(parent, confirmationMessage, Snackbar.LENGTH_LONG);
        View rootView = snackbar.getView();
        snackbar.getView().setBackgroundColor(listActivity.getResources().getColor(R.color.app_teal));
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

        new MaterialDialog.Builder(listActivity)
                .title(R.string.change_name)
                .input(listActivity.getString(R.string.new_name), currentName, new MaterialDialog.InputCallback() {
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

        private int position;

        public NameViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void loadName(int position) {
            this.position = position;
            name.setText(getItem(position));
        }

        @OnClick(R.id.edit_icon)
        public void renamePerson() {
            showRenameDialog(position);
        }

        @OnClick(R.id.delete_icon)
        public void deletePerson() {
            removeName(position);
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        NameViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) listActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.edit_person_name_cell, parent, false);
            holder = new NameViewHolder(view);
            view.setTag(holder);
        }
        else {
            holder = (NameViewHolder) view.getTag();
        }
        holder.loadName(position);
        return view;
    }
}
