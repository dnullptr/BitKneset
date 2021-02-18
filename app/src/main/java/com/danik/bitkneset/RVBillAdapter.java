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

import com.danik.bitkneset.ui.bill.BillFragment;
import com.danik.bitkneset.ui.login.LoginFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class RVBillAdapter extends RecyclerView.Adapter<RVBillAdapter.ViewHolder> implements Filterable {

    Context context;
    List<Bill> billArr;
    List<Bill> billArrFull;

    public static class ViewHolder extends RecyclerView.ViewHolder{ //viewholder is the single bill item , just in code.
        TextView typeToRV;
        TextView descToRV;
        TextView amountToRV;
        TextView dateToRV;
        ImageView isPaidPic;
        Button payUnpaidBtn;
        ImageButton delBillBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            typeToRV=itemView.findViewById(R.id.typeToRV);
            descToRV=itemView.findViewById(R.id.descToRV);
            amountToRV=itemView.findViewById(R.id.amountToRV);
            dateToRV=itemView.findViewById(R.id.dateToRV);
            isPaidPic=itemView.findViewById(R.id.isPaidPic);
            payUnpaidBtn=itemView.findViewById(R.id.payUnpaidBtn);
            delBillBtn=itemView.findViewById(R.id.delOrderBtn);
        }

    }

    public RVBillAdapter(Context context, List<Bill> billArr) {
        this.context = context;
        this.billArr = billArr;
        this.billArr = Toolbox.deDupeList((ArrayList<Bill>) this.billArr);
        this.billArrFull = new ArrayList<>(billArr);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RVBillAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.single_bill_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RVBillAdapter.ViewHolder holder, final int position) {
        holder.typeToRV.setText(billArr.get(position).getType());
        holder.descToRV.setText(billArr.get(position).getDesc());
        holder.amountToRV.setText(String.valueOf(billArr.get(position).getAmount()));
        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy"); //for later if i want to filter dates , it's gonna help me
        holder.dateToRV.setText((billArr.get(position).getDate()));
        if(billArr.get(position).isPaid()) holder.isPaidPic.setImageResource(R.drawable.truepic); else { holder.isPaidPic.setImageResource(R.drawable.falsepic); holder.payUnpaidBtn.setVisibility(View.VISIBLE);}
        if(LoginFragment.user != null) //needed to avoid null when not yet connected
            if(LoginFragment.user.getAccessLevel() == 2) holder.delBillBtn.setVisibility(View.VISIBLE);

        holder.delBillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                FirebaseBiller.deleteBillFromDB(FirebaseBiller.billKeyList.get(position));
                                Log.d("SingletonDeleterBills", FirebaseBiller.billKeyList.get(position));
                                //do deletion
                                Snackbar.make(holder.itemView.getRootView(),"החשבון יימחק בשניות הקרובות!", Snackbar.LENGTH_LONG).show();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                Log.d("DELBLLADAPT", "onClick: Delete Failed of"+FirebaseBiller.billKeyList.get(position));
                                Snackbar.make(holder.itemView.getRootView(),"החשבון לא נמחק!", Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext()); //comes before onCLick dialog above,
                builder.setMessage("אזהרת מנהל, האם בטוח שברצונך למחוק את רשומת החשבון?").setPositiveButton("כן", dialogClickListener)
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
                                FirebaseBiller.billsList.get(position).setPaid(true);
                                FirebaseBiller.updateBillInDB(FirebaseBiller.billKeyList.get(position),FirebaseBiller.billsList.get(position));
                                Log.d("SingletonUpdater", FirebaseBiller.billKeyList.get(position));
                                Snackbar.make(holder.itemView.getRootView(),"החשבון תתעדכן בשניות הקרובות!", Snackbar.LENGTH_LONG).show();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                Log.d("SingletonUpdater", "onClick: Update Failed of"+FirebaseBiller.billKeyList.get(position));
                                Snackbar.make(holder.itemView.getRootView(),"החשבון לא עודכנה עדיין..", Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext()); //comes before onCLick dialog above,
                builder.setMessage("אזהרת מנהל, האם אתה בטוח שחשבון זה שולם?").setPositiveButton("כן", dialogClickListener)
                        .setNegativeButton("לא", dialogClickListener).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return billArr.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        float sumBillsFiltered = 0;
        @Override
        protected FilterResults performFiltering(CharSequence constraint) { //perform filter bafoal live with new regenerating lists that fit constraint string
            List<Bill> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0)
                filteredList.addAll(billArrFull);
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Bill b : billArrFull) {
                    Log.d("TAG", "performFiltering: "+b.getType()+" filPattern "+filterPattern);
                    if (b.getDate().contains(filterPattern) || b.getDesc().contains(filterPattern) || b.getType().contains(filterPattern) || b.getAmount().contains(filterPattern)) {
                        filteredList.add(b);
                        sumBillsFiltered += Float.parseFloat(b.getAmount()); //sum on filter section
                    }
                }
            }
            FilterResults filteredResults = new FilterResults();
            filteredResults.values = filteredList;
            return filteredResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            billArr.clear();
            billArr.addAll((ArrayList)results.values);
            FirebaseBiller.SumAllBills=sumBillsFiltered; //sum on filter section
            notifyDataSetChanged();
        }
    };
}
