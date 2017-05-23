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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stfalcon.frescoimageviewer.ImageViewer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import innovate.ebad.com.instantmessenger.OptionActivities.YourAccount;
import innovate.ebad.com.instantmessenger.OptionActivities.YourChatSetting;
import innovate.ebad.com.instantmessenger.OptionActivities.YourNotification;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileSettingActivity extends BaseActivity {

    Toolbar mainProfileToolbar;

    private static final int REQUEST_WRITE_PERMISSION = 786;


    CircleImageView profilePictureImageView;

    String[] optionArray = {"View Photo", "Change Photo"};
    int[] optionID = {0, 1};

    private Uri mCropImageUri;

    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    TextView userNameTextView, fullNameTextView;

    RippleView rippleView, rippleView2, rippleView3, rippleView4;

    String fullname, email, userNames, picURL;

    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mainProfileToolbar = (Toolbar) findViewById(R.id.mainProfileToolbar);
        setSupportActionBar(mainProfileToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Profile");


        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        userNameTextView = (TextView) findViewById(R.id.profileUsernameTextview);
        fullNameTextView = (TextView) findViewById(R.id.profileNameTextview);


        setProfileData();

        rippleView = (RippleView) findViewById(R.id.rippleView);
        rippleView2 = (RippleView) findViewById(R.id.rippleView2);
        rippleView3 = (RippleView) findViewById(R.id.rippleView3);
        rippleView4 = (RippleView) findViewById(R.id.rippleView4);


        profilePictureImageView = (CircleImageView) findViewById(R.id.profilePictureImageView);
        loadProfilePictureFromPhone();
        profilePictureImageView.setOnClickListener(profilePictureButtonListener);


        rippleView.setOnClickListener(rippleViewListener);
        rippleView2.setOnClickListener(rippleView2Listener);
        rippleView3.setOnClickListener(rippleView3Listener);
        rippleView4.setOnClickListener(rippleView4Listener);
    }


    private void setProfileData() {

        session = new UserSessionManager(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();

        fullname = user.get(UserSessionManager.KEY_FULLNAME);

        // get email
        email = user.get(UserSessionManager.KEY_EMAIL);

        userNames = user.get(UserSessionManager.KEY_USERNAME);

        picURL = user.get(UserSessionManager.KEY_PICURL);
        userNameTextView.setText(userNames);
        fullNameTextView.setText(fullname);
    }


    private void loadProfilePictureFromPhone() {

        String destinationFilename = android.os.Environment.getExternalStorageDirectory().getPath() + File.separatorChar + "profile.jpg";

        File file = new File(destinationFilename);
        if (file.exists()) {
            profilePictureImageView.setImageURI(Uri.parse(destinationFilename));
        } else {
            downloadFile(userNames);

        }
    }

    private void downloadFile(String username) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReferenceFromUrl("gs://instant-messenger-e778f.appspot.com/" + username + "/");
        Log.e("firebase ", storageRef.toString());

        StorageReference islandRef = storageRef.child("profilePicture");
        String destinationFilename = android.os.Environment.getExternalStorageDirectory().getPath() + File.separatorChar;


        File rootPath = new File(destinationFilename);
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath, "profile.jpg");

        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                loadProfilePictureFromPhone();
                Log.e("firebase ", ";local tem file created  created " + localFile.toString());
                //  updateDb(timestamp,localFile.toString(),position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ", ";local tem file not created  created " + exception.toString());
            }
        });
    }

    private void loadImageToFullScreen(String[] picarray) {
        new ImageViewer.Builder(ProfileSettingActivity.this, picarray)
                .setBackgroundColorRes(R.color.myblack)
                .setStartPosition(0)
                .show();
    }

    // For saving,croping and get result from crop activities
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

        addImageToGallery(destinationFilename, ProfileSettingActivity.this);
    }

    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                profilePictureImageView.setImageURI(result.getUri());
                //Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_SHORT).show();

                mCropImageUri = result.getUri();
                requestPermission(result.getUri());

                if (mCropImageUri != null && mAuth.getCurrentUser() != null) {
                    uploadFromUri(mCropImageUri, mAuth.getCurrentUser().getDisplayName());
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    // For requesting write permission for new User
    private void requestPermission(Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {
            savefile(uri);
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


    // For Updating User Picture Again
    private void uploadFromUri(Uri uri, final String userName) {

        final StorageReference photoRef = mStorage.child(userName)
                .child("profilePicture");

        showProfilePictureUpdateDialog();

        photoRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri downloadUri = taskSnapshot.getDownloadUrl();

                        if (downloadUri != null) {
                            Log.wtf("fuckthisuri", String.valueOf(downloadUri));
                            mDatabase.child("profilepicture").child(userName).setValue(downloadUri.toString());
                            updateUserProfiledata(downloadUri);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Upload failed
                        Log.w("shithappens", "uploadFromUri:onFailure", exception);
                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                        hideProgressDialog();
                        Toast.makeText(getApplicationContext(), "Error Uploading picture", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserProfiledata(Uri uri) {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        assert user != null;
        user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Log.d("profileShit", "User profile updated");
                    hideProgressDialog();
                } else {
                    Log.d("profileShit", "This profile is not fucking updated");
                }
            }
        });
    }


    private View.OnClickListener rippleViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent yourAccountIntent = new Intent(ProfileSettingActivity.this, YourAccount.class);
                    yourAccountIntent.putExtra("fullName", fullname);
                    yourAccountIntent.putExtra("email", email);
                    yourAccountIntent.putExtra("username", userNames);
                    startActivity(yourAccountIntent);
                }
            }, 400);
        }
    };


    private View.OnClickListener rippleView2Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(ProfileSettingActivity.this, YourNotification.class));
                }
            }, 400);
        }
    };

    private View.OnClickListener rippleView3Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(ProfileSettingActivity.this, YourChatSetting.class));
                }
            }, 400);
        }
    };

    private View.OnClickListener rippleView4Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            new MaterialDialog.Builder(ProfileSettingActivity.this)
                    .title("Log Out")
                    .content("Are you sure you would like to log out? Your chat history will be cleared and you'll need to login again")
                    .positiveText("Yes")
                    .negativeText("No")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (mAuth.getCurrentUser() != null) {
                                mAuth.signOut();
                                Intent intent = new Intent(ProfileSettingActivity.this, GetStartedActivity.class);
                                session.logoutUser();
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    })
                    .show();
        }
    };

    private View.OnClickListener profilePictureButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            new MaterialDialog.Builder(ProfileSettingActivity.this)
                    .title("Profile Picture")
                    .items((CharSequence[]) optionArray)
                    .itemsIds(optionID)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int position, CharSequence text) {
                            if (position == 0) {
                                String destinationFilename = android.os.Environment.getExternalStorageDirectory().getPath() + File.separatorChar + "profile.jpg";
                                File file = new File(destinationFilename);
                                if (file.exists()) {
                                    final String[] picarray = {"file://" + destinationFilename};
                                    dialog.dismiss();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadImageToFullScreen(picarray);
                                        }
                                    }, 300);
                                } else {
                                    Toast.makeText(getApplicationContext(), "No Picture found", Toast.LENGTH_SHORT).show();
                                }
                            } else if (position == 1) {
                                onSelectImageClick(view);
                            }
                        }
                    })
                    .show();
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