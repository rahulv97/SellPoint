package com.sellpoint.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sellpoint.R;
import com.sellpoint.activities.UdhaarDetailsActivity;
import com.sellpoint.models.UdhaarModel;

import java.util.ArrayList;

public class UdhaarAdapter extends RecyclerView.Adapter<UdhaarAdapter.ViewHolder> implements Filterable {

    Context context;
    String reqType;

    Filter filter;

    ArrayList<UdhaarModel> udhaarList;

    public UdhaarAdapter(Context context, String reqType, Filter filter, ArrayList<UdhaarModel> udhaarList){
        this.context = context;
        this.reqType = reqType;
        this.filter = filter;
        this.udhaarList = udhaarList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.udhaar_listview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UdhaarModel udhaarModel = udhaarList.get(position);

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UdhaarDetailsActivity.class);
                intent.putExtra("ReqType", reqType);
                intent.putExtra("UdhaarData", udhaarModel);
                context.startActivity(intent);
            }
        });

        holder.personName.setText(udhaarModel.getPerson_name());
        holder.returnDate.setText("Return Date: "+udhaarModel.getReturn_date());
        holder.creationDate.setText("Entry Date: "+udhaarModel.getCreation_date());
        holder.prodPrice.setText("₹"+udhaarModel.getPurchase_price());

        Glide.with(context).load(context.getResources().getString(R.string.BASE_URL)+udhaarModel.getPerson_image()).into(holder.profImg);

    }

    @Override
    public int getItemCount() {
        return udhaarList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout item;

        TextView personName, returnDate, creationDate, prodPrice;

        ImageView profImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
            personName = itemView.findViewById(R.id.personName);
            returnDate = itemView.findViewById(R.id.returnDate);
            creationDate = itemView.findViewById(R.id.creationDate);
            prodPrice = itemView.findViewById(R.id.prodPrice);
            profImg = itemView.findViewById(R.id.profImg);
        }
    }
}
