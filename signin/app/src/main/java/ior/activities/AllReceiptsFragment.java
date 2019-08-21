package ior.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.google.samples.quickstart.signin.R;

import java.util.List;

import ior.adapters.GridAdapter;
import ior.engine.Company;
import ior.engine.ServerHandler;

public class AllReceiptsFragment extends Fragment {

    private View view;
    private TextView textViewEmail;
    private GridView gridView;
    private String userEmail;
    private List<Company> companies;
    private GridAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.all_receipts_fragment, container, false);
        gridView = view.findViewById(R.id.gridView_allReceipts);
        textViewEmail = view.findViewById(R.id.textViewEmail_allreceipts);
        Bundle bundle = this.getArguments();

        userEmail = bundle != null ?
                bundle.getString("email", ServerHandler.getInstance().getSignInUser().getEmail())
                : ServerHandler.getInstance().getSignInUser().getEmail();

        textViewEmail.setText(ServerHandler.getInstance().getSignInUser().getEmail());
        companies = ServerHandler.getInstance().getUserCompanies(userEmail);
        initGrid();
        //companies = ServerHandler.getInstance().getCompanies();
        return view;
    }


    private void initGrid() {

        adapter = new GridAdapter(getContext(), R.layout.companies_grid_adapter, companies, userEmail);
        gridView.setAdapter(adapter);

//        gridView.setOnItemClickListener((parent, view, position, id) -> {
//
//            ServerHandler.getInstance().fetchCompanyReceipts(userEmail, view.getTag().toString(), () -> {
//
//
//                Intent intent = new Intent(getContext(), CompanyReceiptsActivity.class);
//                intent.putExtra("email", userEmail);
//                intent.putExtra("company",view.getTag().toString());
//                startActivity(intent);
//            });
//            startActivity(new Intent(getContext(), CompanyReceiptsActivity.class));
//        });
    }
}
