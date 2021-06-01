package com.seproject.mbtimatchingsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class WritePInformation extends AppCompatActivity {
    private DatabaseReference mPostReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    public String name;
    public String id;
    public CheckBox checkBox;
    public String status;
    public String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinfo);

        mFirebaseAuth = FirebaseAuth.getInstance(); //현재 유저 정보를 얻어온다

        EditText name2 = findViewById(R.id.nameEditText);
        EditText id2 = findViewById(R.id.idEditText);
        checkBox = findViewById(R.id.professor);
        checkBox.setChecked(true);
        if(checkBox.isChecked()){
            status = "Professor"; // 초기화면부터 체크박스 활성화  
        }

        Button save_prof = findViewById(R.id.save_prof);
        save_prof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = name2.getText().toString();
                id = id2.getText().toString();

                postFirebaseDatabase(true);

                startLoginActivity(); //정보 저장 성공 시 로그인 화면으로 이동
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // 사용자 이메일 가져오기
                email = profile.getEmail();

            }
        }
    }
 
    // 입력된 정보 파이어베이스 db에 저장
    public void postFirebaseDatabase(boolean add){
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            FirebasePost post = new FirebasePost(id, name, status, email);
            postValues = post.toMap();
        }
        childUpdates.put("/id_list/" + id, postValues);
        mPostReference.updateChildren(childUpdates);
    }

    // 로그인 화면으로 전환
    private void startLoginActivity()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}