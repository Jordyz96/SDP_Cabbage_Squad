package com.cabbage.sdpjournal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;


public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth.AuthStateListener mAuthListener;
    final FirebaseAuth myFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = myFirebaseAuth.getCurrentUser();

    private static final String AUTH_IN = "onAuthStateChanged:signed_in:";
    private static final String AUTH_OUT = "onAuthStateChanged:signed_out";

    private Button reset;
    private EditText etnewPassword;
    private EditText etConfirmPassword;
    ProgressDialog progressDialog;
    Boolean isValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("changing...");


        reset = (Button) findViewById(R.id.bChange);
        reset.setOnClickListener(this);

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
        myFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    //On stop method
    @Override
    public void onStop() {
        super.onStop();
        //Sets listener to catch when the user is signing out.
        if (mAuthListener != null) {
            myFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }
    /**
     * Creates the options menu on the action bar.
     * @param menu Menu at the top right of the screen
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflates the menu menu_other which includes logout and quit functions.
        getMenuInflater().inflate(R.menu.menu_reset, menu);
        return true;
    }

    /**
     * Sets a listener that triggers when an option from the taskbar menu is selected.
     * @param item Which item on the menu was selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Finds which item was selected
        switch(item.getItemId()){
            //If item is logout
            case R.id.action_logout:
                //Sign out of the authenticator and return to login activity.
                myFirebaseAuth.signOut();
                ResetPasswordActivity.this.startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                return true;

            //If item is reset password
            case R.id.action_reset_password:
                ResetPasswordActivity.this.startActivity(new Intent(ResetPasswordActivity.this, ResetPasswordActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        if (view == reset) {
            changePassword();
        }
    }

    private void changePassword() {
        etnewPassword = (EditText) findViewById(R.id.etNewPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);

        String newPassword = etnewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (validationPassed(newPassword, confirmPassword)){
            if (user != null) {
                user.updatePassword(newPassword)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(ResetPasswordActivity.this, "Password updated, login with new password", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(ResetPasswordActivity.this, "Password changing failed, please re-login using your original password and try again", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        }


    }

    private boolean validationPassed(String newP, String confirmP){
        isValid = false;
        etnewPassword = (EditText) findViewById(R.id.etNewPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        //if empty
        if (TextUtils.isEmpty(newP)){
            etnewPassword.setError("Please enter new password");
        }else {
            //Length validation
            if (newP.length() < 6) {
                etnewPassword.setError("Password too short");
            } else if (newP.length() > 25) {
                etnewPassword.setError("Password too long");
            } else {
                //length is good
                //if confirmP == newP
                if (confirmP.equals(newP)){
                    isValid = true;
                } else {
                    etConfirmPassword.setError("Please confirm again");
                }
            }
        }
        return isValid;
    }
}
