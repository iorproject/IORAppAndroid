package ior.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayout;
import android.text.Layout;
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
        textViewEmail.setText(ServerHandler.getInstance().getUser().getEmail());
        companies = ServerHandler.getInstance().getCompanies();
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        new Thread(this::initGrid).start();

    }

    private void initGrid() {

        adapter = new GridAdapter(getContext(), companies);
        gridView.setAdapter(adapter);

    }
}
