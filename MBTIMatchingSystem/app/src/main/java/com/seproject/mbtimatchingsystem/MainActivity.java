package com.seproject.mbtimatchingsystem;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class MainActivity extends AppCompatActivity{

    private FirebaseAuth mAuth=null; //파이어베이스 인스턴스 선언
    private GoogleSignInClient mGoogleSignInClient; //구글 api 클라이언트
    private static final int REQ_SIGN_IN = 9001; //구글 로그인 결과 코드
    private SignInButton googleButton;
    Button logOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth =FirebaseAuth.getInstance(); //파이어베이스 인증 객체 선언

        /*    if(mAuth.getCurrentUser()!=null) { //이미 로그인 되어있으면 다음 액티비티
       Intent intent = new Intent(getApplication(),WriteInformation.class);
       startActivity(intent);
       finish();
       }
       */

        //Configure Google Sign In
        //GoogleSignInOptions 개체를 구성할 때 requestIdToken을 호출
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient= GoogleSignIn.getClient(this, gso);

        googleButton = findViewById(R.id.signInButton);
        googleButton.setOnClickListener(new View.OnClickListener(){ //구글 로그인 버튼 누르면
            @Override
            public void onClick(View view){
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, REQ_SIGN_IN);
            }
        });


        //로그아웃 버튼
        logOutButton = findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                FirebaseAuth.getInstance().signOut();
                mGoogleSignInClient.signOut();
                startToast("로그아웃 하셨습니다.");
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //Result returnned from launching the Intent from GoogleSignApi.getSignInIntent(...);
        //구글로그인 버튼 응답
        if(requestCode == REQ_SIGN_IN){
            GoogleSignInResult result= Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()) //인증결과가 성공적이라면
            {
                GoogleSignInAccount account = result.getSignInAccount();
                resultLogin(account); //로그인 결과 값 출력하라는 매서드
            }
        }
    }

    // 사용자가 정상적으로 로그인한 후에 GoogleSignInAccount 개체에서 ID 토큰을 가져와서
    // Firebase 사용자 인증 정보로 교환하고 Firebase 사용자 인증 정보를 사용해 Firebase에 인증합니다.
    private void resultLogin(GoogleSignInAccount account) { //구글 로그인 결과
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) //로그인에 성공
                        {
                            startToast("로그인 성공");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }else{ //로그인 실패
                            startToast("로그인 실패");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(null);
                        }
                    }
                });
    }

  /*  // 사용자가 정상적으로 로그인한 후에 GoogleSignInAccount 개체에서 ID 토큰을 가져와서
    // Firebase 사용자 인증 정보로 교환하고 Firebase 사용자 인증 정보를 사용해 Firebase에 인증합니다.
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                        if(task.isSuccessful()){
                            //로그인 성공, update UI with the signed-in user's information
                            Snackbar.make(findViewById(R.id.layout_main), "Authentication Successed.", Snackbar.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else{
                            //로그인 실패, display a message to the user.
                            Snackbar.make(findViewById(R.id.layout_main),"Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }*/

    private void updateUI(FirebaseUser user){ //로그인 후에 다음 액티비티로 intent를 옮긴다.
        if(user !=null){
            Intent intent = new Intent(this, WriteInformation.class);
            startActivity(intent);
        }
    }
    private void startToast(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}