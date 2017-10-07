package com.randomappsinc.studentpicker.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.activities.ListActivity;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.utils.NameUtils;
import com.randomappsinc.studentpicker.utils.UIUtils;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditNameListAdapter extends BaseAdapter {

    private ListActivity listActivity;
    private ListInfo content;
    private TextView noContent;
    private TextView numNames;
    private String listName;
    private DataSource dataSource;
    private View parent;

    public EditNameListAdapter(ListActivity listActivity, TextView noContent, TextView numNames,
                               String listName, View parent) {
        this.listActivity = listActivity;
        this.dataSource = new DataSource();
        this.content = dataSource.getListInfo(listName);
        this.noContent = noContent;
        this.numNames = numNames;
        this.listName = listName;
        this.parent = parent;
        setViews();
    }

    private void setViews() {
        if (content.getNumInstances() == 0) {
            numNames.setVisibility(View.GONE);
            noContent.setVisibility(View.VISIBLE);
        } else {
            noContent.setVisibility(View.GONE);
            String names = content.getNumInstances() == 1
                    ? listActivity.getString(R.string.single_name)
                    : listActivity.getString(R.string.plural_names);
            String numNamesText = String.valueOf(content.getNumInstances()) + names;
            numNames.setText(numNamesText);
            numNames.setVisibility(View.VISIBLE);
        }
    }

    public void addNames(String name, int amount) {
        dataSource.addNames(name, listName, amount);
        content.addNames(name, amount);
        notifyDataSetChanged();
        setViews();

        listActivity.getListTabsAdapter().getNameChoosingFragment()
                .getNameChoosingAdapter().addNames(name, amount);

        showConfirmationDialog(true, name);
    }

    private void removeNames(int index, int amount, boolean multiDelete) {
        String nameToRemove = getItem(index);
        dataSource.removeNames(nameToRemove, listName, amount);
        content.removeNames(nameToRemove, amount);
        notifyDataSetChanged();
        setViews();

        if (multiDelete) {
            UIUtils.showSnackbar(parent, listActivity.getString(R.string.names_deleted));
        } else {
            showConfirmationDialog(false, nameToRemove);
        }

        listActivity.getListTabsAdapter().getNameChoosingFragment()
                .getNameChoosingAdapter().removeNames(nameToRemove, amount);
    }

    private void changeName(int position, String newName, int amount) {
        String oldName = getItem(position);
        content.renamePeople(oldName, newName, amount);
        notifyDataSetChanged();

        listActivity.getListTabsAdapter().getNameChoosingFragment()
                .getNameChoosingAdapter().changeNames(oldName, newName, amount);
    }

    private void cloneName(String name, int numClones) {
        dataSource.addNames(name, listName, numClones);
        content.addNames(name, numClones);
        notifyDataSetChanged();
        setViews();

        UIUtils.showSnackbar(parent, listActivity.getString(R.string.clones_added));

        listActivity.getListTabsAdapter().getNameChoosingFragment()
                .getNameChoosingAdapter().addNames(name, numClones);
    }

    public void importNamesFromList(List<String> listsToAbsorb) {
        Map<String, Integer> nameMap = dataSource.importNamesIntoList(listName, listsToAbsorb);
        content = dataSource.getListInfo(listName);
        notifyDataSetChanged();
        setViews();

        listActivity.getListTabsAdapter().getNameChoosingFragment().getNameChoosingAdapter().addNameMap(nameMap);
    }

    private void showConfirmationDialog(boolean addMode, String name) {
        String template = addMode ? listActivity.getString(R.string.added_name) : listActivity.getString(R.string.deleted_name);
        UIUtils.showSnackbar(parent, String.format(template, name));
    }

    @Override
    public int getCount() {
        return content.getNumNames();
    }

    @Override
    public String getItem(int position) {
        return content.getName(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    private void startRenameProcess(int position) {
        int currentAmount = content.getInstancesOfName(getItem(position));
        if (currentAmount == 1) {
            showRenameDialog(position, currentAmount, false);
        } else {
            chooseRenameAmount(position, "");
        }
    }

    private void chooseRenameAmount(final int position, String prefill) {
        final int currentAmount = content.getInstancesOfName(getItem(position));
        MaterialDialog cloneDialog = new MaterialDialog.Builder(listActivity)
                .content(R.string.multiple_renames_title)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(listActivity.getString(R.string.num_copies), prefill, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (input.length() > 0) {
                            int amount = Integer.parseInt(input.toString());
                            boolean validNumber = amount > 0 && amount <= currentAmount;
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(validNumber);
                            return;
                        }
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                    }
                })
                .alwaysCallInputCallback()
                .negativeText(android.R.string.no)
                .positiveText(R.string.next)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        int numCopies = Integer.parseInt(dialog.getInputEditText().getText().toString().trim());
                        showRenameDialog(position, numCopies, true);
                    }
                })
                .build();
        cloneDialog.getInputEditText().setFilters(new InputFilter[]
                {new InputFilter.LengthFilter(String.valueOf(currentAmount).length())});
        cloneDialog.show();
    }

    private void showRenameDialog(final int position, final int amount, boolean multiRename) {
        final String currentName = content.getName(position);
        MaterialDialog renameDialog = new MaterialDialog.Builder(listActivity)
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
                            String newName = dialog.getInputEditText().getText().toString().trim();
                            dataSource.renamePeople(currentName, newName, listName, amount);
                            changeName(position, newName, amount);
                        } else if (which == DialogAction.NEUTRAL) {
                            chooseRenameAmount(position, String.valueOf(amount));
                        }
                    }
                })
                .build();
        if (multiRename) {
            renameDialog.setActionButton(DialogAction.NEUTRAL, R.string.back);
        }
        renameDialog.show();
    }

    private void showDeleteDialog(final int position) {
        final int currentAmount = content.getInstancesOfName(getItem(position));
        if (currentAmount == 1) {
            new MaterialDialog.Builder(listActivity)
                    .title(R.string.confirm_name_deletion)
                    .content(String.format(listActivity.getString(R.string.confirm_name_delete), getItem(position)))
                    .negativeText(android.R.string.no)
                    .positiveText(android.R.string.yes)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            removeNames(position, 1, false);
                        }
                    })
                    .show();
        } else {
            MaterialDialog cloneDialog = new MaterialDialog.Builder(listActivity)
                    .content(R.string.multiple_deletions_title)
                    .inputType(InputType.TYPE_CLASS_NUMBER)
                    .input(listActivity.getString(R.string.num_copies), "", new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            if (input.length() > 0) {
                                int amount = Integer.parseInt(input.toString());
                                boolean validNumber = amount > 0 && amount <= currentAmount;
                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(validNumber);
                                return;
                            }
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        }
                    })
                    .alwaysCallInputCallback()
                    .negativeText(android.R.string.no)
                    .positiveText(R.string.delete)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            int numCopies = Integer.parseInt(dialog.getInputEditText().getText().toString().trim());
                            removeNames(position, numCopies, true);
                        }
                    })
                    .build();
            cloneDialog.getInputEditText().setFilters(new InputFilter[]
                    {new InputFilter.LengthFilter(String.valueOf(currentAmount).length())});
            cloneDialog.show();
        }
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
                                startRenameProcess(position);
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
        @BindView(R.id.person_name) TextView name;

        private NameViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        private void loadName(int position) {
            name.setText(content.getNameText(position));
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
