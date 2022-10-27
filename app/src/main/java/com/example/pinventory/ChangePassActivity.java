package com.example.pinventory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassActivity extends AppCompatActivity {

    private EditText currentPass, newPass, confirmPass;
    private Button changePass;
    String passwordData;
    ProgressDialog dialog;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        currentPass = (EditText) findViewById(R.id.etCurrentPass);
        newPass = (EditText) findViewById(R.id.etNewPass);
        confirmPass = (EditText) findViewById(R.id.etConfirmPass);
        changePass = findViewById(R.id.btnConfirmChangePass);

        dialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPasswordChange();
            }

            public void onPasswordChange() {
                user = firebaseAuth.getCurrentUser();
                final String email = user.getEmail();
                if(currentPass.getText().toString().isEmpty() == false) {
                    AuthCredential credential = EmailAuthProvider.getCredential(email, currentPass.getText().toString());

                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            dialog.setMessage("Changing password, Please wait");
                                if (newPass.getText().toString().equals(confirmPass.getText().toString()) && newPass.getText().toString().isEmpty() == false) {
                                    dialog.show();
                                    user.updatePassword(confirmPass.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        dialog.dismiss();
                                                        Toast.makeText(ChangePassActivity.this,
                                                                "Password successfully changed!", Toast.LENGTH_LONG).show();
                                                        firebaseAuth.signOut();
                                                        finish();
                                                        Intent i = new Intent(ChangePassActivity.this, LoginActivity.class);
                                                        startActivity(i);
                                                    } else {
                                                        dialog.dismiss();
                                                        Toast.makeText(ChangePassActivity.this,
                                                                "Password must be at least 6 characters!", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                } else if (newPass.getText().toString().isEmpty() && confirmPass.getText().toString().isEmpty()) {
                                    dialog.dismiss();
                                    Toast.makeText(ChangePassActivity.this,
                                            "Please Enter your New Password!", Toast.LENGTH_LONG).show();
                                } else if (newPass.getText().toString().isEmpty()) {
                                    dialog.dismiss();
                                    Toast.makeText(ChangePassActivity.this,
                                            "Please Enter your New Password!", Toast.LENGTH_LONG).show();
                                } else if (confirmPass.getText().toString().isEmpty()) {
                                    dialog.dismiss();
                                    Toast.makeText(ChangePassActivity.this,
                                            "Please Confirm your New Password!", Toast.LENGTH_LONG).show();
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(ChangePassActivity.this,
                                            "Password does not matched!", Toast.LENGTH_LONG).show();
                                }
                        } else {
                            Toast.makeText(ChangePassActivity.this,
                                    "Current Password Incorrect!", Toast.LENGTH_LONG).show();
                        } //reAuthenticate onComplete
                    }
                });

                } else {
                    Toast.makeText(ChangePassActivity.this,
                            "Please Enter your Current Password!", Toast.LENGTH_LONG).show();
                }
            }

        });

    } //onCreate
}