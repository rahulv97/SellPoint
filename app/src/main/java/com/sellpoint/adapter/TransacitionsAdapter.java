package com.sellpoint.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sellpoint.R;
import com.sellpoint.models.Transaction;

import java.util.List;

public class TransacitionsAdapter extends RecyclerView.Adapter<TransacitionsAdapter.ViewHolder>{

    List<Transaction> transactions;
    Context context;

    public TransacitionsAdapter(List<Transaction> transactions, Context context){
        this.transactions = transactions;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_itemview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.srno.setText(String.valueOf(position+1));
        holder.oldDate.setText(transaction.getOldDate());
        holder.newDate.setText(transaction.getNewDate());
        holder.days.setText(String.valueOf(transaction.getDaysDifference()));
        holder.transDate.setText(transaction.getInsertionDate());
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView srno, oldDate, newDate, days, transDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            srno = itemView.findViewById(R.id.srno);
            oldDate = itemView.findViewById(R.id.oldDate);
            newDate = itemView.findViewById(R.id.newDate);
            days = itemView.findViewById(R.id.days);
            transDate = itemView.findViewById(R.id.transDate);
        }
    }

}
