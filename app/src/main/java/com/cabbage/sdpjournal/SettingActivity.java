package com.cabbage.sdpjournal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cabbage.sdpjournal.Model.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    private Button logout;
    private Button resetPassword;
    private FirebaseAuth firebaseAuth;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        TextView tvWelcomeText = (TextView) findViewById(R.id.tvWelcome);
        logout = (Button) findViewById(R.id.bLogout);
        resetPassword = (Button) findViewById(R.id.bResetPassword);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            tvWelcomeText.setText(Constants.Welcome_Text + firebaseUser.getEmail());
        }

        logout.setOnClickListener(this);
        resetPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == logout){
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        if (v == resetPassword){
            startActivity(new Intent(this, ResetPasswordActivity.class));
            finish();
        }
    }
}
