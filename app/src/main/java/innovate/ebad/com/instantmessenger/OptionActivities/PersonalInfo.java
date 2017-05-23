package innovate.ebad.com.instantmessenger.OptionActivities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import innovate.ebad.com.instantmessenger.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class PersonalInfo extends AppCompatActivity {

    Toolbar personalInfoToolbar;
    TextView userNameTextView, fullNameTextView;
    CircleImageView imageView;

    String fullName, userName, picUri;

    RippleView rippleView;

    Switch switchCompat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info_layout);

        personalInfoToolbar = (Toolbar) findViewById(R.id.personalInfoToolbar);
        setSupportActionBar(personalInfoToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Info");

        userNameTextView = (TextView) findViewById(R.id.senderProfileUsernameTextview);
        fullNameTextView = (TextView) findViewById(R.id.senderProfileNameTextview);
        imageView = (CircleImageView) findViewById(R.id.senderProfilePictureImageView);

        rippleView = (RippleView) findViewById(R.id.rippleView);

        switchCompat = (Switch) findViewById(R.id.switch1);

        intializeView();

        rippleView.setOnClickListener(rippleViewListener);


    }

    private void intializeView() {

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if (b != null) {
            fullName = (String) b.get("fullName");
            userName = (String) b.get("userName");
            picUri = (String) b.get("picUri");
        }

        userNameTextView.setText(userName);
        fullNameTextView.setText(fullName);

        File file = new File(picUri);

        if (file.exists()) {
            imageView.setImageURI(Uri.parse(picUri));
        } else {
            imageView.setImageResource(R.drawable.user);
        }

        imageView.setOnClickListener(imageViewListener);

    }

    private View.OnClickListener imageViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String[] picarray = {"file://" + picUri};
            new ImageViewer.Builder(PersonalInfo.this, picarray)
                    .setBackgroundColorRes(R.color.myblack)
                    .setStartPosition(0)
                    .show();

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


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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

}
