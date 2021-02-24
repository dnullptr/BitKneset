package com.danik.bitkneset;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.danik.bitkneset.ui.login.LoginFragment;
import com.google.android.material.snackbar.Snackbar;

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
        Button payUnpaidBtn;
        ImageButton delOrderBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameToRV=itemView.findViewById(R.id.nameToRV);
            descToRV=itemView.findViewById(R.id.descToRV);
            amountToRV=itemView.findViewById(R.id.amountToRV);
            dateToRV=itemView.findViewById(R.id.dateToRV);
            isPaidPic=itemView.findViewById(R.id.isPaidPic);
            payUnpaidBtn=itemView.findViewById(R.id.payUnpaidBtn);
            delOrderBtn=itemView.findViewById(R.id.delOrderBtn);
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
    public void onBindViewHolder(@NonNull final RVAdapter.ViewHolder holder, final int position) {
        holder.nameToRV.setText(aliyotArr.get(position).getUser());
        holder.descToRV.setText(aliyotArr.get(position).getDesc() +" ("+aliyotArr.get(position).getType()+")");
        holder.amountToRV.setText(String.valueOf(aliyotArr.get(position).getAmount()));
        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy"); //for later if i want to filter dates , it's gonna help me
        holder.dateToRV.setText((aliyotArr.get(position).getDate()));
        if(aliyotArr.get(position).isPaid()) holder.isPaidPic.setImageResource(R.drawable.truepic); else { holder.isPaidPic.setImageResource(R.drawable.falsepic); holder.payUnpaidBtn.setVisibility(View.VISIBLE);}
        if(LoginFragment.user != null) //needed to avoid null when not yet connected
            if(LoginFragment.user.getAccessLevel() == 2) holder.delOrderBtn.setVisibility(View.VISIBLE);

        holder.delOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                FirebaseHelper.thisUser_fbr.deleteOrderFromDB(FirebaseHelper.thisUser_fbr.orderKeyList.get(position));
                                Log.d("SingletonDeleter", FirebaseHelper.thisUser_fbr.orderKeyList.get(position));
                                //do deletion of order
                                Snackbar.make(holder.itemView.getRootView(),"התרומה תימחק בשניות הקרובות!", Snackbar.LENGTH_LONG).show();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                Log.d("DELORDADAPT", "onClick: Delete Failed of"+FirebaseHelper.thisUser_fbr.orderKeyList.get(position));
                                Snackbar.make(holder.itemView.getRootView(),"התרומה לא נמחקה!", Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext()); //comes before onCLick dialog above,
                builder.setMessage("אזהרת מנהל, האם בטוח שברצונך למחוק את התרומה?").setPositiveButton("כן", dialogClickListener)
                        .setNegativeButton("לא", dialogClickListener).show();

            }
        });

        holder.payUnpaidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                FirebaseHelper.thisUser_fbr.aliyot.get(position).setPaid(true);
                                FirebaseHelper.thisUser_fbr.updateOrderInDB(FirebaseHelper.thisUser_fbr.orderKeyList.get(position),FirebaseHelper.thisUser_fbr.aliyot.get(position));
                                Log.d("SingletonUpdater", FirebaseHelper.thisUser_fbr.orderKeyList.get(position));
                                Snackbar.make(holder.itemView.getRootView(),"התרומה תתעדכן בשניות הקרובות!", Snackbar.LENGTH_LONG).show();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                Log.d("SingletonUpdater", "onClick: Update Failed of"+FirebaseHelper.thisUser_fbr.orderKeyList.get(position));
                                Snackbar.make(holder.itemView.getRootView(),"התרומה לא עודכנה עדיין..", Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext()); //comes before onCLick dialog above,
                builder.setMessage("אזהרת מנהל, האם אתה בטוח שהזמנה זו שולמה?").setPositiveButton("כן", dialogClickListener)
                        .setNegativeButton("לא", dialogClickListener).show();
            }
        });
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
