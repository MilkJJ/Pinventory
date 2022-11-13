package com.example.pinventory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductRVAdapter extends RecyclerView.Adapter<ProductRVAdapter.ViewHolder> {

    public ProductRVAdapter(ArrayList<ProductRVModel> productRVModelArrayList, Context context, ProductClickInterface productClickInterface) {
        this.productRVModelArrayList = productRVModelArrayList;
        this.context = context;
        this.productClickInterface = productClickInterface;
    }

    public void setFilteredList(ArrayList<ProductRVModel> filteredList){
        this.productRVModelArrayList = filteredList;
        notifyDataSetChanged();
    }

    private ArrayList<ProductRVModel> productRVModelArrayList;
    private Context context;
    int lastPos = -1;
    private ProductClickInterface productClickInterface;

    @NonNull

    @Override
    public ProductRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ProductRVModel productRVModel = productRVModelArrayList.get(position);

        Picasso.with(this.context).load(productRVModel.getProductImg())
                .placeholder(R.drawable.ic_no_photo).fit().centerInside()
                .into(holder.productIV);

        holder.productNameTV.setText(productRVModel.getProductName());
        holder.productQtyTV.setText("Stock: "+productRVModel.getProductQty());
        holder.expiryDateTV.setText("Expiry: "+productRVModel.getExpiryDate());

        setAnimation(holder.itemView, position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productClickInterface.onProductClick(position);
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
        return productRVModelArrayList.size();
    }

    public interface ProductClickInterface{
        void onProductClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView productNameTV, productQtyTV, expiryDateTV;
        private ImageView productIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTV = itemView.findViewById(R.id.idTVProductName);
            productQtyTV = itemView.findViewById(R.id.idTVQuantity);
            expiryDateTV = itemView.findViewById(R.id.idTVExpiryDate);
            productIV = itemView.findViewById(R.id.idIVProduct);
        }
    }


}
