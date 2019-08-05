package ior.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.samples.quickstart.signin.R;

import ior.engine.ServerHandler;

public class AllReceiptsFragment extends Fragment {

    private View view;
    private TextView textViewEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.all_receipts_fragment, container, false);

        textViewEmail = view.findViewById(R.id.textViewEmail_allreceipts);
        textViewEmail.setText(ServerHandler.getInstance().getUser().getEmail());
        return view;

    }
}
