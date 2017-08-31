package com.cabbage.sdpjournal.NoteModel;

/**
 * Created by Junwen on 30/8/17.
 */

public class Constants {


    //Database
    public final static String Users_End_Point = "users";
    public final static String Notes_End_Point = "notes";

    //other Strings
    public static final String Welcome_Text = "Welcome ";
}

//        etnewPassword = (EditText) findViewById(R.id.etNewPassword);
//                etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
//                String newPassword = etnewPassword.getText().toString().trim();
//                String conFirmPassword = etConfirmPassword.getText().toString().trim();
//                //validates
//                //if newPassword is empty
//                if (TextUtils.isEmpty(newPassword)) {
//                etnewPassword.setError("Please enter password");
//                } else if (TextUtils.isEmpty(conFirmPassword)) {
//                etConfirmPassword.setError("Please confirm");
//                } else {
////Not empty
//final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//        if (firebaseUser != null && !newPassword.equals("")) {
//        if (newPassword.length() < 6) {
//        etnewPassword.setError("Password too short");
//        } else if (newPassword.length() > 25) {
//        etnewPassword.setError("Password too long");
//        } else {
//        if (conFirmPassword.equals(newPassword)) {
//        progressDialog.show();
//        firebaseUser.updatePassword(newPassword)
//        .addOnCompleteListener(new OnCompleteListener<Void>() {
//@Override
//public void onComplete(@NonNull Task<Void> task) {
//        progressDialog.dismiss();
//        if (task.isSuccessful()) {
//        Toast.makeText(ResetPasswordActivity.this, "Password has changed, log in with new password", Toast.LENGTH_SHORT).show();
//        firebaseAuth.signOut();
//        startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
//        finish();
//        } else {
//        Toast.makeText(ResetPasswordActivity.this, "Try again", Toast.LENGTH_SHORT).show();
//        }
//        }
//        });
//        } else {
//        //confirm != new
//        etConfirmPassword.setError("Confirm again");
//        }
//        }
//        } else {
//        etnewPassword.setError("Password must not be empty");
//        }
//        }

