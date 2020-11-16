package com.danik.bitkneset;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.danik.bitkneset.ui.login.LoginFragment;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.danik.bitkneset.ui.login.LoginViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FirebaseHelper {
    public static boolean Success=false;
    public static FirebaseDatabase database;
    public static DatabaseReference myRef;
    public static FirebaseAuth mAuth;
    public static List<Map<String,String>> fromDBList;
    public static List<Map<String,Long>> fromDB1List; //same list , pulled at the same time , with diff casting to maps of <string,long> we'll get the accessLevel which is numeric
    public static Thread orderDownloader;
    public static FirebaseRetriever thisUser_fbr;


    public FirebaseHelper(final String path)
    {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(path);
        mAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "FirebaseHelper: Connected");
        FirebaseHelper.myRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                fromDBList=new ArrayList<>();
                fromDB1List=new ArrayList<>();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    fromDBList.add((Map) data.getValue());
                    fromDB1List.add((Map) data.getValue());
                }
                Log.d(TAG, "onDataChange: DOWNLOADED ALL LISTS AND ARE READY");
                orderDownloader=new Thread()
                {
                    @Override
                    public void run() {
                        thisUser_fbr=new FirebaseRetriever("Orders");
                        Log.d(TAG, "onOrderDownloadThread: Fired-Up Thread Run()!");
                    }
                };
                orderDownloader.run();
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

    public static boolean registerUser(User user)
    {
        try {
            myRef.push().setValue(user);
            Log.d(TAG, "registerUser: Done!");
            return true;
        }
        catch (Exception e)
        {
            Toast.makeText(null,"Can't Register Now , Check the Network"+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static int loginUser(final User fromInput) {


        Success=false;

                for(int i=0;i<fromDBList.size();i++){
                    User temp=new User(fromDBList.get(i).get("username"),fromDBList.get(i).get("password"), Math.toIntExact(fromDB1List.get(i).get("accessLevel")),fromDBList.get(i).get("fullName"));
                    if(fromInput.compareTo(temp)){ //if it has the same credentials then Logged in!
                        LoginFragment.user=temp;
                        LoginFragment.user.Connected=true;
                        LoginViewModel.toCheck.Connected=true;
                        Log.d(TAG, "onDataChange: bdika me FBH lirot im yesh access le object be LoginFragment :"+LoginFragment.user.username+"fullname: "+LoginFragment.user.getFullName());
                        Success=true;

                    Log.d(TAG, "CONNECTED to Firebase with: " + fromDBList.get(i).get("username")+LoginFragment.user.Connected);}

                }

                return LoginFragment.user!=null?LoginFragment.user.accessLevel:0;
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

}



