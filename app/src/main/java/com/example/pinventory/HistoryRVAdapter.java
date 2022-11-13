package com.example.pinventory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryRVAdapter extends RecyclerView.Adapter<HistoryRVAdapter.ViewHolder> {

    public HistoryRVAdapter(ArrayList<HistoryRVModel> historyRVModelArrayList, Context context, HistoryClickInterface historyClickInterface) {
        this.historyRVModelArrayList = historyRVModelArrayList;
        this.context = context;
        this.historyClickInterface = historyClickInterface;
    }

    private ArrayList<HistoryRVModel> historyRVModelArrayList;
    private Context context;
    int lastPos = -1;
    private HistoryClickInterface historyClickInterface;

    @NonNull

    @Override
    public HistoryRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        HistoryRVModel historyRVModel = historyRVModelArrayList.get(position);

        holder.actionHistoryTV.setText(historyRVModel.getActionHistory());

        setAnimation(holder.itemView, position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyClickInterface.onHistoryClick(position);
            }
        });
    }

    private void setAnimation(View itemView, int position){
        if(position>lastPos){
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            itemView.setAnimation(animation);
            lastPos = position;
        }
    }

    @Override
    public int getItemCount() {
        return historyRVModelArrayList.size();
    }

    public interface HistoryClickInterface{
        void onHistoryClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView actionHistoryTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            actionHistoryTV = itemView.findViewById(R.id.idTVHistory); //history_rv_item.xml

        }
    }


}
