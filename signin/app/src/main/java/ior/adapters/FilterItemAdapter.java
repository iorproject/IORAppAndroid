package ior.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.samples.quickstart.signin.R;

import java.util.List;

public class FilterItemAdapter extends RecyclerView.Adapter<FilterItemAdapter.ViewHolder> {

    private Context mContex;
    private List<FilterItem> items;



    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewName;
        private RelativeLayout relativeLayout;

        public ViewHolder(View view) {

            super(view);
            textViewName = view.findViewById(R.id.textViewName_filterItemAdapter);
            relativeLayout = view.findViewById(R.id.relativelayout_filterItemAdapter);
        }
    }

    public FilterItemAdapter(Context context, List<FilterItem> items) {

        this.mContex = context;
        this.items = items;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContex);
        View view = inflater.inflate(R.layout.filter_tem_adapter, parent, false);
        return new FilterItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        viewHolder.textViewName.setText(items.get(position).getName());
        FilterItem item = items.get(position);


        viewHolder.relativeLayout.setOnClickListener(v -> {

            item.setSelected(!item.isSelected());
            updateItem(item, viewHolder);

        });

        updateItem(item, viewHolder);

        if (position != 0) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(65, 0, 0, 0);
            viewHolder.relativeLayout.setLayoutParams(params);

        }



    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void updateItem(FilterItem item, ViewHolder viewHolder) {

        if (item.isSelected()) {

            viewHolder.relativeLayout.setBackgroundResource(R.drawable.advanced_serach_item_selected_background);
            viewHolder.textViewName.setTextColor(Color.WHITE);
        }
        else {

            viewHolder.relativeLayout.setBackgroundResource(R.drawable.advanced_search_item_unselected);
            viewHolder.textViewName.setTextColor(Color.BLACK);
        }
    }

}
