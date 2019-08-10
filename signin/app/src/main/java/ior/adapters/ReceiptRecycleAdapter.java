package ior.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.samples.quickstart.signin.R;

import java.util.List;

public class ReceiptRecycleAdapter extends RecyclerView.Adapter<ReceiptRecycleAdapter.ViewHolder> {

    private Context mContex;
    private List<Integer> receipts;
    private int resourceId;

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    public ReceiptRecycleAdapter(Context context, int resourceId, List<Integer> items) {

        this.mContex = context;
        this.receipts = items;
        this.resourceId = resourceId;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {

        LayoutInflater inflater = LayoutInflater.from(mContex);
        View view = inflater.inflate(resourceId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

    }


    @Override
    public int getItemCount() {
        return receipts.size();
    }
}
