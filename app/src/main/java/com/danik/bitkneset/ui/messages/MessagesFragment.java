package com.danik.bitkneset.ui.messages;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.danik.bitkneset.FirebaseMessager;
import com.danik.bitkneset.Message;
import com.danik.bitkneset.R;
import com.danik.bitkneset.RVMSGAdapter;
import com.danik.bitkneset.ui.login.LoginFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessagesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private MessagesViewModel messagesViewModel;
    public ImageButton sendBtn;
    public EditText msg, date;
    TextView who;
    public RecyclerView msgrv;
    public RecyclerView.Adapter msgrvAdapter;
    Switch swOnOff;
    public ProgressBar progressBarMessages;
    public FirebaseMessager fbm;
    public RVMSGAdapter rvmsgAdapter;
    public FloatingActionButton fabSend;
    public TextView newMsg;
    public static Switch deleteMode;
    public FBBringMeMessages fbBringMeMessages = new FBBringMeMessages();
    public SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        messagesViewModel =
                ViewModelProviders.of(this).get(MessagesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_messages, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        messagesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        newMsg = root.findViewById(R.id.newMsg);
        msg = root.findViewById(R.id.messageEditText);
        sendBtn = root.findViewById(R.id.sendMsgBtn);
        msgrv = root.findViewById(R.id.rvMsgs);
        fabSend = root.findViewById(R.id.fabSend);
        deleteMode = root.findViewById(R.id.deletionSwitch);
        progressBarMessages = root.findViewById(R.id.progressBarMessages);
        swipeRefreshLayout = root.findViewById(R.id.swipeRefresh);
        setHasOptionsMenu(true);

        fbm = new FirebaseMessager("Messages");

        //////////SWIPE LISTENER AFTER IMPLEMENTING MYSELF AS ONE/////////
        swipeRefreshLayout.setOnRefreshListener(this);

        //////////FAB SEND FLOATING BUTTON///////////////
        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fabSend.isExpanded()){
                    newMsg.setVisibility(View.VISIBLE);
                    sendBtn.setVisibility(View.VISIBLE);
                    msg.setVisibility(View.VISIBLE);
                    fabSend.setImageDrawable(getResources().getDrawable(R.drawable.minusicon));
                    fabSend.setExpanded(true);
                    msgrv.setLayoutParams(new ConstraintLayout.LayoutParams(RecyclerView.LayoutParams.FILL_PARENT, msgrv.getHeight()-(msgrv.getHeight()/70)));

                }
                else {
                    newMsg.setVisibility(View.GONE);
                    sendBtn.setVisibility(View.GONE);
                    msg.setVisibility(View.GONE);
                    fabSend.setImageDrawable(getResources().getDrawable(R.drawable.plusicon));
                    fabSend.setExpanded(false);
                    msgrv.setLayoutParams(new ConstraintLayout.LayoutParams(RecyclerView.LayoutParams.FILL_PARENT, msgrv.getHeight()+(msgrv.getHeight()/70)));
                }
            }
        });

        ///////////DELETION MODE SWITCH/////////////////////
        if(LoginFragment.user != null)
        {
            if(LoginFragment.user.getAccessLevel() == 2)
                deleteMode.setVisibility(View.VISIBLE);
            else deleteMode.setVisibility(View.INVISIBLE);
        }
        deleteMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        //////////SUBMIT MESSAGE BUTTON////////////////////
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginFragment.user == null) {
                    Toast.makeText(getContext(), "אינך מחובר , אנא התחבר בלשונית ההתחברות", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (msg.getText().length() < 1) {
                    Toast.makeText(getContext(), "גוף ההודעה ריק , תן לנו איזה חידוש צדיק..", Toast.LENGTH_SHORT).show();
                    return;
                }


                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //SimpleDateFormat dateFormatprev = new SimpleDateFormat("dd/mm/yyyy");
                                // java.util.Date d = dateFormatprev.parse(date.getText().toString());

                                Message message = new Message(LoginFragment.user.getFullName(),msg.getText().toString(),new Date().toString()); // build current message as class obj
                                fbm.pushMessageToDB(message);
                                Snackbar.make(getView(), "ההודעה נשלחה!", Snackbar.LENGTH_LONG).show();
                                rvmsgAdapter.notifyDataSetChanged();
                                msgrv.removeAllViews();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                Toast.makeText(getContext(), "עוד לא שלחנו, אפשר להוסיף או לשנות תוכן", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext()); //comes before onCLick dialog above,
                builder.setMessage("לשלוח את ההודעה?").setPositiveButton("כן", dialogClickListener)
                        .setNegativeButton("רק רגע..", dialogClickListener).show();

            }
        });
        fbBringMeMessages.execute();

        return root;
    }

    @Override
    public void onRefresh() { //as i implement SwipeRefresh i can call from wherever i want
    rvmsgAdapter.notifyDataSetChanged();
    swipeRefreshLayout.setRefreshing(false);
    }


    /////////////PRIVATE ASYNC TASK CLASS FOR ASYNC MESSAGER/////////////
    private class FBBringMeMessages extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarMessages.setVisibility(View.VISIBLE);
            progressBarMessages.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            msgrv.setLayoutManager(layoutManager);
            msgrv.setHasFixedSize(true);

            msgrv.setAdapter(rvmsgAdapter);
            progressBarMessages.setVisibility(View.INVISIBLE);
            progressBarMessages.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
            Looper.loop();
            rvmsgAdapter.notifyDataSetChanged();
            msgrv.removeAllViews();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(Looper.myLooper() == null)
                Looper.prepare();
            List<Message> placeboList = new ArrayList<>();
            placeboList.add(new Message("maazab","מה הולך","26/11/2020"));


            if (fbm != null && LoginFragment.user != null)
                rvmsgAdapter = new RVMSGAdapter(getContext(), fbm.pullMessagesFromDB());  //FULL LIST FOR ADMIN
            else
                rvmsgAdapter = new RVMSGAdapter(getContext(), placeboList);
            return null;
        }
    }
}
