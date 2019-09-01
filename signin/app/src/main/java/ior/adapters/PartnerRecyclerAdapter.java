package ior.adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.samples.quickstart.signin.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import ior.activities.AllReceiptsFragment;
import ior.activities.MyReceiptsActivityNav;
import ior.engine.ServerHandler;
import ior.engine.User;
import ior.engine.ePartner;
import utils.IorUtils;

public class PartnerRecyclerAdapter extends RecyclerView.Adapter<PartnerRecyclerAdapter.PartnerViewHolder> {


    private Context mContext;
    private List<User> mData;
    private Dialog mDialog;
    private CircularImageView circularImageViewDialog;
    private TextView nameTvDialog;
    private TextView emailTvDialog;
    private ePartner typeFragment;
    private Button actionRemoveDialogBT;
    private String buttonMsg;
    private String removeDialogMsg;
    private Button acceptDialogBT;

    public PartnerRecyclerAdapter(Context context, List<User> data, ePartner ePartner)
    {
      this.mContext = context;
      this.mData = data;
      mDialog = new Dialog(mContext);
      this.typeFragment = ePartner;
      initDynamicFragmentData();
      initDialogComponents();
    }


    public PartnerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.item_partner, parent, false);
        PartnerViewHolder partnerViewHolder = new PartnerViewHolder(v);
        return partnerViewHolder;
    }


    public void onBindViewHolder(@NonNull PartnerViewHolder viewHolder, int i) {

        viewHolder.partner_card.setBackgroundResource(R.drawable.card_partners_bg_befor_edit);
        String name = mData.get(i).getName();
        name = name.length() > 15 ? name.substring(0, 12) + "..." : name;
        viewHolder.nameTV.setText(name);
        viewHolder.emailTV.setText(mData.get(i).getEmail());
        viewHolder.edit_IMB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(viewHolder,i);
            }
        });

        viewHolder.dynamicButton.setText(buttonMsg);
        viewHolder.partner_card.setOnClickListener(this.typeFragment != ePartner.FOLLOWING ? null :
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = mData.get(i).getEmail();
                        ProgressDialog dialog = new ProgressDialog(mContext);
                        IorUtils.showProgressDialog(dialog, mContext);
                        ServerHandler.getInstance().fetchUserAllReceipts(email,
                                () -> {
                                    dialog.dismiss();
                                    Intent intent = new Intent(mContext, MyReceiptsActivityNav.class);
                                    intent.putExtra("email", email);
                                    mContext.startActivity(intent);
                                }, null);
                    }
                });

        Bitmap bitmap = mData.get(i).getProfileImage() != null? mData.get(i).getProfileImage() : IorUtils.getDefultProfileImage();
        viewHolder.partner_pic.setImageBitmap(bitmap);

    }

    private void showDialog(PartnerViewHolder viewHolder,int position)
    {
        circularImageViewDialog.setImageBitmap(
                mData.get(position).getProfileImage()!= null ? mData.get(position).getProfileImage()
                        : IorUtils.getDefultProfileImage());
        nameTvDialog.setText(mData.get(position).getName());
        emailTvDialog.setText(mData.get(position).getEmail());
        filterActionDialogButton(viewHolder,position);
        mDialog.show();
    }

    private void initDialogComponents()
    {
        mDialog.setContentView(R.layout.view_parent_dialog);
//        Button cancleButton = mDialog.findViewById(R.id.cancle_dialog_partner_by);
//        cancleButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDialog.cancel();
//            }
//        });
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        circularImageViewDialog = mDialog.findViewById(R.id.partner_pic_dialog);
        nameTvDialog = mDialog.findViewById(R.id.name_partner_view_dialog_tv);
        emailTvDialog = mDialog.findViewById(R.id.email_partner_view_dialog_tv);
        actionRemoveDialogBT = mDialog.findViewById(R.id.remove_dialog_partner_bt);
        actionRemoveDialogBT.setText(removeDialogMsg);
        acceptDialogBT = mDialog.findViewById(R.id.accecpt_request_partner_bt);
    }

    public void setAdapterDate(List<User> iData)
    {
        this.mData = iData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        return  mData != null ? mData.size() : 0;
    }


    public static class PartnerViewHolder extends RecyclerView.ViewHolder{

        private TextView nameTV;
        private TextView emailTV;
        private CircularImageView partner_pic;
        private LinearLayout partner_card;
        private ImageButton edit_IMB;
        private Button dynamicButton;


        public PartnerViewHolder(View itemView)
        {
            super(itemView);
            nameTV = itemView.findViewById(R.id.name_partner_tv);
            emailTV = itemView.findViewById(R.id.email_partner_tv);
            partner_pic = itemView.findViewById(R.id.partner_pic);
            partner_card = itemView.findViewById(R.id.partner_card);
            edit_IMB = itemView.findViewById(R.id.edit_imageButton);
            dynamicButton = itemView.findViewById(R.id.follow_partner_button);
        }
    }

    private void initDynamicFragmentData()
    {
        switch (typeFragment)
        {
            case FOLLOWING:
                buttonMsg = ePartner.FOLLOWING.toString();
                removeDialogMsg = "Unfollow";

                break;

            case FOLLOWER:
                buttonMsg = ePartner.FOLLOWER.toString();
                removeDialogMsg = "Remove follower";

                break;

            case REQUEST:
                buttonMsg = ePartner.REQUEST.toString();
                removeDialogMsg = "Cancel Request";
                break;
        }
    }

    private void filterActionDialogButton(PartnerViewHolder viewHolder ,int position)
    {
        switch (typeFragment)
        {
            case FOLLOWING:
                actionRemoveDialogBT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        unfolloweButtonWasClicked(viewHolder,position);
                    }
                });
                break;

            case FOLLOWER:
                actionRemoveDialogBT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeFollower(viewHolder,position);
                    }
                });
                break;

            case REQUEST:
                acceptDialogBT.setVisibility(View.VISIBLE);
                acceptDialogBT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        acceptButtonWasClicked(viewHolder,position);
                    }
                });
                actionRemoveDialogBT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rejectFriendship(viewHolder,position);
                    }
                });
                break;
        }

    }

    private void removeFollower(PartnerViewHolder viewHolder ,int position)
    {
        viewHolder.dynamicButton.setText("removed!");
        setViewHolderAfterDialogButton(viewHolder);
        ServerHandler.getInstance().removeFollower(mData.get(position).getEmail());
        mDialog.cancel();
    }

    private void acceptButtonWasClicked(PartnerViewHolder viewHolder ,int position)
    {
        viewHolder.dynamicButton.setText("accepted!");
        setViewHolderAfterDialogButton(viewHolder);
        ServerHandler.getInstance().accecptFriendship(mData.get(position).getEmail());
        mDialog.cancel();
    }

    private void rejectFriendship(PartnerViewHolder viewHolder,int position)
    {
        viewHolder.dynamicButton.setText("rejected!");
        mDialog.cancel();
        setViewHolderAfterDialogButton(viewHolder);
        ServerHandler.getInstance().rejectFriendshipRequest(mData.get(position).getEmail());

    }

    private void unfolloweButtonWasClicked(PartnerViewHolder viewHolder,int position)
    {
        viewHolder.dynamicButton.setText("unfollow");
     setViewHolderAfterDialogButton(viewHolder);
     mDialog.cancel();
        ServerHandler.getInstance().unfolloweRequest(mData.get(position).getEmail());
    }

    private void setViewHolderAfterDialogButton(PartnerViewHolder viewHolder)
    {
        viewHolder.partner_card.setBackground(ContextCompat.getDrawable(mContext,R.drawable.card_partners_bg_after_edit));
        viewHolder.edit_IMB.setOnClickListener(null);
    }
}
