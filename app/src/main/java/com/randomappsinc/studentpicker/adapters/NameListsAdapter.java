package com.randomappsinc.studentpicker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NameListsAdapter extends BaseAdapter {

    private Context context;
    private List<String> content;
    private TextView noContent;
    private DataSource dataSource;
    private PreferencesManager preferencesManager;

    public NameListsAdapter(Context context, TextView noContent) {
        this.context = context;
        this.content = new ArrayList<>();
        this.noContent = noContent;
        this.dataSource = new DataSource(context);
        this.preferencesManager = new PreferencesManager(context);
        refreshList();
    }

    public void refreshList() {
        content.clear();
        content.addAll(preferencesManager.getNameLists());
        Collections.sort(content);
        notifyDataSetChanged();
        setNoContent();
    }

    private void setNoContent() {
        int viewVisibility = content.isEmpty() ? View.VISIBLE : View.GONE;
        noContent.setVisibility(viewVisibility);
    }

    public void addList(String itemName) {
        content.add(itemName);
        Collections.sort(content);
        notifyDataSetChanged();
        setNoContent();
        preferencesManager.addNameList(itemName);
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
        new MaterialDialog.Builder(context)
                .title(R.string.rename_list)
                .input(context.getString(R.string.new_list_name), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        boolean submitEnabled = !(input.toString().trim().isEmpty() ||
                                preferencesManager.doesListExist(input.toString().trim()));
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
                            preferencesManager.renameList(getItem(position), newListName);
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
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dataSource.deleteList(getItem(position));
                        preferencesManager.removeNameList(getItem(position));
                        content.remove(position);
                        setNoContent();
                        notifyDataSetChanged();
                    }
                })
                .show();
    }

    class NameListViewHolder {
        @BindView(R.id.list_name) TextView listName;

        private int position;

        NameListViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        void loadList(int position) {
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
