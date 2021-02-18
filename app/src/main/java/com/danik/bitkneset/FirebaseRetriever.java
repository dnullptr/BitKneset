package com.danik.bitkneset;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.danik.bitkneset.ui.login.LoginFragment;
import com.danik.bitkneset.ui.login.LoginViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FirebaseRetriever {
    public static boolean Success=false;
    public static FirebaseDatabase database;
    public static DatabaseReference myRef;
    public static FirebaseAuth mAuth;
    public static List<Map<String,String>> fromDBList;
    public List<Map<String,Long>> AliyotFloatList;
    public List<Map<String,Boolean>> AliyotPaidList;
    public List<Map<String,String>> Aliyot;
    public List<Order> aliyot = new ArrayList<Order>();
    public static List<String> orderKeyList = new ArrayList<String>();
    public static float SumAllOrders = 0;
    public FirebaseRetriever(final String path)
    {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(path);
        mAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "FirebaseRetreiever: Connected");
        FirebaseRetriever.myRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                SumAllOrders = 0;
                fromDBList = new ArrayList<>();


                AliyotFloatList = new ArrayList<>();
                AliyotPaidList = new ArrayList<>();
                Aliyot = new ArrayList<>();


                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    fromDBList.add((Map) data.getValue());
                    AliyotFloatList.add((Map) data.getValue());
                    AliyotPaidList.add((Map) data.getValue());
                    Aliyot.add((Map) data.getValue());
                    Log.d(TAG, "OrderOnDataChangeKEYSTONE: "+data.getKey());
                    orderKeyList.add(data.getKey());

                }
                Log.d(TAG, "FireBaseRetriever -> Downloaded FB Data!");
                for (Map<String, Long> map : AliyotFloatList)
                    Log.d(TAG, "FOR DBG: " + map.get("amount"));
                for (Map<String, String> map : fromDBList)
                    Log.d(TAG, "FOR DBG: " + map.get("user") + " DATE:" + map.get("date"));
                for (Map<String, Boolean> map : AliyotPaidList)
                    Log.d(TAG, "FOR DBG: " + map.get("paid"));

                for (int i = 0; i < fromDBList.size(); i++) {
                    Order temp = new Order(fromDBList.get(i).get("user"), fromDBList.get(i).get("type"),fromDBList.get(i).get("desc"), AliyotFloatList.get(i).get("amount"), AliyotPaidList.get(i).get("paid"),fromDBList.get(i).get("date") );
                    Log.d(TAG, "getFullOrderList: " + temp.getUser() + " " + temp.getType() + " " + temp.isPaid() + " " + temp.getAmount() + " " + temp.getDate().toString());
                    aliyot.add(temp);
                    SumAllOrders += AliyotFloatList.get(i).get("amount");

                }
                Log.d(TAG, "FireBaseRetriever -> Downloaded Aliyot List of Maps!");
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



    public boolean pushOrderToDB(Order order) {
        try {
            myRef.push().setValue(order);
            Log.d(TAG, "Push Order : Done!");
            return true;
        }
        catch (Exception e)
        {
            Toast.makeText(null,"Can't Push Order to DB , Check the Network"+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    public static boolean updateOrderInDB(String keystone,Order order)
    {
        Log.d(TAG, "updateOrderInDB With Keystone: "+keystone);
        DatabaseReference pathToUpdate=myRef.child(keystone);
        if(orderKeyList.size()>0) {
            pathToUpdate.setValue(order);
            return true;
        }

        return false;
    }

    public static boolean deleteOrderFromDB(String keystone)
    {
        Log.d(TAG, "deleteOrderFromDB With Keystone: "+keystone);
        DatabaseReference pathToDelete=myRef.child(keystone);
        if(orderKeyList.size()>0) {
            pathToDelete.removeValue();
            return true;
        }

        return false;
    }

    public List<Order> getFullOrderList() {
        return aliyot;
    }

    public List<Order> getFilteredUserOrderList(User me)
    {
        List<Order> fullOrderList = getFullOrderList();
        List<Order> filteredList = new ArrayList<>();

            String usernameFilter = me.getFullName();
            for (Order o : fullOrderList) {
                if ( o.getUser().contains(usernameFilter))
                    filteredList.add(o);
            }
        return filteredList;
    }

}




