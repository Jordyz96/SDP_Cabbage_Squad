package com.cabbage.sdpjournal.Model;

import java.util.Random;

/**
 * Created by Junwen on 30/8/17.
 */

public class Constants {


    //Database
    public final static String Users_End_Point = "users";
    public final static String Entries_End_Point = "entries";
    public final static String Journals_End_Point = "journals";
    public final static String Attachments_End_Point = "attachments";

    //User
    public static final String AUTH_IN = "onAuthStateChanged:signed_in:";
    public static final String AUTH_OUT = "onAuthStateChanged:signed_out";

    //other Strings
    public static final String Reset_Password = "Reset Password";
    public static final String journalID = "journalID";
    public static final String Default_Color = "defualt";
    public static final String Select_Color = "Select your cover color";
    public static final String New_Journal = "New Journal";

    //entry status
    public static final String Entry_Status_Normal = "normal";
    public static final String Entry_Status_Hidden = "hidden";
    public static final String Entry_Status_Deleted = "deleted";

    //gallery
    public static final int Gallery_Request = 1;

    //some helper functions

    public String randomString(){
        Random gen = new Random(474587); //put in random seed
        int min = 6;
        int max = 10;

// we want 20 random strings
        int len = min+gen.nextInt(max-min+1);
        StringBuilder s = new StringBuilder(len);
        for(int i=0; i < 20; i++){
            while(s.length() < len){
                //97 is ASCII for character 'a', and 26 is number of alphabets
                s.append((char)(97+gen.nextInt(26)));
            }

        }
        return s.toString();
    }

    public long removeLastNDigits(long x, long n) {
        return (long) (x / Math.pow(10, n));
    }

}

