package com.danik.bitkneset;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> implements Filterable {

    Context context;
    List<Order> aliyotArr;
    List<Order> aliyotArrFull;

    public static class ViewHolder extends RecyclerView.ViewHolder{ //viewholder is the single rv item , just in code.
        TextView nameToRV;
        TextView descToRV;
        TextView amountToRV;
        TextView dateToRV;
        ImageView isPaidPic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameToRV=itemView.findViewById(R.id.nameToRV);
            descToRV=itemView.findViewById(R.id.descToRV);
            amountToRV=itemView.findViewById(R.id.amountToRV);
            dateToRV=itemView.findViewById(R.id.dateToRV);
            isPaidPic=itemView.findViewById(R.id.isPaidPic);
        }

    }

    public RVAdapter(Context context, List<Order> aliyotArr) {
        this.context = context;
        this.aliyotArr = aliyotArr;
        this.aliyotArrFull = new ArrayList<>(aliyotArr);
    }

    @NonNull
    @Override
    public RVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.single_rv_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RVAdapter.ViewHolder holder, int position) {
        holder.nameToRV.setText(aliyotArr.get(position).getUser());
        holder.descToRV.setText(aliyotArr.get(position).getDesc());
        holder.amountToRV.setText(String.valueOf(aliyotArr.get(position).getAmount()));
        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy"); //for later if i want to filter dates , it's gonna help me
        holder.dateToRV.setText((aliyotArr.get(position).getDate()));
        if(aliyotArr.get(position).isPaid()) holder.isPaidPic.setImageResource(R.drawable.truepic); else holder.isPaidPic.setImageResource(R.drawable.falsepic);

    }

    @Override
    public int getItemCount() {
        return aliyotArr.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) { //perform filter bafoal live with new regenerating lists that fit constraint string
            List<Order> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0)
                filteredList.addAll(aliyotArrFull);
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Order o : aliyotArrFull) {
                    if (o.getDate().contains(filterPattern) || o.getDesc().contains(filterPattern) || o.getType().contains(filterPattern) || o.getUser().contains(filterPattern))
                        filteredList.add(o);
                }
            }
            FilterResults filteredResults = new FilterResults();
            filteredResults.values = filteredList;
            return filteredResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            aliyotArr.clear();
            aliyotArr.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };
}
