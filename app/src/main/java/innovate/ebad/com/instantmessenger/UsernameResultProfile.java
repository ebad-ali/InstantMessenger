package innovate.ebad.com.instantmessenger;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stfalcon.frescoimageviewer.ImageViewer;

import de.hdodenhof.circleimageview.CircleImageView;
import innovate.ebad.com.instantmessenger.Constants.Data;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class UsernameResultProfile extends AppCompatActivity {

    Toolbar UsernameResultProfileToolbar;

    CircleImageView imageView;
    TextView userNameTextView, FullNameTextView;
    RippleView addFriendView;

    private DatabaseReference mDatabase;

    String fullName, userName, uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result_username_layout);

        UsernameResultProfileToolbar = (Toolbar) findViewById(R.id.SuccessPersonalInfoToolbar);
        setSupportActionBar(UsernameResultProfileToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Info");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        imageView = (CircleImageView) findViewById(R.id.senderSuccessProfilePictureImageView);
        FullNameTextView = (TextView) findViewById(R.id.senderSuccessProfileNameTextview);
        userNameTextView = (TextView) findViewById(R.id.senderSuccessProfileUsernameTextview2);

        addFriendView = (RippleView) findViewById(R.id.rippleView);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if (b != null) {
            fullName = (String) b.get("fullName");
            uri = (String) b.get("picuri");
            userName = (String) b.get("username");

            userNameTextView.setText(userName);
            FullNameTextView.setText(fullName);
            imageView.setImageURI(Uri.parse(uri));
        }

        addFriendView.setOnClickListener(addFriendViewListener);

        imageView.setOnClickListener(imageViewListener);
    }

    private View.OnClickListener imageViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String[] picarray = {"file://" + uri};
            new ImageViewer.Builder(UsernameResultProfile.this, picarray)
                    .setBackgroundColorRes(R.color.myblack)
                    .setStartPosition(0)
                    .show();
        }
    };


    private View.OnClickListener addFriendViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDatabase.child("friends").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child(userName).child("Name").setValue(fullName);
            mDatabase.child("friends").child(userName).child(Data.currentUserName).child("Name").setValue(Data.currentUserFullName);

            String s = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

            mDatabase.child("chats").child(s).child(userName + "room").setValue("");
            mDatabase.child("chats").child(userName).child(s + "room").setValue("");


            Intent intent = new Intent(getApplicationContext(), MainChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("senderusername", userName);
            intent.putExtra("recieverusername", s);
            intent.putExtra("chatroomname", userName + "room");
            intent.putExtra("senderfullname", fullName);
            intent.putExtra("senderpicuri", uri);
            startActivity(intent);
            finish();


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
