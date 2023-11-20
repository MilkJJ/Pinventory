package com.example.pinventory;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// This code saves the session and log user back in if user did not log out
public class Home extends Application {

    private DatabaseReference userDatabase;
    @Override
    public void onCreate() {
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        super.onCreate();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            checkUserRole(firebaseUser.getUid());
//            Intent intent = new Intent(this,MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
        }
    }

    private void checkUserRole(final String uid) {
        userDatabase.child(uid).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String role = dataSnapshot.getValue(String.class);
                if (role != null) {
                    if (role.equals("admin")) {
                        // User is an admin, go to AdminHomepage
                        Intent intent = new Intent(Home.this, AdminHomepage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        // User is a regular user, go to MainActivity
                        Intent intent = new Intent(Home.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if needed
                Toast.makeText(Home.this, "Failed to check user role.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
