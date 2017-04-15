package com.randomappsinc.studentpicker.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.Activities.ListActivity;
import com.randomappsinc.studentpicker.Database.DataSource;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.Utils.NameUtils;
import com.randomappsinc.studentpicker.Utils.UIUtils;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

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

    public EditNameListAdapter(ListActivity listActivity, TextView noContent, TextView numNames,
                               String listName, View parent) {
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
        } else {
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

    public void cloneName(String name, int numClones) {
        for (int i = 0; i < numClones; i++) {
            dataSource.addName(name, listName);
            content.add(name);
            listActivity.getListTabsAdapter().getNameChoosingFragment().getNameChoosingAdapter().addName(name);
        }
        Collections.sort(content);
        notifyDataSetChanged();
        setViews();
        UIUtils.showSnackbar(parent, listActivity.getString(R.string.clones_added));
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

    private void showRenameDialog(final int position) {
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
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String newName = dialog.getInputEditText().getText().toString().trim();
                        dataSource.renamePerson(currentName, newName, listName);
                        changeName(position, newName);
                    }
                })
                .show();
    }

    private void showDeleteDialog(final int position) {
        String confirmation = listActivity.getString(R.string.are_you_sure) + "\"" + getItem(position) + "\"" +
                listActivity.getString(R.string.from_this_list);
        new MaterialDialog.Builder(listActivity)
                .title(R.string.confirm_name_deletion)
                .content(confirmation)
                .negativeText(android.R.string.no)
                .positiveText(android.R.string.yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        removeName(position);
                    }
                })
                .show();
    }

    private void showCloneDialog(final int position) {
        MaterialDialog cloneDialog = new MaterialDialog.Builder(listActivity)
                .content(R.string.cloning_title)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(listActivity.getString(R.string.num_copies), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        boolean isValid = input.length() > 0 && (Integer.parseInt(input.toString()) > 0);
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(isValid);
                    }
                })
                .alwaysCallInputCallback()
                .negativeText(android.R.string.no)
                .positiveText(R.string.add)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        int numCopies = Integer.parseInt(dialog.getInputEditText().getText().toString().trim());
                        cloneName(getItem(position), numCopies);
                    }
                })
                .build();
        cloneDialog.getInputEditText().setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});
        cloneDialog.show();
    }

    public void showNameOptions(final int position) {
        new MaterialDialog.Builder(listActivity)
                .items(NameUtils.getNameOptions(getItem(position)))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                showRenameDialog(position);
                                break;
                            case 1:
                                showDeleteDialog(position);
                                break;
                            case 2:
                                showCloneDialog(position);
                        }
                    }
                })
                .show();
    }

    public class NameViewHolder {
        @Bind(R.id.person_name) TextView name;

        public NameViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void loadName(int position) {
            name.setText(getItem(position));
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
        } else {
            holder = (NameViewHolder) view.getTag();
        }
        holder.loadName(position);
        return view;
    }
}
