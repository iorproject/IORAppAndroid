package ior.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.samples.quickstart.signin.R;

import java.util.ArrayList;
import java.util.List;

import ior.engine.Receipt;
import ior.engine.ServerHandler;

public class ViewStatisticsActivity extends AppCompatActivity {

    private static String TAG = "ViewStatisticsActivity";

    private TextView totalPricePurchase;
    private TextView amountOfPurchases;
    private TextView averagePricePurchase;
    private String email;
    private List<Receipt> receipts;

    private float[] yData = {25.3f,10.6f,66.76f,44.32f,46.01f,16.89f,23.9f};
    private String [] xData = {"Amazon","Booking","Gmail","Alibaba","Ebay","ASOS","hotels.com"};
    private PieChart mPieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_statistics);
        Log.d(TAG,"OnCreate: starting pieChart");
        totalPricePurchase = findViewById(R.id.total_purchases_value);
        amountOfPurchases = findViewById(R.id.amount_of_purchases_value);
        averagePricePurchase = findViewById(R.id.average_purchase_value);
        mPieChart = findViewById(R.id.companyPieChart);
        //TODO: achieve email
        email = "ior46800@gmail.com";
        ServerHandler.getInstance().fetchUserCompanies(email,this::displayStatistics);


        Description description = new Description();
        description.setText("Company expenses (in ILS)");
        mPieChart.setDescription(description);
        mPieChart.setRotationEnabled(true);
        mPieChart.setHoleRadius(25f);
        mPieChart.setTransparentCircleAlpha(0);
        mPieChart.setCenterText("Company chart");
        mPieChart.setCenterTextSize(10);
        //mPieChart.setDrawEntryLabels(true);

        addDataSet();
    }

    private void displayStatistics(){
        totalPricePurchase.setText(Double.toString(ServerHandler.getInstance().getTotalPurchases(email)));
        averagePricePurchase.setText(Double.toString(ServerHandler.getInstance().getAveragePurchase(email)));
        amountOfPurchases.setText(Integer.toString(ServerHandler.getInstance().getAmountOfPurchases(email)));
    }

    private void addDataSet(){
        List<PieEntry> yEntry = new ArrayList<>();
        List<String> xEntry = new ArrayList<>();

        for(int i = 0; i< yData.length;++i){
            yEntry.add(new PieEntry(yData[i],i));
            xEntry.add(xData[i]);
        }

        //create data set
        PieDataSet pieDataSet = new PieDataSet(yEntry, "Companies");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        //add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.DKGRAY);
        colors.add(Color.MAGENTA);
        colors.add(Color.GRAY);
        colors.add(Color.GREEN);
        colors.add(Color.YELLOW);
        colors.add(Color.LTGRAY);
        pieDataSet.setColors(colors);

        //add legend to to chart
        Legend legand = mPieChart.getLegend();
//        legand.setForm(Legend.LegendForm.LINE);
        legand.setForm(Legend.LegendForm.CIRCLE);

        PieData piedata = new PieData(pieDataSet);
        mPieChart.setData(piedata);
        mPieChart.invalidate();
    }


}
