package com.example.pinventory;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
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

public class AddProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView idProductImage;
    private TextInputEditText productNameEdt, productDescEdt, productQtyEdt, etDate; //Expiry Date
    DatePickerDialog.OnDateSetListener mSetListener;

    private Button addProductBtn, buttonChooseImage;
    private ProgressBar progressBar;
    private FirebaseDatabase firebaseDatabase;
    private String productID;
    private FirebaseUser user;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference HistoryDBRef;

    private DatabaseReference mDatabase;
    private StorageTask mUploadTask;
    boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productNameEdt = findViewById(R.id.idEdtProductName);
        productDescEdt = findViewById(R.id.idEdtProductDesc);
        productQtyEdt = findViewById(R.id.idEdtProductQty);
        etDate = findViewById(R.id.et_date);

        Date d = new Date();
        CharSequence s = DateFormat.format("d/MM/yyyy ", d.getTime());

        //final Calender calender = Calender.getInstance();
        final int year = 2022; //calender.get(Calender.YEAR);
        final int month = 11; // calender.get(Calender.MONTH);
        final int day = 24; //calender.get(Calender.DAY_OF_MONTH);

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddProductActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String date = day + "/" + month + "/" + year;
                        etDate.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        idProductImage = findViewById(R.id.idProductImage);
        buttonChooseImage = findViewById(R.id.button_choose_image);

        addProductBtn = findViewById(R.id.idBtnAddProduct); //upload
        progressBar = findViewById(R.id.progressBar);
        //Expiry Date

        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = firebaseDatabase.getReference("Products");
        HistoryDBRef = firebaseDatabase.getReference("History");

        buttonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mUploadTask != null && mUploadTask.isInProgress()) {

                    Toast.makeText(AddProductActivity.this, "Upload in Progress!", Toast.LENGTH_SHORT).show();
                }
                if (productNameEdt.getText().length() < 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
                    builder.setTitle("Insert Fail ");
                    builder.setMessage("Name cannot be empty");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                    check = true;
                }
                if (productQtyEdt.getText().length() < 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
                    builder.setTitle("Insert Fail ");
                    builder.setMessage("Quantity cannot be empty");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                    check = true;
                }
                if (productDescEdt.getText().length() < 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
                    builder.setTitle("Insert Fail ");
                    builder.setMessage("Description cannot be empty");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                    check = true;
                }
                if (etDate.getText().length() < 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
                    builder.setTitle("Insert Fail ");
                    builder.setMessage("Date cannot be empty");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                    check = true;
                }

                if (!check) {
                    progressBar.setVisibility(View.VISIBLE);
                    uploadFile();
                    saveToHistory();
                }
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
                                "' has been added on " + s.toString() + " by " + username;

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
                        Toast.makeText(AddProductActivity.this, "Username not found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case where the user does not exist
                    Toast.makeText(AddProductActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddProductActivity.this, "Error:" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.with(this).load(mImageUri).into(idProductImage);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

            if (currentUser != null) {
                String createdBy = currentUser.getUid(); // Get the current user's ID

                String productKey = mDatabaseRef.push().getKey();
                productID = productKey;

                StorageReference fileReference = mStorageRef.child(productKey + "/" + productKey + "." + getFileExtension(mImageUri));

                mUploadTask = fileReference.putFile(mImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Uri downloadUrl = uri;
                                        Toast.makeText(AddProductActivity.this, "Product Added Successfully!", Toast.LENGTH_LONG).show();

                                        String productName = productNameEdt.getText().toString().trim();
                                        String productDesc = productDescEdt.getText().toString().trim();
                                        String productQty = productQtyEdt.getText().toString().trim();
                                        String expiryDate = etDate.getText().toString().trim();
                                        String productImg = downloadUrl.toString();

                                        DatabaseReference newProductRef = mDatabaseRef.push();
                                        String productKey = newProductRef.getKey();

                                        // Create a ProductRVModel object
                                        ProductRVModel productRVModel = new ProductRVModel(createdBy, productName, productDesc, productQty, expiryDate, productImg, productKey);

                                        newProductRef.setValue(productRVModel)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        DatabaseReference productIDRef = newProductRef.child("productID");
                                                        productIDRef.setValue(productKey)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(AddProductActivity.this, "Product Added!", Toast.LENGTH_SHORT).show();
                                                                        checkUserRole(currentUser.getUid());
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(AddProductActivity.this, "Failed to update productID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(AddProductActivity.this, "Failed to add product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                progressBar.setProgress((int) progress);
                            }
                        });
            } else {
                Toast.makeText(AddProductActivity.this, "User not logged in.", Toast.LENGTH_SHORT).show();
            }
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "No image file selected!", Toast.LENGTH_SHORT).show();
        }
    }


    ;

    private void checkUserRole(final String uid) {
        mDatabase.child(uid).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String role = dataSnapshot.getValue(String.class);
                if (role != null) {
                    if (role.equals("admin")) {
                        // User is an admin, go to AdminHomepage
                        Intent intent = new Intent(AddProductActivity.this, AdminHomepage.class);
                        intent.putExtra("adminId", uid); // Pass the uid as an extra
                        UserData.getInstance().setUserID(uid);
                        startActivity(intent);
                    } else {
                        // User is a regular user, go to MainActivity
                        Intent intent = new Intent(AddProductActivity.this, MainActivity.class);
                        intent.putExtra("userID",uid);
                        UserData.getInstance().setUserID(uid);
                        startActivity(intent);
                    }
                    finish(); // Close the login activity
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if needed
                Toast.makeText(AddProductActivity.this, "Failed to check user role.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}