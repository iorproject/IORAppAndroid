package ior.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.samples.quickstart.signin.R;

import java.io.ByteArrayOutputStream;
import java.net.URI;

import ior.engine.ServerHandler;
import utils.IorUtils;

public class ShowProfileActivity extends AppCompatActivity  {

    private Dialog mDialog;
    private static final int IMAGE_PICK_CODE =1000;
    private static final int PERMISSION_CODE =1000;
    private static final int PERMISSION_CAMERA =0;
    private ImageView m_ProfileImage;
    private Button m_UploadImageButton;
    private String m_ProfileImageString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);
        getSupportActionBar().hide();
        final LayoutInflater factory = getLayoutInflater();
        final View textEntryView = factory.inflate(R.layout.popupeditprofile, null);
        m_UploadImageButton =  textEntryView.findViewById(R.id.uploadImage);
        m_ProfileImage = findViewById(R.id.profilePic);
        mDialog = new Dialog(this);
        ImageButton edit = findViewById(R.id.imageButton_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });
        m_UploadImageButton.setText("wdfwfewf");
        m_UploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPic(v);
            }
        });

       // m_ProfileImage.setImageBitmap(getBitmapFromString());
    }

    public void editProfile()
    {
        mDialog.setContentView(R.layout.popupeditprofile);
        mDialog.show();
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
        if (requestCode ==  RESULT_OK || requestCode == IMAGE_PICK_CODE)
        {
            Uri chosenImage = data.getData();
            try {
                 bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), chosenImage);
            }
            catch (Exception e){}
            finally {
                m_ProfileImage.setImageURI(chosenImage);
            }
        }
        else if (requestCode == PERMISSION_CAMERA)
        {
            bitmap = (Bitmap)data.getExtras().get("data");
            m_ProfileImage.setImageBitmap(bitmap);
        }

        setBitmapToString(bitmap);
    }

    private void setBitmapToString(Bitmap bitmap)
    {
        String image = IorUtils.setBitmapToString(bitmap);
        m_ProfileImageString =IorUtils.setBitmapToString(bitmap);
        String email = ServerHandler.getInstance().getSignInUser().getEmail();
        ServerHandler.getInstance().setUserProfileImage(m_ProfileImageString,email);
        ServerHandler.getInstance().getSignInUser().setProfileImage(m_ProfileImageString);
    }

    public void takePicture(View v)
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,PERMISSION_CAMERA);
    }

    private Bitmap getBitmapFromString()
    {
        try{
            return IorUtils.getBitmapFromString(m_ProfileImageString);
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}
