package com.randomappsinc.studentpicker.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.Database.DataSource;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.Utils.PreferencesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class NameListsAdapter extends BaseAdapter {
    private Context context;
    private List<String> content;
    private TextView noContent;
    private DataSource dataSource;

    public NameListsAdapter(Context context, TextView noContent) {
        this.context = context;
        this.content = new ArrayList<>();
        this.noContent = noContent;
        this.dataSource = new DataSource();
        refreshList();
    }

    public void refreshList() {
        content.clear();
        content.addAll(PreferencesManager.get().getNameLists());
        Collections.sort(content);
        setNoContent();
        notifyDataSetChanged();
    }

    private void setNoContent() {
        int viewVisibility = content.isEmpty() ? View.VISIBLE : View.GONE;
        noContent.setVisibility(viewVisibility);
    }

    public void addList(String itemName) {
        content.add(itemName);
        Collections.sort(content);
        setNoContent();
        PreferencesManager.get().addNameList(itemName);
        notifyDataSetChanged();
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
        new MaterialDialog.Builder(context)
                .title(R.string.rename_list)
                .input(context.getString(R.string.new_list_name), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        boolean submitEnabled = !(input.toString().trim().isEmpty() ||
                                PreferencesManager.get().doesListExist(input.toString().trim()));
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(submitEnabled);
                    }
                })
                .alwaysCallInputCallback()
                .negativeText(android.R.string.cancel)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            String newListName = dialog.getInputEditText().getText().toString().trim();
                            dataSource.renameList(getItem(position), newListName);
                            PreferencesManager.get().renameList(getItem(position), newListName);
                            content.set(position, newListName);
                            notifyDataSetChanged();
                        }
                    }
                })
                .show();
    }

    private void showDeleteDialog(final int position) {
        new MaterialDialog.Builder(context)
                .title(R.string.confirm_deletion_title)
                .content(R.string.confirm_deletion_message)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dataSource.deleteList(getItem(position));
                        PreferencesManager.get().removeNameList(getItem(position));
                        PreferencesManager.get().removeNamesListCache(getItem(position));
                        content.remove(position);
                        setNoContent();
                        notifyDataSetChanged();
                    }
                })
                .show();
    }

    public class NameListViewHolder {
        @Bind(R.id.list_name) TextView listName;

        private int position;

        public NameListViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void loadList(int position) {
            this.position = position;
            this.listName.setText(getItem(position));
        }

        @OnClick(R.id.edit_icon)
        public void renameList() {
            showRenameDialog(position);
        }

        @OnClick(R.id.delete_icon)
        public void deleteList() {
            showDeleteDialog(position);
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        NameListViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.names_list_item, parent, false);
            holder = new NameListViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (NameListViewHolder) view.getTag();
        }
        holder.loadList(position);
        return view;
    }
}
