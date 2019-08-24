package ior.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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

    private static final String UNDEFINED_DATE = "No Limit";
    private static String TAG = "ViewStatisticsActivity";
    private Context context;
    private TextView totalPricePurchase;
    private TextView amountOfPurchases;
    private TextView averagePricePurchase;
    private TextView mostExpensivePurchase;
    private TextView latestPurchase;
    private String email;
    private ImageView imageViewStartDate;
    private ImageView imageViewEndDate;

    private TextView textViewStartDate;
    private TextView textViewEndDate;
    private Button submitButton;

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
        mostExpensivePurchase = findViewById(R.id.most_expensive_purchase_value);
        imageViewStartDate = findViewById(R.id.imageViewCalendarStart_viewStatistics);
        imageViewEndDate = findViewById(R.id.imageViewCalendarEnd_viewStatistics);
        textViewStartDate = findViewById(R.id.textViewStartDateVal_viewStatistics);
        textViewEndDate = findViewById(R.id.textViewEndDateVal_viewStatistics);
        submitButton = findViewById(R.id.buttonSubmit_viewStatistics);
        latestPurchase = findViewById(R.id.latest_purchase_value);
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

        submitButton.setOnClickListener(this::submitDateRange);
        imageViewStartDate.setOnClickListener(this::chooseDate);
        imageViewEndDate.setOnClickListener(this::chooseDate);

        navViewBottom = findViewById(R.id.nav_view);
        navViewBottom.setSelectedItemId(R.id.navigation_statInfo);

        navViewBottom.setOnNavigationItemSelectedListener(menuItem -> {
            IorUtils.onNavigationItemSelected(this, menuItem);
            return false;
        });

        displayStatistics(Optional.empty(),Optional.empty());
    }


    public void submitDateRange(View v) {

        String startDate = textViewStartDate.getText().toString().equals(UNDEFINED_DATE) ?
                null : textViewStartDate.getText().toString();

        String endDate = textViewEndDate.getText().toString().equals(UNDEFINED_DATE) ?
                null : textViewEndDate.getText().toString();

        if((startDate == null && endDate != null) || (startDate != null && endDate == null)){
            Toast.makeText(context, "You have to choose start time and end time", Toast.LENGTH_SHORT).show();
            return;
        }
        Date dateEnd = null;
        Date dateStart = null;
        if (startDate != null && endDate != null) {

            DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            try {
                dateEnd = format.parse(endDate);
                dateStart = format.parse(startDate);
                if (!dateStart.before(dateEnd)) {
                    Toast.makeText(context, "Start date is not before end date", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {

                    dateEnd.setTime(dateEnd.getTime() + (1000 * 60 * 60 * 24));
                    endDate = format.format(dateEnd);
                }
            }
            catch (Exception e) {

            }

        }

        displayStatistics(Optional.ofNullable(dateStart), Optional.ofNullable(dateEnd));

    }


    private void displayStatistics(Optional<Date> startDate, Optional<Date> endDate){
        totalPricePurchase.setText(Double.toString(Math.round(ServerHandler.getInstance().getTotalPurchases(email,startDate,endDate) * 100.0) / 100.0));
        averagePricePurchase.setText(Double.toString((Math.round(ServerHandler.getInstance().getAveragePurchase(email,startDate,endDate) * 100.0) / 100.0)));
        amountOfPurchases.setText(Integer.toString(ServerHandler.getInstance().getAmountOfPurchases(email,startDate,endDate)));
        mostExpensivePurchase.setText(Double.toString(Math.round(ServerHandler.getInstance().getMostExpensivePurchase(email,startDate,endDate) * 100.0) / 100.0));
        latestPurchase.setText(Double.toString(Math.round(ServerHandler.getInstance().getLatestPurchase(email,startDate,endDate) * 100.0) / 100.0));
        xData = ServerHandler.getInstance().getCompaniesName(email,startDate,endDate);
        yData = ServerHandler.getInstance().getCompaniesTotalPrice(email,startDate,endDate);
        addDataSet();
        mPieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                int pos = h.toString().indexOf("x: ");
                pos =Integer.parseInt(h.toString().substring(pos +3,pos +4));
                String company = xData.get(pos);

                CompanyStatisticsDialog companyStatisticsDialog = new CompanyStatisticsDialog(context,email,startDate,endDate,company);
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
        pieDataSet.setSliceSpace(3);
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

    public void chooseDate(View v) {

        //TODO :set the brightness
        Intent intent = new Intent(context, CalendarActivity.class);
        int code = v.getId() == imageViewStartDate.getId() ? START_DATE_CODE : END_DATE_CODE;
        intent.putExtra("code", code);
        startActivityForResult(intent, code);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO : set the brightness
        if (resultCode == START_DATE_CODE) {

            String date = data.getStringExtra("date");
            textViewStartDate.setText(date);

        }
        else if (resultCode == END_DATE_CODE) {

            String date = data.getStringExtra("date");
            textViewEndDate.setText(date);
        }

    }


    private static final int START_DATE_CODE = 1;
    private static final int END_DATE_CODE = 2;


}
