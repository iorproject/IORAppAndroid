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
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.samples.quickstart.signin.R;

import java.util.ArrayList;
import java.util.List;

import ior.adapters.ReceiptRecycleAdapter;
import ior.engine.Receipt;
import ior.engine.ServerHandler;
import utils.IorUtils;

public class CompanyReceiptsActivity extends AppCompatActivity {
    private TextView mTextMessage;

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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                    mTextMessage.setText(R.string.title_home);
//                    return true;
//                case R.id.navigation_dashboard:
//                    mTextMessage.setText(R.string.title_dashboard);
//                    return true;
//                case R.id.navigation_notifications:
//                    mTextMessage.setText(R.string.title_notifications);
//                    return true;
//            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_receipts);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Intent intent = getIntent();
        userEmail = intent.getStringExtra("email");
        companyName = intent.getStringExtra("company");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(companyName + "'s receipts");
        receipts = ServerHandler.getInstance().getCompanyReceipts(userEmail, companyName);
        counter = findViewById(R.id.textViewCounter_companyReceipts);
        imageViewNext = findViewById(R.id.imageView_next_companyReceipts);
        imageViewPrev = findViewById(R.id.imageView_prev_companyReceipts);
        container = findViewById(R.id.frameLayout_companyReceipts);

        navViewBottom = findViewById(R.id.nav_view);
        navViewBottom.setOnNavigationItemSelectedListener(menuItem -> {
            return IorUtils.onNavigationItemSelected(this, menuItem);
        });

        recycleLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView = findViewById(R.id.recyclerView_companyReceipts);
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
            }
            else {
                imageViewPrev.setImageResource(R.drawable.next_arrow_able);
                imageViewPrev.setEnabled(true);
            }


            if (currentPosition < receipts.size() - 1) {
                imageViewNext.setImageResource(R.drawable.next_arrow_able);
                imageViewNext.setEnabled(true);
            }
            else {
                imageViewNext.setImageResource(R.drawable.next_arrow_unable);
                imageViewNext.setEnabled(false);
            }

        }
    });



    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MyReceiptsActivityNav.class));
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
