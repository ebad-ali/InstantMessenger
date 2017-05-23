package innovate.ebad.com.instantmessenger.OptionActivities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import innovate.ebad.com.instantmessenger.BaseActivity;
import innovate.ebad.com.instantmessenger.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class YourAccount extends BaseActivity {

    Toolbar yourAccountToolbars;

    TextView emailTextView, fullNameTextView, userNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_account_layout);

        yourAccountToolbars = (Toolbar) findViewById(R.id.yourAccountToolbar);
        setSupportActionBar(yourAccountToolbars);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Your Account");

        userNameTextView = (TextView) findViewById(R.id.yourAccountUserName);
        emailTextView = (TextView) findViewById(R.id.yourAccountEmail);
        fullNameTextView = (TextView) findViewById(R.id.yourAccountName);


        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if (b != null) {
            String fullName = (String) b.get("fullName");
            String email = (String) b.get("email");
            String userName = (String) b.get("username");

            userNameTextView.setText(userName);
            emailTextView.setText(email);
            fullNameTextView.setText(fullName);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
