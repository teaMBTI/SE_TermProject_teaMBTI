package com.seproject.mbtimatchingsystem;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddCourse extends AppCompatActivity {


    private DatabaseReference mPostReference;
    private FirebaseAuth mAuth; //파이어베이스 인스턴스 선언
    private FirebaseUser mFirebaseUser;
    public String courseNum;
    public String courseName;
    public String pf_id;
    public String pf_name;
    EditText courseNum2;
    EditText courseName2;
    EditText pf_id2;
    EditText pf_name2;
    Button okAddCourseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_course);

        courseNum2 = findViewById(R.id.courseNum);
        courseName2 = findViewById(R.id.courseName);
        pf_id2 = findViewById(R.id.pf_id);
        pf_name2=findViewById(R.id.pf_name);
        okAddCourseButton =findViewById(R.id.okAddCourseButton);


        /* addCourse button listener */
        okAddCourseButton.setOnClickListener(new View.OnClickListener() { //강좌 개설 확인 버튼
            @Override
            public void onClick(View view) {
                courseNum = courseNum2.getText().toString();
                courseName = courseName2.getText().toString();
                pf_id = pf_id2.getText().toString();
                pf_name = pf_name2.getText().toString();

                Toast.makeText(AddCourse.this, "학수번호: " + courseNum + "\n과목 이름: " + courseName + "\n교수 교번: "+ pf_id + "\n교수 이름: "+ pf_name, Toast.LENGTH_SHORT).show();
                postDBData(true); //데이터 파이어베이스 입력

                Intent intent = new Intent(getApplicationContext(),ListCourseRoom.class);
                startActivity(intent);
                // 중첩을 피하기 위해서 다른 activity 로 갈때 quit activity
                finish();
            }
        });



    }


    private void postDBData(boolean add) {
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if (add) {
            DBData post = new DBData(courseNum, courseName, pf_id, pf_name);
            postValues = post.toMap();
        }
        childUpdates.put("/course_list/" + courseNum, postValues);
        mPostReference.updateChildren(childUpdates);
    }

}