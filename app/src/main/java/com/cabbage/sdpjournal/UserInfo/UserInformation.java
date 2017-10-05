package com.cabbage.sdpjournal.UserInfo;

/**
 * Class to store account details while they are moved to the database
 * @author Sean Carmichael
 * @version 1.0
 * @since 22.08.2017
 */
public class UserInformation {

    private String mEmail;
    private String mUsername;

    /**
     * Constructor for creating an new object with all of it's variables set
     * @param email email of the user
     * @param username username of the user
     */
    public UserInformation(String email, String username){
        mEmail = email;
        mUsername = username;
    }

    /**
     * Getter for mEmail
     * @return mEmail
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Setter for mEmail
     * @param mEmail
     */
    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    /**
     * Getter for mUsername
     * @return mUsername
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * Setter for mUsername
     * @param mUsername
     */
    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }
}
