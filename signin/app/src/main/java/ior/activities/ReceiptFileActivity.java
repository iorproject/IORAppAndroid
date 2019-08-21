package ior.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.samples.quickstart.signin.R;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

public class ReceiptFileActivity extends AppCompatActivity {

    private WebView webView;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_file);

        url  = getIntent().getStringExtra("url");
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = (int) (dm.widthPixels * 0.8);
        int height = (int) (dm.heightPixels * 0.72);

        getWindow().setLayout(width, height);

        getSupportActionBar().hide();



        //String url = "https://storage.googleapis.com/iorproject.appspot.com/receipts/ior46800@gmail.com/1562317568000/IV_IN194076978.pdf?GoogleAccessId=firebase-adminsdk-j2rk3@iorproject.iam.gserviceaccount.com&Expires=1574616015&Signature=RnjYHHPpZFpVWTsM22KsfUwDHaKa37F3sLhDE4F9uvpZYG2Fa5SCg56wE6813kgWNyXK3B3HX6R6ewXccIOD%2BeOj9UZxIKS10hRxC%2FOth82grcbAwXMlTj4MNp6raBLq6xZ8fdm4eZgZBua3VqeS70JZWHAn1cdtGoXd6BHNXkdaCgF48AZjri9ulmz5STL6npX8CF8DhtdP4CWi%2BCWc4%2FZ7f5dSQGXB8Ty0ZBjdyN2KcDsuE%2FLe%2FttbqJ3QyFmTecL92nPCB%2FkVk0HrodNq1Xs7jdS17NAMixXVXapFtLV7brvIOgqyFRrlkHzITpJ0dItEAt6JKAr3%2FEKNDN67bw%3D%3D";
        try{

            url= URLEncoder.encode(url,"UTF-8");
            webView = findViewById(R.id.webView_file);
            webView.setWebViewClient(new WebViewClient());
            //webView.getSettings().setSupportZoom(true);
            webView.getSettings().setJavaScriptEnabled(true);
            String url2 = "http://www.pdf995.com/samples/pdf.pdf";
            webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url="+  url);

            //webView.loadUrl(url);

            webView.getSettings().setBuiltInZoomControls(true);


        }
        catch (Exception e) {


            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();

        }




    }





}