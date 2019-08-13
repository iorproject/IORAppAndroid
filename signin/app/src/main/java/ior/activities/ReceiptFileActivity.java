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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_file);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = (int) (dm.widthPixels * 0.8);
        int height = (int) (dm.heightPixels * 0.72);

        getWindow().setLayout(width, height);

        getSupportActionBar().hide();



        String url = "https://firebasestorage.googleapis.com/v0/b/iorproject.appspot.com/o/receipts%2Fior46800%40gmail.com%2FInvoice_51706333284.pdf?alt=media&token=a4fb5bbc-ef3c-44a8-a71b-7dd3e17dc2d4";
        try{

            url= URLEncoder.encode(url,"UTF-8");
            webView = findViewById(R.id.webView_file);
            webView.setWebViewClient(new WebViewClient());
            webView.getSettings().setSupportZoom(true);
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