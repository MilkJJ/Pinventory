package com.example.pinventory;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
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

    private DatabaseReference databaseReference, testingRef;
    private DatabaseReference HistoryDBRef;

    private String productID;
    private ProductRVModel productRVModel;
    private Uri mImageUri;

    private StorageReference mStorageRef;
    private StorageTask mUploadTask;

    private boolean usingQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseDatabase = FirebaseDatabase.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        productImage = findViewById(R.id.idProductImage);
        productNameEdt = findViewById(R.id.idEdtProductName);
        productDescEdt = findViewById(R.id.idEdtProductDesc);
        productQtyEdt = findViewById(R.id.idEdtProductQty);
        expiryDateEdt = findViewById(R.id.et_date);

        //final Calender calender = Calender.getInstance();
        final int year = 2023; //calender.get(Calender.YEAR);
        final int month = 1; // calender.get(Calender.MONTH);
        final int day = 1; //calender.get(Calender.DAY_OF_MONTH);

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

            databaseReference = firebaseDatabase.getReference("Products")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()+productID)
                    .child(productID);
        }
        else{

            if (extras != null) {
                qrText = extras.getString("productQR");
                Log.d("test2323",qrText);

            }
           String qr1 = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String qr2 = qrText;
            String tempID = qr2.substring(qr1.length(),qr2.length());
            System.out.println(tempID);
            //new changes
            testingRef =firebaseDatabase.getInstance().getReference("Products")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(qrText);

            databaseReference =firebaseDatabase.getInstance().getReference("Products")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(qrText)
                    .child(tempID);

            testingRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    System.out.println("KKKKK2");
                    for(DataSnapshot ds: snapshot.getChildren() ){
                        for(DataSnapshot productid : ds.getChildren()) {
                            System.out.println(ds+"\n KKKKK4"+productid);
                           productRVModel = ds.getValue(ProductRVModel.class);
                            System.out.println(productRVModel.getProductName());
                            productNameEdt.setText(productRVModel.getProductName());
                            productDescEdt.setText(productRVModel.getProductDesc());
                            productQtyEdt.setText(productRVModel.getProductQty());
                            Picasso.with(EditProductActivity.this).load(productRVModel.getProductImg()).into(productImage);
                            expiryDateEdt.setText(productRVModel.getExpiryDate());
                            productID = productRVModel.getProductID();
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
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
                    uploadFile();
                    saveToHistory();
                }
            }
        });

        deleteProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct();

                Date d = new Date();
                CharSequence s  = DateFormat.format("d/MM/yyyy ", d.getTime());

                HistoryDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String actionHistory = "'" + productNameEdt.getText().toString().trim() + "' has been deleted on " + s.toString();
                        HistoryRVModel historyRVModel = new HistoryRVModel(actionHistory);

                        HistoryDBRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(System.currentTimeMillis()+ "")
                                .setValue(historyRVModel);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(EditProductActivity.this, "Error:" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }


    private void saveToHistory() {
        HistoryDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Date d = new Date();
                CharSequence s  = DateFormat.format("d/MM/yyyy ", d.getTime());

                String actionHistory = "'" + productNameEdt.getText().toString().trim() + "' has been modified on " + s.toString();
                HistoryRVModel historyRVModel = new HistoryRVModel(actionHistory);

                HistoryDBRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(System.currentTimeMillis()+ "")
                        .setValue(historyRVModel);
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
        Intent intent = new Intent(EditProductActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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

    private void uploadFile(){
        if(mImageUri != null){
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
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

                                    Toast.makeText(EditProductActivity.this, "Upload Successfully!", Toast.LENGTH_LONG).show();

                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            progressBar.setVisibility(View.GONE);
                                            databaseReference.updateChildren(map);
                                            Toast.makeText(EditProductActivity.this, "Product Updated!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(EditProductActivity.this, MainActivity.class));
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

            Toast.makeText(EditProductActivity.this, "Upload Successfully!", Toast.LENGTH_LONG).show();

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressBar.setVisibility(View.GONE);
                    databaseReference.updateChildren(map);
                    Toast.makeText(EditProductActivity.this, "Product Updated!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditProductActivity.this, MainActivity.class));

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

}