package com.example.pinventory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private TextView banner, registerUser, login_direct;
    private EditText editTextUserName, editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        banner = (TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);

        registerUser = (Button) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        login_direct = (TextView) findViewById(R.id.login_direct);
        login_direct.setOnClickListener(this);

        editTextUserName = (EditText) findViewById(R.id.userName);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Show terms and conditions popup when the signup page is opened
        showTermsAndConditionsPopup();
    }

    // CODE START FOR TERMS AND CONDITIONS DIALOG POPUP
    private void showTermsAndConditionsPopup() {
        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);

        // The title and message for the dialog
        builder.setTitle("Terms and Conditions");

        // Enable scrolling for the message
        TextView textView = dialogView.findViewById(R.id.textView4);

        // FOR THE BODY TEXT
        textView.setText("Welcome to Pinventory!\n" +
                "\n" +
                "PLEASE read through the TERMS AND CONDITIONS and PRIVACY POLICY before signing up\n" +
                "\n" +
                "By accessing or using Pinventory, you agree to comply with and be bound by the following terms and conditions of use. If you do not agree with these terms, please do not use the app.\n" +
                "\n" +
                "1. Acceptance of Terms\n" +
                "\n" +
                "You acknowledge that you have read, understood, and agree to be bound by these terms and conditions.\n" +
                "\n" +
                "2. User Accounts\n" +
                "\n" +
                "You are responsible for maintaining the confidentiality of your account credentials.\n" +
                "You agree to provide accurate and complete information when creating your account.\n" +
                "\n" +
                "3. Privacy Policy\n" +
                "\n" +
                "Please review our Privacy Policy to understand how we collect, use, and protect your personal information.\n" +
                "\n" +
                "4. User Conduct\n" +
                "\n" +
                "You agree not to:\n" +
                "\n" +
                "i.   Violate any applicable laws or regulations.\n" +
                "ii.  Infringe on the rights of others.\n" +
                "iii. Use the app for any unauthorized or illegal purpose.\n" +
                "\n" +
                "5. Intellectual Property\n" +
                "\n" +
                "All content and materials on Pinventory are the property of the company.\n" +
                "You may not use, reproduce, or distribute any content without permission.\n" +
                "\n" +
                "6. Termination of Service\n" +
                "\n" +
                "We reserve the right to terminate or suspend your account and access to the app for violations of these terms.\n" +
                "\n" +
                "7. Dispute Resolution\n" +
                "\n" +
                "Any disputes arising out of or related to these terms will be resolved through in an appropriate manner.\n" +
                "\n" +
                "8. Disclaimer of Warranties\n" +
                "\n" +
                "Pinventory is provided \"as is\" without any warranties. We do not guarantee the accuracy, completeness, or reliability of the app.\n" +
                "\n" +
                "9. Limitation of Liability\n" +
                "\n" +
                "Pinventory is not liable for any indirect, incidental, or consequential damages arising from the use of Pinventory.\n" +
                "\n" +
                "10. Changes to Terms and Conditions\n" +
                "\n" +
                "We reserve the right to update these terms and conditions at any time. Changes will be effective upon posting.\n" +
                "\n" +
                "11. Governing Law\n" +
                "\n" +
                "These terms and conditions are governed by the laws.");
        // FOR THE BODY TEXT

        // Enable scrolling for the TextView
        textView.setMovementMethod(new ScrollingMovementMethod());

        // Add the custom layout to the dialog
        builder.setView(dialogView);

        // Add buttons to the dialog
        builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked Agree
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked Cancel
                dialog.dismiss();
                Intent intent = new Intent(RegisterUser.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Add a third button for "Privacy Policy"
        builder.setNeutralButton("Privacy Policy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showPrivacyPolicyPopup();
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    // CODE END FOR TERMS AND CONDITIONS DIALOG POPUP

    // CODE START FOR PRIVACY POLICY DIALOG POPUP
    private void showPrivacyPolicyPopup() {
        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);

        // The title and message for the dialog
        builder.setTitle("Privacy Policy");

        // Enable scrolling for the message
        TextView textView = dialogView.findViewById(R.id.textView4);

        // FOR THE BODY TEXT
        textView.setText("Privacy Policy for Pinventory\n" +
                "\n" +
                "Last Updated: 21/11/2023\n" +
                "\n" +
                "Welcome to Pinventory. This Privacy Policy is designed to help you understand how we collect, use, share, and protect your personal information.\n" +
                "\n" +
                "1. Information We Collect:\n" +
                "\n" +
                "We may collect the following types of personal information:\n" +
                "\n" +
                "i.   Email Address.\n" +
                "ii.  Password.\n" +
                "iii. User Name.\n" +
                "\n" +
                "2. How We Use Your Information:\n" +
                "\n" +
                "We use the collected information for the following purposes:\n" +
                "\n" +
                "To personalize your experience.\n" +
                "To analyze app usage and optimize performance.\n" +
                "\n" +
                "3. Legal Basis for Processing:\n" +
                "\n" +
                "We process your personal information based on your consent and our legitimate interests in providing and improving our app.\n" +
                "\n" +
                "4. Data Sharing:\n" +
                "\n" +
                "We may share your information with third-party service providers for app-related services. We do not sell your personal information to third parties.\n" +
                "\n" +
                "5. Security Measures:\n" +
                "\n" +
                "We take reasonable measures to protect your personal information from unauthorized access or disclosure, such as data encryption.\n" +
                "\n" +
                "6. User Rights:\n" +
                "\n" +
                "You have the right to access, correct, or delete your personal information. Contact us at contact@email.com to exercise your rights.\n" +
                "\n" +
                "7. Retention Period:\n" +
                "\n" +
                "We retain your personal information for as long as necessary for the purposes outlined in this policy or as required by law.\n" +
                "\n" +
                "8. Changes to the Privacy Policy:\n" +
                "\n" +
                "We may update this Privacy Policy. Changes will be effective upon posting. Check this page for the latest version.\n" +
                "\n" +
                "10. Contact Information:\n" +
                "\n" +
                "For privacy-related inquiries or concerns, contact us at contact@email.com.\n" +
                "\n" +
                "Thank you for using Pinventory!");
        // FOR THE BODY TEXT

        // Enable scrolling for the TextView
        textView.setMovementMethod(new ScrollingMovementMethod());

        // Add the custom layout to the dialog
        builder.setView(dialogView);

        // Add buttons to the dialog
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked Agree
                dialog.dismiss();
                showTermsAndConditionsPopup();
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    // CODE END FOR PRIVACY POLICY DIALOG POPUP

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.banner:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.registerUser:
                registerUser();
                break;
            case R.id.login_direct:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }

    private void registerUser(){
        String userName = editTextUserName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if(userName.isEmpty()){
            editTextUserName.setError("Username is required!");
            editTextUserName.requestFocus();
            return;
        }
        if(email.isEmpty()){
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please provide a valid email!");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }
        if(password.length() < 8){
            editTextPassword.setError("Minimum password length is 8 characters!");
            editTextPassword.requestFocus();
            return;
        }
        // New checks for uppercase letter and symbol
        if (!containsUppercase(password)) {
            editTextPassword.setError("Password must contain at least one uppercase letter!");
            editTextPassword.requestFocus();
            return;
        }
        if (!containsSymbol(password)) {
            editTextPassword.setError("Password must contain at least one symbol!");
            editTextPassword.requestFocus();
            return;
        }
        if (!containsNumber(password)) {
            editTextPassword.setError("Password must contain at least one digit!");
            editTextPassword.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            User user = new User(userName, email,"user", true);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                Toast.makeText(RegisterUser.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                                startActivity(new Intent(RegisterUser.this, LoginActivity.class));

                                                //Redirect to Login Layout
                                            } else{
                                                Toast.makeText(RegisterUser.this, "Failed to register! Please try again!", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        } else{
                            Toast.makeText(RegisterUser.this, "Failed to register! Please try again!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
    private boolean containsUppercase(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    // Helper method to check if the password contains at least one symbol
    private boolean containsSymbol(String password) {
        String symbols = "!@#$%^&*()-_=+[]{}|;:'\",.<>?/";
        for (char c : password.toCharArray()) {
            if (symbols.contains(String.valueOf(c))) {
                return true;
            }
        }
        return false;
    }
    private boolean containsNumber(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }
}

