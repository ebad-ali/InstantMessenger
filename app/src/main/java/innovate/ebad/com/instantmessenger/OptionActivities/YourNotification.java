package innovate.ebad.com.instantmessenger.OptionActivities;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.andexert.library.RippleView;

import innovate.ebad.com.instantmessenger.BaseActivity;
import innovate.ebad.com.instantmessenger.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class YourNotification extends BaseActivity {

    Toolbar yourNotificationToolbar;
    RippleView rippleView, rippleView2, rippleView3, rippleView4;
    SwitchCompat switchCompat, switchCompat2, switchCompat3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_notification_layout);

        yourNotificationToolbar = (Toolbar) findViewById(R.id.yourNotificationToolbar);
        setSupportActionBar(yourNotificationToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Notification");


        rippleView = (RippleView) findViewById(R.id.rippleView);
        rippleView2 = (RippleView) findViewById(R.id.rippleView2);
        rippleView3 = (RippleView) findViewById(R.id.rippleView3);
        rippleView4 = (RippleView) findViewById(R.id.rippleView4);

        switchCompat = (SwitchCompat) findViewById(R.id.yourNotificationSwitch);
        switchCompat2 = (SwitchCompat) findViewById(R.id.yourNotificationSwitch2);
        switchCompat3 = (SwitchCompat) findViewById(R.id.yourNotificationSwitch4);


        switchCompat.setOnClickListener(switchCompatListener);
        switchCompat2.setOnClickListener(switchCompat2Listener);
        switchCompat3.setOnClickListener(switchCompat3Listener);

        rippleView.setOnClickListener(rippleViewListener);
        rippleView2.setOnClickListener(rippleView2Listener);
        rippleView3.setOnClickListener(rippleView3Listener);
        rippleView4.setOnClickListener(rippleView4Listener);

    }

    private View.OnClickListener switchCompatListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (switchCompat.isChecked()) {
                switchCompat.setChecked(false);
            } else {
                switchCompat.setChecked(true);
            }
        }
    };


    private View.OnClickListener switchCompat2Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (switchCompat2.isChecked()) {
                switchCompat2.setChecked(false);
            } else {
                switchCompat2.setChecked(true);
            }
        }
    };


    private View.OnClickListener switchCompat3Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (switchCompat3.isChecked()) {
                switchCompat3.setChecked(false);
            } else {
                switchCompat3.setChecked(true);
            }
        }
    };

    private View.OnClickListener rippleViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (switchCompat.isChecked()) {
                switchCompat.setChecked(false);
            } else {
                switchCompat.setChecked(true);
            }
        }
    };


    private View.OnClickListener rippleView2Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (switchCompat2.isChecked()) {
                switchCompat2.setChecked(false);
            } else {
                switchCompat2.setChecked(true);
            }
        }
    };

    private View.OnClickListener rippleView3Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "LED COLOR", Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener rippleView4Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (switchCompat3.isChecked()) {
                switchCompat3.setChecked(false);
            } else {
                switchCompat3.setChecked(true);
            }
        }
    };

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
