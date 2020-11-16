package com.danik.bitkneset.ui.aliyot;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.danik.bitkneset.ui.login.LoginFragment;

public class AliyotViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AliyotViewModel() {
        mText = new MutableLiveData<>();
        if(LoginFragment.user != null)
        mText.setValue("שלום "+ LoginFragment.user.getFullName());
        else mText.setValue("אנא התחבר קודם.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}