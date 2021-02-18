package com.danik.bitkneset.ui.login;

public class AdvancedThread extends Thread {
    String username,password;

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
