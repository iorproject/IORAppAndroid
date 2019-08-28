package ior.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.samples.quickstart.signin.R;

import java.util.List;
import java.util.stream.Collectors;

import ior.adapters.AddingPartnerFBAdapter;
import ior.adapters.PartnerRecyclerAdapter;
import ior.engine.ServerHandler;
import ior.engine.User;
import ior.engine.ePartner;

public class FollowersFragment extends Fragment {


    private RecyclerView recyclerView;
    private List<User> followers;
    private PartnerRecyclerAdapter partnerRecyclerAdapter;
    private FloatingActionButton mfloatingActionButton;
    private AddingPartnerFBAdapter mAddingPartnerFBAdapter;



    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_partners_fragment, container, false);
        recyclerView = view.findViewById(R.id.partners_recyclerview);
        mfloatingActionButton = view.findViewById(R.id.floatingActionButton);
        mfloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionButtonWasClicked();
            }
        });
        mAddingPartnerFBAdapter = new AddingPartnerFBAdapter(getContext());
        followers = ServerHandler.getInstance().getSignInUser().getFollowers();
        partnerRecyclerAdapter = new PartnerRecyclerAdapter(getContext(), followers, ePartner.FOLLOWER);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(partnerRecyclerAdapter);
        EditText searchPartner = view.findViewById(R.id.search_partner_ED);
        searchPartner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPartners(searchPartner);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && partnerRecyclerAdapter!=null)
        {
            partnerRecyclerAdapter.setAdapterDate(ServerHandler.getInstance().getSignInUser().getFollowers());
        }
    }

    private void filterPartners(EditText editText)
    {
        if(followers!=null)
        {
            List<User> filterUsers = followers.stream().filter((user) -> user.getName().startsWith(editText.getText().toString())).collect(Collectors.toList());
            partnerRecyclerAdapter.setAdapterDate(filterUsers);
        }
    }

    private void floatingActionButtonWasClicked()
    {
        mAddingPartnerFBAdapter.showDialog();
    }
}
