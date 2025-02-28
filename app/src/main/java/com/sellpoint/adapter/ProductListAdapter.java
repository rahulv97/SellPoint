package com.sellpoint.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
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

import com.sellpoint.R;
import com.sellpoint.activities.ItemDetailsActivity;
import com.sellpoint.models.ItemsModel;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> implements Filterable {

    Context context;
    ArrayList<ItemsModel> itemsList;

    Filter filter;

    public ProductListAdapter(Context context, ArrayList<ItemsModel> itemsList, Filter filter){
        this.context = context;
        this.itemsList = itemsList;
        this.filter = filter;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ItemsModel item = itemsList.get(position);

        holder.prodName.setText(item.getItemName());
        holder.creationDate.setText("Purchased On: "+item.getCreationDate());
        holder.prodPrice.setText("₹"+item.getPurchasePrice());

        String inputDateStr = item.getWarrantyDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

        try {
            // Parse input date
            Date inputDate = dateFormat.parse(inputDateStr);
            // Get the current date
            Date currentDate = new Date();

            // Compare dates
            if (inputDate.after(currentDate)) {
                holder.wrImg.setImageDrawable(context.getResources().getDrawable(R.drawable.warranty_ic));
            } else if (inputDate.before(currentDate)) {
                holder.wrImg.setImageDrawable(context.getResources().getDrawable(R.drawable.no_warranty_ic));
            } else {
                holder.wrImg.setImageDrawable(context.getResources().getDrawable(R.drawable.warranty_ic));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }



        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ItemDetailsActivity.class);
                intent.putExtra("ItemData", item);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout item;
        TextView prodName, creationDate, prodPrice;
        ImageView wrImg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
            prodName = itemView.findViewById(R.id.prodName);
            creationDate = itemView.findViewById(R.id.creationDate);
            prodPrice = itemView.findViewById(R.id.prodPrice);
            wrImg = itemView.findViewById(R.id.wrImg);
        }
    }

}
