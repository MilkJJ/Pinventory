package com.example.pinventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        logout = (Button) findViewById(R.id.signOut);
        btnHomepage = (Button) findViewById(R.id.btnHomepage);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        btnHomepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(account != null) {
                    SignOut();
                } else {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                }
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
                } else {
                    if(account != null){
                        String userName = account.getDisplayName();
                        String email = account.getEmail();

                        final TextView greetingTextView = (TextView) findViewById(R.id.greeting);
                        final TextView userNameTextView = (TextView) findViewById(R.id.userName);
                        final TextView emailTextView = (TextView) findViewById(R.id.emailAddress);

                        greetingTextView.setText("Welcome, " + userName + "!");
                        userNameTextView.setText(userName);
                        emailTextView.setText(email);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "ERROR happened!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void SignOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }
}