package com.danik.bitkneset.ui.login;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.os.Build;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.danik.bitkneset.FirebaseHelper;
import com.danik.bitkneset.R;
import com.danik.bitkneset.User;
import com.danik.bitkneset.data.LoginDataSource;
import com.danik.bitkneset.data.LoginRepository;
import com.danik.bitkneset.data.Result;
import com.danik.bitkneset.data.model.LoggedInUser;
import com.danik.bitkneset.ui.login.LoginFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class LoginViewModel extends ViewModel {
    public static LoginRepository loginRepository=new LoginRepository(new LoginDataSource());
    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private MutableLiveData<String> mText= new MutableLiveData<>();
    public FirebaseHelper fbh=new FirebaseHelper("Users");
    public static User toCheck;
    public static AdvancedThread fireLoginThread;
    public static int accessToReturn=0;
    public static FirebaseAuth mAuth;
    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public int loginUser(final User fromInput) {

        FirebaseHelper.myRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N) //check if can do something
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Map<String,String> fromDB= (Map)data.getValue();
                    Map<String,Long> fromDB1= (Map)data.getValue();
                    /*for(String key : fromDB1.keySet())
                        Log.d(TAG, "stam log print key is:"+key+" val is: "+fromDB1.get(key));*/

                    User temp=new User(fromDB.get("username"),fromDB.get("password"), Math.toIntExact(fromDB1.get("accessLevel")));
                    if(fromInput.compareTo(temp)){ //if it has the same credentials then Logged in!
                        LoginFragment.user=temp;
                        accessToReturn=1;
                        if(accessToReturn==1)
                        Log.d(TAG, "onDataChange: looks like we're in and identified with "+accessToReturn);}
                        if(accessToReturn==0) Log.d(TAG, "onDataChange: big no no on login, with "+accessToReturn);


                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        return accessToReturn;
    }


    //till here i impleneted try of fbh inbranched.

    public void login(String username, String password) throws InterruptedException {
        // can be launched in a separate asynchronous job
        //Result<LoggedInUser> result = loginRepository.login(username, password);
        if (LoginFragment.loginOrRegister.isChecked()) //register mode
        {
            if (fbh.registerUser(new User(username, password, 1))) {
                Toast.makeText(null, "הרשמה הצליחה , מעכשיו אתה חבר", Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(null, "משהו קרה בהרשמה..והיא לא הצליחה", Toast.LENGTH_LONG).show();

        } else //login mode
        {
                    toCheck = new User(username, password, 1);

                    fireLoginThread=new AdvancedThread(username,password){
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        public void run()
                        {
                           accessToReturn= FirebaseHelper.loginUser(toCheck);
                        } //it's up here
                    };
                    fireLoginThread.run(); 

        }




        fireLoginThread.join(); //throws interrupted reason
        Log.d(TAG, "login: inb4 if accesstoreturn is :"+accessToReturn);
        if (accessToReturn==1 || accessToReturn==2) {

            loginResult.setValue(new LoginResult(new LoggedInUserView(LoginFragment.user.getFullName())));

           Result<LoggedInUser> result = loginRepository.login(username, password);
            Log.d(TAG, "login: repo see whats all the nulls "+result.toString());
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // check strength of pass
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 6;
    }

    public LiveData<String> getText() {
        return mText;
    }
}
