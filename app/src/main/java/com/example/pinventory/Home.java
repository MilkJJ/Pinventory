package com.example.pinventory;

import android.app.Application;
import android.content.Intent;
import android.view.View;
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
    private DatabaseReference mDatabase;

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
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        mDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    if (user.getRole().equals("admin")) {
                        // User is an admin, go to AdminHomepage
                        Intent intent = new Intent(Home.this, AdminHomepage.class);
                        intent.putExtra("adminId", uid); // Pass the uid as an extra
                        UserData.getInstance().setUserID(uid);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        //finish(); // Close the login activity
                    } else {
                        // User is a regular user, check the status
                        if (user.isStatus()) {
                            // User status is true (enabled), go to MainActivity
                            Intent intent = new Intent(Home.this, MainActivity.class);
                            intent.putExtra("userID", uid);
                            UserData.getInstance().setUserID(uid);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            //finish(); // Close the login activity
                        } else {
                            // User status is false (disabled), show a message
                            Toast.makeText(Home.this, "Your account is disabled. Please contact the administrator.", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut(); // Sign out the user
                        }
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
