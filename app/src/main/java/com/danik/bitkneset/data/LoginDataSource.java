package com.danik.bitkneset.data;

import android.util.Log;

import com.danik.bitkneset.data.model.LoggedInUser;
import com.danik.bitkneset.ui.login.LoginFragment;
import com.danik.bitkneset.ui.login.LoginViewModel;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            LoggedInUser loggedInUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            LoginFragment.user.getFullName());
            return new Result.Success<>(loggedInUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        LoginViewModel.accessToReturn=0;
        Log.d("Logout DS", "logout: logged out");
    }
}
