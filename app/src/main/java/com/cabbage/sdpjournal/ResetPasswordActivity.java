package com.cabbage.sdpjournal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Button reset;
    private EditText etnewPassword;
    private EditText etConfirmPassword;
    ProgressDialog progressDialog;
    Boolean isValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("changing...");


        reset = (Button) findViewById(R.id.bChange);
        reset.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == reset) {
            changePassword();
        }
    }

    private void changePassword() {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
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
