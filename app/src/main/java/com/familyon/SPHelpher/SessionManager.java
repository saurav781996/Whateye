package com.familyon.SPHelpher;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;


public class SessionManager {
   // Shared Preferences
   SharedPreferences pref;

   // Editor for Shared preferences
   Editor editor;

   // Context
   Context _context;

   // Shared pref mode
   int PRIVATE_MODE = 0;


   // Sharedpref file name
   private static final String PREF_NAME = "WAN";

   // All Shared Preferences Keys
   private static final String IS_LOGIN = "IsLoggedIn";



   // Email address (make variable public to access from outside)
   //public static final String KEY_ID = "id";
//    public static final String KEY_NAME = "name";
//    public static final String KEY_EMAIL = "email";
//    public static final String KEY_NUMBER= "number";
//    public static final String KEY_CCODE = "ccode";

    public static final String KEY_TIMER= "timer";
   // Constructor
   public SessionManager(Context context){
       this._context = context;
       pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
       editor = pref.edit();
   }


   /**
    * Create login session
    * */



    public void createSession(String timer){

        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_TIMER, timer);

        // Storing id in pref
//        editor.putString(KEY_ID, id);
//        editor.putString(KEY_NAME, name);
//        editor.putString(KEY_EMAIL, email);
//        editor.putString(KEY_NUMBER, num);
//        editor.putString(KEY_CCODE, ccode);



        // commit changes
        editor.commit();
    }



   /**
    * Check login method wil check user login status
    * If false it will redirect user to login page
    * Else won't do anything
    * */
  /* public void checkLogin(){
       // Check login status
       if(!this.isLoggedIn()){
           // user is not logged in redirect him to Login Activity
           Intent i = new Intent(_context, Registration.class);
           // Closing all the Activities
           i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

           // Add new Flag to start new Activity
           i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

           // Staring Login Activity
           _context.startActivity(i);
       }

   }*/



   /**
    * Get stored session data
    * */



   public HashMap<String, String> getUserDetails(){

       HashMap<String, String> user = new HashMap<String, String>();


       user.put(KEY_TIMER, pref.getString(KEY_TIMER, null));
       // user name
     //  user.put(KEY_NAME, pref.getString(KEY_NAME, null));

       // user id
//       user.put(KEY_ID, pref.getString(KEY_ID, null));
//
//       user.put(KEY_NAME, pref.getString(KEY_NAME, null));
//
//       user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
//
//
//       user.put(KEY_NUMBER, pref.getString(KEY_NUMBER, null));
//
//       user.put(KEY_CCODE, pref.getString(KEY_CCODE, null));


       // return user
       return user;
   }





    /**
    * Clear session details
    * */
   /*public void logoutUser(){
       // Clearing all data from Shared Preferences
       editor.clear();
       editor.commit();

       // After logout redirect user to Loing Activity
       Intent i = new Intent(_context, Registration.class);
       // Closing all the Activities
       i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

       i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
       // Add new Flag to start new Activity
       i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

       // Staring Login Activity
       _context.startActivity(i);
   }*/

   /**
    * Quick check for login
    * **/
   // Get Login State
   public boolean isLoggedIn()
   {
       return pref.getBoolean(IS_LOGIN, false);
   }
}