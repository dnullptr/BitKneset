package com.danik.bitkneset;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FirebasePlayer { //static instances class , helps to connect and retrieve the Orders for later to be processed.
    public static boolean Success=false;
    public static FirebaseDatabase database;
    public static DatabaseReference myRef;
    public static FirebaseAuth mAuth;
    public static String halachaSiteUrl,halachaMP3Url;



    public FirebasePlayer(final String path)
    {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(path);
        mAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "FirebasePlayer: Connected");
        FirebasePlayer.myRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                halachaSiteUrl = (String)dataSnapshot.getValue();
                halachaMP3Url = ((String) dataSnapshot.getValue()).replace("ReadHalacha","Download");
                Log.d(TAG, "onDataChangePlayerFB: "+dataSnapshot.getValue()+" \nMP3:"+ ((String) dataSnapshot.getValue()).replace("ReadHalacha","Download"));


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



}



