package com.danik.bitkneset.ui.login;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.danik.bitkneset.FirebaseHelper;
import com.danik.bitkneset.R;
import com.danik.bitkneset.User;
import com.danik.bitkneset.ui.home.HomeFragment;


import static android.content.ContentValues.TAG;

public class LoginFragment extends Fragment {
    private LoginViewModel loginViewModel;
    public static User user;
    public static CheckBox loginOrRegister;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        loginViewModel =
                ViewModelProviders.of(this).get(LoginViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_login, container, false);
        final TextView splash = root.findViewById(R.id.loginSplash);
        final TextView textView = root.findViewById(R.id.login);
        final EditText usernameEditText = root.findViewById(R.id.username);
        final EditText passwordEditText = root.findViewById(R.id.password);
        final Button loginButton = root.findViewById(R.id.login);
        final ProgressBar loadingProgressBar = root.findViewById(R.id.loading);
        loginOrRegister = root.findViewById(R.id.checkBox);
        final EditText fullNameText = root.findViewById(R.id.fullNameText);
        final TextView connectedWelcome = root.findViewById(R.id.connectedWelcome);
        Button logout=root.findViewById(R.id.logout);
        //SET TO VISIBLE ALL , logout INVISIBLE
        textView.setVisibility(View.VISIBLE);
        usernameEditText.setVisibility(View.VISIBLE);
        passwordEditText.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        logout.setVisibility(View.INVISIBLE);
        splash.setVisibility(View.VISIBLE);
        connectedWelcome.setVisibility(View.INVISIBLE);

        if (user == null) {

            loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), new Observer<LoginFormState>() {
                @Override
                public void onChanged(@Nullable LoginFormState loginFormState) {
                    if (loginFormState == null) {
                        return;
                    }
                    loginButton.setEnabled(loginFormState.isDataValid());
                    if (loginFormState.getUsernameError() != null) {
                        usernameEditText.setError(getString(loginFormState.getUsernameError()));
                    }
                    if (loginFormState.getPasswordError() != null) {
                        passwordEditText.setError(getString(loginFormState.getPasswordError()));
                    }
                }
            });

            loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), new Observer<LoginResult>() {
                @Override
                public void onChanged(@Nullable LoginResult loginResult) {
                    if (loginResult == null) {
                        return;
                    }
                    loadingProgressBar.setVisibility(View.GONE);
                    if (loginResult.getError() != null) { //if error is not null it means we used the error property instead of success property since they're both nullables.
                        Toast.makeText(getContext(), "סיסמה שגויה או לא רשום במערכת!", Toast.LENGTH_SHORT).show();
                    }
                    if (loginResult.getSuccess() != null) { //if success property is used , we succeeded and BTW error property is null instead.
                        Toast.makeText(getContext(), "התחברת בהצלחה כמשתמש רגיל!", Toast.LENGTH_SHORT).show();
                        getFragmentManager().beginTransaction().detach(LoginFragment.this).attach(LoginFragment.this).commit();
                    }


                    //Complete and destroy login activity once successful

                }
            });

            TextWatcher afterTextChangedListener = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // ignore
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // ignore
                }

                @Override
                public void afterTextChanged(Editable s) {
                    loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
            };
            usernameEditText.addTextChangedListener(afterTextChangedListener);
            passwordEditText.addTextChangedListener(afterTextChangedListener);
            passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        try {
                            loginViewModel.login(usernameEditText.getText().toString(),
                                    passwordEditText.getText().toString());
                            loadingProgressBar.setVisibility(View.VISIBLE); //show that we're doing stuff
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }
            });

            loginOrRegister.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { //if new , let him type his fullname.
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        fullNameText.setVisibility(View.VISIBLE);
                    else fullNameText.setVisibility(View.GONE);
                }
            });

            new Thread() {
                public void run() {
                    loginButton.setOnClickListener(new View.OnClickListener() {  //Login Or Register part of code
                        @Override
                        public void onClick(View v) {
                            FirebaseHelper fbh = new FirebaseHelper("Users");
                            loadingProgressBar.setVisibility(View.VISIBLE); //show that we are thinking :)
                            try {
                                loginViewModel.login(usernameEditText.getText().toString(),
                                        passwordEditText.getText().toString()); //PO laavod
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            }.run();

        }//endif not connected
        else //if connected already show logout
        {
            //SET TO VISIBLE ALL
            textView.setVisibility(View.INVISIBLE);
            usernameEditText.setVisibility(View.INVISIBLE);
            passwordEditText.setVisibility(View.INVISIBLE);
            loginButton.setVisibility(View.INVISIBLE);
            loadingProgressBar.setVisibility(View.INVISIBLE);
            loginOrRegister.setVisibility(View.INVISIBLE);
            fullNameText.setVisibility(View.INVISIBLE);
            splash.setVisibility(View.INVISIBLE);
            connectedWelcome.setVisibility(View.VISIBLE);
            logout.setVisibility(View.VISIBLE);
            logout.setEnabled(true);
            connectedWelcome.setText(connectedWelcome.getText()+LoginFragment.user.getFullName()+"!"); //welcome YOURNAME!
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    LoginFragment.user=null;
                                    Toast.makeText(getContext(),"התנתקת בהצלחה!",Toast.LENGTH_SHORT).show();
                                    getFragmentManager().beginTransaction().detach(LoginFragment.this).attach(LoginFragment.this).commit(); //refresh fragment and re-commit
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("אתה בטוח שברצונך להתנתק?").setPositiveButton("כן", dialogClickListener)
                            .setNegativeButton("אולי אשאר..", dialogClickListener).show();

                }
            });
        }

            loginViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(@Nullable String s) {
                    textView.setText(s);
                }
            });
            return root;
        }

}
