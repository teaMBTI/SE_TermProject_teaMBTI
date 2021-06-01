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
        okAddCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //information about course
                courseNum = courseNum2.getText().toString();
                courseName = courseName2.getText().toString();
                pf_id = pf_id2.getText().toString();
                pf_name = pf_name2.getText().toString();

                Toast.makeText(AddCourse.this, "학수번호: " + courseNum + "\n과목 이름: " + courseName + "\n교수 교번: "+ pf_id + "\n교수 이름: "+ pf_name, Toast.LENGTH_SHORT).show();
                postDBData(true); //Input data to database

                Intent intent = new Intent(getApplicationContext(),ListCourseRoom.class);
                startActivity(intent);
                // Quit activity, when going to another activity to avoid overlap.
                finish();
            }
        });


    }

    //Input data to database
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