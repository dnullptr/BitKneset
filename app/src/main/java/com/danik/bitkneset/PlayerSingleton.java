package com.danik.bitkneset;
import android.media.MediaPlayer;


public class PlayerSingleton extends MediaPlayer { //this is a try to kill older instance BUT I give another instance before.
    //this eliminates double audio playing while thread lost in old fragment memory.
    public static int pingPongSwitch = 0;

    public static PlayerSingleton ping = null;
    public static PlayerSingleton pong = null;
    private PlayerSingleton()
    {
        super();
    }
    public static PlayerSingleton getInstance()
    {
        pingPongSwitch = (pingPongSwitch == 1)? 2 :1;
        if(ping == null && pingPongSwitch ==1) {
            ping = new PlayerSingleton();
            if (pong != null)
                pong.stop();
            return ping;
        }
        else if(pingPongSwitch == 2)
            ping.stop();
            pong = new PlayerSingleton();
            return pong;


        }


    }

