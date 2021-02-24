package com.danik.bitkneset;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RecoveryEngine extends AppCompatActivity {
    FirebaseHelper fbh;
    TextView secretLbl;
    EditText fullname,amount,date,order,secretPass;
    ImageButton recoverPassBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_engine);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fullname = findViewById(R.id.editFullnameToCheck);
        amount = findViewById(R.id.editOrderAmountToCheck);
        date = findViewById(R.id.editOrderDateToCheck);
        order = findViewById(R.id.editOrderNameToCheck);
        recoverPassBtn = findViewById(R.id.tryRecoverPass);
        secretLbl = findViewById(R.id.urPassSecretLbl);
        secretPass = findViewById(R.id.secretPass);
        fbh = new FirebaseHelper("Users");

        recoverPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean Found=false;
                if(!Toolbox.isDateValid(date.getText().toString()))
                {
                    Toast.makeText(getApplicationContext(),"התאריך אינו חוקי",Toast.LENGTH_LONG).show();

                }
                for (Order o: FirebaseHelper.thisUser_fbr.aliyot) {
                    Log.d("RecoveryDebug", "onClick: "+o.getDesc()+" "+o.getAmount());
                    Log.d("Inb4For", "onClick: "+o.getUser()+", "+fullname.getText()+" "+o.getDate()+", "+date.getText());
                    Log.d("Inb4For",  "TRUEAMNT? "+(o.getAmount() == Float.parseFloat(amount.getText().toString())));
                    if(o.getUser().contentEquals(fullname.getText()) && o.getDate().contentEquals(date.getText()) && o.getAmount() == Float.parseFloat(amount.getText().toString()) && o.getDesc().contentEquals(order.getText()))
                    {
                        Log.d("RecoveryDebug", "size is  "+FirebaseHelper.fromDBList.size());
                        for (int i = 0; i < FirebaseHelper.fromDBList.size(); i++) {
                            Log.d("RecoveryDebug", "onPassGuess: "+FirebaseHelper.fromDBList.get(i).get("fullName")+" "+fullname.getText());
                            if(FirebaseHelper.fromDBList.get(i).get("fullName").contentEquals(fullname.getText())){
                                secretPass.setText(FirebaseHelper.fromDBList.get(i).get("password"));
                                secretLbl.setVisibility(View.VISIBLE);
                                secretPass.setVisibility(View.VISIBLE);
                                Found=true;
                                Toast.makeText(getApplicationContext(),"הסיסמה נמצאה!",Toast.LENGTH_LONG).show();
                            }

                        }

                        }


                    }
                if(!Found)
                    Toast.makeText(getApplicationContext(),"לצערנו, הסיסמה לא נמצאה..",Toast.LENGTH_LONG).show();

                }
            });




    }
}