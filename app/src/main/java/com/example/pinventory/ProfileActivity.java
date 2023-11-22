package com.example.pinventory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    private Button logout, btnHomepage;
    private TextView TVChangePass;
    private DatabaseReference mDatabase;
    private DatabaseReference userDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        logout = (Button) findViewById(R.id.signOut);
        btnHomepage = (Button) findViewById(R.id.btnHomepage);
        TVChangePass = findViewById(R.id.TVChangePass);

        TVChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ChangePassActivity.class));
            }
        });

        btnHomepage.setOnClickListener(new View.OnClickListener() {

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            @Override
            public void onClick(View v) {
                checkUserRole(firebaseUser.getUid());
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        final TextView greetingTextView = (TextView) findViewById(R.id.greeting);
        final TextView userNameTextView = (TextView) findViewById(R.id.userName);
        final TextView emailTextView = (TextView) findViewById(R.id.emailAddress);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    String userName = userProfile.userName;
                    String email = userProfile.email;

                    greetingTextView.setText("Welcome, " + userName + "!");
                    userNameTextView.setText(userName);
                    emailTextView.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "ERROR happened!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure you want to logout?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ProfileActivity.super.onBackPressed();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
                        Intent intent = new Intent(ProfileActivity.this, AdminHomepage.class);
                        intent.putExtra("adminId", uid); // Pass the uid as an extra
                        UserData.getInstance().setUserID(uid);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        //finish(); // Close the login activity
                    } else {
                        // User is a regular user, check the status
                        if (user.isStatus()) {
                            // User status is true (enabled), go to MainActivity
                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                            intent.putExtra("userID", uid);
                            UserData.getInstance().setUserID(uid);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            //finish(); // Close the login activity
                        } else {
                            // User status is false (disabled), show a message
                            Toast.makeText(ProfileActivity.this, "Your account is disabled. Please contact the administrator.", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut(); // Sign out the user
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if needed
                Toast.makeText(ProfileActivity.this, "Failed to check user role.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}