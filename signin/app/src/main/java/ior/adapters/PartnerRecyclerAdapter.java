package ior.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerViewAccessibilityDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.samples.quickstart.signin.R;

import java.util.List;

import ior.engine.User;

public class PartnerRecyclerAdapter extends RecyclerView.Adapter<PartnerRecyclerAdapter.PartnerViewHolder> {


    Context mContext;
    List<User> mData;



    public PartnerRecyclerAdapter(Context context, List<User> data)
    {
      this.mContext = context;
      this.mData = data;
    }


    public PartnerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.item_partner, parent, false);
        PartnerViewHolder partnerViewHolder = new PartnerViewHolder(v);
        return partnerViewHolder;
    }

    public void onBindViewHolder(@NonNull PartnerViewHolder viewHolder, int i) {

            viewHolder.emailTV.setText(mData.get(i).getEmail());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class PartnerViewHolder extends RecyclerView.ViewHolder{

        private TextView nameTV;
        private TextView emailTV;


        public PartnerViewHolder(View itemView)
        {
            super(itemView);
            nameTV = itemView.findViewById(R.id.partner_name);
            emailTV = itemView.findViewById(R.id.partner_email);
        }
    }

}
