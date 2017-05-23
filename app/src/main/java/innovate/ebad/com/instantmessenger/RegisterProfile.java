package innovate.ebad.com.instantmessenger;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import innovate.ebad.com.instantmessenger.Constants.Data;
import innovate.ebad.com.instantmessenger.Model.Profile;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterProfile extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "ProfileActivity";

    Toolbar profileToolbar;

    private static final int REQUEST_WRITE_PERMISSION = 786;

    private Boolean exit = false;

    CircleImageView imageView;

    ImageButton profilePictureButton;
    Button doneButton;

    EditText userNameText, firstNameText, lastNameText, ageText;

    final boolean[] result = {true};
    String tempUserName = "";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private StorageReference mStorage;

    private Uri mCropImageUri;

    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_profile);

        profileToolbar = (Toolbar) findViewById(R.id.signUpToolbarProfile);
        setSupportActionBar(profileToolbar);
        setTitle("Profile");

        session = new UserSessionManager(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mStorage = FirebaseStorage.getInstance().getReference();

        profilePictureButton = (ImageButton) findViewById(R.id.picButton);

        imageView = (CircleImageView) findViewById(R.id.profilePictureImageView);

        doneButton = (Button) findViewById(R.id.doneButton);

        userNameText = (EditText) findViewById(R.id.profileUsernameTextview);
        firstNameText = (EditText) findViewById(R.id.firstNameTextView);
        lastNameText = (EditText) findViewById(R.id.lastNameTextView);
        ageText = (EditText) findViewById(R.id.ageTextView);

        // Setting Listners

        userNameText.setOnFocusChangeListener(userNameListener);

        doneButton.setOnClickListener(donebuttonListener);

        profilePictureButton.setOnClickListener(profilePictureListener);

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


    private void setUpProfile() {
        if (!validateForm()) {
            return;
        }

        if (!result[0]) {
            showUserRegisterError();
        }

        String firstNames, userNames, lastNames, picuri;
        int age;

        firstNames = firstNameText.getText().toString();
        lastNames = lastNameText.getText().toString();
        userNames = userNameText.getText().toString();

        picuri = "default";

        age = Integer.parseInt(ageText.getText().toString());

        showProfileDialog();
        writeNewUserData(userNames, firstNames, lastNames, age, picuri);

       /* Intent intent = new Intent(RegisterProfile.this, TemporaryActivity.class);
        intent.putExtra("USER_NAME", userNames);
        startActivity(intent);
*/
    }

    private void writeNewUserData(String username, String first, String last, int age, String picURI) {

        Profile profile = new Profile(first, last, picURI, age);

        uploadFromUri(mCropImageUri, username, profile);
        Log.d("picURI", String.valueOf(profile.getPictureURI()));
        mDatabase.child("username").child(username).setValue(mAuth.getCurrentUser().getUid());
        //  mDatabase.child("profile").child(username).setValue(profile);
        // mAuth.getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(username));
    }

    private void validateUserName(String username) {
        tempUserName = username;

        if (validateUsernamecharacter(username)) {
            final String[] tempuser = new String[1];

            mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("username").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    tempuser[0] = dataSnapshot.getValue(String.class);
                    Log.wtf("valuetemp", tempuser[0]);

                    if (tempuser[0] != null) {
                        result[0] = false;
                        Log.wtf(TAG, "" + result[0]);
                        showUserRegisterError();
                    } else {
                        result[0] = true;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            Log.wtf("valuetemp", tempuser[0]);
            Log.wtf(TAG + "me", "" + result[0]);
        } else {
            userNameText.setError("Invalid username");
        }
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(firstNameText.getText().toString())) {
            firstNameText.setError("Required");
            result = false;
        } else {
            firstNameText.setError(null);
        }

        if (TextUtils.isEmpty(lastNameText.getText().toString())) {
            lastNameText.setError("Required");
            result = false;
        } else {
            lastNameText.setError(null);
        }

        if (TextUtils.isEmpty(ageText.getText().toString())) {
            ageText.setError("Required");
            result = false;
        } else {
            ageText.setError(null);
        }

        if (TextUtils.isEmpty(userNameText.getText().toString())) {
            userNameText.setError("Required");
            userNameText.requestFocus();
            result = false;
        } else {
            userNameText.setError(null);
        }

        if (mCropImageUri == null) {
            Toast.makeText(getApplicationContext(), "Please update profile picture", Toast.LENGTH_SHORT).show();
            result = false;
        }

        return result;
    }

    private void showUserRegisterError() {
        userNameText.setError("Username already taken");
        userNameText.requestFocus();
    }

    private void validateUserName2(String username) {
        tempUserName = username;

        if (validateUsernamecharacter(username)) {


            final String[] tempuser = new String[1];

            mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("username").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    tempuser[0] = dataSnapshot.getValue(String.class);
                    Log.wtf("valuetemp", tempuser[0]);

                    if (tempuser[0] != null) {
                        result[0] = false;
                        Log.wtf(TAG, "" + result[0]);
                        showUserRegisterError();
                    } else {
                        result[0] = true;
                        setUpProfile();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            Log.wtf("valuetemp", tempuser[0]);
            Log.wtf(TAG + "me", "" + result[0]);
        } else {
            userNameText.setError("Invalid username");
        }
    }

    private static boolean validateUsernamecharacter(String username) {
        boolean check = true;

        if (username.contains("$") || username.contains(".") || username.contains("[") || username.contains("]") || username.contains("#")) {
            check = false;
        }
        return check;
    }

    private void requestPermission(Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {
            savefile(uri);
        }
    }

    public void onSelectImageClick(View view) {
        startCropImageActivity(null);
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setMultiTouchEnabled(true)
                .setActivityTitle("Set Profile Picture")
                .setInitialCropWindowPaddingRatio(0)
                .start(this);
    }

    private void savefile(Uri sourceuri) {

        String sourceFilename = sourceuri.getPath();
        String destinationFilename = android.os.Environment.getExternalStorageDirectory().getPath() + File.separatorChar + "profile.jpg";


        File file = new File(destinationFilename);
        if (file.exists()) {
            file.delete();
            Log.wtf("Its deleted bitch", "yes");
        }

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFilename));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        addImageToGallery(destinationFilename, RegisterProfile.this);
    }

    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private void uploadFromUri(Uri uri, final String userName, final Profile profile) {

        final StorageReference photoRef = mStorage.child(userName)
                .child("profilePicture");

        photoRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri downloadUri = taskSnapshot.getDownloadUrl();

                        if (downloadUri != null) {
                            Log.wtf("fuckthisuri", String.valueOf(downloadUri));
                            profile.setPictureURI(downloadUri.toString());
                            mDatabase.child("profile").child(userName).setValue(profile);
                            mDatabase.child("profilepicture").child(userName).setValue(downloadUri.toString());
                            writeToUserSessionManager(profile.getFirstName() + " " + profile.getLastName(), mAuth.getCurrentUser().getEmail(), userName, downloadUri.toString());
                            updateUserProfiledata(userName, downloadUri);
                            //hideProgressDialog();
                        }
                        //      callNextActivity(userName);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Upload failed
                        Log.w(TAG, "uploadFromUri:onFailure", exception);
                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                        hideProgressDialog();
                       // Toast.makeText(getApplicationContext(), "Error Uploading picture", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserProfiledata(final String userName, Uri uri) {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .setPhotoUri(uri)
                .build();

        assert user != null;
        user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Log.d("profileShit", "User profile updated");
                    hideProgressDialog();
                    callNextActivity(userName);
                } else {
                    Log.d("profileShit", "This profile is not fucking updated");
                }
            }
        });
    }

    private void callNextActivity(String userName) {
        Intent intent = new Intent(RegisterProfile.this, MainMessageActivity.class);
        intent.putExtra("USER_NAME", userName);
        startActivity(intent);
        finish();
    }


    private void writeToUserSessionManager(String fullName, String email, String UserName, String picUrl) {
        session.createUserLoginSession(fullName, email, UserName, picUrl);
        Data.currentUserFullName = fullName;
    }

    private View.OnFocusChangeListener userNameListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (!hasFocus) {
              //  Toast.makeText(getApplicationContext(), "fuck it", Toast.LENGTH_SHORT).show();
                String userNameme = "";
                userNameme = userNameText.getText().toString();
                if (!userNameme.equals("")) {
                    if (userNameText.getText().toString().length() > 6) {
                        validateUserName(userNameText.getText().toString());
                    } else {
                        userNameText.setError(getString(R.string.error_username_shorter));
                    }
                }
            }
        }
    };


    private View.OnClickListener donebuttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            if (validateForm()) {

                if (tempUserName.equals(userNameText.getText().toString())) {
                    if (result[0]) {
                        setUpProfile();
                    } else {
                        showUserRegisterError();
                    }
                } else {
                    Log.wtf(TAG + "buttonlistenertwo", userNameText.getText().toString());
                    if (userNameText.getText().toString().length() > 6) {
                        validateUserName2(userNameText.getText().toString());
                    } else {
                        userNameText.setError(getString(R.string.error_username_shorter));
                    }
                }
            }
        }
    };


    private View.OnClickListener profilePictureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onSelectImageClick(v);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageView.setImageURI(result.getUri());
             //   Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
                mCropImageUri = result.getUri();
                requestPermission(result.getUri());
                //savefile(result.getUri());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
              //  Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (mCropImageUri != null) {
                savefile(mCropImageUri);
            }
        }
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
}

