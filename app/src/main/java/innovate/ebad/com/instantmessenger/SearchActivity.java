package innovate.ebad.com.instantmessenger;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import innovate.ebad.com.instantmessenger.Adapter.FriendAdapter;
import innovate.ebad.com.instantmessenger.Adapter.UserFoundAdapter;
import innovate.ebad.com.instantmessenger.Adapter.UserNotFoundAdapter;
import innovate.ebad.com.instantmessenger.Adapter.UserSearchingAdapater;
import innovate.ebad.com.instantmessenger.Model.Profile;
import innovate.ebad.com.instantmessenger.Model.SearchProfile;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SearchActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText searchText;
    ImageView searchClearButton;

    ListView userSearchListView;


    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    ArrayList<SearchProfile> friendList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        searchForFriends();

        userSearchListView = (ListView) findViewById(R.id.searchListView);
        userSearchListView.setDivider(null);
        userSearchListView.setDividerHeight(0);


        searchText = (EditText) findViewById(R.id.search_view);
        searchClearButton = (ImageView) findViewById(R.id.search_clear);


        searchClearButton.setOnClickListener(searchClearButtonListener);


        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                Log.e("onTextChanged", "" + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("afterTextChanged", "" + s.length());
                if (s.length() == 0) {
                    callFriendFound(friendList);
                } else {
                    userSearchListView.setAdapter(null);
                }
            }
        });


        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    if (!searchText.getText().toString().isEmpty()) {
                        validateUserName(searchText.getText().toString());
                        callSearchingAdapter();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please Enter username", Toast.LENGTH_SHORT).show();
                    }

                    return true;
                }
                return false;
            }
        });


    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void callSearchingAdapter() {

        ArrayList<String> list = new ArrayList<>();
        list.add("Searching");

        ArrayAdapter<String> adapter = new UserSearchingAdapater(SearchActivity.this, list);
        userSearchListView.setAdapter(adapter);
    }

    private void callUserFoundAdapter(Profile profile, String userName, Uri uri) {

        String fullname = profile.getFirstName() + " " + profile.getLastName();

        ArrayList<SearchProfile> list = new ArrayList<>();
        list.add(new SearchProfile(fullname, userName, uri));


        ArrayAdapter<SearchProfile> adapter = new UserFoundAdapter(SearchActivity.this, list);
        userSearchListView.setAdapter(adapter);

    }

    private void callFriendFound(ArrayList<SearchProfile> list) {

        ArrayAdapter<SearchProfile> adapter = new FriendAdapter(SearchActivity.this, list);
        userSearchListView.setAdapter(adapter);

    }

    private void callUserNotFoundAdapater(String userName) {

        String text = "Username " + "'" + userName + "'" + " not found";

        ArrayList<String> list = new ArrayList<>();
        list.add(text);

        ArrayAdapter<String> adapter = new UserNotFoundAdapter(SearchActivity.this, list);
        userSearchListView.setAdapter(adapter);
    }

    private void validateUserName(final String username) {

        final String[] tempuser = new String[1];

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("username").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tempuser[0] = dataSnapshot.getValue(String.class);
                Log.wtf("valuetemp", tempuser[0]);

                if (tempuser[0] != null) {
                    setUserProfileData(username);
                } else {
                    //user doesnt exist
                    callUserNotFoundAdapater(username);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.wtf("valuetemp", tempuser[0]);
    }

    private void setUserProfileData(final String username) {
        mDatabase.child("profile").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile profile = dataSnapshot.getValue(Profile.class);

                if (profile != null) {
                    //callUserFoundAdapter(profile, username, "gs://instant-messenger-e778f.appspot.com/" + username + "/profilePicture");
                    downloadFile(username, profile);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void searchForFriends() {
        mDatabase.child("friends").child(mAuth.getCurrentUser().getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String username = snapshot.getKey();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                            String fullname = (String) snapshot1.getValue();
                            String path = android.os.Environment.getExternalStorageDirectory().getPath() + File.separatorChar + username + "profile.jpg";

                            File file = new File(path);
                            if(file.exists())
                            {
                                friendList.add(new SearchProfile(fullname, username, Uri.parse(path)));
                            }
                            else {
                                friendList.add(new SearchProfile(fullname, username, Uri.parse("")));
                                downloadFileForFriend(username);
                            }

                            Log.e("friends", username + fullname);
                        }
                    }
                    callFriendFound(friendList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private View.OnClickListener searchClearButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            searchText.setText("");
            userSearchListView.setAdapter(null);
            callFriendFound(friendList);
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


    private void downloadFile(final String username, final Profile profile) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReferenceFromUrl("gs://instant-messenger-e778f.appspot.com/" + username + "/");


        StorageReference islandRef = storageRef.child("profilePicture");
        String destinationFilename = android.os.Environment.getExternalStorageDirectory().getPath() + File.separatorChar;


        File rootPath = new File(destinationFilename);
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath, username + "profile.jpg");

        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ", ";local tem file created  created " + localFile.toString());
                String destionation = android.os.Environment.getExternalStorageDirectory().getPath() + File.separatorChar + username + "profile.jpg";
                callUserFoundAdapter(profile, username, Uri.parse(destionation));
                //  updateDb(timestamp,localFile.toString(),position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ", ";local tem file not created  created " + exception.toString());
            }
        });
    }

    private void downloadFileForFriend(final String username) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReferenceFromUrl("gs://instant-messenger-e778f.appspot.com/" + username + "/");


        StorageReference islandRef = storageRef.child("profilePicture");
        String destinationFilename = android.os.Environment.getExternalStorageDirectory().getPath() + File.separatorChar;


        File rootPath = new File(destinationFilename);
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath, username + "profile.jpg");

        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ", ";local tem file created  created " + localFile.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ", ";local tem file not created  created " + exception.toString());
            }
        });
    }



}
