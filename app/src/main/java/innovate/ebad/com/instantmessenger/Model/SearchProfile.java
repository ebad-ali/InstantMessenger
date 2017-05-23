package innovate.ebad.com.instantmessenger.Model;

import android.net.Uri;

/**
 * Created by Ebad on 7/5/2017.
 */

public class SearchProfile {

    private String userName,fullName;
    private Uri picUri;

    public SearchProfile(String fullName, String userName, Uri picUri) {
        this.userName = userName;
        this.fullName = fullName;
        this.picUri = picUri;
    }

    public String getUserName() {
        return userName;
    }

    public String getFullName() {
        return fullName;
    }

    public Uri getPicUri() {
        return picUri;
    }
}
