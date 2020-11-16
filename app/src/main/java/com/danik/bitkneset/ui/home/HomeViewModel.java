package com.danik.bitkneset.ui.home;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class HomeViewModel extends ViewModel {
    public String parasha;
    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        HTMLBringMeInfo htmlBringMeInfo = new HTMLBringMeInfo();
        htmlBringMeInfo.execute();
        mText.setValue("אם אין חיבור - אין פרשה");
    }

    public LiveData<String> getText() {
        return mText;
    }

    private class HTMLBringMeInfo extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBarHome.setVisibility(View.VISIBLE);
           // progressBarHome.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
           // progressBarHome.setVisibility(View.INVISIBLE);
         //  progressBarHome.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
            mText.setValue(parasha);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String url="https://www.aish.co.il/tp/";
            try {
                Document doc = Jsoup.connect(url).get();
                Elements data = doc.select("h2.parshaTitle");
                for (int i = 0; i < data.size(); i++) {
                    parasha = data.select("h2.parshaTitle").eq(i).text().trim();


                    Log.d("PULLING HTML GAVE : ", parasha);
                }
            } catch (IOException e) {
                Log.d(TAG, "doInBackground: "+ e);;
            }
            return null;
        }
    }
}