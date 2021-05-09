package com.seproject.mbtimatchingsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WriteSInformation extends AppCompatActivity {
    private DatabaseReference mPostReference;
   public String name;
   public String id;
   public String[] MBTI = {"ISTJ","ISFJ","INFJ","INTJ",
                            "ISTP","ISFP","INFP","INTP",
                            "ESTP","ESFP","ENFP","ENTP",
                            "ESTJ","ESFJ","ENFJ","ENTJ"};
   public String mbti;
   public String call;
   public String katalk;
   public CheckBox checkBox;
   public String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sinfo);

        EditText name2 = findViewById(R.id.nameEditText);
        EditText id2 = findViewById(R.id.idEditText);
        EditText call2 = findViewById(R.id.callEditText);
        EditText katalk2 = findViewById(R.id.katalkEditText);

        Spinner spinner = (Spinner)findViewById(R.id.mbti);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, MBTI);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mbti = MBTI[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        checkBox = findViewById(R.id.student);
        checkBox.setChecked(true);
        if(checkBox.isChecked()){
            status = "Student";
        }
        Button save = findViewById(R.id.save_std);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = name2.getText().toString();
                id = id2.getText().toString();
                call = call2.getText().toString();
                katalk = katalk2.getText().toString();

                postFirebaseDatabase(true);

                startLoginActivity(); //정보저장 성공 시 메인 화면으로 이동
                }
        });

    }

    public void postFirebaseDatabase(boolean add){
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            FirebasePost post = new FirebasePost(id, name, mbti, call, katalk, status);
            postValues = post.toMap();
        }
        childUpdates.put("/id_list/" + id, postValues);
        mPostReference.updateChildren(childUpdates);
    }

    private void startLoginActivity()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}