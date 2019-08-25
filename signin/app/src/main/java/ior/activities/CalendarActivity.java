package ior.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import com.google.samples.quickstart.signin.R;

import java.util.Calendar;

import ior.adapters.FilterItemAdapter;

public class CalendarActivity extends AppCompatActivity {

    private int code;
    private CalendarView calendarView;
    Button buttonReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Intent intent = getIntent();
        code = intent.getIntExtra("code", -1);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        calendarView = findViewById(R.id.calendarView);
        buttonReset = findViewById(R.id.buttonResetDate_calendar);
        buttonReset.setOnClickListener(this::resetDate);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = (int)(dm.widthPixels * 0.7);
        int height = (int)(dm.heightPixels * 0.63);

        getWindow().setLayout(width, height);


        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {

            String date = dayOfMonth + "/" + (month + 1) + "/" + year;
            Intent intent2 = new Intent();
            intent2.putExtra("date", date);
            setResult(code, intent2);
            finish();
        });

    }

    private String getMonth(int month) {

        String res = "";


        return res;

    }

    public void resetDate(View view) {
        int resultCode = code == AdvancedSearchFragment.START_DATE_CODE ?
                AdvancedSearchFragment.RESET_START_DATE_CODE : AdvancedSearchFragment.RESET_END_DATE_CODE;
        setResult(resultCode);
        finish();
    }



    @Override
    public void onBackPressed() {

        finish();
    }
}
