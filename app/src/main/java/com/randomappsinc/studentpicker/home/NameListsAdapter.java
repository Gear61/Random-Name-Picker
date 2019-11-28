package com.randomappsinc.studentpicker.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

public class NameListsAdapter extends RecyclerView.Adapter<NameListsAdapter.NameListViewHolder> {

    public interface OnListItemClickListener {
        void onItemClick(int position);
    }

    private Context context;
    private List<String> content;
    private TextView noContent;
    private DataSource dataSource;
    private PreferencesManager preferencesManager;
    private OnListItemClickListener listItemClickListener;

    NameListsAdapter(OnListItemClickListener listItemClickListener, Context context, TextView noContent) {
        this.listItemClickListener = listItemClickListener;
        this.context = context;
        this.content = new ArrayList<>();
        this.noContent = noContent;
        this.dataSource = new DataSource(context);
        this.preferencesManager = new PreferencesManager(context);
        refreshList();
    }

    @NonNull
    @Override
    public NameListsAdapter.NameListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.names_list_item, parent, false);
        return new NameListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NameListsAdapter.NameListViewHolder holder, int position) {
        holder.loadList(position);
    }

    @Override
    public int getItemCount() {
        return content.size();
    }

    void refreshList() {
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

    void addList(String itemName) {
        content.add(itemName);
        Collections.sort(content);
        notifyDataSetChanged();
        setNoContent();
        preferencesManager.addNameList(itemName);
    }

    String getItem(int position) {
        return content.get(position);
    }

    private void showRenameDialog(final int position) {
        new MaterialDialog.Builder(context)
                .title(R.string.rename_list)
                .input(context.getString(R.string.new_list_name), "", (dialog, input) -> {
                    boolean submitEnabled = !(input.toString().trim().isEmpty() ||
                            preferencesManager.doesListExist(input.toString().trim()));
                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(submitEnabled);
                })
                .alwaysCallInputCallback()
                .negativeText(android.R.string.cancel)
                .onAny((dialog, which) -> {
                    if (which == DialogAction.POSITIVE) {
                        String newListName = dialog.getInputEditText().getText().toString().trim();
                        dataSource.renameList(getItem(position), newListName);
                        preferencesManager.renameList(getItem(position), newListName);
                        content.set(position, newListName);
                        notifyItemChanged(position);
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
                .onPositive((dialog, which) -> {
                    dataSource.deleteList(getItem(position));
                    preferencesManager.removeNameList(getItem(position));
                    content.remove(position);
                    setNoContent();
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, 1);
                })
                .show();
    }

    class NameListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.list_name) TextView listName;

        private int position;


        NameListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
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

        @Override
        public void onClick(View v) {
            listItemClickListener.onItemClick(position);
        }
    }
}
