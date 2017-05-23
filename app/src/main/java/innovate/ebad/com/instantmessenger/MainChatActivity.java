package innovate.ebad.com.instantmessenger;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stfalcon.chatkit.messages.MessageInput;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import innovate.ebad.com.instantmessenger.Adapter.MainMessageAdapter;
import innovate.ebad.com.instantmessenger.Constants.Data;
import innovate.ebad.com.instantmessenger.Model.Message;
import innovate.ebad.com.instantmessenger.Model.MessageDialog;
import innovate.ebad.com.instantmessenger.Model.MessageModel;
import innovate.ebad.com.instantmessenger.OptionActivities.PersonalInfo;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainChatActivity extends AppCompatActivity {

    ArrayList<MessageModel> messageList = new ArrayList<>();
    ArrayList<String> list = new ArrayList<>();

    Toolbar chatMessageToolbar;
    ListView messagesListView;
    MessageInput input;
    int index;
    boolean found = false;

    private DatabaseReference mDatabaseRecieve, mDatabaseSend, mDatabaseDialogSender, mDatabaseDialogReciever;

    private StorageReference mStorage;


    private SecureRandom random = new SecureRandom();


    ArrayAdapter<MessageModel> adapter;

    String senderFullName, senderUsername, senderPicUri, chatRoomName;

    Calendar cal;
    DateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_chat_layout);

        chatMessageToolbar = (Toolbar) findViewById(R.id.chatMessageToolbar);
        setSupportActionBar(chatMessageToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if (b != null) {
            senderFullName = (String) b.get("senderfullname");
            senderPicUri = (String) b.get("senderpicuri");
            chatRoomName = (String) b.get("chatroomname");
            senderUsername = (String) b.get("senderusername");
        }

        mStorage = FirebaseStorage.getInstance().getReference();


        intializeDatabase();

        addMessagelist();
        setTitle(senderFullName);

        input = (MessageInput) findViewById(R.id.input);

        messagesListView = (ListView) findViewById(R.id.messageTextListView);

        input.setInputListener(inputListener);

        input.setAttachmentsListener(inputAttachmentListener);

        adapter = new MainMessageAdapter(MainChatActivity.this, messageList);
        messagesListView.setAdapter(adapter);
        messagesListView.setDivider(null);
        messagesListView.setDividerHeight(0);

        mDatabaseRecieve.addChildEventListener(messageListener);

        chatMessageToolbar.setOnClickListener(chatMessageToolbarListner);

    }

    private MessageInput.AttachmentsListener inputAttachmentListener = new MessageInput.AttachmentsListener() {
        @Override
        public void onAddAttachments() {

            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 10);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == 10) {

            String artist_name;
            Uri uriSound = data.getData();


            String[] proj = {MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Artists.ARTIST};


            Cursor tempCursor = managedQuery(uriSound,
                    proj, null, null, null);

            tempCursor.moveToFirst(); //reset the cursor
            int col_index = -1;
            int numSongs = tempCursor.getCount();
            int currentNum = 0;
            do {
                col_index = tempCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                artist_name = tempCursor.getString(col_index);
                currentNum++;
            } while (tempCursor.moveToNext());


            uploadMusic(uriSound, artist_name);

            //    Toast.makeText(getApplicationContext(), artist_name, Toast.LENGTH_SHORT).show();


            //play(this, uriSound);
        }
    }


    private void uploadMusic(Uri uri, final String songName) {

        final String path = nextSessionId();

        final StorageReference musicRef = mStorage.child("music")
                .child(path);

        boolean showMinMax = true;
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Uploading your music")
                .content("Please wait")
                .cancelable(false)
                .progress(false, 100, showMinMax)
                .show();

        final int[] increment = {0};


        musicRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri downloadUri = taskSnapshot.getDownloadUrl();

                        if (downloadUri != null) {
                            Log.wtf("fuckthisuri", String.valueOf(downloadUri));
                            Toast.makeText(getApplicationContext(), "uploaded Sucessfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            cal = Calendar.getInstance();
                            dateFormat = new SimpleDateFormat("hh:mm a");

                            Message message = new Message(songName, dateFormat.format(cal.getTime()), Data.currentUserName, "music", String.valueOf(downloadUri));
                            AddMessagetoList(message);
                            addSendMessagetoList(message);

                        }
                    }
                })

                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());

                        dialog.incrementProgress(progress - increment[0]);
                        System.out.println("Upload is " + progress + "% done");
                        increment[0] = progress;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Upload failed
                        Log.w("shithappens", "uploadFromUri:onFailure", exception);
                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                        Toast.makeText(getApplicationContext(), "Error Uploading picture", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private View.OnClickListener chatMessageToolbarListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(MainChatActivity.this, PersonalInfo.class);
            intent.putExtra("fullName", senderFullName);
            intent.putExtra("userName", senderUsername);
            intent.putExtra("picUri", senderPicUri);
            startActivity(intent);
        }
    };


    public String nextSessionId() {
        return new BigInteger(130, random).toString(32);
    }

    private void intializeDatabase() {

        mDatabaseSend = FirebaseDatabase.getInstance().getReference().child("chats").child(senderUsername).child(Data.currentUserName + "room");
        mDatabaseRecieve = FirebaseDatabase.getInstance().getReference().child("chats").child(Data.currentUserName).child(senderUsername + "room");
    }

    private ChildEventListener messageListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            callMessageRecieve(dataSnapshot);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            callMessageRecieve(dataSnapshot);
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

    private MessageInput.InputListener inputListener = new MessageInput.InputListener() {
        @Override
        public boolean onSubmit(CharSequence input) {

            cal = Calendar.getInstance();
            dateFormat = new SimpleDateFormat("hh:mm a");

            Message message = new Message("" + input, dateFormat.format(cal.getTime()), Data.currentUserName, "message", "");
            AddMessagetoList(message);
            addSendMessagetoList(message);
            return true;
        }
    };


    private void callMessageRecieve(DataSnapshot dataSnapshot) {

        Iterator i = dataSnapshot.getChildren().iterator();
        String message, time, userName, type, uri;

        while (i.hasNext()) {

            message = (String) ((DataSnapshot) i.next()).getValue();
            time = (String) ((DataSnapshot) i.next()).getValue();
            type = (String) ((DataSnapshot) i.next()).getValue();
            uri = (String) ((DataSnapshot) i.next()).getValue();
            userName = (String) ((DataSnapshot) i.next()).getValue();

            if (userName.equals(Data.currentUserName)) {
                callSearchingAdapter(message, time, false, type, uri);
                addMeesageDialogDatabasse(message, time);
            } else {
                callSearchingAdapter(message, time, true, type, uri);
                addMeesageDialogDatabasse(message, time);
            }
        }
    }

    private void AddMessagetoList(Message message) {
        Map<String, Object> map = new HashMap<>();
        String temp_key = mDatabaseSend.push().getKey();
        mDatabaseSend.updateChildren(map);

        DatabaseReference message_root = mDatabaseSend.child(temp_key);
        message_root.setValue(message);


    }

    private void addSendMessagetoList(Message message) {
        Map<String, Object> map = new HashMap<>();
        String temp_key = mDatabaseRecieve.push().getKey();
        mDatabaseRecieve.updateChildren(map);

        DatabaseReference message_root = mDatabaseRecieve.child(temp_key);
        message_root.setValue(message);

    }

    private void addMeesageDialogDatabasse(String message, String time) {

        MessageDialog messageDialogsender = new MessageDialog(Data.currentUserFullName, time, message, Data.currentUserName + "profile.jpg", 0, Data.currentUserName);
        mDatabaseDialogSender = FirebaseDatabase.getInstance().getReference();
        mDatabaseDialogSender.child("allchats").child(senderUsername).child(Data.currentUserName).setValue(messageDialogsender);

        MessageDialog messageDialogreciever = new MessageDialog(senderFullName, time, message, senderUsername + "profile.jpg", 0, senderUsername);
        mDatabaseDialogReciever = FirebaseDatabase.getInstance().getReference();
        mDatabaseDialogReciever.child("allchats").child(Data.currentUserName).child(senderUsername).setValue(messageDialogreciever);
    }

    private void callSearchingAdapter(String message, String time, boolean checker, String type, String uri) {

        if (checker) {
            messageList.add(new MessageModel(message, time, type, checker, uri));
            Data.messageDialogs.get(index).setLastMessage("" + message);
            Data.messageDialogs.get(index).setMessageTime("" + time);
        } else {
            messageList.add(new MessageModel(message, time, type, checker, uri));
            Data.messageDialogs.get(index).setLastMessage("" + message);
            Data.messageDialogs.get(index).setMessageTime("" + time);
        }
        adapter.notifyDataSetChanged();

    }

    private void addMessagelist() {

        cal = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("hh:mm a");


        if (Data.messageDialogs.size() == 0) {
            Data.messageDialogs.add(new MessageDialog(senderFullName, dateFormat.format(cal.getTime()), "", senderPicUri, 0, senderUsername));
            index = 0;
        } else {
            for (int i = 0; i < Data.messageDialogs.size(); i++) {
                if (Data.messageDialogs.get(i).getFullName().equals(senderFullName)) {
                    index = i;
                    found = true;
                }
            }
            if (!found) {
                Data.messageDialogs.add(new MessageDialog(senderFullName, dateFormat.format(cal.getTime()), "", senderPicUri, 0, senderUsername));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
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