package innovate.ebad.com.instantmessenger;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Ebad on 7/5/2017.
 */

public class UserSessionManager {

    // Shared Preferences reference
    SharedPreferences pref;

    //Editor reference for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;


    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREFER_NAME = "InstantMessengerPref";

    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";

    // Enrollement (make variable public to access from outside)
    public static final String KEY_FULLNAME = "fullname";

    // Password (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    //URL of the of the address to acess
    public static final String KEY_USERNAME = "username";

    //
    public static final String KEY_PICURL = "picurl";


    // Constructor
    public UserSessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //Create login session
    public void createUserLoginSession(String fullName, String email, String userName,String picUrl) {
        // Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_FULLNAME, fullName);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        editor.putString(KEY_USERNAME, userName);

        editor.putString(KEY_PICURL,picUrl);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {

        //Use hashmap to store user credentials
        HashMap<String, String> user = new HashMap<>();

        // user Enrollement
        user.put(KEY_FULLNAME, pref.getString(KEY_FULLNAME, null));

        // user password
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // user login url
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));

        //
        user.put(KEY_PICURL, pref.getString(KEY_PICURL, null));

        // return user
        return user;
    }

    public void logoutUser() {

        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Main Activity
        String destinationFilename = android.os.Environment.getExternalStorageDirectory().getPath() + File.separatorChar + "profile.jpg";

        File f = new File(destinationFilename);

        if (f.exists()) {

            f.delete();
        }
    }

    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }


}
