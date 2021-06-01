package com.seproject.mbtimatchingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth; //Firebase Instance Declaration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        Button button2 = findViewById(R.id.loginButton);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        Button button3 = findViewById(R.id.gotoJoinButton);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startJoinActivity();
            } //Go to Membership Screen
        });

    }

    @Override
    //When initializing an activity, make sure that the user is currently logged in.
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    //    Use the createUserWithEmailAndPassword method to obtain email addresses and passwords,
//    validate them, and create a new createAccount method to create new users.
    private void login() {
        String email = ((EditText) findViewById(R.id.emailEditText)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();

        if (email.length() > 0 && password.length() > 0) { //When entered in editText
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                startToast("로그인 성공");
                                startListCourseRoomActivity(); //Go to main screen on login success
                            } else {
                                // If sign in fails, display a message to the user.
                                if (task.getException() != null) //Print a message when an error occurs while logging in.
                                {
                                    startToast(task.getException().toString());
                                }
                            }
                        }
                    });

        } else { //Not entered in editText
            startToast("이메일 또는 비밀번호를 입력해주세요.");
        }
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1); //Force Termination
    }

    private void startListCourseRoomActivity() {
        Intent intent = new Intent(this, ListCourseRoom.class);
         // If user go back to the main after logging in, turn off the app, not the screen that contains the login information.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void startJoinActivity() {
        Intent intent = new Intent(this, JoinActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public String isValidEmail(String email) {
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if (m.matches()) {
            return "Email Valid";
        }
        return "Email Invalid"; // Failure unless email structure
    }

    public String isValidPassword(String password) {
        String testPasswordData = password;
        if (testPasswordData.length() > 5)
            return "Password Valid";
        else
            return "Password Invalid"; //Password less than 5 digits failed
    }

    public String loginCheck(String email, String password) {
        String testEmailData = " email:harry@google.com, status:Student, email:mj@gmail.com, status:Student, email:bere@google.com, status:Student ...";
        String testPasswordData = "password:123456 , status:Student, password:183743, status:Professor ...";

        if (testEmailData.contains(email) && testPasswordData.contains(password)) {
                return "Login Success"; 
            }
        else
            return "Login Fail"; //Failure if no data matches
    }
}


