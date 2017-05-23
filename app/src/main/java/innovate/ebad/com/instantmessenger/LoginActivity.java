package innovate.ebad.com.instantmessenger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import innovate.ebad.com.instantmessenger.Constants.Data;
import innovate.ebad.com.instantmessenger.Model.Profile;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends BaseActivity {

    Toolbar signIntoolbar;
    Button signInButton;
    Button forgotPassword;
    EditText emailText, passwordText;

    private FirebaseAuth mAuth;

    UserSessionManager session;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        signIntoolbar = (Toolbar) findViewById(R.id.signInToolbar);
        setSupportActionBar(signIntoolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Login");

        mAuth = FirebaseAuth.getInstance();
        session = new UserSessionManager(getApplicationContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();


        signInButton = (Button) findViewById(R.id.signInAButton);
        forgotPassword = (Button) findViewById(R.id.forgotPassword);

        emailText = (EditText) findViewById(R.id.emailTextView);
        passwordText = (EditText) findViewById(R.id.passwordTextview);

        forgotPassword.setOnClickListener(forgotPasswordListener);

        signInButton.setOnClickListener(signInButtonListener);

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

    private void checkLogin() {

        if (validateForm()) {

            showLoginDialog();

            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    hideProgressDialog();
                    if (task.isSuccessful()) {
                        //Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                        if (mAuth.getCurrentUser().getDisplayName() != null) {
                            setUserProfileData(mAuth.getCurrentUser().getDisplayName());
                        }
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();

                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }



    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(emailText.getText().toString())) {
            emailText.setError("Required");
            result = false;
        } else {
            emailText.setError(null);
        }

        if (TextUtils.isEmpty(passwordText.getText().toString())) {
            passwordText.setError("Required");
            result = false;
        } else {
            passwordText.setError(null);
        }
        return result;
    }

    private void setUserProfileData(final String username) {
        mDatabase.child("profile").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile profile = dataSnapshot.getValue(Profile.class);

                if (profile != null) {
                    //callUserFoundAdapter(profile, username, "gs://instant-messenger-e778f.appspot.com/" + username + "/profilePicture");
                    writeToUserSessionManager(profile.getFirstName() + " " + profile.getLastName(), mAuth.getCurrentUser().getEmail(), mAuth.getCurrentUser().getDisplayName(), String.valueOf(mAuth.getCurrentUser().getPhotoUrl()));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void writeToUserSessionManager(String fullName, String email, String UserName, String picUrl) {
        session.createUserLoginSession(fullName, email, UserName, picUrl);
        Data.currentUserFullName = fullName;
        callMessageActivity(UserName);
    }

    private View.OnClickListener signInButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            checkLogin();
        }
    };

    private View.OnClickListener forgotPasswordListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new MaterialDialog.Builder(LoginActivity.this)
                    .title("Forgot password")
                    .inputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                    .positiveText("Submit")
                    .negativeText("Cancel")
                    .cancelable(false)
                    .input("Enter your email", "", new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                            if (input.length() > 0) {
                                // Log.d("dialoginput", String.valueOf(input));
                                //Toast.makeText(getApplicationContext(), "" + input, Toast.LENGTH_SHORT).show();
                                forgotpasswordcaller("" + input);
                            } else {
                                Toast.makeText(getApplicationContext(), "Please enter your email.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }).show();

        }
    };

    private void callMessageActivity(String userName) {
        Intent intent = new Intent(LoginActivity.this, MainMessageActivity.class);
        intent.putExtra("USER_NAME", userName);
        startActivity(intent);
        finish();
    }

    private void forgotpasswordcaller(String email) {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            new MaterialDialog.Builder(LoginActivity.this)
                                    .content("Email containing Password Reset Link has been sent to your email.")
                                    .positiveText("OK")
                                    .show();

                        } else {
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                e.printStackTrace();
                            }

                        }
                    }
                });
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
