package com.sellpoint.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.sellpoint.R;
import com.sellpoint.activities.SignupActivity;
import com.sellpoint.models.User;
import com.sellpoint.models.UsersModel;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> implements Filterable {

    ArrayList<UsersModel> usersList;
    Context context;

    Filter filter;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public UsersAdapter(ArrayList<UsersModel> usersList, Context context, Filter filter){
        this.usersList = usersList;
        this.context = context;
        this.filter = filter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_listview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UsersModel user = usersList.get(position);

        holder.shopName.setText(user.getShopName());
        holder.ownerName.setText(user.getOwnerName());
        holder.mobile.setText(user.getMobile());
        holder.email.setText(user.getEmail());
        holder.password.setText(user.getPassword());
        holder.creationDate.setText(user.getCreationDate());
        holder.expiryDate.setText(user.getExpiryDate());

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SignupActivity.class);
                intent.putExtra("UserData", user);
                intent.putExtra("NewOrEdit", "Admin");
                context.startActivity(intent);
            }
        });

        holder.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setMessage("Do you want to login as "+user.getOwnerName()+"?")
                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sharedPreferences = context.getSharedPreferences("SellPointPrefs", MODE_PRIVATE);
                                editor = sharedPreferences.edit();

                                editor.putString("AdminLogin", sharedPreferences.getString("Mobile", ""));
                                editor.putString("Mobile", user.getMobile());
                                editor.apply();
                                dialog.dismiss();
                                ((Activity)context).finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
        return usersList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView shopName, ownerName, mobile, email, password, creationDate, expiryDate;
        LinearLayout item;

        AppCompatButton login;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            shopName = itemView.findViewById(R.id.shopName);
            ownerName = itemView.findViewById(R.id.ownerName);
            mobile = itemView.findViewById(R.id.mobile);
            email = itemView.findViewById(R.id.email);
            password = itemView.findViewById(R.id.password);
            creationDate = itemView.findViewById(R.id.creationDate);
            expiryDate = itemView.findViewById(R.id.expiryDate);
            item = itemView.findViewById(R.id.item);
            login = itemView.findViewById(R.id.login);
        }
    }
}
