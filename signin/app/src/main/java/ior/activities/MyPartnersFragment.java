package ior.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.samples.quickstart.signin.R;

import java.util.List;

import ior.engine.ServerHandler;

public class MyPartnersFragment extends Fragment {

    List<String> partners;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_partners_fragment, container, false);

        partners = ServerHandler.getInstance().getPartners();
        TextView textView = view.findViewById(R.id.textViewPart);
        textView.setText(partners.get(0));

        return view;
    }

}
