package com.example.pinventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements ProductRVAdapter.ProductClickInterface {

    private RecyclerView productRV;
    private ProgressBar progressBar;
    private FloatingActionButton addFAB;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ArrayList<ProductRVModel> productRVModelArrayList;
    private RelativeLayout bottomSheetRL;
    private ProductRVAdapter productRVAdapter;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchView = findViewById(R.id.searchViewProduct);
        mSearchView.clearFocus();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        productRV = findViewById(R.id.idRVProducts);
        progressBar = findViewById(R.id.progressBar);
        addFAB = findViewById(R.id.idAddFAB);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Products").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        productRVModelArrayList = new ArrayList<>();
        bottomSheetRL = findViewById(R.id.idRLBSheet);

        productRVAdapter = new ProductRVAdapter(productRVModelArrayList, this, this);
        productRV.setLayoutManager(new LinearLayoutManager(this));
        productRV.setAdapter(productRVAdapter);
        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddProductActivity.class));
            }
        });

        getAllProducts();
    }

    private void filterList(String text) {
        ArrayList<ProductRVModel> filteredList = new ArrayList<>();
        for (ProductRVModel item : productRVModelArrayList) {
            if (item.getProductName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No data found!", Toast.LENGTH_SHORT).show();
        } else {
            productRVAdapter.setFilteredList(filteredList);
        }
    }

    private void getAllProducts() {
        productRVModelArrayList.clear();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);
                productRVModelArrayList.add(snapshot.getValue(ProductRVModel.class));
                productRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);
                productRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                productRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);
                productRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onProductClick(int position) {
        displayBottomSheet(productRVModelArrayList.get(position));
    }

    private void displayBottomSheet(ProductRVModel productRVModel) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog, bottomSheetRL);
        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();

        TextView productNameTV = layout.findViewById(R.id.idTVProductName);
        TextView productDescTV = layout.findViewById(R.id.idTVDescription);
        TextView productQtyTV = layout.findViewById(R.id.idTVQuantity);
        ImageView productIV = layout.findViewById(R.id.idIVProduct);

        Button editBtn = layout.findViewById(R.id.idBtnEdit);


        productNameTV.setText(productRVModel.getProductName());
        productDescTV.setText(productRVModel.getProductDesc());
        productQtyTV.setText("Stock: " + productRVModel.getProductQty());
        Picasso.with(this).load(productRVModel.getProductImg())
                .placeholder(R.drawable.ic_no_photo).fit().centerInside()
                .into(productIV);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, EditProductActivity.class);
                i.putExtra("product", productRVModel);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.mmQRCode:
                Intent i = new Intent(MainActivity.this, ChangePassActivity.class);
                startActivity(i);
                this.finish();
                return true;

            case R.id.mmHistory:
                Intent j = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(j);
                this.finish();
                return true;

            case R.id.mmProfile:
                Intent k = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(k);
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}