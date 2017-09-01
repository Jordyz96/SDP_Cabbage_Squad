package com.cabbage.sdpjournal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cabbage.sdpjournal.Model.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;

/**
 * Gets the email and password of a user and matches it with the database, signs the user in and
 * moves to the main activity if successful. Offers access to the register activity from new users
 *
 * @author Sean Carmichael
 * @version 1.0
 * @since 22.08.2017
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    //Constants
    private static final String LOGGING_IN_MESSAGE = "Logging in...";
    private static final String EXIT_KEY = "EXIT";
    private static final String AUTH_IN = "onAuthStateChanged:signed_in:";
    private static final String AUTH_OUT = "onAuthStateChanged:signed_out";
    private static final String FAILED_LOGIN = "Could not login, please try again";

    //Firebase variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Progress dialog variable
    private ProgressDialog progressDialog;

    //Layout elements
    private Button loginBtn;
    private Button registerBtn;
    private TextView tvForgotPassword;
    private EditText emailEt;
    private EditText passwordEt;

    /**
     * What happens on the creation of the activity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Inflates the activity_login layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //If an intent comes in with the key EXIT close this activity, effectively closing the app
        if (getIntent().getBooleanExtra(EXIT_KEY, false)) {
            finish();
        }

        //Gets reference for the firebase authentication service
        mAuth = FirebaseAuth.getInstance();

        //Initialises progress dialog
        progressDialog = new ProgressDialog(this);

        //Initialises the layout elements
        loginBtn = (Button) findViewById(R.id.loginBtn);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        emailEt = (EditText) findViewById(R.id.emailEt);
        passwordEt = (EditText) findViewById(R.id.passwordEt);

        //Sets listeners that trigger when the login button is pressed and another for the register button
        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);

        //Set listener that triggers when a user signs out
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, AUTH_IN + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, AUTH_OUT);
                }
                // ...
            }
        };
    }

    //On start method
    @Override
    public void onStart() {
        super.onStart();
        //Sets a listener to catch when the user is signing in.
        mAuth.addAuthStateListener(mAuthListener);
    }

    //On stop method
    @Override
    public void onStop() {
        super.onStop();
        //Sets listener to catch when the user is signing out.
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * OnClick method for when either of the listeners are triggered
     *
     * @param v The view of the button clicked
     */
    @Override
    public void onClick(View v) {
        //Determine which button was pressed
        if (v == loginBtn) {
            //Calls loginUser function
            loginUser();
        }
        if (v == registerBtn) {
            //If register moves to register activity
            LoginActivity.this.startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        }
        if (v == tvForgotPassword) {

            //if a user forgot his password and click "forgotPassword", popup dialog will show up and ask for an email address
            //and send a reset email to the given email address

            AlertDialog.Builder ab = new AlertDialog.Builder(LoginActivity.this);
            View myview = getLayoutInflater().inflate(R.layout.dialog_reset_password, null);

            TextView tvLabel = (TextView) myview.findViewById(R.id.tvResetRequestLabel);
            final EditText etemailAddress = (EditText) myview.findViewById(R.id.etEmAddress);
            Button resetBtn = (Button) myview.findViewById(R.id.resetPasswordBtn);

            tvLabel.setText(Constants.Reset_Password);
            ab.setView(myview);
            final AlertDialog dialog = ab.create();
            dialog.show();

            resetBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(etemailAddress.getText().toString())) {
                        progressDialog.setMessage("Sending reset email...");
                        progressDialog.show();

                        FirebaseAuth.getInstance().sendPasswordResetEmail(etemailAddress.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        if (task.isSuccessful()) {
                                            Toast.makeText(LoginActivity.this, "Reset email has been sent, please check your email.", Toast.LENGTH_SHORT).show();
                                            dialog.cancel();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(LoginActivity.this, "Email address must not be empty.", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    /**
     * Function that gets the email and password, makes sure they are valid and match, if so send to main activity
     */
    private void loginUser() {
        //Gather text from both fields
        String email = emailEt.getText().toString();
        String password = passwordEt.getText().toString();

        //Initialise variables for checking valid text was entered
        boolean cancel = false;
        View focusView = null;

        //If the email field is empty ask the user to type something
        if (TextUtils.isEmpty(email)) {
            //Set and error message on email edit text
            emailEt.setError(getString(R.string.error_no_email));
            //Set focus back to email field
            focusView = emailEt;
            //Flag the function as cancelled so not to proceed
            cancel = true;
        }

        //If password field is empty ask user to type something
        if (TextUtils.isEmpty(password)) {
            passwordEt.setError(getString(R.string.error_no_password));
            focusView = passwordEt;
            cancel = true;
        } else {
            //otherwise check if the password is too long or short
            switch (isPasswordValid(password)) {
                case 0:
                    //If too short tell user
                    passwordEt.setError(getString(R.string.error_invalid_short_password));
                    focusView = passwordEt;
                    cancel = true;
                case 1:
                    //If too long tell user
                    passwordEt.setError(getString(R.string.error_invalid_long_password));
                    focusView = passwordEt;
                    cancel = true;
            }
        }

        // If there is an error, don't attempt login and focus the first
        if (cancel) {
            // form field with an error.
            focusView.requestFocus();
        } else {
            //Else show a progress dialog informing the user they are being logged in
            progressDialog.setMessage(LOGGING_IN_MESSAGE);
            progressDialog.show();

            //Run log in process through firebase with email and password
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //If they match the database move to main activity
                    if (task.isSuccessful()) {
                        LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        //Else inform user that login was unsuccessful
                        Toast.makeText(LoginActivity.this, FAILED_LOGIN, Toast.LENGTH_SHORT).show();
                    }
                    //Close the progress dialog
                    progressDialog.dismiss();
                }
            });
        }
    }

    /**
     * Function checking if the password too long or short
     *
     * @param password password entered in the password field
     * @return too short, too long or correct
     */
    private int isPasswordValid(String password) {
        int output = 0;
        if (password.length() < 6) {
            output = 0;
        } else if (password.length() > 16) {
            output = 1;
        } else {
            output = 2;
        }
        return output;
    }
}
