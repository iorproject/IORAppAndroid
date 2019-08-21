package ior.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.samples.quickstart.signin.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import ior.adapters.ReceiptRecycleAdapter;
import ior.engine.Receipt;
import ior.engine.ServerHandler;
import ior.engine.eCurrency;
import utils.IorUtils;

public class CompanyReceiptsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReceiptRecycleAdapter adapter;
    private List<Receipt> receipts;
    private String userEmail;
    private String companyName;
    private int currentPosition = 0;
    private TextView counter;
    private ImageView imageViewNext;
    private ImageView imageViewPrev;
    private RecyclerView.LayoutManager recycleLayoutManager;
    private FrameLayout container;
    private BottomNavigationView navViewBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_receipts);

        Intent intent = getIntent();
        userEmail = intent.getStringExtra("email");
        companyName = intent.getStringExtra("company");
        String barTitle = intent.getStringExtra("barTitle");
        barTitle = barTitle.substring(0, 1).toUpperCase() + barTitle.substring(1);
        boolean filter = intent.getBooleanExtra("filter", false);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(barTitle);
        if (!filter)
            receipts = ServerHandler.getInstance().getCompanyReceipts(userEmail, companyName);

        else {

            String startDateStr = intent.getStringExtra("startDate");
            String endDateStr = intent.getStringExtra("endDate");
            float minPrice = intent.getFloatExtra("minPrice", 0);
            float maxPrice = intent.getFloatExtra("maxPrice", 0);
            List<String> currenciesStr = intent.getStringArrayListExtra("currencies");
            List<eCurrency> currencies = new ArrayList<>();
            for (String c : currenciesStr)
                currencies.add(eCurrency.createCurrency(c));

            List<String> companies = intent.getStringArrayListExtra("companies");

            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            try{

                Date startDate = startDateStr == null ? format.parse("1/1/2019") : format.parse(startDateStr);
                Date endDate = endDateStr == null ? Calendar.getInstance().getTime() : format.parse(endDateStr);
                receipts = ServerHandler.getInstance().getReceiptsFiltered(userEmail,
                        companies, startDate, endDate, minPrice, maxPrice, currencies);
            }
            catch (Exception e) {

                int fff = 5;

            }


        }


        counter = findViewById(R.id.textViewCounter_companyReceipts);
        imageViewNext = findViewById(R.id.imageView_next_companyReceipts);
        imageViewPrev = findViewById(R.id.imageView_prev_companyReceipts);
        container = findViewById(R.id.frameLayout_companyReceipts);
        navViewBottom = findViewById(R.id.nav_view);
        recyclerView = findViewById(R.id.recyclerView_companyReceipts);

        navViewBottom.setOnNavigationItemSelectedListener(menuItem ->
                IorUtils.onNavigationItemSelected(this, menuItem));


        if (receipts.size() == 0) {

            counter.setVisibility(View.INVISIBLE);
            imageViewNext.setVisibility(View.INVISIBLE);
            imageViewPrev.setVisibility(View.INVISIBLE);
            Toast t = Toast.makeText(this, "No Receipts found", Toast.LENGTH_LONG);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();
            return;


        }


        recycleLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        adapter = new ReceiptRecycleAdapter(this, R.layout.receipts_adapter, receipts, container);
        recyclerView.setLayoutManager(recycleLayoutManager);
        recyclerView.setAdapter(adapter);

        if (receipts.size() > 1)
            imageViewNext.setImageResource(R.drawable.next_arrow_able);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    //Dragging
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    currentPosition = ((LinearLayoutManager) recycleLayoutManager).findFirstVisibleItemPosition();                 /*
                    Here load the Image to image view with picaso
                 */

                }
            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                currentPosition = ((LinearLayoutManager) recycleLayoutManager).findFirstVisibleItemPosition();
                /* Log.e ("VisibleItem", String.valueOf(firstVisibleItem));*/

                counter.setText((currentPosition + 1) + " / " + receipts.size());

                if (currentPosition == 0) {
                    imageViewPrev.setImageResource(R.drawable.prev_arrow_unable);
                    imageViewPrev.setEnabled(false);
                } else {
                    imageViewPrev.setImageResource(R.drawable.next_arrow_able);
                    imageViewPrev.setEnabled(true);
                }


                if (currentPosition < receipts.size() - 1) {
                    imageViewNext.setImageResource(R.drawable.next_arrow_able);
                    imageViewNext.setEnabled(true);
                } else {
                    imageViewNext.setImageResource(R.drawable.next_arrow_unable);
                    imageViewNext.setEnabled(false);
                }

            }
        });
    }
    

    public void scrollReceipt(View view) {

        int newIdex = currentPosition - 1;

        if (view.getId() == imageViewNext.getId())
            newIdex = currentPosition + 1;

        recycleLayoutManager.smoothScrollToPosition(recyclerView, null , newIdex);
        //recyclerView.scrollToPosition(currentPosition);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        container.setAlpha(1);
    }
}
