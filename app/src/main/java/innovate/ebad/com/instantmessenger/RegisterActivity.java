package innovate.ebad.com.instantmessenger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import innovate.ebad.com.instantmessenger.Model.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterActivity extends BaseActivity {


    private static final String TAG = "SignUpActivity";
    final boolean[] result = {true};


    Toolbar registerToolbar;
    Button signUpButton;
    Button facebookButton, twitterButton, googleButton;
    EditText emailAddressText, passwordText;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerToolbar = (Toolbar) findViewById(R.id.signInToolbarRegister);
        setSupportActionBar(registerToolbar);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();


        signUpButton = (Button) findViewById(R.id.signUpButton);

        facebookButton = (Button) findViewById(R.id.buttonFacebook);
        twitterButton = (Button) findViewById(R.id.buttonTwitter);
        googleButton = (Button) findViewById(R.id.buttonGoogle);


        emailAddressText = (EditText) findViewById(R.id.signUpEmail);
        passwordText = (EditText) findViewById(R.id.signUpPassword);

        signUpButton.setOnClickListener(signUpListener);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Register");


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


    @Override
    protected void onResume() {
        super.onResume();

    }

    private void signUP() {
        Log.d(TAG, "SignuP");
        if (!validateForm()) {
            return;
        }
        showProgressDialog();

        String email = emailAddressText.getText().toString();
        String password = passwordText.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                hideProgressDialog();

                if (task.isSuccessful()) {
                    onAuthSuccess(task.getResult().getUser());
                } else {
                    Log.wtf(TAG, task.getException());
                    Toast.makeText(RegisterActivity.this, "Sign Up Failed",
                            Toast.LENGTH_SHORT).show();

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        emailAddressText.setError(getString(R.string.error_invalid_email));
                        emailAddressText.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        emailAddressText.setError(getString(R.string.error_email_exist));
                        emailAddressText.requestFocus();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void onAuthSuccess(FirebaseUser user) {

        // Write new user
        writeNewUser(user.getUid(), user.getEmail());

        // Go to MainActivity
        startActivity(new Intent(RegisterActivity.this, RegisterProfile.class));
        finish();
    }

    private void writeNewUser(String uid, String email) {

        User user = new User(email, passwordText.getText().toString());

        mDatabase.child("users").child(uid).setValue(user);
        //   mDatabase.child("username").child(username).setValue(uid);
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(emailAddressText.getText().toString())) {
            emailAddressText.setError("Required");
            result = false;
        } else {
            emailAddressText.setError(null);
        }

        if (TextUtils.isEmpty(passwordText.getText().toString())) {
            passwordText.setError("Required");
            result = false;
        } else if (passwordText.getText().toString().length() < 6) {
            passwordText.setError(getString(R.string.error_weak_password));
            result = false;
        } else {
            passwordText.setError(null);
        }

        return result;
    }


    private View.OnClickListener signUpListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (validateForm()) {
                signUP();
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


}
