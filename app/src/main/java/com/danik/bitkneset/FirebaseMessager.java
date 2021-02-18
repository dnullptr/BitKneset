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

public class FirebaseMessager { //semi static class , i found all deletion fields required to be static to be accessed from anywhere WITHOUT creating new instance.
        public static boolean Success=false;
        public static FirebaseDatabase database;
        public static DatabaseReference myRef;
        public static FirebaseAuth mAuth;
        public static List<Map<String,String>> fromDBList;
        public List<Message> messageList = new ArrayList<Message>();
        public static List<String> messageKeyList = new ArrayList<String>();
        public FirebaseMessager(final String path)
        {
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference(path);
            mAuth = FirebaseAuth.getInstance();
            Log.d(TAG, "FirebaseMessages: Connected");

            com.danik.bitkneset.FirebaseMessager.myRef.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    fromDBList = new ArrayList<>();


                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        fromDBList.add((Map) data.getValue());
                        Log.d(TAG, "MessageOnDataChangeKEYSTONE: "+data.getKey());
                        messageKeyList.add(data.getKey());

                    }
                    Log.d(TAG, "FireBaseMessager -> Pulled all messages!");

                    for (Map<String, String> map : fromDBList)
                        Log.d(TAG, "FOR DBG: " + map.get("user") + " DATE:" + map.get("date"));

                    for (int i = 0; i < fromDBList.size(); i++) { //adding message to list out of generic json map...
                        Message temp = new Message(fromDBList.get(i).get("user"),fromDBList.get(i).get("body"),fromDBList.get(i).get("date"));
                        Log.d(TAG, "getFullMSGList: " + temp.getUser() + " " + temp.getBody() + " " + temp.getDate());  //delete when ready
                        messageList.add(temp);
                    }
                    Log.d(TAG, "FireBaseMessager -> Downloaded Message List of Maps!");
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



        public boolean pushMessageToDB(Message message) {
            try {
                myRef.push().setValue(message);
                Log.d(TAG, "Push Message : Done!");
                return true;
            }
            catch (Exception e)
            {
                Toast.makeText(null,"Can't Publish Message to DB , Check the Network"+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        public static boolean deleteMessageFromDB(String keystone)
        {
            Log.d(TAG, "deleteMessageFromDB With Keystone: "+keystone);
            DatabaseReference pathToDelete=myRef.child(keystone);
            if(messageKeyList.size()>0) {
                pathToDelete.removeValue();
                return true;
            }

            return false;
        }

        public static void reconstructKeystones()
        {
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            Log.d(TAG, "FirebaseMessages: Connected");

            com.danik.bitkneset.FirebaseMessager.myRef.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    fromDBList = new ArrayList<>();


                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        fromDBList.add((Map) data.getValue());
                        Log.d(TAG, "onDataChangeKEYSTONE: "+data.getKey());
                        messageKeyList.add(data.getKey());

                    }
                    Log.d(TAG, "FireBaseMessager -> Reconstructed all message keystones!");

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }

        public List<Message> pullMessagesFromDB()
        {
                return messageList;
        }



}
