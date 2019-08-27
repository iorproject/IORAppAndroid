package ior.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.samples.quickstart.signin.R;

import java.util.ArrayList;
import java.util.List;
import ior.adapters.GridAdapter;
import ior.engine.Company;
import ior.engine.ServerHandler;

public class AllReceiptsFragment extends Fragment {

    private View view;
    private GridView gridView;
    private String userEmail;
    private List<Company> companies;
    private GridAdapter adapter;
    private LinearLayout linearLayout;
    private TextView textViewMessage;
    private List<String> usersEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.all_receipts_fragment, container, false);
        gridView = view.findViewById(R.id.gridView_allReceipts);
        Bundle bundle = this.getArguments();

        userEmail = bundle != null ?
                bundle.getString("email", ServerHandler.getInstance().getSignInUser().getEmail())
                : ServerHandler.getInstance().getSignInUser().getEmail();


        linearLayout = view.findViewById(R.id.linear_allReceipts);
        textViewMessage = view.findViewById(R.id.textView_message_allReceipts);
        companies = ServerHandler.getInstance().getUserCompanies(userEmail);

        if (companies.size() == 0) {
            linearLayout.setVisibility(View.GONE);
            textViewMessage.setVisibility(View.VISIBLE);
        }

        else {
            linearLayout.setVisibility(View.VISIBLE);
            textViewMessage.setVisibility(View.GONE);
            initGrid();
        }

        return view;
    }

    private void initGrid() {

        adapter = new GridAdapter(getContext(), R.layout.companies_grid_adapter, companies, userEmail);
        gridView.setAdapter(adapter);
    }
}