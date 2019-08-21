package ior.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.CalendarView;

import com.google.samples.quickstart.signin.R;

import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {

    private int code;
    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Intent intent = getIntent();
        code = intent.getIntExtra("code", -1);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        calendarView = findViewById(R.id.calendarView);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = (int)(dm.widthPixels * 0.75);
        int height = (int)(dm.heightPixels * 0.55);

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




    @Override
    public void onBackPressed() {

        finish();
    }
}
