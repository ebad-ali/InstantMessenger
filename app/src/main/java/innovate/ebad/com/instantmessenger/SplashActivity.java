package innovate.ebad.com.instantmessenger;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            callGetStartedActivity();
        }
        else {
            FirebaseUser user = mAuth.getCurrentUser();
            String userName = user.getDisplayName();

            Log.d("fuckinsplashh",""+userName);

            if (userName != null) {
                callMessageActivity(userName);
            }
            else {
                mAuth.signOut();
                callGetStartedActivity();
            }
        }
    }

    private void callMessageActivity(final String userName) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainMessageActivity.class);
                intent.putExtra("USER_NAME", userName);
                startActivity(intent);
                finish();
            }
        }, 1500);
    }

    private void callGetStartedActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent intent = new Intent(SplashActivity.this, GetStartedActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
