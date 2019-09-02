package ior.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.samples.quickstart.signin.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ior.engine.ServerHandler;
import utils.IorUtils;

public class ShowProfileActivity extends AppCompatActivity  {

    private  BottomDialog mDialog;
    private static final int IMAGE_PICK_CODE =1000;
    private static final int PERMISSION_CODE =1000;
    private static final int PERMISSION_CAMERA =0;
    private ImageView m_ProfileImage;
    private ImageButton mEditBT;
    private Button m_UploadImageButton;
    private TextView mProfileNameTV;
    private TextView mProfileEmailTV;
    private TextView mRegiserDateTV;
    private TextView mParentsAmountTV;
    private TextView mRecieptsAmountTV;
    private TextView mFollowersAmountTV;
    private TextView mEmailLastTimeScanTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);
        getSupportActionBar().hide();
        initProfileComponentsActivity();
    }

    private void initChangeProfilePic()
    {
//        mDialog = new Dialog(this);
//        mDialog.setContentView(R.layout.popup_edit_profile);
//        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
//        m_UploadImageButton =  mDialog.findViewById(R.id.uploadImage);
//        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        m_UploadImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                uploadPic(v);
//            }
//        });

    }

    private void initProfileComponentsActivity()
    {
        mParentsAmountTV = findViewById(R.id.numberOfPartnersTV);
        mFollowersAmountTV = findViewById(R.id.numberOffollowersTV);
        mRecieptsAmountTV = findViewById(R.id.numberOfRecieptsTV);
        mEmailLastTimeScanTV = findViewById(R.id.tv_lastScan_profile);
        mRecieptsAmountTV.setText(getShortNumber(ServerHandler.getInstance().getSignInUser().getRecieptsAmount()));
        mFollowersAmountTV.setText(getShortNumber(ServerHandler.getInstance().getSignInUser().getFollowersAmount()));
        mParentsAmountTV.setText(getShortNumber(ServerHandler.getInstance().getSignInUser().getPartnersAmount()));
        m_ProfileImage = findViewById(R.id.profilePic);
        m_ProfileImage.setImageBitmap(ServerHandler.getInstance().getSignInUser().getProfileImage());
        mProfileNameTV = findViewById(R.id.profile_name);
        mProfileNameTV.setText(ServerHandler.getInstance().getSignInUser().getName());
        mEditBT = findViewById(R.id.imageButton_edit);
        mEditBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPicProfile();
            }
        });
        mProfileEmailTV = findViewById(R.id.email_profile);
        mProfileEmailTV.setText(ServerHandler.getInstance().getSignInUser().getEmail());
        mRegiserDateTV = findViewById(R.id.registerDate_profile);
        Date date = ServerHandler.getInstance().getSignInUser().getRegisterDate();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm a");
        mRegiserDateTV.setText(dateFormat.format(date));
        Date lastScan = ServerHandler.getInstance().getSignInUser().getLastEmailScan();
        mEmailLastTimeScanTV.setText(dateFormat.format(lastScan));
        mDialog = new BottomDialog();

    }

    public void editPicProfile()
    {
        mDialog.show(getSupportFragmentManager(),"example");
    }

    public void uploadPic(View v)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED){//permission not granted, request it.
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions,PERMISSION_CODE);
            }
            else
            {//permission already grated

                pickImageFromGallery();
            }
        }
        else
        {//system os is less then marshmellow
            pickImageFromGallery();
        }
    }

    private void pickImageFromGallery()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        if (data != null)
        {
            if (requestCode ==  RESULT_OK || requestCode == IMAGE_PICK_CODE)
            {
                Uri chosenImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), chosenImage);
                }
                catch (Exception e){}
                finally {
                    m_ProfileImage.setImageBitmap(rotateImage(bitmap,90));                }
            }
            else if (requestCode == PERMISSION_CAMERA)
            {
                bitmap = (Bitmap)data.getExtras().get("data");
                m_ProfileImage.setImageBitmap(rotateImage(bitmap,90));
            }

            ServerHandler.getInstance().getSignInUser().setProfileImage(bitmap);
            ServerHandler.getInstance().setUserProfileImage(IorUtils.getStringFromBitmap(bitmap),
                    ServerHandler.getInstance().getSignInUser().getEmail());
        }

        mDialog.dismiss();
    }

    public void takePicture(View v)
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,PERMISSION_CAMERA);
    }

    private String getShortNumber(int number)
    {
        String result ="";
        if (number/ 1000000 > 0)
        {
            result = String.format("%.1fM", number/ 10000000.0);
        }
        else if (number/ 10000 > 0)
        {
            result = String.format("%dK", number/ 1000);
        }
        else
        {
            result = String.valueOf(number);
        }

        return result;
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        if (source != null) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                    true);
        }

        return null;
    }
}
