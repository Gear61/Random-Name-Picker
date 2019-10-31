package com.randomappsinc.studentpicker.editing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.database.DataSource;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NameCreationACAdapter extends BaseAdapter implements Filterable {

    private List<String> suggestions;
    private DataSource dataSource;

    private Context context;

    public NameCreationACAdapter(Context context) {
        this.context = context;
        this.dataSource = new DataSource(context);
        this.suggestions = new ArrayList<>();
    }

    static class ViewHolder {
        @BindView(R.id.suggestion) TextView suggestion;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public String getItem(int position) {
        return position >= suggestions.size() ? "" : suggestions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.name_suggestion_cell, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.suggestion.setText(getItem(position));
        return view;
    }

    @Override
    public Filter getFilter()
    {
        return nameFilter;
    }

    private final Filter nameFilter = new Filter() {
        public String convertResultToString(Object resultValue) {
            return (resultValue).toString();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                suggestions.addAll(dataSource.getMatchingNames(constraint.toString()));
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            }
            else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notifyDataSetChanged();
        }
    };
}

