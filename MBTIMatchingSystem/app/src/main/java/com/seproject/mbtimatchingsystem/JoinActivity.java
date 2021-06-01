package com.seproject.mbtimatchingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class JoinActivity extends AppCompatActivity {
    private static final String TAG = "JoinActivity";
    private FirebaseAuth mAuth; //Firebase Instance Declaration


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_member);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        Button button = findViewById(R.id.save); //Create membership button object & click listener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinMember();
            }
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
    private void joinMember()
    {
        String name = ((EditText)findViewById(R.id.nameEditText)).getText().toString();
        String email = ((EditText)findViewById(R.id.emailEditText)).getText().toString();
        String password = ((EditText)findViewById(R.id.passwordEditText)).getText().toString();
        String passwordCheck = ((EditText)findViewById(R.id.passwordCheckEditText)).getText().toString();
        CheckBox student = findViewById(R.id.chk_stud);
        CheckBox professor = findViewById(R.id.chk_prof);

        if(name.length() > 0 && email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0) { //editText에 입력되면

            if (password.equals(passwordCheck)) //Complete membership when both password and password verification match
            {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    //Log.d(TAG, "createUserWithEmail:success");
                                    startToast("회원가입 성공");
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "사용자 이름 등록됨.");
                                                    }
                                                }
                                            });
                                    if(professor.isChecked())
                                    {
                                        startWritePInfoActivity();
                                    }
                                    if(student.isChecked())
                                    {
                                        startWriteSInfoActivity();
                                    }

                                    finish(); //Delete the entered information

                                    //UI Logic on Success
                                } else {
                                    // If sign in fails, display a message to the user.
                                    //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    if (task.getException() != null) //Print a message when an error occurs.
                                    {
                                        startToast(task.getException().toString());
                                    }

                                    //UI logic on failure
                                }
                            }
                        });
            } else {
                //Print a toast message if the password and password verification do not match.
                startToast("비밀번호가 일치하지 않습니다!");
            }
        } else {//Not entered in editText
            startToast("정보를 입력해주세요.");
            }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1); //Forced Termination
    }

    private void startToast(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void startWriteSInfoActivity()
    {
        Intent intent = new Intent(this, WriteSInformation.class);
        startActivity(intent);
    }

    private void startWritePInfoActivity()
    {
        Intent intent2 = new Intent(this, WritePInformation.class);
        startActivity(intent2);
    }

}

