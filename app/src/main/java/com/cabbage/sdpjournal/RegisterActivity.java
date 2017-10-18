package com.cabbage.sdpjournal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cabbage.sdpjournal.UserInfo.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;

/**
 * Activity class that allows a user to register to the database and sends them to the main activity
 * if register is successful
 *
 * @author Sean Carmichael
 * @version 1.0
 * @since 22.08.2017
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    //Constants
    private static final String REGISTERING_USER_MESSAGE = "Registering User...";
    private static final String REGISTER_SUCCESS = "Registered Successfully";
    private static final String REGISTER_FAILED = "Could not register, please try again";
    private static final String LOGIN_SUCCESS = "Login was successful";
    private static final String LOGIN_FAILED = "Could not login, please try again";
    private static final String EXIT_KEY = "EXIT";
    private static final String AUTH_IN = "onAuthStateChanged:signed_in:";
    private static final String AUTH_OUT = "onAuthStateChanged:signed_out";
    private static final String DB_USERS = "users";
    private static final String DOT_COM = ".com";
    private static final String AT = "@";

    //Progress dialog
    ProgressDialog progressDialog;

    //Layout elements
    Button registerBtn;
    TextView toLoginBtn;
    EditText emailEt;
    EditText passwordEt;

    //Firebase variables
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    /**
     * What happens on the creation of the activity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Get authentication instance
        mAuth = FirebaseAuth.getInstance();
        //Get the instance of the database.
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        //Initalises progress dialog
        progressDialog = new ProgressDialog(this);

        //Initialises layout elemenets
        registerBtn = (Button) findViewById(R.id.registerBtn);
        toLoginBtn = (TextView) findViewById(R.id.click_to_login);
        emailEt = (EditText) findViewById(R.id.emailEt);
        passwordEt = (EditText) findViewById(R.id.passwordEt);

        //Set a listener for the register button and one for the back button
        registerBtn.setOnClickListener(this);
        toLoginBtn.setOnClickListener(this);

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
     * Function called when any on click listener is triggered
     *
     * @param v view of the button clicked
     */
    @Override
    public void onClick(View v) {
        //If button pressed is the register button run registerUser
        if (v == registerBtn) {
            registerUser();
        } else {
            //If back button go back to login activity
            RegisterActivity.this.startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        }
    }

    /**
     * Function that check if fields are valid and if so creates a user with them
     */
    private void registerUser() {
        //Gathers the string from the fields
        final String email = emailEt.getText().toString();
        final String password = passwordEt.getText().toString();

        //Initialises variables of validation process
        boolean cancel = false;
        View focusView = null;

        //If email field is empty
        if (TextUtils.isEmpty(email)) {
            //Set an error message in email field
            emailEt.setError(getString(R.string.error_no_email));
            //Change focus back to email field
            focusView = emailEt;
            //Set a flag to make sure the process does not continue with invalid information
            cancel = true;
            //Call isEmailValid function to check email validity
        } else if (!isEmailValid(email)) {
            emailEt.setError(getString(R.string.error_invalid_email));
            focusView = emailEt;
            cancel = true;
        }

        //If password field is empty
        if (TextUtils.isEmpty(password)) {
            passwordEt.setError(getString(R.string.error_no_password));
            focusView = passwordEt;
            cancel = true;
        } else {
            //If not empty call isEnteredValid function to check if the password is too short or long
            switch (isEnteredValid(password)) {
                case 0:
                    passwordEt.setError(getString(R.string.error_invalid_short_password));
                    focusView = passwordEt;
                    cancel = true;
                    break;
                case 1:
                    passwordEt.setError(getString(R.string.error_invalid_long_password));
                    focusView = passwordEt;
                    cancel = true;
                    break;
            }
        }


        // If there is an error, don't attempt login and focus the first
        if (cancel) {
            // form field with an error.
            focusView.requestFocus();
        } else {
            //Show a progress dialog telling the user they are being registered
            progressDialog.setMessage(REGISTERING_USER_MESSAGE);
            progressDialog.show();

            //Create a user with the email and password enter
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //If successful show a toast telling the user and call the loginUser to log them in
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, REGISTER_SUCCESS, Toast.LENGTH_SHORT).show();
                        loginUser(email, password);
                        //If unsuccessful show a toast telling the user
                    } else {
                        Toast.makeText(RegisterActivity.this, REGISTER_FAILED, Toast.LENGTH_SHORT).show();
                    }
                    //Close the progress Dialog
                    progressDialog.dismiss();
                }
            });
        }
    }

    /**
     * Signs the new user in and set's thier username in the database
     *
     * @param email    Email entered of new user
     * @param password Password entered of new user
     */
    private void loginUser(final String email, String password) {
        //Calling the sign-in with the email and password
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //If successful
                if (task.isSuccessful()) {
                    //Get the current userID
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = user.getUid();
                    //Creates a new user information object with the email and username
                    UserInformation userInformation = new UserInformation(email);
                    //Sets the new object's valuables as nodes of the user in the database
                    myRef.child(DB_USERS).child(userID).setValue(userInformation);
                    //Creates a toast to inform the user everything is set up
                    Toast.makeText(RegisterActivity.this, LOGIN_SUCCESS, Toast.LENGTH_SHORT).show();
                    //Moves the new user to the main activity
                    RegisterActivity.this.startActivity(new Intent(RegisterActivity.this, JournalListActivity.class));
                    //If unsuccessful create a toast informing the user
                } else {
                    Toast.makeText(RegisterActivity.this, LOGIN_FAILED, Toast.LENGTH_SHORT).show();
                    //Send the user to the login screen to attempt to login again
                    RegisterActivity.this.startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }
            }
        });
    }

    /**
     * Checks if the passed email is valid
     *
     * @param email the email that was submitted for validation
     * @return
     */
    private boolean isEmailValid(String email) {
        return email.contains(AT) && email.endsWith(DOT_COM);
    }

    /**
     * Checks if the passed string is too long or short
     *
     * @param string The string entered for validation
     * @return if its too short to long or neither
     */
    private int isEnteredValid(String string) {
        //Initialises the output
        int output = 0;
        //If the string is 6 or less print 0 (too short)
        if (string.length() < 6) {
            output = 0;
            //If the string is more than 16 print 1 (too long)
        } else if (string.length() > 16) {
            output = 1;
            //if neither than it is correct
        } else {
            output = 2;
        }
        return output;
    }


}