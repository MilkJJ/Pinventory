package com.example.pinventory;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

public class AdminStatisticFragment extends Fragment implements HistoryRVAdapter.HistoryClickInterface {

    private RecyclerView historyRV;
    private ProgressBar progressBar;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ArrayList<HistoryRVModel> historyRVModelArrayList;
    private HistoryRVAdapter historyRVAdapter;
    private SearchView mSearchView;

    public AdminStatisticFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin_statistic_list, container, false);

        historyRV = rootView.findViewById(R.id.idAdminRVStatistics);
        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        firebaseDatabase = FirebaseDatabase.getInstance();

        historyRVModelArrayList = new ArrayList<>();

        historyRVAdapter = new HistoryRVAdapter(historyRVModelArrayList, requireContext(), this);
        historyRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        historyRV.setAdapter(historyRVAdapter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("History");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot userSnapshot, @Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);

                TreeMap<String, HistoryRVModel> itemMap = new TreeMap<>(Collections.reverseOrder());

                // Iterate through each user's branch under "History"
                for (DataSnapshot itemSnapshot : userSnapshot.getChildren()) {
                    // Assuming each item has a unique identifier
                    //String itemId = itemSnapshot.getKey();

                    // Assuming each item has an "actionHistory" field
//                    String actionHistory1 = itemSnapshot.child("actionHistory").getValue(String.class);
//
//                    if (itemId != null && actionHistory1 != null) {
                        // Create a HistoryRVModel or use the data as needed
                        //HistoryRVModel historyModel = new HistoryRVModel(itemId, actionHistory);

                        HistoryRVModel actionHistory = itemSnapshot.getValue(HistoryRVModel.class);

                        //itemMap.put(actionHistory);

                        historyRVModelArrayList.add(actionHistory);

                        //historyRVModelArrayList.add(actionHistory);

                        historyRVAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);
                historyRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                historyRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);
                historyRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return rootView;
    } //end onCreate

    @Override
    public void onHistoryClick(int position) {

    }


}