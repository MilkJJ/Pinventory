package com.example.pinventory;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdminItemListFragment extends Fragment implements ProductRVAdapter.ProductClickInterface {

    private RecyclerView productRV;
    private ProgressBar progressBar;
    private FloatingActionButton addFAB;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference1;

    private ArrayList<ProductRVModel> productRVModelArrayList;
    private RelativeLayout bottomSheetRL;
    private ArrayList<ProductRVModel> QRList;
    private ProductRVAdapter productRVAdapter;
    private SearchView mSearchView;

    public AdminItemListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin_item_list, container, false);

        mSearchView = rootView.findViewById(R.id.searchViewProduct);
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

        productRV = rootView.findViewById(R.id.idAdminRVProducts);
        progressBar = rootView.findViewById(R.id.progressBar);
        addFAB = rootView.findViewById(R.id.idAdminAddFAB);

        progressBar.setVisibility(View.GONE);

        firebaseDatabase = FirebaseDatabase.getInstance();


        productRVModelArrayList = new ArrayList<>();

        productRVAdapter = new ProductRVAdapter(productRVModelArrayList, requireContext(), this);
        productRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        productRV.setAdapter(productRVAdapter);

        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), AddProductActivity.class));
            }
        });
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference1 = firebaseDatabase.getReference("Products");

        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressBar.setVisibility(View.VISIBLE);
                    productRVModelArrayList.clear(); // Clear the list before adding new data

                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        ProductRVModel product = productSnapshot.getValue(ProductRVModel.class);
                        productRVModelArrayList.add(product);
                    }

                    productRVAdapter.notifyDataSetChanged();
                    if (!productRVModelArrayList.isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if needed
            }
        });

        return rootView;
    }

    private void filterList(String text) {
        ArrayList<ProductRVModel> filteredList = new ArrayList<>();
        for (ProductRVModel item : productRVModelArrayList) {
            if (item.getProductName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(requireContext(), "No product found!", Toast.LENGTH_SHORT).show();
        } else {
            productRVAdapter.setFilteredList(filteredList);
        }
    }

    @Override
    public void onProductClick(int position) {
        displayBottomSheet(productRVModelArrayList.get(position));
    }

    private void displayBottomSheet(ProductRVModel productRVModel) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View layout = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_dialog, bottomSheetRL);
        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();

        TextView productNameTV = layout.findViewById(R.id.idTVProductName);
        TextView productDescTV = layout.findViewById(R.id.idTVDescription);
        TextView productQtyTV = layout.findViewById(R.id.idTVQuantity);
        TextView ExpiryDateTV = layout.findViewById(R.id.idTVExpiryDate);
        ImageView productIV = layout.findViewById(R.id.idIVProduct);

        Button editBtn = layout.findViewById(R.id.idBtnEdit);
        Button generateQR = layout.findViewById(R.id.idBtnView);


        productNameTV.setText(productRVModel.getProductName());
        productDescTV.setText(productRVModel.getProductDesc());
        productQtyTV.setText("Stock: " + productRVModel.getProductQty());
        ExpiryDateTV.setText("Expiry: "+ productRVModel.getExpiryDate());
        Picasso.with(requireContext()).load(productRVModel.getProductImg())
                .placeholder(R. drawable.ic_no_photo).fit().centerInside()
                .into(productIV);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(requireContext(), EditProductActivity.class);
                i.putExtra("product", productRVModel);
                startActivity(i);
            }
        });
        generateQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(requireContext(), ViewQR.class);
                //give value to generate qr
                i.putExtra("makeQR",productRVModel.getProductID());
                i.putExtra("productQR",productRVModel);
                startActivity(i);
            }
        });
    }

}