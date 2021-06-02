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

public class AddTeamProject extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDataBase;
    private DatabaseReference mFirebaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;

    public String courseNum;
    public String TPName;
    public String buff1;
    public String buff2;
    public int totalStuNum;
    public int teamNum;

    EditText edit_courseNum;
    EditText edit_TPName;
    EditText edit_totalStuNum;
    EditText edit_teamNum;
    Button doneBtn;

    String course_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_teamproject);

        Intent passedIntent = getIntent();
        if (passedIntent != null) {
            course_num = passedIntent.getStringExtra("courseNum"); //get course number from ListTeamProject activity

        }

        edit_courseNum = findViewById(R.id.courseNum);
        edit_courseNum.setText(course_num);
        edit_TPName = findViewById(R.id.TPName);
        edit_totalStuNum = findViewById(R.id.totalStuNum);
        edit_teamNum = findViewById(R.id.teamNum);
        doneBtn = findViewById(R.id.doneButton);


        doneBtn.setOnClickListener(new View.OnClickListener() { //A listener for the team project creation complete button
            @Override
            public void onClick(View view) {
                courseNum = edit_courseNum.getText().toString();
                TPName = edit_TPName.getText().toString();
                buff1 = edit_totalStuNum.getText().toString();
                totalStuNum = Integer.parseInt(buff1);
                buff2 = edit_teamNum.getText().toString();
                teamNum = Integer.parseInt(buff2);

                Toast.makeText(AddTeamProject.this, "팀프로젝트명: " + TPName + "\n총 학생수: "
                        + totalStuNum + "\n팀 개수: " + teamNum, Toast.LENGTH_SHORT).show();
                uploadData(true);

                Intent intent = new Intent(getApplicationContext(), ListTeamProject.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //Upload team project information(Team project name, the total number of students, the number of team) to DB
    private void uploadData(boolean add) {
        mFirebaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference courseRef = mFirebaseReference.child("course_list");

        Map<String, Object> postValues = null;
        if (add) {
            PostTeamProject post = new PostTeamProject(TPName, totalStuNum, teamNum);
            postValues = post.toMap();
        }

        courseRef.child(courseNum).child("teamprojectlist").child(TPName).setValue(postValues);
    }
}