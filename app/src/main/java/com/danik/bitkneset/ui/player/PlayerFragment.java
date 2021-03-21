package com.danik.bitkneset.ui.player;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.danik.bitkneset.FirebasePlayer;
import com.danik.bitkneset.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class PlayerFragment extends Fragment {

    private PlayerViewModel playerViewModel;
    public volatile MediaPlayer mediaPlayer; //the engine of media itself
    Button playBtn, stopBtn;
    ; //the ref to play button
    TextView songName; //the ref to text of song name
    TextView timeText; //the ref to text of time
    SeekBar seekBar; //ref to seek bar in ui
    Handler handler; //work with time based events like my great player.
    Runnable runnable; // thread for any async reason.
    Thread playThrd, playSeekThrd, stopThrd; //play threads to be used explicitly
    String HalahaTitle;
    ProgressBar progressBar;

    public View rootFinal;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        playerViewModel =
                ViewModelProviders.of(this).get(PlayerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_player, container, false);
        rootFinal = root;
        playBtn = root.findViewById(R.id.buttonPlay);
        stopBtn = root.findViewById(R.id.buttonStop);
        seekBar = root.findViewById(R.id.seekBar);
        songName = root.findViewById(R.id.songName);
        progressBar = root.findViewById(R.id.progressBarPlayer);

        //Halaha Yomit Section
        mediaPlayer = new MediaPlayer();
        handler = new Handler();
        HTMLBringMeMedia htmlBringMeMedia = new HTMLBringMeMedia();
        htmlBringMeMedia.execute();


        /////////////////////////SEEK BAR ///////////////////////////////////////

        root.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    mediaPlayer.stop();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) mediaPlayer.seekTo(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                Log.d("TAG", "Play: IM HEREEEE");
                playBtn = rootFinal.findViewById(R.id.buttonPlay);
                songName = rootFinal.findViewById(R.id.songName);
                timeText = rootFinal.findViewById(R.id.songName2);



                        if (playBtn.getText().equals("Play")) {
                            seekBar.setMax(mediaPlayer.getDuration());
                            mediaPlayer.start();
                            PlayingEvents();
                            mediaIconSet(rootFinal, 1);
                            //.setVisibility(View.VISIBLE);
                            songName.setText(HalahaTitle);


                        } else {
                            mediaPlayer.pause();
                            mediaIconSet(rootFinal, 2);
                            //eqAnim.setVisibility(View.INVISIBLE);
                        }
                        if (mediaPlayer.isPlaying()) {
                            playBtn.setText("Pause");
                            playBtn.setBackground(getResources().getDrawable(R.drawable.pause128, null));
                        } else {
                            playBtn.setText("Play");
                            playBtn.setBackground(getResources().getDrawable(R.drawable.play128, null));
                        }



                playSeekThrd = new Thread() {

                    public void run() {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition()); //set seek val
                        handler.postDelayed(this, 1000);
                    }

                };
                playSeekThrd.start();
            }
        });


        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
                playBtn.setText("Play");
                playBtn.setBackground(getResources().getDrawable(R.drawable.play128,null)); //new LOGO changes as well.
                stopThrd=new Thread(){public void run(){

                    timeText.setText("Stopped");
                    mediaIconSet(rootFinal,0);

                }};
                handler.postDelayed(stopThrd,1000);
            }
        });


        return root;
    }

    /////// custom funcs ///////
    public void mediaIconSet(View root, int sel) { //i decided that 0-stop 1-play 2-pause
        ImageView playIcon = root.findViewById(R.id.playIcon);
        ImageView stopIcon = root.findViewById(R.id.stopIcon);
        ImageView pauseIcon = root.findViewById(R.id.pauseIcon);
        switch (sel) {
            case 0:
                playIcon.setVisibility(View.INVISIBLE);
                pauseIcon.setVisibility(View.INVISIBLE);
                stopIcon.setVisibility(View.VISIBLE);
                break;

            case 1:
                playIcon.setVisibility(View.VISIBLE);
                pauseIcon.setVisibility(View.INVISIBLE);
                stopIcon.setVisibility(View.INVISIBLE);
                break;

            case 2:
                playIcon.setVisibility(View.INVISIBLE);
                pauseIcon.setVisibility(View.VISIBLE);
                stopIcon.setVisibility(View.INVISIBLE);
                break;
        }
    }


    public CharSequence nicelyFormat(int timeMs) {
        String TAG = "Checking";
        CharSequence cs = "";
        Log.d(TAG, "nicelyFormat: " + timeMs / 1000);
        int timeInSec = timeMs / 1000;
        int Min = timeInSec / 60;
        int Sec = timeInSec - Min * 60;

        return "" + String.valueOf(Min) + ":" + (Sec < 10 ? "0" + String.valueOf(Sec) : String.valueOf(Sec));
    }

    public void PlayingEvents() {
        String TAG = "Debug";
        Log.d(TAG, "PlayingEvents: HERE ");


        if (mediaPlayer.isPlaying()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    timeText.setText(nicelyFormat(mediaPlayer.getCurrentPosition())); //set time val

                    PlayingEvents();
                }

            };

        } else {
            runnable = new Runnable() {
                @Override
                public void run() {

                    PlayingEvents();
                }

            };
        }
        handler.postDelayed(runnable, 1000);


    }

    private class HTMLBringMeMedia extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.INVISIBLE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(mediaPlayer != null)
                if(mediaPlayer.isPlaying())
                    mediaPlayer.release();
            String url = "https://www.hidabroot.org/%D7%9B%D7%A0%D7%99%D7%A1%D7%AA-%D7%94%D7%A9%D7%91%D7%AA";
            try {


                String urlHalaha = FirebasePlayer.halachaSiteUrl;
                Document doc1 = Jsoup.connect(urlHalaha).get();
                Elements data1 = doc1.select("#ctl00_ContentPlaceHolderMain_div_Mp3_2 > a");
                HalahaTitle = doc1.select("#ctl00_ContentPlaceHolderMain_lblHalachaTitle").get(0).ownText();
               String HalahaYomit = data1.get(0).outerHtml();
               String uriToMp3Yomit = ""+FirebasePlayer.halachaMP3Url; //replacing will make the site think i want to download , and provide mp3!!
                Log.d(TAG, "doInBackground: "+uriToMp3Yomit);


                mediaPlayer.setDataSource(uriToMp3Yomit);
                mediaPlayer.prepare();


            } catch (IOException e) {
                Log.d(TAG, "doInBackground: " + e);
                ;
            }
            return null;
        }
    }
}

