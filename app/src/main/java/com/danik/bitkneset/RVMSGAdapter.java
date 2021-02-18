package com.danik.bitkneset;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.danik.bitkneset.ui.login.LoginFragment;
import com.danik.bitkneset.ui.messages.MessagesFragment;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RVMSGAdapter extends RecyclerView.Adapter<RVMSGAdapter.ViewHolder> implements Filterable {

    Context context;
    List<Message> msgArr;
    List<Message> msgArrFull;

    public static class ViewHolder extends RecyclerView.ViewHolder{ //viewholder is the single msg item now, just in code.
        TextView nameToRV;
        TextView msgToRV;
        TextView msgDateToRV;
        ImageButton delMsgBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameToRV=itemView.findViewById(R.id.msgNameToRV);
            msgToRV=itemView.findViewById(R.id.msgToRV);
            msgDateToRV=itemView.findViewById(R.id.msgDateToRV);
            delMsgBtn=itemView.findViewById(R.id.delMsgBtn);
        }

    }

    public RVMSGAdapter(Context context, List<Message> msgArr) {
        this.context = context;
        this.msgArr = msgArr;
        this.msgArrFull = new ArrayList<>(msgArr);
    }

    @NonNull
    @Override
    public RVMSGAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.single_msg_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RVMSGAdapter.ViewHolder holder, final int position) {
        holder.nameToRV.setText(msgArr.get(position).getUser());
        holder.msgToRV.setText(msgArr.get(position).getBody());
        holder.msgDateToRV.setText((msgArr.get(position).getDate()));
        holder.delMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MessagesFragment.deleteMode.isChecked()) {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    FirebaseMessager.deleteMessageFromDB(FirebaseMessager.messageKeyList.get(position));
                                    Log.d("DELMSGADAPT", "onClick: Delete Message from FB");
                                    Snackbar.make(holder.itemView.getRootView(),"ההודעה תימחק בשניות הקרובות!", Snackbar.LENGTH_LONG).show();
                                    FirebaseMessager.reconstructKeystones();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    Log.d("DELMSGADAPT", "onClick: Delete Failed of"+FirebaseMessager.messageKeyList.get(position)+" due to low accessLevel or the switch is OFF");
                                    Snackbar.make(holder.itemView.getRootView(),"הודעה לא נמחקה!", Snackbar.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext()); //comes before onCLick dialog above,
                    builder.setMessage("בטוח שברצונך למחוק את ההודעה?"+" "+FirebaseMessager.fromDBList.get(position).get("body")+" "+FirebaseMessager.messageKeyList.get(position)).setPositiveButton("כן", dialogClickListener)
                            .setNegativeButton("לא", dialogClickListener).show();

                }
                else {
                    Log.d("DELMSGADAPT", "onClick: Delete Failed due to low accessLevel or the switch is OFF");
                    Snackbar.make(holder.itemView.getRootView(),"מצב מחיקה מבוטל או שאינך מורשה למחוק הודעות", Snackbar.LENGTH_LONG).show();
                    }

            }
        });

    }

    @Override
    public int getItemCount() {
        return msgArr.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) { //perform filter actually live with new regenerating lists that fit constraint string
            List<Message> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0)
                filteredList.addAll(msgArrFull);
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Message m : msgArrFull) {
                    if (m.getDate().contains(filterPattern) || m.getBody().contains(filterPattern) || m.getUser().contains(filterPattern))
                        filteredList.add(m);
                }
            }
            FilterResults filteredResults = new FilterResults();
            filteredResults.values = filteredList;
            return filteredResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            msgArr.clear();
            msgArr.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };
}
