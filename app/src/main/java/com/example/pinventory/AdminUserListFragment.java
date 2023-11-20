package com.example.pinventory;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
public class AdminUserListFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminUserListAdapter userAdapter;
    private List<User> userList;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersReference;

    public AdminUserListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin_user_list, container, false);

        recyclerView = rootView.findViewById(R.id.RV_user);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        userList = new ArrayList<>();
        userAdapter = new AdminUserListAdapter(userList);
        recyclerView.setAdapter(userAdapter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        usersReference = firebaseDatabase.getReference("Users");

        // Fetch data from Firebase
        fetchUserData();

        return rootView;
    }

    private void fetchUserData() {
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String uid = userSnapshot.getKey(); // Get UID from outer layer

                    User user = userSnapshot.getValue(User.class);

                    // Ensure that the UID field is set
                    if (user != null) {
                        user.setUid(uid);
                        userList.add(user);
                    }
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }
}