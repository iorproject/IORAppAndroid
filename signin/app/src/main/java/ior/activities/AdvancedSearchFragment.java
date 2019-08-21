package ior.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.api.client.util.DateTime;
import com.google.samples.quickstart.signin.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import ior.adapters.FilterItem;
import ior.adapters.FilterItemAdapter;
import ior.engine.Company;
import ior.engine.ServerHandler;
import ior.engine.eCurrency;

public class AdvancedSearchFragment extends Fragment {

    private static final String UNDEFINED_DATE = "No Limit";
    private static final int START_DATE_CODE = 1;
    private static final int END_DATE_CODE = 2;


    private View view;
    private RecyclerView recyclerViewCompany;
    private FilterItemAdapter adapterCompany;
    private RecyclerView.LayoutManager recycleLayoutManagerCompany;
    private RecyclerView recyclerViewCurrency;
    private FilterItemAdapter adapterCurrency;
    private RecyclerView.LayoutManager getRecycleLayoutManagerCurrency;
    List<FilterItem> companies = new ArrayList<>();
    List<FilterItem> currencies = new ArrayList<>();
    float maxPrice;
    private String userEmail;
    private CheckBox checkBoxCompany;
    private CheckBox checkBoxCurrency;
    private TextView textViewStartDate;
    private TextView textViewEndDate;
    private ImageView imageViewStartDate;
    private ImageView imageViewEndDate;
    private TextView textViewPriceRange;
    private CrystalRangeSeekbar rangeSeekBarPrice;
    private Button buttonApply;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.advanced_search_fragment, container, false);
        Bundle bundle = this.getArguments();

        userEmail = bundle != null ?
                bundle.getString("email", ServerHandler.getInstance().getSignInUser().getEmail())
                : ServerHandler.getInstance().getSignInUser().getEmail();

        recyclerViewCompany = view.findViewById(R.id.recycleViewCompany_advancedSearch);
        recyclerViewCurrency = view.findViewById(R.id.recycleViewCurrency_advancedSearch);
        checkBoxCompany = view.findViewById(R.id.checkBoxCompany_advancedSearch);
        checkBoxCurrency = view.findViewById(R.id.checkBoxCurrency_advancedSearch);
        textViewStartDate = view.findViewById(R.id.textViewStartDateVal_advancedSearch);
        textViewEndDate = view.findViewById(R.id.textViewEndDateVal_advancedSearch);
        imageViewStartDate = view.findViewById(R.id.imageViewCalendarStart_advancedSearch);
        imageViewEndDate = view.findViewById(R.id.imageViewCalendarEnd_advancedSearch);
        buttonApply = view.findViewById(R.id.buttonApply_advancedSearch);
        buttonApply.setOnClickListener(this::applySearch);

        initPriceRange();


        imageViewStartDate.setOnClickListener(this::chooseDate);
        imageViewEndDate.setOnClickListener(this::chooseDate);

        checkBoxCompany.setOnCheckedChangeListener((buttonView, isChecked) -> {

            for (int i = 0; i < companies.size(); i++) {

                companies.get(i).setSelected(isChecked);
                adapterCompany.notifyItemChanged(i);

            }

            String text = isChecked ? "remove all" : "select all" ;
            checkBoxCompany.setText(text);
        });

        checkBoxCurrency.setOnCheckedChangeListener((buttonView, isChecked) -> {

            for (int i = 0; i < currencies.size(); i++) {

                currencies.get(i).setSelected(isChecked);
                adapterCurrency.notifyItemChanged(i);
            }

            String text = isChecked ? "remove all" : "select all" ;
            checkBoxCurrency.setText(text);
        });

        recycleLayoutManagerCompany = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        getRecycleLayoutManagerCurrency = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerViewCompany.setLayoutManager(recycleLayoutManagerCompany);
        recyclerViewCurrency.setLayoutManager(getRecycleLayoutManagerCurrency);

        adapterCompany = new FilterItemAdapter(getContext() , companies);
        List<Company> com = ServerHandler.getInstance().getUserCompanies(userEmail);
        for (Company c : com) {
            companies.add(new FilterItem(c.getName()));
        }

        recyclerViewCompany.setAdapter(adapterCompany);


        adapterCurrency = new FilterItemAdapter(getContext(), currencies);
        List<eCurrency> userCurrencies = ServerHandler.getInstance().getUserCurrencies(userEmail);
        for (eCurrency curr : userCurrencies)
            currencies.add(new FilterItem(curr.toString()));

        recyclerViewCurrency.setAdapter(adapterCurrency);

        Button button = view.findViewById(R.id.button);
        button.setOnClickListener(v -> {

            Intent intent = new Intent(view.getContext(), CompanyReceiptsActivity.class);
            intent.putExtra("filter", true);
            intent.putExtra("email", ServerHandler.getInstance().getSignInUser().getEmail());
            startActivity(intent);

        });
        return view;
    }

    private void initPriceRange() {

        textViewPriceRange = view.findViewById(R.id.textViewPriceRange_advancedSearch);
        rangeSeekBarPrice = view.findViewById(R.id.rangeSeekbarPrice_advancedSearch);

        int maxReceiptPrice = (int)ServerHandler.getInstance().getUserMaxPriceReceipt(userEmail) + 1;
        rangeSeekBarPrice.setMinValue(0);
        rangeSeekBarPrice.setMaxValue(maxReceiptPrice);
        rangeSeekBarPrice.setMinStartValue(0);
        rangeSeekBarPrice.setMaxStartValue(maxReceiptPrice);


        textViewPriceRange.setText("Price range 0 - " + maxReceiptPrice);


        rangeSeekBarPrice.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {

            textViewPriceRange.setText("Price range " + minValue + " - " + maxValue);
        });

    }


    public void chooseDate(View v) {

        view.setAlpha(0.5f);
        Intent intent = new Intent(getContext(), CalendarActivity.class);
        int code = v.getId() == imageViewStartDate.getId() ? START_DATE_CODE : END_DATE_CODE;
        intent.putExtra("code", code);
        startActivityForResult(intent, code);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        view.setAlpha(1.0f);
        if (resultCode == START_DATE_CODE) {

            String date = data.getStringExtra("date");
            textViewStartDate.setText(date);

        }
        else if (resultCode == END_DATE_CODE) {

            String date = data.getStringExtra("date");
            textViewEndDate.setText(date);
        }

    }

    public void applySearch(View v) {

        ArrayList<String> selectedCompanies = new ArrayList<>();
        ArrayList<String> selectedCurrencies = new ArrayList<>();

        for (FilterItem item : companies.stream().filter(i -> i.isSelected()).collect(Collectors.toList()))
            selectedCompanies.add(item.getName());

        for (FilterItem item : currencies.stream().filter(i -> i.isSelected()).collect(Collectors.toList()))
            selectedCurrencies.add(item.getName());

        Number min = rangeSeekBarPrice.getSelectedMinValue();
        Number max = rangeSeekBarPrice.getSelectedMaxValue();
        float minPrice = min.floatValue();
        float maxPrice = max.floatValue();

        String startDate = textViewStartDate.getText().toString().equals(UNDEFINED_DATE) ?
                null : textViewStartDate.getText().toString();

        String endDate = textViewEndDate.getText().toString().equals(UNDEFINED_DATE) ?
                null : textViewEndDate.getText().toString();

        if (selectedCompanies.size() == 0) {
            Toast.makeText(getContext(), "You have to choose at least one company", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (selectedCurrencies.size() == 0) {
            Toast.makeText(getContext(), "You have to choose at least one currency", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (startDate != null && endDate != null) {

            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date dateEnd = format.parse(endDate);
                Date dateStart = format.parse(startDate);
                if (!dateStart.before(dateEnd)) {

                    Toast.makeText(getContext(), "Start date is not before end date", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {

                    dateEnd.setTime(dateEnd.getTime() + (1000 * 60 * 60 * 24));
                    endDate = format.format(dateEnd);
                    int x = 5;
                }
            }
            catch (Exception e) {

            }

        }

        Intent intent = new Intent(getContext(), CompanyReceiptsActivity.class);
        intent.putExtra("filter", true);
        intent.putExtra("barTitle", "Search Results");
        intent.putExtra("email", userEmail);
        intent.putExtra("startDate", startDate);
        intent.putExtra("endDate", endDate);
        intent.putExtra("minPrice", minPrice);
        intent.putExtra("maxPrice", maxPrice);
        intent.putStringArrayListExtra("currencies", selectedCurrencies);
        intent.putStringArrayListExtra("companies", selectedCompanies);

        startActivity(intent);



//        String startDateStr = intent.getStringExtra("startDate");
//        String endDateStr = intent.getStringExtra("endDate");
//        float minPrice = intent.getFloatExtra("minPrice", 0);
//        float maxPrice = intent.getFloatExtra("maxPrice", 0);
//        List<String> currenciesStr = intent.getStringArrayListExtra("currencies");
//        List<eCurrency> currencies = new ArrayList<>();
//        for (String c : currenciesStr)
//            currencies.add(eCurrency.createCurrency(c));
//
//        List<String> companies = intent.getStringArrayListExtra("companies");

    }

}
