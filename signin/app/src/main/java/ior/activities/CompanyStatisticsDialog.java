package ior.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.samples.quickstart.signin.R;

import java.util.Date;
import java.util.Optional;

import ior.engine.ServerHandler;

public class CompanyStatisticsDialog extends Dialog {


    private static String TAG = "CompanyStatisticsActivity";

    private ImageView companyImage;
    private TextView companyNameTextView;
    private TextView amountOfPurchases;
    private TextView totalPurchases;
    private TextView averagePurchases;
    private String companyName;
    private String userEmail;
    private Optional<Date> startDate;
    private Optional<Date> endDate;

    public CompanyStatisticsDialog(Context context, String userEmail, Optional<Date> startDate, Optional<Date> endDate, String companyName) {
        super(context);
        this.userEmail = userEmail;
        this.companyName = companyName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_company_statistics);
        companyImage = findViewById(R.id.CompanyPic);
        companyNameTextView = findViewById(R.id.companyTextView);
        amountOfPurchases = findViewById(R.id.amountOfPurchases);
        totalPurchases = findViewById(R.id.totalPricePurchase);
        averagePurchases = findViewById(R.id.averagePricePurchase);

        companyImage.setImageBitmap(ServerHandler.getInstance().getCompany(companyName).getBitmap());
        companyNameTextView.setText(companyName);
        totalPurchases.setText(Double.toString(Math.round(ServerHandler.getInstance().getTotalPurchasesPerCompany(userEmail, companyName, startDate, endDate) * 100.0) / 100.0));
        averagePurchases.setText(Double.toString((Math.round(ServerHandler.getInstance().getAveragePurchasePerCompany(userEmail, companyName, startDate, endDate) * 100.0) / 100.0)));
        amountOfPurchases.setText(Integer.toString(ServerHandler.getInstance().getAmountOfPurchasesPerCompany(userEmail, companyName, startDate, endDate)));
    }
}
