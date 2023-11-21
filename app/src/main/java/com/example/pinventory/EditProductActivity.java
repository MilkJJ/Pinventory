package com.example.pinventory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private TextInputEditText productNameEdt, productDescEdt, productQtyEdt, expiryDateEdt; //Expiry Date
    private ImageView productImage;
    private Button updateProductBtn, deleteProductBtn, buttonChooseImage;
    private ProgressBar progressBar;
    private FirebaseDatabase firebaseDatabase;

    private DatabaseReference databaseReference;
    private DatabaseReference HistoryDBRef;

    private String productID;
    private ProductRVModel productRVModel;
    private Uri mImageUri;

    private StorageReference mStorageRef;
    private StorageTask mUploadTask;
    private DatabaseReference mDatabase;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private boolean usingQR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseDatabase = FirebaseDatabase.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        productImage = findViewById(R.id.idProductImage);
        productNameEdt = findViewById(R.id.idEdtProductName);
        productDescEdt = findViewById(R.id.idEdtProductDesc);
        productQtyEdt = findViewById(R.id.idEdtProductQty);
        expiryDateEdt = findViewById(R.id.et_date);

        //final Calender calender = Calender.getInstance();
        final int year = 2022; //calender.get(Calender.YEAR);
        final int month = 11; // calender.get(Calender.MONTH);
        final int day = 24; //calender.get(Calender.DAY_OF_MONTH);

        expiryDateEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EditProductActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month+1;
                        String date = day+"/"+month+"/"+year;
                        expiryDateEdt.setText(date);
                    }
                },year,month,day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                datePickerDialog.show();
            }
        });

        updateProductBtn = findViewById(R.id.idBtnUpdateProduct);
        deleteProductBtn = findViewById(R.id.idBtnDeleteProduct);
        progressBar = findViewById(R.id.progressBar);

        buttonChooseImage = findViewById(R.id.button_choose_image);
        Bundle extras = getIntent().getExtras();
        String qrText = null;
        productRVModel = getIntent().getParcelableExtra("product");
        if(productRVModel != null){
            productNameEdt.setText(productRVModel.getProductName());
            productDescEdt.setText(productRVModel.getProductDesc());
            productQtyEdt.setText(productRVModel.getProductQty());
            Picasso.with(this).load(productRVModel.getProductImg()).into(productImage);
            expiryDateEdt.setText(productRVModel.getExpiryDate());
            productID = productRVModel.getProductID();

            // Update the database reference path
            databaseReference = firebaseDatabase.getReference("Products").child(productID);
            mStorageRef = FirebaseStorage.getInstance().getReference("uploads").child(productID);
        }
        else {
            if (extras != null) {
                qrText = extras.getString("productQR");
                Log.d("test2323", qrText);
                String decryptedProductID = decryptQRCode(qrText);

                // Load the product information using the decrypted product ID
                loadProductInformation(decryptedProductID);
            }
        }


        HistoryDBRef = firebaseDatabase.getReference("History");

        buttonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        updateProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if(mUploadTask != null && mUploadTask.isInProgress()){
                    Toast.makeText(EditProductActivity.this, "Upload in Progress!", Toast.LENGTH_SHORT).show();
                } else {
                    updateProduct();
                    saveToHistory();
                }
            }
        });

        deleteProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                deleteProduct();
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    private void saveToHistory() {
        Date d = new Date();
        CharSequence s = DateFormat.format("d/MM/yyyy ", d.getTime());

        // Get the current user's UID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the "Users" node
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // Retrieve the current user's username
        usersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                // Check if the user exists
                if (userSnapshot.exists()) {
                    // Get the username from the "Users" node
                    String username = userSnapshot.child("userName").getValue(String.class);
                    if (username != null) {
                        // Use the username in the action history
                        String actionHistory = "'" + productNameEdt.getText().toString().trim() +
                                "' has been modified on " + s.toString() + " by " + username;

                        // Create a HistoryRVModel object
                        HistoryRVModel historyRVModel = new HistoryRVModel(actionHistory);

                        // Reference to the "History" node
                        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("History");

                        // Save the action history to the "History" node
                        historyRef.child(currentUserId)
                                .child(System.currentTimeMillis() + "")
                                .setValue(historyRVModel);
                    } else {
                        // Handle the case where the username is null
                        Toast.makeText(EditProductActivity.this, "Username not found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case where the user does not exist
                    Toast.makeText(EditProductActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProductActivity.this, "Error:" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteProduct(){
        databaseReference.removeValue();
        Toast.makeText(this, "Product Removed!", Toast.LENGTH_SHORT).show();

        Date d = new Date();
        CharSequence s = DateFormat.format("d/MM/yyyy ", d.getTime());

        // Get the current user's UID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the "Users" node
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // Retrieve the current user's username
        usersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                // Check if the user exists
                if (userSnapshot.exists()) {
                    // Get the username from the "Users" node
                    String username = userSnapshot.child("userName").getValue(String.class);

                    if (username != null) {
                        // Use the username in the action history
                        String actionHistory = "'" + productNameEdt.getText().toString().trim() +
                                "' has been removed on " + s.toString() + " by " + username;

                        // Create a HistoryRVModel object
                        HistoryRVModel historyRVModel = new HistoryRVModel(actionHistory);

                        // Reference to the "History" node
                        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("History");

                        // Save the action history to the "History" node
                        historyRef.child(currentUserId)
                                .child(System.currentTimeMillis() + "")
                                .setValue(historyRVModel);
                    } else {
                        // Handle the case where the username is null
                        Toast.makeText(EditProductActivity.this, "Username not found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case where the user does not exist
                    Toast.makeText(EditProductActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProductActivity.this, "Error:" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        checkUserRole(firebaseUser.getUid());
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.with(this).load(mImageUri).into(productImage);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void updateProduct(){
        if(mImageUri != null){
            StorageReference fileReference = mStorageRef.child(productID
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    String productName = productNameEdt.getText().toString().trim();
                                    String productDesc = productDescEdt.getText().toString().trim();
                                    String productQty = productQtyEdt.getText().toString().trim();
                                    String expiryDate = expiryDateEdt.getText().toString().trim();
                                    String productImg = downloadUrl.toString();
                                    //Date and Bar/QR

                                    Map<String, Object> map = new HashMap<>();

                                    map.put("productName", productName);
                                    map.put("productDesc", productDesc);
                                    map.put("productQty", productQty);
                                    map.put("expiryDate", expiryDate);
                                    map.put("productImg", productImg);
                                    map.put("productID", productID);

                                    //Toast.makeText(EditProductActivity.this, "Upload Successfully!", Toast.LENGTH_LONG).show();

                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            progressBar.setVisibility(View.GONE);
                                            databaseReference.updateChildren(map);
                                            Toast.makeText(EditProductActivity.this, "Product Updated!", Toast.LENGTH_SHORT).show();
                                            checkUserRole(firebaseUser.getUid());
                                            //startActivity(new Intent(EditProductActivity.this, MainActivity.class));
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(EditProductActivity.this, "Failed to update product info!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });
        } else if (!productRVModel.getProductImg().isEmpty()) {
            String productName = productNameEdt.getText().toString().trim();
            String productDesc = productDescEdt.getText().toString().trim();
            String productQty = productQtyEdt.getText().toString().trim();
            String expiryDate = expiryDateEdt.getText().toString().trim();

            //Date and Bar/QR

            Map<String, Object> map = new HashMap<>();

            map.put("productName", productName);
            map.put("productDesc", productDesc);
            map.put("productQty", productQty);
            map.put("expiryDate", expiryDate);
            map.put("productID", productID);

            //Toast.makeText(EditProductActivity.this, "Upload Successfully!", Toast.LENGTH_LONG).show();

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressBar.setVisibility(View.GONE);
                    databaseReference.updateChildren(map);
                    Toast.makeText(EditProductActivity.this, "Product Updated!", Toast.LENGTH_SHORT).show();
                    checkUserRole(firebaseUser.getUid());
                    //startActivity(new Intent(EditProductActivity.this, MainActivity.class));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(EditProductActivity.this, "Failed to update product info!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "No image file selected!", Toast.LENGTH_SHORT).show();
        }
    }


    private void checkUserRole(final String uid) {
        mDatabase.child(uid).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String role = dataSnapshot.getValue(String.class);
                if (role != null) {
                    if (role.equals("admin")) {
                        // User is an admin, go to AdminHomepage
                        Intent intent = new Intent(EditProductActivity.this, AdminHomepage.class);
                        intent.putExtra("adminId", uid); // Pass the uid as an extra
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        UserData.getInstance().setUserID(uid);
                        startActivity(intent);
                    } else {
                        // User is a regular user, go to MainActivity
                        Intent intent = new Intent(EditProductActivity.this, MainActivity.class);
                        intent.putExtra("userID",uid);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        UserData.getInstance().setUserID(uid);
                        startActivity(intent);
                    }
                    finish(); // Close the login activity
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if needed
                Toast.makeText(EditProductActivity.this, "Failed to check user role.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String decryptQRCode(String encryptedQRCode) {

        String encryptedProductID = encryptedQRCode;

        // Extract the original product ID (remove the 3 random characters)
        int middleIndex = encryptedProductID.length() / 2;
        StringBuilder decryptedProductID = new StringBuilder();
        decryptedProductID.append(encryptedProductID.charAt(middleIndex - 1));
        decryptedProductID.append(encryptedProductID.charAt(middleIndex));
        decryptedProductID.append(encryptedProductID.charAt(middleIndex + 1));

        return decryptedProductID.toString();
    }
    private void loadProductInformation(String productID) {
        DatabaseReference productRef = firebaseDatabase.getInstance().getReference("Products")
                .child(productID);

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the createdBy field from the database
                    String createdByUserID = dataSnapshot.child("createdBy").getValue(String.class);

                    // Check the user's role
                    checkUserRole(createdByUserID, dataSnapshot);
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditProductActivity.this);
                    builder.setTitle("Scan QR Code");
                    builder.setMessage("Item Not Found.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent back = new Intent(EditProductActivity.this, MainActivity.class);
                            startActivity(back);
                            dialogInterface.dismiss();
                        }
                    }).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if needed
            }
        });
    }

    private void checkUserRole(String createdByUserID, DataSnapshot productSnapshot) {
        DatabaseReference userRoleRef = firebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("role");

        userRoleRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot roleSnapshot) {
                String userRole = roleSnapshot.getValue(String.class);

                // Check the user's role
                if (userRole != null) {
                    if (userRole.equals("admin")) {
                        // Admin has permission, load the product information
                        loadProductData(productSnapshot);
                    } else {
                        // Check if the logged-in user is the creator of the product
                        if (createdByUserID != null && createdByUserID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            // User has permission, load the product information
                            loadProductData(productSnapshot);
                        } else {
                            // User doesn't have permission, show an alert or handle as needed
                            showPermissionDeniedDialog();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if needed
            }
        });
    }

    private void loadProductData(DataSnapshot dataSnapshot) {
        ProductRVModel productRVModel = dataSnapshot.getValue(ProductRVModel.class);
        if (productRVModel != null) {
            productNameEdt.setText(productRVModel.getProductName());
            productDescEdt.setText(productRVModel.getProductDesc());
            productQtyEdt.setText(productRVModel.getProductQty());
            Picasso.with(EditProductActivity.this).load(productRVModel.getProductImg()).into(productImage);
            expiryDateEdt.setText(productRVModel.getExpiryDate());
        }
    }

    private void showPermissionDeniedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProductActivity.this);
        builder.setTitle("Permission Denied");
        builder.setMessage("You don't have permission to edit this product.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent back = new Intent(EditProductActivity.this, MainActivity.class);
                startActivity(back);
                dialogInterface.dismiss();
            }
        }).show();
    }

}