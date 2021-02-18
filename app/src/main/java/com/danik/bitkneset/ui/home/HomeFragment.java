package com.danik.bitkneset.ui.home;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.danik.bitkneset.MainActivity;
import com.danik.bitkneset.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {
    public String AsyncKnisa,AsyncYetsia;
    private HomeViewModel homeViewModel;
    public ProgressBar progressBarHome;
    public TextView KnisaVal,YetsiaVal;
    public Button chkShabbatBtn;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        KnisaVal = root.findViewById(R.id.knisaVal);
        YetsiaVal = root.findViewById(R.id.yetsiaVal);
        progressBarHome = root.findViewById(R.id.progressBarHome);
        chkShabbatBtn = root.findViewById(R.id.chkShabatBtn);
        final HTMLBringMeInfo htmlBringMeInfo = new HTMLBringMeInfo();

        chkShabbatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KnisaVal.setVisibility(View.VISIBLE);
                YetsiaVal.setVisibility(View.VISIBLE);
                htmlBringMeInfo.execute();
                chkShabbatBtn.setVisibility(View.INVISIBLE);
            }
        });

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
    private class HTMLBringMeInfo extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarHome.setVisibility(View.VISIBLE);
            progressBarHome.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBarHome.setVisibility(View.INVISIBLE);
            progressBarHome.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
            KnisaVal.setText(AsyncKnisa);
            YetsiaVal.setText(AsyncYetsia);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String url="https://www.hidabroot.org/%D7%9B%D7%A0%D7%99%D7%A1%D7%AA-%D7%94%D7%A9%D7%91%D7%AA";
            try {

                Document doc = Jsoup.connect(url).get();
                Elements data = doc.select("#article_inner > div.cities_list > article:nth-child(3) > div:nth-child(2)");
                AsyncKnisa = data.get(0).ownText();
                AsyncKnisa = AsyncKnisa.substring(AsyncKnisa.length()-5); //going from the end minus the length of time
                //Log.d(TAG, "doInBackground: Knisa "+AsyncKnisa);
                data = doc.select("#article_inner > div.cities_list > article:nth-child(3) > div:nth-child(3)");
                AsyncYetsia = data.get(0).ownText();
                AsyncYetsia = AsyncYetsia.substring(AsyncYetsia.length()-5); //going from the end minus the length of time
                //Log.d(TAG, "doInBackground: Yetsia "+AsyncYetsia);


               /* //Halaha Yomit Section
                String urlHalaha = "http://halachayomit.co.il/he/default.aspx";
                Document doc1 = Jsoup.connect(urlHalaha).get();
                Elements data1 = doc1.select("#ctl00_ContentPlaceHolderMain_div_Mp3_2 > a");
                String HalahaTitle = doc1.select("#ctl00_ContentPlaceHolderMain_lblHalachaTitle").get(0).ownText();
                String HalahaYomit = data1.get(0).outerHtml();
                String uriToMp3Yomit = "http://halachayomit.co.il/he/"+HalahaYomit.substring(9,37);


                Log.d(TAG, "doInBackground: "+HalahaTitle+"\n"+ HalahaYomit + "\n"+uriToMp3Yomit);
                MediaPlayer mediaPlayer2 = new MediaPlayer();
                mediaPlayer2.setDataSource(uriToMp3Yomit);
                mediaPlayer2.prepare();
                mediaPlayer2.start();
                */




            } catch (IOException e) {
                Log.d(TAG, "doInBackground: "+ e);;
            }
            return null;
        }
    }

}
