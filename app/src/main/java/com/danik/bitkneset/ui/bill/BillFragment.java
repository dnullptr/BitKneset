package com.danik.bitkneset.ui.bill;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danik.bitkneset.Bill;
import com.danik.bitkneset.FirebaseBiller;
import com.danik.bitkneset.FirebaseHelper;
import com.danik.bitkneset.R;
import com.danik.bitkneset.RVAdapter;
import com.danik.bitkneset.RVBillAdapter;
import com.danik.bitkneset.ui.login.LoginFragment;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class BillFragment extends Fragment {

    private BillViewModel billViewModel;
    Button submitBtn;
    Spinner spinner;
    String selectedType; //spinner and a type to hold its selection like i did with Aliyot
    Switch sw;
    EditText desc, amount, date;
    RecyclerView rv;
    RecyclerView.Adapter rvAdapter;
    RecyclerView.LayoutManager layoutManager;
    ProgressBar progressBarBill;
    TextView income,outcome;
    final FirebaseBiller fbl = new FirebaseBiller("Bills"); //get fbl instance of bills
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        billViewModel =
                ViewModelProviders.of(this).get(BillViewModel.class);
        View root = inflater.inflate(R.layout.fragment_bill, container, false);
        if(LoginFragment.user == null || (LoginFragment.user != null && LoginFragment.user.getAccessLevel() == 1)) //if normal user, inflate without panel
            root = inflater.inflate(R.layout.fragment_bill_dupe, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        billViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        desc = root.findViewById(R.id.descBill);
        amount = root.findViewById(R.id.amountBill);
        date = root.findViewById(R.id.dateBill);
        sw = root.findViewById(R.id.switchBill);
        spinner = root.findViewById(R.id.spinnerBill);
        submitBtn = root.findViewById(R.id.submitBill);
        progressBarBill = root.findViewById(R.id.progressBarBill);
        rv = root.findViewById(R.id.rvBill);
        income = root.findViewById(R.id.balIn);
        outcome = root.findViewById(R.id.balOut);
        setHasOptionsMenu(true);


        if (LoginFragment.user != null){
            if (LoginFragment.user.getAccessLevel() == 2){ //if admin -> give the switch as an option
                sw.setVisibility(View.VISIBLE);
                desc.setVisibility(View.VISIBLE);
                amount.setVisibility(View.VISIBLE);
                date.setVisibility(View.VISIBLE);
                sw.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);
                submitBtn.setVisibility(View.VISIBLE);
            }
            else
            {
                sw.setVisibility(View.INVISIBLE);
                sw.setVisibility(View.GONE);
                desc.setVisibility(View.GONE);
                amount.setVisibility(View.GONE);
                date.setVisibility(View.GONE);
                sw.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                submitBtn.setVisibility(View.GONE);
                //ViewGroup.LayoutParams params=rv.getLayoutParams();
                //params.height+=100;
                // rv.setLayoutParams(params);
                ((ConstraintLayout)root.findViewById(R.id.linearLayout2)).setVisibility(View.GONE);

            }
        }
        else //must me 'else' here to invoke case of null along with not null but not admin
        {
            sw.setVisibility(View.INVISIBLE);
            sw.setVisibility(View.GONE);
            desc.setVisibility(View.GONE);
            amount.setVisibility(View.GONE);
            date.setVisibility(View.GONE);
            sw.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            submitBtn.setVisibility(View.GONE);
            ((ConstraintLayout)root.findViewById(R.id.linearLayout2)).setVisibility(View.GONE);
        }


        /////////////////////SPINNER/////////////////////

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //////////SUBMIT ORDER BUTTON////////////////////
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginFragment.user == null) {
                    Toast.makeText(getContext(), "אינך מחובר , אנא התחבר בלשונית ההתחברות", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (desc.getText().length() < 1) {
                    Toast.makeText(getContext(), "תיאור החשבון ריק", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (amount.getText().length() < 1) {
                    Toast.makeText(getContext(), "סכום החשבון אינו תקין", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (date.getText().length() < 1) {
                    Toast.makeText(getContext(), "תאריך אינו תקין", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedType.length() < 1) {
                    Toast.makeText(getContext(), "לא בחרת סוג חשבון לתיעוד, אנא בחר אותו מהרשימה כעת כדי שנוכל להתקדם", Toast.LENGTH_SHORT).show();
                    return;
                }

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                Bill toPush = new Bill(selectedType,desc.getText().toString(),amount.getText().toString(),true,date.getText().toString());
                                fbl.pushBillToDB(toPush);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                Toast.makeText(getContext(), "לבקשתך, לא בוצע תיעוד החשבון עדיין.", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("אתה בטוח שברצונך לתעד חשבון זה?").setPositiveButton("כן", dialogClickListener)
                        .setNegativeButton("רק רגע..", dialogClickListener).show();

            }
        });

        spinner.setAdapter(new ArrayAdapter<String>(root.getContext(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.bill_types)));


        progressBarBill.setVisibility(View.VISIBLE);
        progressBarBill.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));

        final List<Bill> placeboList = new ArrayList<>();
        //placeboList.add(new Bill("חשמל","תשלום חוב עבור סוכות","550",true,"15/02/2021"));
        final RVBillAdapter[] rvAdapter = new RVBillAdapter[1];


        rv.setAdapter(rvAdapter[0]);
        progressBarBill.setVisibility(View.INVISIBLE);
        progressBarBill.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));

        Handler h = new Handler(); //using handler to choreographically fix time events (sorry for the long adverb :D)
        final View finalRoot = root;
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                outcome.setText(String.valueOf(FirebaseBiller.SumAllBills));
                income.setText(String.valueOf(FirebaseHelper.thisUser_fbr.SumAllOrders));
                if (LoginFragment.user != null) {
                    rvAdapter[0] = new RVBillAdapter(getContext(), fbl.pullBillsFromDB());  //FULL LIST FOR ADMIN
                }
                else
                    rvAdapter[0] = new RVBillAdapter(getContext(), placeboList);

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(finalRoot.getContext());
                rv.setLayoutManager(layoutManager);
                rv.setHasFixedSize(true);
                rv.setAdapter(rvAdapter[0]);
                Log.d(TAG, "run: Handler");

            }
        },1000);


        //////////SWITCH ADMIN OR NORMAL////////////////////
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked) //download orders of all
                {
                    rvAdapter[0].getFilter().filter("");
                    sw.setText(sw.getTextOn());
                } else //download my orders
                {
                    rvAdapter[0].getFilter().filter(LoginFragment.user.getFullName());
                    sw.setText(sw.getTextOff());
                }
            }
        });

        ///////////FROM HERE I DO FILTER SEARCH , WILL TRY TO MAKE IT GENERIC AS POSSIBLE ////////
        SearchView searchView = root.findViewById(R.id.searchViewBill);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                rvAdapter[0].getFilter().filter(newText);

                return false;
            }
        });
        return root;
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search,menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(rvAdapter != null)
                    ((RVAdapter) rvAdapter).getFilter().filter(newText);
                return false;
            }
        });
    }
}

