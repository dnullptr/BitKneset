package com.danik.bitkneset;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FirebaseBiller {
    public static boolean Success=false;
    public static FirebaseDatabase database;
    public static DatabaseReference myRef;
    public static FirebaseAuth mAuth;
    public static List<Map<String,String>> fromDBList;
    public static List<Map<String,Boolean>> fromDBListPaid;
    public static List<Bill> billsList = new ArrayList<Bill>();
    public static List<String> billKeyList = new ArrayList<String>();
    public static float SumAllBills = 0;
    public FirebaseBiller(final String path)
    {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(path);
        mAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "FirebaseBiller: Connected");

        com.danik.bitkneset.FirebaseBiller.myRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fromDBList = new ArrayList<>();
                fromDBListPaid = new ArrayList<>();

                fromDBList.clear(); //due to informal singleton we need to clear the lists that might be full from last invoke, anyways we can do clear() since it's not null..
                fromDBListPaid.clear();

                SumAllBills = 0;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    fromDBList.add((Map) data.getValue());
                    fromDBListPaid.add((Map) data.getValue());
                    Log.d(TAG, "BillOnDataChangeKEYSTONE: "+data.getKey());
                    billKeyList.add(data.getKey());

                }
                Log.d(TAG, "FireBaseBiller -> Pulled all bills!");

                for (Map<String, String> map : fromDBList)
                    Log.d(TAG, "FOR DBG: " + map.get("user") + " DATE:" + map.get("date"));

                for (int i = 0; i < fromDBList.size(); i++) { //adding bill to list out of generic json map...
                    Bill temp = new Bill(fromDBList.get(i).get("type"),fromDBList.get(i).get("desc"),fromDBList.get(i).get("amount"),fromDBListPaid.get(i).get("paid"),fromDBList.get(i).get("date"));
                    SumAllBills += Float.parseFloat(fromDBList.get(i).get("amount"));
                    Log.d(TAG, "getFullBillList: " + temp.getUser() + " " + temp.getType() + " " + temp.getDate() +" "+ temp.getAmount());
                    billsList.add(temp);
                }
                Log.d(TAG, "FireBaseBiller -> Downloaded Bills List of Maps!");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void setMyRef(String path)
    {
        this.myRef=database.getReference(path);
    }



    public boolean pushBillToDB(Bill bill) {
        try {
            myRef.push().setValue(bill);
            Log.d(TAG, "Push Bill to FB : Done!");
            return true;
        }
        catch (Exception e)
        {
            Toast.makeText(null,"Can't Publish Bill to DB , Check the Network"+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static boolean deleteBillFromDB(String keystone)
    {
        Log.d(TAG, "deleteBillFromDB With Keystone: "+keystone);
        DatabaseReference pathToDelete=myRef.child(keystone);
        if(billKeyList.size()>0) {
            pathToDelete.removeValue();
            return true;
        }

        return false;
    }

    public static boolean updateBillInDB(String keystone,Bill bill)
    {
        Log.d(TAG, "updateOrderInDB With Keystone: "+keystone);
        DatabaseReference pathToUpdate=myRef.child(keystone);
        if(billKeyList.size()>0) {
            pathToUpdate.setValue(bill);
            return true;
        }

        return false;
    }

    public static void reconstructKeystones()
    {
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "FirebaseBiller: Connected");

        com.danik.bitkneset.FirebaseBiller.myRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fromDBList = new ArrayList<>();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    fromDBList.add((Map) data.getValue());
                    Log.d(TAG, "onDataChangeKEYSTONE: "+data.getKey());
                    billKeyList.add(data.getKey());

                }
                Log.d(TAG, "FireBaseBiller -> Reconstructed all bill keystones!");

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public List<Bill> pullBillsFromDB()
    {
        return billsList;
    }

}
