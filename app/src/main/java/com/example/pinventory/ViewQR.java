package com.example.pinventory;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class ViewQR extends AppCompatActivity {
    ImageView ivOutput;
    TextView txtName;
    private ProductRVModel productRVModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_qr);
            ivOutput=findViewById(R.id.iv_output);
            txtName=findViewById(R.id.ProductQRName);

            String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Bundle resultIntent = getIntent().getExtras();
            String productIDQR = null;
            if (resultIntent != null) {
                productIDQR = resultIntent.getString("makeQR");

            }
            productRVModel = getIntent().getParcelableExtra("productQR");
            txtName.setText("Name: "+productRVModel.getProductName()+"\nDescription: "
                            +productRVModel.getProductDesc()+"\nQuantity: "+
                             productRVModel.getProductQty()+"\nExpiryDate: "+
                            productRVModel.getExpiryDate());
            MultiFormatWriter writer = new MultiFormatWriter();
            try {
                BitMatrix matrix = writer.encode(user+productIDQR, BarcodeFormat.QR_CODE,350,350);

                BarcodeEncoder encoder = new BarcodeEncoder();

                Bitmap bitmap = encoder.createBitmap(matrix);

                ivOutput.setImageBitmap(bitmap);

            } catch (WriterException e) {
                e.printStackTrace();
            }
    }
}