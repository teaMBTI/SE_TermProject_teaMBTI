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
    private FirebaseAuth mAuth; //파이어베이스 인스턴스 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        //onCreate() 메서드에서 FirebaseAuth 인스턴스를 초기화합니다.
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
            } //회원가입 화면으로
        });

    }

    @Override
    //활동을 초기화할 때 사용자가 현재 로그인되어 있는지 확인합니다.
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    //createUserWithEmailAndPassword 메서드를 사용하여 이메일 주소와 비밀번호를 가져와 유효성을 검사한 후 신규 사용자를 만드는 새 createAccount 메서드를 만듭니다.
    private void login() {
        String email = ((EditText) findViewById(R.id.emailEditText)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();

        if (email.length() > 0 && password.length() > 0) { //editText에 입력되면
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                startToast("로그인 성공");
                                startListCourseRoomActivity(); //로그인 성공 시 메인 화면으로 이동
                            } else {
                                // If sign in fails, display a message to the user.
                                if (task.getException() != null) //로그인 중 에러 발생 시 메세지 출력
                                {
                                    startToast(task.getException().toString());
                                }

                                //실패 시 UI 로직
                            }
                        }
                    });

        } else {//editText에 입력 안됨
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
        System.exit(1); //메인에서 로그아웃 하고 뒤로가기했는데 또 메인 뜨는 일 없게 강제종료
    }

    private void startListCourseRoomActivity() {
        Intent intent = new Intent(this, ListCourseRoom.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 로그인 후 메인으로 가면 뒤로가기 시 다시 로그인정보가 들어 있는 화면이 아닌 앱꺼지기
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
        return "Email Invalid"; // 이메일 구조가 아니면 실패
    }

    public String isValidPassword(String password) {
        String testPasswordData = password;
        if (testPasswordData.length() > 5)
            return "Password Valid";
        else
            return "Password Invalid"; //비밀 번호 5자리 이하 실패
    }

    public String loginCheck(String email, String password) {
        String testEmailData = " email:harry@google.com, status:Student, email:mj@gmail.com, status:Student, email:bere@google.com, status:Student ...";
        String testPasswordData = "password:123456 , status:Student, password:183743, status:Professor ...";

        if (testEmailData.contains(email) && testPasswordData.contains(password)) {
                return "Login Success"; 
            }
        else
            return "Login Fail"; //일치하는 데이터가 없다면 실패
    }
}


