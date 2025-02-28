package com.sellpoint.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sellpoint.R;
import com.sellpoint.interfaces.DeleteInterface;
import com.sellpoint.models.Banner;

import java.util.ArrayList;

public class BannersAdapter extends RecyclerView.Adapter<BannersAdapter.ViewHolder> {

    ArrayList<Banner> bannerList;
    Context context;

    DeleteInterface deleteInterface;

    public BannersAdapter(Context context, ArrayList<Banner> bannerList, DeleteInterface deleteInterface){
        this.context = context;
        this.bannerList = bannerList;
        this.deleteInterface = deleteInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_listview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String bannerUrl = bannerList.get(position).getImagePath();
        int bannerID = bannerList.get(position).getId();

        Glide.with(context).load(context.getResources().getString(R.string.BASE_URL)+bannerUrl).into(holder.bannerImg);

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setMessage("Do you want to delete this banner?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteInterface.deleteBanner(bannerID);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView bannerImg, deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImg = itemView.findViewById(R.id.bannerImg);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
