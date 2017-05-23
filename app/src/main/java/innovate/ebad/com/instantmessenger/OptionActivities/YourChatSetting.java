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

public class YourChatSetting extends BaseActivity {

    Toolbar yourChatToolbar;
    RippleView rippleView, rippleView2;
    SwitchCompat switchCompat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_chat_setting_layout);

        yourChatToolbar = (Toolbar) findViewById(R.id.yourChatSettingToolbar);
        setSupportActionBar(yourChatToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Chat Setting");

        rippleView = (RippleView) findViewById(R.id.rippleView);
        rippleView2 = (RippleView) findViewById(R.id.rippleView2);

        switchCompat = (SwitchCompat) findViewById(R.id.yourChatSwitch);

        switchCompat.setOnClickListener(switchCompatListener);

        rippleView.setOnClickListener(rippleViewListener);
        rippleView2.setOnClickListener(rippleView2Listener);

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
            Toast.makeText(getApplicationContext(), "chat bubble", Toast.LENGTH_SHORT).show();
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
