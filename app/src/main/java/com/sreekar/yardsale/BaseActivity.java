package com.sreekar.yardsale;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

/*
The Base class from which all the other activities are derived from. There are common function in
this class such as a progress dialog which is shown when a long running process is loading, and
functions to get the User Id and username.
 */

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    //Method for showing a progress dialog
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    //Method for hiding the progress dialog after the necessary objects have loaded
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    //Method that gets the User ID and stores it in a string
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    //Method that derives a Username by deleting the @example.com from the email used for registration and stores it in a string
    public String getUserName() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        return getUsernameFromEmail(email);
    }

    public String getUsernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }
}
