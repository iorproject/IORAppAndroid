package ior.activities;

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
import android.widget.CompoundButton;

import com.google.samples.quickstart.signin.R;

import java.util.ArrayList;
import java.util.List;

import ior.adapters.FilterItem;
import ior.adapters.FilterItemAdapter;
import ior.adapters.ReceiptRecycleAdapter;
import ior.engine.Company;
import ior.engine.ServerHandler;

public class AdvancedSearchFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private FilterItemAdapter adapter;
    private RecyclerView.LayoutManager recycleLayoutManager;
    List<FilterItem> companies = new ArrayList<>();
    List<FilterItem> currencies = new ArrayList<>();
    float maxPrice;
    private String userEmail;
    private CheckBox checkBoxCompany;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.advanced_search_fragment, container, false);
        Bundle bundle = this.getArguments();

        userEmail = bundle != null ?
                bundle.getString("email", ServerHandler.getInstance().getSignInUser().getEmail())
                : ServerHandler.getInstance().getSignInUser().getEmail();

        recyclerView = view.findViewById(R.id.recycleViewCompany_advancedSearch);
        checkBoxCompany = view.findViewById(R.id.checkBoxCompany_advancedSearch);
        checkBoxCompany.setOnCheckedChangeListener((buttonView, isChecked) -> {

            for (int i = 0; i < companies.size(); i++) {

                companies.get(i).setSelected(isChecked);
                adapter.notifyItemChanged(i);

            }



        });

        recycleLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(recycleLayoutManager);
        adapter = new FilterItemAdapter(getContext() , companies);
        List<Company> com = ServerHandler.getInstance().getUserCompanies(userEmail);
        for (Company c : com) {
            companies.add(new FilterItem(c.getName()));
        }


        recyclerView.setAdapter(adapter);


        Button button = view.findViewById(R.id.button);
        button.setOnClickListener(v -> {

            Intent intent = new Intent(view.getContext(), CompanyReceiptsActivity.class);
            intent.putExtra("filter", true);
            intent.putExtra("email", ServerHandler.getInstance().getSignInUser().getEmail());
            startActivity(intent);

        });
        return view;
    }
}
