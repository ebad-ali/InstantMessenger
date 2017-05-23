package innovate.ebad.com.instantmessenger;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;
import innovate.ebad.com.instantmessenger.Constants.Data;
import innovate.ebad.com.instantmessenger.Model.MessageDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainMessageActivity extends AppCompatActivity {

    private Boolean exit = false;
    Toolbar Messagetoolbar;
    FloatingActionButton userNameAddButton;

    ListView messageListView;
    ArrayAdapter<MessageDialog> adapter;

    ArrayList<MessageDialog> mMessagesDialog = new ArrayList<>();

    private DatabaseReference mDatabaseDialogReciever;


    TextView noMessageView;

    CircularProgressView progressViewMessage;

    String[] optionArray = {"Delete", "Mute", "Chat Info"};
    int[] optionID = {0, 1, 2};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_main);

        Messagetoolbar = (Toolbar) findViewById(R.id.mainMessageToolbar);
        setSupportActionBar(Messagetoolbar);
        setTitle(R.string.app_name);

        intializeViews();

        mDatabaseDialogReciever.addChildEventListener(messageDialogListener);

    }


    private void intializeViews() {
        progressViewMessage = (CircularProgressView) findViewById(R.id.progressbarMessagelayout);

        UserSessionManager session = new UserSessionManager(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();

        Data.currentUserFullName = user.get(UserSessionManager.KEY_FULLNAME);
        Data.currentUserName = user.get(UserSessionManager.KEY_USERNAME);

        mDatabaseDialogReciever = FirebaseDatabase.getInstance().getReference().child("allchats").child(Data.currentUserName);

        messageListView = (ListView) findViewById(R.id.messagesList);

        userNameAddButton = (FloatingActionButton) findViewById(R.id.searchFloatingButton);

        userNameAddButton.setOnClickListener(userNameAddButtonListener);

        noMessageView = (TextView) findViewById(R.id.noChatMessage);
        noMessageView.setVisibility(View.GONE);

        adapter = new MessageDialogAdapter(MainMessageActivity.this, mMessagesDialog);
        messageListView.setAdapter(adapter);
        messageListView.setDivider(null);
        messageListView.setDividerHeight(0);

        mDatabaseDialogReciever.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    progressViewMessage.setVisibility(View.GONE);
                    noMessageView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Raleway-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private ChildEventListener messageDialogListener = new ChildEventListener() {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            callMessageDialogReciever(dataSnapshot);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            callMessageDialogRecieverChanged(dataSnapshot);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void callMessageDialogReciever(DataSnapshot dataSnapshot) {

        //mMessagesDialog.clear();

        Iterator i = dataSnapshot.getChildren().iterator();
        String lastMessage, time, senderFullname, sendpicUri, username;
        Long unreadmessages;


        while (i.hasNext()) {
            senderFullname = (String) ((DataSnapshot) i.next()).getValue();
            lastMessage = (String) ((DataSnapshot) i.next()).getValue();
            time = (String) ((DataSnapshot) i.next()).getValue();
            sendpicUri = (String) ((DataSnapshot) i.next()).getValue();
            unreadmessages = (Long) ((DataSnapshot) i.next()).getValue();
            username = (String) ((DataSnapshot) i.next()).getValue();

            if (senderFullname.equals(Data.currentUserFullName)) {
                callMessageDialogaddAdapter(senderFullname, lastMessage, time, sendpicUri, username);
            } else {
                callMessageDialogaddAdapter(senderFullname, lastMessage, time, sendpicUri, username);
            }
        }

    }

    private void callMessageDialogRecieverChanged(DataSnapshot dataSnapshot) {

        //mMessagesDialog.clear();

        Iterator i = dataSnapshot.getChildren().iterator();
        String lastMessage, time, senderFullname, sendpicUri, username;
        Long unreadmessages;


        while (i.hasNext()) {
            senderFullname = (String) ((DataSnapshot) i.next()).getValue();
            lastMessage = (String) ((DataSnapshot) i.next()).getValue();
            time = (String) ((DataSnapshot) i.next()).getValue();
            sendpicUri = (String) ((DataSnapshot) i.next()).getValue();
            unreadmessages = (Long) ((DataSnapshot) i.next()).getValue();
            username = (String) ((DataSnapshot) i.next()).getValue();


            if (senderFullname.equals(Data.currentUserFullName)) {
                callMessageDialogaddAdapterChanged(senderFullname, lastMessage, time, sendpicUri, username);
            } else {
                callMessageDialogaddAdapterChanged(senderFullname, lastMessage, time, sendpicUri, username);
            }
        }

    }

    private void callMessageDialogaddAdapterChanged(String senderFull, String lastmessage, String time, String uri, String username) {

        boolean checkme = false;
        int indexme = 0;

        for (int i = 0; i < mMessagesDialog.size(); i++) {
            if (mMessagesDialog.get(i).getUserName().equals(username)) {
                indexme = i;
                checkme = true;
            }
        }

        if (checkme) {
            mMessagesDialog.get(indexme).setLastMessage(lastmessage);
            mMessagesDialog.get(indexme).setMessageTime(time);
        } else {
            mMessagesDialog.add(new MessageDialog(senderFull, time, lastmessage, uri, 1, username));
        }

        adapter.notifyDataSetChanged();
        progressViewMessage.setVisibility(View.GONE);
        noMessageView.setVisibility(View.GONE);

    }

    private void callMessageDialogaddAdapter(String senderFull, String lastmessage, String time, String uri, String username) {
        mMessagesDialog.add(new MessageDialog(senderFull, time, lastmessage, uri, 1, username));
        adapter.notifyDataSetChanged();
        progressViewMessage.setVisibility(View.GONE);
        noMessageView.setVisibility(View.GONE);

    }

    private View.OnClickListener userNameAddButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainMessageActivity.this, SearchActivity.class));
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_setting) {
            startActivity(new Intent(this, ProfileSettingActivity.class));
            //startActivity(new Intent(this, MainChatActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
            startActivity(intent);
            finish();
            System.exit(0);
            // finish activity
        } else {
            Toast.makeText(getApplicationContext(), "Press Back again to leave.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }


    public class MessageDialogAdapter extends ArrayAdapter<MessageDialog> {

        Context mContext;
        RippleView rippleView;
        ArrayList<MessageDialog> messagesList;

        public MessageDialogAdapter(Context mContext, ArrayList<MessageDialog> messagesList) {
            super(mContext, R.layout.activity_message_dialog_main_custom_list, messagesList);
            this.mContext = mContext;
            this.messagesList = messagesList;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {


            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View rowView = inflater.inflate(R.layout.activity_message_dialog_main_custom_list, parent, false);


            rippleView = (RippleView) rowView.findViewById(R.id.rippleViewme);


            CircleImageView imageView = (CircleImageView) rowView.findViewById(R.id.senderUserProfilePictureImageView);
            TextView senderFullName = (TextView) rowView.findViewById(R.id.senderNameTextView);
            TextView lastMessage = (TextView) rowView.findViewById(R.id.senderMessageTextView);
            TextView lastMessageTime = (TextView) rowView.findViewById(R.id.senderMessageTimeTextView);
            TextView unseenMessages = (TextView) rowView.findViewById(R.id.senderMessageUnreadTextView);


            String destinationFilename = android.os.Environment.getExternalStorageDirectory().getPath() + File.separatorChar;
            destinationFilename += messagesList.get(position).getPicId();


            File file = new File(destinationFilename);

            if (file.exists()) {
                imageView.setImageURI(Uri.parse(destinationFilename));
            } else {
                imageView.setImageResource(R.drawable.user);
            }
            senderFullName.setText(messagesList.get(position).getFullName());
            lastMessage.setText(messagesList.get(position).getLastMessage());
            lastMessageTime.setText(messagesList.get(position).getMessageTime());

            if (messagesList.get(position).getUnreadMessages() > 0) {
                unseenMessages.setText("" + messagesList.get(position).getUnreadMessages());
            } else {
                unseenMessages.setVisibility(View.GONE);
            }

            rippleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String destinationFilenames = android.os.Environment.getExternalStorageDirectory().getPath() + File.separatorChar;
                            destinationFilenames += messagesList.get(position).getPicId();

                            Intent intent = new Intent(mContext, MainChatActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("senderusername", messagesList.get(position).getUserName());
                            intent.putExtra("recieverusername", Data.currentUserName);
                            intent.putExtra("chatroomname", messagesList.get(position).getUserName() + "room");
                            intent.putExtra("senderfullname", messagesList.get(position).getFullName());
                            intent.putExtra("senderpicuri", destinationFilenames);
                            mContext.startActivity(intent);
                        }
                    }, 350);
                }
            });


            return rowView;
        }
    }
}