package com.example.pinventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminHomepage extends AppCompatActivity {
    private Toolbar toolbar;
    private String adminId;
    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_homepage);
        AdminItemListFragment adminItemListFragment = new AdminItemListFragment();
        loadFragment(adminItemListFragment);
        mAuth = FirebaseAuth.getInstance();
// Initialize the bottomNavigationView and set up its listener
        bottomNavigationView = findViewById(R.id.bottomNavMain_admin);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.itemList:
                        // Handle the home tab
                        AdminItemListFragment adminItemListFragment = new AdminItemListFragment();
                        Bundle argsH = new Bundle();
                        argsH.putString("adminID", adminId);
                        adminItemListFragment.setArguments(argsH);
                        loadFragment(adminItemListFragment);
                        return true;
                    case R.id.userList:
                        AdminUserListFragment adminUserListFragment = new AdminUserListFragment();
                        Bundle args = new Bundle();
                        args.putString("adminID", adminId); // Pass the UID to the fragment
                        adminUserListFragment.setArguments(args); // Set the arguments
                        loadFragment(adminUserListFragment);
                        return true;
                    case R.id.historyList:
                        AdminStatisticFragment adminStatisticFragment = new AdminStatisticFragment();
                        Bundle argsHis = new Bundle();
                        argsHis.putString("adminID", adminId);
                        adminStatisticFragment.setArguments(argsHis);
                        loadFragment(adminStatisticFragment);
                        return true;
                }
                return false;
            }
        });

    }
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_admin, fragment);
        transaction.addToBackStack(null); // Optional, to add fragments to the back stack
        transaction.commit();
    }
    @Override
    public void onBackPressed() {
        // Check if the current fragment is the HomeFragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_admin);

        if (currentFragment instanceof AdminItemListFragment) {
            // Handle the back button press in the HomeFragment as needed
            // For example, show a dialog or take some other action
        }
        else {
            super.onBackPressed(); // Allow normal back navigation for other fragments
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.tb_logout:
                logout();
                return true;
            case R.id.mmProfile:
                Intent k = new Intent(AdminHomepage.this, ProfileActivity.class);
                startActivity(k);
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        mAuth.signOut();
        // After signing out, you can redirect the user to the login screen or perform any other actions you need.
        // For example, you can start a LoginActivity:
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        // Make sure to finish the current activity to prevent the user from navigating back to it.
        finish();
    }
}