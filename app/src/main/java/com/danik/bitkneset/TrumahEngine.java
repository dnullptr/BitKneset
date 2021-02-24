package com.danik.bitkneset;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.danik.bitkneset.ui.aliyot.AliyotFragment;

public class TrumahEngine extends AppCompatActivity {
    public static Intent trumahInstance;
    final FirebaseHelper fbh = new FirebaseHelper("Orders"); //get fbh instance of orders or "aliyot" as i like to call them :)
    TextView mutav,zuzim;
    EditText cardNumber,expiryDate,cvv;
    ImageButton sendPaymentBtn,finishAndReturn,sendLaterBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trumah_engine);

        mutav = findViewById(R.id.mutavExtra);
        zuzim = findViewById(R.id.zuzimExtra);

        cardNumber = findViewById(R.id.cardNumberEditText);
        expiryDate = findViewById(R.id.expiryDateEditText);
        cvv = findViewById(R.id.cvvEditText);

        sendPaymentBtn = findViewById(R.id.sendPaymentBtn);
        finishAndReturn = findViewById(R.id.finishActivity);
        sendLaterBtn = findViewById(R.id.sendLaterBtn);

        final Intent intent = getIntent(); //call intent from master fragment that sent him :)
        final Intent resIntent = new Intent(TrumahEngine.this, AliyotFragment.class);
        mutav.setText(intent.getStringExtra("mutav"));
        zuzim.setText(String.valueOf(intent.getFloatExtra("zuzim",0)));

        finishAndReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sendLaterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:

                                    ////HERE COMES CODE OF CHANGING THE PAYMENT TO V IN FIREBASE
                                    Order pending = (Order) intent.getParcelableExtra("pendingOrder");
                                    pending.setPaid(false);
                                    Log.d("TrumahEngine", "isParcelOkay?: "+pending.getUser()+" "+pending.getDesc());
                                    if(fbh.pushOrderToDB(pending))
                                        Toast.makeText(v.getContext(),"הרישום הצליח , תודה רבה!",Toast.LENGTH_SHORT).show();


                                    finishAndReturn.setVisibility(View.VISIBLE);
                                    sendPaymentBtn.setVisibility(View.INVISIBLE);

                                    break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                setResult(RESULT_CANCELED,resIntent);
                                finishAndReturn.setVisibility(View.VISIBLE);
                                Toast.makeText(v.getContext(),"לא בוצע רישום של הזמנה.",Toast.LENGTH_SHORT).show();

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("תרצה לרשום את ההזמנה ולשלם מאוחר יותר?").setPositiveButton("כן", dialogClickListener)
                        .setNegativeButton("לא", dialogClickListener).show();

            }
        });
        sendPaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                Log.d("TAG", "CARDDDD: "+cardNumber.getText().toString()+" exp: "+expiryDate.getText().toString()+" cvv: "+cvv.getText().toString()+" cvvlen: "+cvv.getText().toString().length());
                                if(cardNumber.getText().toString().length() < 8)
                                {
                                    Toast.makeText(v.getContext(), "מספר הכרטיס אינו תקין, וודא לפחות 8 ספרות", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                                if(expiryDate.getText().toString().length()>2){
                                if((expiryDate.getText().toString().length()!=5 && expiryDate.getText().toString().charAt(2) != '/') ) {
                                    Toast.makeText(v.getContext(), "תאריך התפוגה אינו תקין , וודא שהוא מצורת MM/YY", Toast.LENGTH_SHORT).show();
                                    break;
                                }}
                                else {Toast.makeText(v.getContext(), "תאריך התפוגה קצר מדי , וודא שהוא מצורת MM/YY", Toast.LENGTH_SHORT).show();
                                    break;}

                                if(cvv.getText().toString().length() != 3 && cvv.getText().toString().length() != 4 )  //4 for AMEX haha
                                {
                                    Toast.makeText(v.getContext(), "ספרות הבטיחות אינן תקינות וודא שמכילות 3 או 4 ספרות", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                                int mm = Integer.parseInt(expiryDate.getText().toString().subSequence(0,1).toString());
                                int yy = Integer.parseInt(expiryDate.getText().toString().subSequence(3,4).toString());
                                int cvvNum = Integer.parseInt(cvv.getText().toString());
                                boolean SuccessOnPayment=payNow(cardNumber.getText().toString(),mm,yy,cvvNum); //actual call to pay service.

                                if(SuccessOnPayment){
                                    ////HERE COMES CODE OF CHANGING THE PAYMENT TO V IN FIREBASE TODOTODOBOMMMM
                                    Order pending = (Order) intent.getParcelableExtra("pendingOrder");
                                    Log.d("TrumahEngine", "isParcelOkay?: "+pending.getUser()+" "+pending.getDesc());
                                    if(fbh.pushOrderToDB(pending))
                                        Toast.makeText(v.getContext(),"התשלום הצליח , תודה רבה!",Toast.LENGTH_SHORT).show();


                                    finishAndReturn.setVisibility(View.VISIBLE);
                                    sendPaymentBtn.setVisibility(View.INVISIBLE);
                                }
                                else {
                                    resIntent.putExtra("result",false);
                                    trumahInstance.putExtra("result",false);
                                    setResult(RESULT_CANCELED,resIntent);
                                    finishAndReturn.setVisibility(View.VISIBLE);
                                    Toast.makeText(v.getContext(),"התשלום נכשל בדרך.. לא חויבת",Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //resIntent.putExtra("result",false);
                                //trumahInstance.putExtra("result",false);
                                setResult(RESULT_CANCELED,resIntent);
                                finishAndReturn.setVisibility(View.VISIBLE);
                                Toast.makeText(v.getContext(),"התשלום לא בוצע , ולא חויבת",Toast.LENGTH_SHORT).show();

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("בטוח שברצונך לבצע תשלום?").setPositiveButton("כן", dialogClickListener)
                        .setNegativeButton("לא", dialogClickListener).show();
            }
        });
    }
    public boolean payNow(String cardNumber,int ddExp,int yyExp,int cvv){
        //DEMO ALGO OF PAYMENT VERIFICATION , NO REAL MONEY WILL BE CHARGED TZADIK!
        return true;
    }
}