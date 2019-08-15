package ior.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.samples.quickstart.signin.R;

import java.util.ArrayList;
import java.util.List;

import ior.adapters.PartnerRecyclerAdapter;
import ior.engine.User;

public class MyPartnersFragment extends Fragment {

    //List<String> partners;
    private RecyclerView recyclerView;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_partners_fragment, container, false);

        //partners = ServerHandler.getInstance().getPartners();
        recyclerView = view.findViewById(R.id.partners_recyclerview);
        List<User> partners = new ArrayList<>();
        partners.add(new User("eliran@3123",null));
        partners.add(new User("moshe@3123",null));
        partners.add(new User("sapir@3123",null));


        PartnerRecyclerAdapter partnerRecyclerAdapter = new PartnerRecyclerAdapter(getContext(),partners);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setAdapter(partnerRecyclerAdapter);
        return view;
    }

}
