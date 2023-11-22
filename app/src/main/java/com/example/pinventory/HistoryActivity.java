package com.example.pinventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class HistoryActivity extends AppCompatActivity implements HistoryRVAdapter.HistoryClickInterface {

    private RecyclerView historyRV;
    private ProgressBar progressBar;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ArrayList<HistoryRVModel> historyRVModelArrayList;
    private HistoryRVAdapter historyRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        historyRV = findViewById(R.id.idRVHistory);
        progressBar = findViewById(R.id.progressBar);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("History").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        historyRVModelArrayList = new ArrayList<>();

        historyRVAdapter = new HistoryRVAdapter(historyRVModelArrayList, this, this);
        historyRV.setLayoutManager(new LinearLayoutManager(this));
        historyRV.setAdapter(historyRVAdapter);

        progressBar.setVisibility(View.VISIBLE);
        getHistories();
    }


    private void getHistories() {
        historyRVModelArrayList.clear();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);

                HistoryRVModel historyModel = snapshot.getValue(HistoryRVModel.class);

                if (historyModel != null) {
                    historyRVModelArrayList.add(historyModel);

                    // Sort the list based on the timestamp (currentTimeMillis())
                    Collections.sort(historyRVModelArrayList, new Comparator<HistoryRVModel>() {
                        @Override
                        public int compare(HistoryRVModel o1, HistoryRVModel o2) {
                            // Reverse order for descending sorting
                            return Long.compare(Long.parseLong(o2.getTimestamp()), Long.parseLong(o1.getTimestamp()));
                        }
                    });

                    historyRVAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);
                historyRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                historyRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);
                historyRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onHistoryClick(int position) {
        //displayBottomSheet(historyRVModelArrayList.get(position));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(HistoryActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.mmQRCode:
                Intent i = new Intent(HistoryActivity.this, CaptureAct.class);
                startActivity(i);
                this.finish();
                return true;

            case R.id.mmHistory:
                Intent j = new Intent(HistoryActivity.this, HistoryActivity.class);
                startActivity(j);
                this.finish();
                return true;

            case R.id.mmProfile:
                Intent k = new Intent(HistoryActivity.this, ProfileActivity.class);
                startActivity(k);
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}