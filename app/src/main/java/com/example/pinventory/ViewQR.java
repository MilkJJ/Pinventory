package com.example.pinventory;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Random;

public class ViewQR extends AppCompatActivity {
    ImageView ivOutput;
    TextView txtName;
    private ProductRVModel productRVModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_qr);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivOutput = findViewById(R.id.iv_output);
        txtName = findViewById(R.id.ProductQRName);

        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Bundle resultIntent = getIntent().getExtras();
        String productIDQR = null;
        if (resultIntent != null) {
            productIDQR = resultIntent.getString("makeQR");

        }
        productRVModel = getIntent().getParcelableExtra("productQR");

        // Add encryption to the product ID
        String encryptedProductID = encryptProductID(productIDQR);

        txtName.setText("Name: " + productRVModel.getProductName() + "\nDescription: "
                + productRVModel.getProductDesc() + "\nQuantity: " +
                productRVModel.getProductQty() + "\nExpiryDate: " +
                productRVModel.getExpiryDate());

        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(encryptedProductID, BarcodeFormat.QR_CODE, 350, 350);

            BarcodeEncoder encoder = new BarcodeEncoder();

            Bitmap bitmap = encoder.createBitmap(matrix);

            ivOutput.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private String encryptProductID(String productID) {
        // Insert 3 random characters in the middle of the product ID
        Random random = new Random();
        int middleIndex = productID.length() / 2;
        StringBuilder productIDBuilder = new StringBuilder(productID);
        for (int i = 0; i < 3; i++) {
            char randomChar = (char) (random.nextInt(26) + 'a'); // Random lowercase letter
            productIDBuilder.insert(middleIndex, randomChar);
        }
        return productIDBuilder.toString();
    }
}
