package com.example.pinventory;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class AddProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView idProductImage;
    private TextInputEditText productNameEdt, productDescEdt, productQtyEdt, etDate; //Expiry Date
    DatePickerDialog.OnDateSetListener mSetListener;

    private Button addProductBtn, buttonChooseImage;
    private ProgressBar progressBar;
    private FirebaseDatabase firebaseDatabase;
    private String productID;

    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference HistoryDBRef;

    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        productNameEdt = findViewById(R.id.idEdtProductName);
        productDescEdt = findViewById(R.id.idEdtProductDesc);
        productQtyEdt = findViewById(R.id.idEdtProductQty);
        etDate = findViewById(R.id.et_date);

        //final Calender calender = Calender.getInstance();
        final int year = 2002; //calender.get(Calender.YEAR);
        final int month = 1; // calender.get(Calender.MONTH);
        final int day = 1; //calender.get(Calender.DAY_OF_MONTH);

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddProductActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month+1;
                        String date = day+"/"+month+"/"+year;
                        etDate.setText(date);
                    }
                },year,month,day);
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
                progressBar.setVisibility(View.VISIBLE);
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(AddProductActivity.this, "Upload in Progress!", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                    saveToHistory();
                }
            }
        });
    }

    private void saveToHistory() {
        HistoryDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HistoryDBRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(System.currentTimeMillis()+ "")
                        .setValue(productNameEdt.getText().toString().trim() + " has been added");
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
            StorageReference fileReference = mStorageRef.child(productID
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    Toast.makeText(AddProductActivity.this, "Upload Successfully!", Toast.LENGTH_LONG).show();

                                    String productName = productNameEdt.getText().toString().trim();
                                    String productDesc = productDescEdt.getText().toString().trim();
                                    String productQty = productQtyEdt.getText().toString().trim();
                                    String expiryDate = etDate.getText().toString().trim();
                                    String productImg = downloadUrl.toString(); //productImgEdt.getText().toString().trim();
                                    productID = productName;
                                    ProductRVModel productRVModel = new ProductRVModel(productName, productDesc, productQty, expiryDate, productImg, productID);

                                    mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            progressBar.setVisibility(View.GONE);
                                            mDatabaseRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child(productID)
                                                    .setValue(productRVModel);

                                            Toast.makeText(AddProductActivity.this, "Product Added!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(AddProductActivity.this, MainActivity.class));
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(AddProductActivity.this, "Error:" + error.toString(), Toast.LENGTH_SHORT).show();
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
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "No image file selected!", Toast.LENGTH_SHORT).show();
        }
    }

    ;

}