package com.danik.bitkneset.ui.aliyot;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danik.bitkneset.FirebaseHelper;
import com.danik.bitkneset.FirebaseRetriever;
import com.danik.bitkneset.Order;
import com.danik.bitkneset.R;
import com.danik.bitkneset.RVAdapter;
import com.danik.bitkneset.ui.login.LoginFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class AliyotFragment extends Fragment {

    private AliyotViewModel aliyotViewModel;
    Button submitBtn;
    Spinner spinner;
    String selectedType; //spinner and a type to hold its selection
    Switch sw;
    EditText desc, amount, date;
    Order[] aliyot;
    RecyclerView rv;
    RecyclerView.Adapter rvAdapter;
    RecyclerView.LayoutManager layoutManager;
    ProgressBar progressBarAliyot;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        aliyotViewModel =
                ViewModelProviders.of(this).get(AliyotViewModel.class);
        View root = inflater.inflate(R.layout.fragment_aliyot, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        aliyotViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        desc = root.findViewById(R.id.desc);
        amount = root.findViewById(R.id.amount);
        date = root.findViewById(R.id.date);
        sw = root.findViewById(R.id.switch1);
        spinner = root.findViewById(R.id.spinner);
        submitBtn = root.findViewById(R.id.submitOrder);
        progressBarAliyot = root.findViewById(R.id.progressBarAliyot);
        setHasOptionsMenu(true);

        final FirebaseHelper fbh = new FirebaseHelper("Orders"); //get fbh instance of orders or "aliyot" as i like to call them :)
        if (LoginFragment.user != null)
            if (LoginFragment.user.getAccessLevel() == 1) //if admin -> give the switch as an option
                sw.setVisibility(View.VISIBLE);
            else sw.setVisibility(View.INVISIBLE);


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

        //////////SWITCH ADMIN OR NORMAL////////////////////
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked) //download orders of all
                {
                    sw.setText(sw.getTextOn());
                } else //download my orders
                {
                    sw.setText(sw.getTextOff());
                }
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
                    Toast.makeText(getContext(), "תיאור ההזמנה ריק", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (amount.getText().length() < 1) {
                    Toast.makeText(getContext(), "סכום ההזמנה אינו תקין", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (date.getText().length() < 1) {
                    Toast.makeText(getContext(), "תאריך אינו תקין", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedType.length() < 1) {
                    Toast.makeText(getContext(), "לא בחרת סוג הזמנה, אנא בחר אותו מהרשימה כעת כדי שנוכל להתקדם", Toast.LENGTH_SHORT).show();
                    return;
                }

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //SimpleDateFormat dateFormatprev = new SimpleDateFormat("dd/mm/yyyy");
                                // java.util.Date d = dateFormatprev.parse(date.getText().toString());

                                Order order = new Order(LoginFragment.user.getFullName(), selectedType, desc.getText().toString(), Float.parseFloat(amount.getText().toString()), false, date.getText().toString());
                                fbh.pushOrderToDB(order); // push actual order as object to DB!
                                Toast.makeText(getContext(), "ההזמנה בוצעה , אשריך!", Toast.LENGTH_LONG).show();


                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                Toast.makeText(getContext(), "לא בוצעה ההזמנה , טרם חוייבת (אך טרם זכית במצווה :D)", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("אתה בטוח שברצונך לשלם? תועבר לעמוד האשראי").setPositiveButton("כן", dialogClickListener)
                        .setNegativeButton("רק רגע..", dialogClickListener).show();

            }
        });

        spinner.setAdapter(new ArrayAdapter<String>(root.getContext(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.order_types)));

        //FirebaseRetriever fbr=new FirebaseRetriever("Orders");

        progressBarAliyot.setVisibility(View.VISIBLE);
        progressBarAliyot.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
        rv = root.findViewById(R.id.rvAliyot);
        List<Order> placeboList = new ArrayList<>();
        placeboList.add(new Order("Username", "Truma", "STAMSTAM", (float) 45.5, false, "20/07/1990"));
        final RVAdapter rvAdapter;
        if (fbh.thisUser_fbr != null)
            rvAdapter = new RVAdapter(getContext(), fbh.thisUser_fbr.getFullOrderList());
        else
            rvAdapter = new RVAdapter(getContext(), placeboList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        rv.setLayoutManager(layoutManager);
        rv.setHasFixedSize(true);

        rv.setAdapter(rvAdapter);
        progressBarAliyot.setVisibility(View.INVISIBLE);
        progressBarAliyot.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));

        ///////////FROM HERE I DO FILTER SEARCH , WILL TRY TO MAKE IT GENERIC AS POSSIBLE ////////
        SearchView searchView = root.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                rvAdapter.getFilter().filter(newText);
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

    /*
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
                ((RVAdapter) rvAdapter).getFilter().filter(newText);
                return false;
            }
        });
    }
    */
}

