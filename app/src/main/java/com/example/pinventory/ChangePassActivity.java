package com.example.pinventory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pinventory.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassActivity extends AppCompatActivity {

    private EditText currentPass, newPass, confirmPass;
    private Button changePass;
    ProgressDialog dialog;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentPass = findViewById(R.id.etCurrentPass);
        newPass = findViewById(R.id.etNewPass);
        confirmPass = findViewById(R.id.etConfirmPass);
        changePass = findViewById(R.id.btnConfirmChangePass);

        dialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPasswordChange();
            }
        });
    }

    private void onPasswordChange() {
        user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String currentPassword = currentPass.getText().toString();
            String newPassword = newPass.getText().toString();
            String confirmPassword = confirmPass.getText().toString();

            if (TextUtils.isEmpty(currentPassword)) {
                showToast("Please Enter your Current Password!");
                return;
            }

            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        performPasswordChange(newPassword, confirmPassword);
                    } else {
                        showToast("Current Password Incorrect!");
                    }
                }
            });
        } else {
            showToast("User not authenticated. Please log in again.");
        }
    }

    private void performPasswordChange(String newPassword, String confirmPassword) {
        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            showToast("Please Enter your New Password and Confirm Password!");
        } else if (newPassword.length() < 8) {
            showToast("Password must be at least 8 characters!");
        } else if (!containsUppercase(newPassword)) {
            showToast("Password must contain at least one uppercase letter!");
        } else if (!containsSpecialSymbol(newPassword)) {
            showToast("Password must contain at least one special symbol!");
        } else if (!containsDigit(newPassword)) {
            showToast("Password must contain at least one digit!");
        } else if (!TextUtils.equals(newPassword, confirmPassword)) {
            showToast("Passwords do not match!");
        } else {
            updatePassword(newPassword);
        }
    }

    private boolean containsUppercase(String password) {
        return password.matches(".*[A-Z].*");
    }

    private boolean containsSpecialSymbol(String password) {
        return password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    }

    private boolean containsDigit(String password) {
        return password.matches(".*\\d.*");
    }

    private void updatePassword(String newPassword) {
        dialog.setMessage("Changing password, Please wait");
        dialog.show();

        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
                if (task.isSuccessful()) {
                    showToast("Password successfully changed!");
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(ChangePassActivity.this, LoginActivity.class));
                } else {
                    showToast("Failed to change password. Please try again.");
                }
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(ChangePassActivity.this, message, Toast.LENGTH_LONG).show();
    }
}
