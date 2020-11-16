package com.danik.bitkneset.ui.login;

public class AdvancedThread extends Thread {
    String username,password;
    int accessLevel;

public AdvancedThread(String username,String password)
    {
    super();
    this.username = username;
    this.password = password;

    }

    public void run(String username,String password) {
        super.run();
    }
}
