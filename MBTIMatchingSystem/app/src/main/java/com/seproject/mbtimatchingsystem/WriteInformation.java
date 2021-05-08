package com.seproject.mbtimatchingsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
//test push commit
public class WriteInformation extends AppCompatActivity {

    Button studentBtn;
    Button professorBtn;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient; //구글 api 클라이언트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_information);

        studentBtn = findViewById(R.id.studentBtn);
        professorBtn= findViewById(R.id.professorBtn);

        mAuth = FirebaseAuth.getInstance();

        /* buttons click*/
        studentBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

               // getSupportFragmentManager().beginTransaction().replace(R.id.menu_fragment_container,agentInformation).commit();
               // studentBtn.setImageResorce(R.drawable.나중에 채움);
            }
        });

        professorBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // getSupportFragmentManager().beginTransaction().replace(R.id.menu_fragment_container,agentInformation).commit();
                // professorBtn.setImageResorce(R.drawable.나중에 채움);
            }
        });
    }


}