package com.sreekar.yardsale;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sreekar.yardsale.models.User;

/**
 * This page is the first page the user sees when they open the app for the first time.
 * This page allows for registration and login of a user. After a user logs in they are
 * sent to the Main Activity.
 */
public class SignInActivity extends BaseActivity implements View.OnClickListener {

    // Initialize variables
    private static final String TAG = "SignInActivity";

    private DatabaseReference database;
    private FirebaseAuth auth;

    private EditText emailField;
    private EditText passwordField;
    private Button signInButton;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Get firebase database reference
        database = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        // Initialize views
        emailField = (EditText) findViewById(R.id.field_email);
        passwordField = (EditText) findViewById(R.id.field_password);
        signInButton = (Button) findViewById(R.id.button_sign_in);
        signUpButton = (Button) findViewById(R.id.button_sign_up);

        // Setup click listeners
        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if the user is already signed in, if the user is already signed in take the
        // user to the main activity.
        if (auth.getCurrentUser() != null) {
            startMainActivity();
        }
    }

    // Signs in or registers user based on which button is pressed
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_sign_in) {
            signIn();
        } else if (i == R.id.button_sign_up) {
            signUp();
        }
    }

    /**
     * This method signs the user in and if the user enters the correct username and password
     * user will be sent to the MainActivity
     */
    private void signIn() {
        // get the email and password from the text fields
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        // make sure user has entered all the required fields.
        if (!validateForm(email, password)) {
            return;
        }

        // show the progress bar while we try to signin the user using auth.
        showProgressDialog();

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                    hideProgressDialog();

                    if (task.isSuccessful()) {
                        startMainActivity();
                    } else {
                        Toast.makeText(SignInActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    /**
     * This method creates a new user.
     */
    private void signUp() {
        // get the email and password from the text fields
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        // make sure user has entered all the required fields.
        if (!validateForm(email, password)) {
            return;
        }

        // show the progress bar while we try to signin the user using auth.
        showProgressDialog();

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                    hideProgressDialog();

                    if (task.isSuccessful()) {
                        createUser(task.getResult().getUser());
                    } else {
                        Toast.makeText(SignInActivity.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void createUser(FirebaseUser firebaseUser) {
        String username = getUsernameFromEmail(firebaseUser.getEmail());

        User user = new User(username, firebaseUser.getEmail());

        // Sends user data to database
        database.child("users").child(firebaseUser.getUid()).setValue(user);

        startMainActivity();
    }

    private void startMainActivity() {
        startActivity(new Intent(SignInActivity.this, MainActivity.class));
        finish();
    }

    // Form valdation. If the form is filled out wrong, an error message is shown.
    private boolean validateForm(String email, String password) {
        boolean result = true;
        emailField.setError(null);
        passwordField.setError(null);

        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required");
            result = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required");
            result = false;
        }

        return result;
    }
}
