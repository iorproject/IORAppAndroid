package ior.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.samples.quickstart.signin.R;

import java.util.List;

import ior.engine.ServerHandler;
import ior.engine.User;

public class AddingPartnerFBAdapter {

    private Context mContext;
    private Dialog mDialog;
    private View mDoneLayout;
    private View mErrorLayout;
    private View mCircleProgress;
    private TextView mErorMessageTV;
    private Button mSendReqBT;
    private EditText mEmailPartnerET;


    public AddingPartnerFBAdapter(Context mContext)
    {
        this.mContext = mContext;
        mDialog = new Dialog(this.mContext);
        initDialogComponents();
    }

    private void initDialogComponents()
    {
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.add_partner_dialog);
        mEmailPartnerET = mDialog.findViewById(R.id.email_request_partner);
        mDoneLayout = mDialog.findViewById(R.id.done_layout);
        mErrorLayout = mDialog.findViewById(R.id.eroor_layout);
        mCircleProgress = mDialog.findViewById(R.id.circle_progress);
        mErorMessageTV = mDialog.findViewById(R.id.error_message_add_partner_dialog);
        mSendReqBT = mDialog.findViewById(R.id.sent_request_partner_dialog);
        mSendReqBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sentButtonWasClicked();
            }
        });
    }

    private void sentButtonWasClicked()
    {
        mDoneLayout.setVisibility(View.GONE);
        mErrorLayout.setVisibility(View.GONE);
        mErorMessageTV.setText("");
        mCircleProgress.setVisibility(View.VISIBLE);
        String email = mEmailPartnerET.getText().toString();
        List<User> t = ServerHandler.getInstance().getSignInUser().getPartners();
        if (ServerHandler.getInstance().getSignInUser().getPartners_Followers().get("partners").containsKey(email))
        {
            errorEvent("This user is already your partner");
        }
        else if (ServerHandler.getInstance().getSignInUser().getEmail().equals(email))
            errorEvent("You cant send request to yourself");
        else
        {
            ServerHandler.getInstance().sendRequestFriendship(email,(temp)->afterSendRequest(temp));
        }
    }

    private void afterSendRequest(String msg)
    {
        mCircleProgress.setVisibility(View.GONE);
        if (msg == null)
        {
            doneEvent();
        }
        else
        {
           errorEvent(msg);
        }


    }

    public void errorEvent(String errorMsg)
    {
        mErorMessageTV.setTextColor(ContextCompat.getColor(mContext,R.color.red_error));
        mErorMessageTV.setText(errorMsg);
        mErrorLayout.setVisibility(View.VISIBLE);
        mCircleProgress.setVisibility(View.GONE);
        mSendReqBT.setText("Resend");
    }

    public void doneEvent()
    {
        mErorMessageTV.setText("succeed!");
        mErorMessageTV.setTextColor(ContextCompat.getColor(mContext,R.color.done_color));
        mDoneLayout.setVisibility(View.VISIBLE);
        mErrorLayout.setVisibility(View.GONE);
        mCircleProgress.setVisibility(View.GONE);
        mSendReqBT.setClickable(false);
        new Handler().postDelayed(mDialog::dismiss, 1000);
    }

    public void showDialog()
    {
        mErorMessageTV.setText("");
        mSendReqBT.setText("send");
        mErrorLayout.setVisibility(View.GONE);
        mDoneLayout.setVisibility(View.GONE);
        mCircleProgress.setVisibility(View.INVISIBLE);
        mSendReqBT.setClickable(true);
        mEmailPartnerET.setText("");
        mDialog.show();
    }
}
