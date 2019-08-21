package ior.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.samples.quickstart.signin.R;

import java.util.ArrayList;
import java.util.List;

import ior.engine.Receipt;
import ior.engine.ServerHandler;
import utils.IorUtils;

public class ViewStatisticsActivity extends AppCompatActivity {

    private BottomNavigationView navViewBottom;


    private class ColorRGB{
        private int red;
        private int green;
        private int blue;

        public ColorRGB(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }

    private static String TAG = "ViewStatisticsActivity";
    private Context context;
    private TextView totalPricePurchase;
    private TextView amountOfPurchases;
    private TextView averagePricePurchase;
    private String email;

    private List<String> xData;
    private List<Float> yData;
    private PieChart mPieChart;
    private ColorRGB baseColor = new ColorRGB(80,148,176);;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_statistics);
        context = this;
        Log.d(TAG,"OnCreate: starting pieChart");
        totalPricePurchase = findViewById(R.id.total_purchases_value);
        amountOfPurchases = findViewById(R.id.amount_of_purchases_value);
        averagePricePurchase = findViewById(R.id.average_purchase_value);
        mPieChart = findViewById(R.id.companyPieChart);
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        Description description = new Description();
        description.setText("Company expenses (in ILS)");
        mPieChart.setDescription(description);
        mPieChart.setRotationEnabled(true);
        mPieChart.setHoleRadius(25f);
        mPieChart.setTransparentCircleAlpha(0);
        mPieChart.setCenterText("Company chart");
        mPieChart.setCenterTextSize(10);


        navViewBottom = findViewById(R.id.nav_view);
        navViewBottom.setSelectedItemId(R.id.navigation_statInfo);

        navViewBottom.setOnNavigationItemSelectedListener(menuItem -> {
            return IorUtils.onNavigationItemSelected(this, menuItem);
        });


        //mPieChart.setDrawEntryLabels(true);
        displayStatistics();
    }

    private void displayStatistics(){
        totalPricePurchase.setText(Double.toString(Math.round(ServerHandler.getInstance().getTotalPurchases(email) * 100.0) / 100.0));
        averagePricePurchase.setText(Double.toString((Math.round(ServerHandler.getInstance().getAveragePurchase(email) * 100.0) / 100.0)));
        amountOfPurchases.setText(Integer.toString(ServerHandler.getInstance().getAmountOfPurchases(email)));
        xData = ServerHandler.getInstance().getCompaniesName(email);
        yData = ServerHandler.getInstance().getCompaniesTotalPrice(email);
        addDataSet();
        mPieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                int pos = e.toString().indexOf("y: ");
                String totalPrice = e.toString().substring(pos +3);
                for (int i =0; i<yData.size();++i){
                    if(yData.get(i) == Float.parseFloat(totalPrice)){
                        pos = i;
                        break;
                    }
                }
                String company = xData.get(pos);

                CompanyStatisticsDialog companyStatisticsDialog = new CompanyStatisticsDialog(context,email,company);
                companyStatisticsDialog.show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

    }


    private double getHue(int red, int green, int blue) {

        float min = Math.min(Math.min(red, green), blue);
        float max = Math.max(Math.max(red, green), blue);

        if (min == max) {
            return 0;
        }

        float hue = 0f;
        if (max == red) {
            hue = (green - blue) / (max - min);

        } else if (max == green) {
            hue = 2f + (blue - red) / (max - min);

        } else {
            hue = 4f + (red - green) / (max - min);
        }

        hue = hue * 60;
        if (hue < 0) hue = hue + 360;

        return hue;
    }

        private void addDataSet(){
        List<PieEntry> yEntry = new ArrayList<>();
        List<String> xEntry = new ArrayList<>();

        for(int i = 0; i< yData.size();++i){
            yEntry.add(new PieEntry(yData.get(i),i));
            xEntry.add(xData.get(i));
        }

        //create data set
        PieDataSet pieDataSet = new PieDataSet(yEntry, "Companies");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        //add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        float saturaion = 0.55f;
        float brightness = 0.69f;
        double baseHue = getHue(baseColor.red,baseColor.green,baseColor.blue);
        colors.add(Color.rgb(baseColor.red,baseColor.green,baseColor.blue));

            double step = (240.0 / (double)xData.size());

            for (int i = 1; i < xData.size(); ++i)
            {
                double nextColorHue = (baseHue + step * ((double)i)) % 240.0;
                colors.add(ColorUtils.HSLToColor(new float[]{(float)nextColorHue,saturaion,brightness}));
            }


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
