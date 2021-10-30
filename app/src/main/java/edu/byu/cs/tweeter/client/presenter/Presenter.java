package edu.byu.cs.tweeter.client.presenter;

import android.view.View;

public class Presenter {
    protected View view;

    public Presenter(View view) {
        this.view = view;
    }

    public interface View {
        void displayErrorMessage(String message);
        void displayInfoMessage(String message);
    }


}
