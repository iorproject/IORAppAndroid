package ior.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.services.people.v1.model.Birthday;
import com.google.samples.quickstart.signin.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import ior.activities.ReceiptFileActivity;
import ior.engine.Receipt;
import ior.engine.ServerHandler;

public class ReceiptRecycleAdapter extends RecyclerView.Adapter<ReceiptRecycleAdapter.ViewHolder> {

    private static final int MAX_TEXT_LENGTH = 15;
    private Activity mContex;
    private List<Receipt> receipts;
    private int resourceId;
    private View parent;

    public class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView imageViewCompany;
        TextView textViewEmail;
        TextView textViewCompany;
        TextView textViewID;
        TextView textViewDate;
        TextView textViewTotalPrice;
        TextView textViewFileName;
        ImageView imageViewDownloadFile;
        ImageView imageViewPreviewFile;
        LinearLayout linearLayoutDownloadFile;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            //imageView = itemView.findViewById(R.id.imageView);
            imageViewCompany = itemView.findViewById(R.id.imageView_company_receiptAdapter);
            textViewEmail = itemView.findViewById(R.id.textView_emailVal_receiptAdapter);
            textViewCompany = itemView.findViewById(R.id.textView_companyVal_receiptAdapter);
            textViewID = itemView.findViewById(R.id.textView_receiptIdVal_receiptAdapter);
            textViewDate = itemView.findViewById(R.id.textView_date_receiptAdapter);
            textViewTotalPrice = itemView.findViewById(R.id.textView_totalPriceVal_receiptAdapter);
            textViewFileName = itemView.findViewById(R.id.textView_fileNameVal_receiptAdapter);
            imageViewDownloadFile = itemView.findViewById(R.id.imageView_downloadFile_receiptAdapter);
            imageViewPreviewFile = itemView.findViewById(R.id.imageView_previewFile_receiptAdapter);
            linearLayoutDownloadFile = itemView.findViewById(R.id.linear_downloadFile_receiptAdapter);
        }
    }

    public ReceiptRecycleAdapter(Activity context, int resourceId, List<Receipt> items, View parent) {

        this.mContex = context;
        this.receipts = items;
        this.resourceId = resourceId;
        this.parent = parent;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {

        LayoutInflater inflater = LayoutInflater.from(mContex);
        View view = inflater.inflate(resourceId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        Receipt currentReceipt = receipts.get(position);

        String receiptId = currentReceipt.getReceiptNumber().equals("") ? "---" : currentReceipt.getReceiptNumber();
        viewHolder.textViewID.setText(receiptId);
        viewHolder.textViewCompany.setText(currentReceipt.getCompany());
        viewHolder.textViewEmail.setText(currentReceipt.getEmail());
        String ppp = String.valueOf(currentReceipt.getTotalPrice());
        String ttt = currentReceipt.getCurrency().toString();
        String price = ppp +" " + ttt;
        viewHolder.textViewTotalPrice.setText(price);
        DateFormat format = new SimpleDateFormat("dd MMM yyyy");
        String date = format.format(currentReceipt.getCreationDate());
        viewHolder.textViewDate.setText(date);
        if (currentReceipt.getFileName().equals("") || currentReceipt.getAttachmentUrl().equals("")) {
            viewHolder.imageViewPreviewFile.setVisibility(View.GONE);
            viewHolder.linearLayoutDownloadFile.setVisibility(View.GONE);
        }
        else {
            viewHolder.imageViewPreviewFile.setVisibility(View.VISIBLE);
            viewHolder.linearLayoutDownloadFile.setVisibility(View.VISIBLE);
        }

        TextView textViewFileName = viewHolder.textViewFileName;
        String fileName = currentReceipt.getFileName().equals("") ? "---" : currentReceipt.getFileName();
        if (fileName.length() > MAX_TEXT_LENGTH) {

            fileName = fileName.substring(0, MAX_TEXT_LENGTH - 3) + "...";
            textViewFileName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast toast = Toast.makeText(mContex, currentReceipt.getFileName(), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.NO_GRAVITY, v.getLeft() - 150, v.getTop() + 150);
                    toast.show();
                }
            });
        }

        textViewFileName.setText(fileName);
        Bitmap bitmap = ServerHandler.getInstance().getCompany(currentReceipt.getCompany()).getBitmap();
        if (bitmap == null)
            bitmap = BitmapFactory.decodeResource(mContex.getResources(), R.mipmap.ic_error_loading);

        viewHolder.imageViewCompany.setImageBitmap(bitmap);
        viewHolder.imageViewPreviewFile.setOnClickListener(v -> {

            Intent intent = new Intent(mContex, ReceiptFileActivity.class);
            intent.putExtra("url", currentReceipt.getAttachmentUrl());
            mContex.startActivityForResult(intent,
                    1);
            parent.setAlpha(0.7f);
            //mContex.startActivity(new Intent(mContex, ReceiptFileActivity.class));



        });


        viewHolder.imageViewDownloadFile.setOnClickListener(v -> {

                //url= URLEncoder.encode(url,"UTF-8");
                ServerHandler.getInstance().downloadFile(mContex, currentReceipt.getAttachmentUrl(),
                        currentReceipt.getFileName(), () -> {

                    Toast.makeText(mContex, "File Download Successfully", Toast.LENGTH_SHORT).show();
                });
        });
    }


    @Override
    public int getItemCount() {
        return receipts.size();
    }

}
