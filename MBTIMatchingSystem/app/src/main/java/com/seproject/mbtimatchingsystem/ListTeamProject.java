package com.seproject.mbtimatchingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListTeamProject extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference mPostReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference ref;
    TextView courseName;
    String id_listEmail;
    String nowEmail;
    String nowCourseNum;

    String nowStatus;
    String course;

    ListView listView;
    ArrayAdapter<String> adapter;
    List<Object> tpList = new ArrayList<Object>();
    List<Object> testList = new ArrayList<Object>();

    List<Object> emailList = new ArrayList<Object>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_teamproject);

        courseName = (TextView) findViewById(R.id.course_name);

        //Get course information from ListCourseRoom activity
        Intent passedIntent = getIntent();
        if (passedIntent != null) {
            course = passedIntent.getStringExtra("course");
            courseName.setText(course);
            nowCourseNum = cuttingCourseNum(course);
        }

        //Firebase Authentication Object Declaration and get current user's class
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        int i = 0;
        for (UserInfo profile : user.getProviderData()) {
            String currentUserEmail = profile.getUid(); // Get current user's email
            if (i == 1)
                nowEmail = currentUserEmail;
            i++;
        }
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("id_list"); //Get reference instance of "id_list"
        ref.addValueEventListener(new ValueEventListener() {   //To get user's status when click addTeamProject button
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    id_listEmail = snapshot.getValue().toString(); //read String type data from Firebase DB
                    nowStatus = id_listEmail;
                    id_listEmail = cuttingEmail(id_listEmail); //Get user's email from value that is read from DB
                    if (nowEmail.equals(id_listEmail)) {
                        nowStatus = cuttingStatus(nowStatus);
                        Log.e("MMMYYTAGG", "현재 유저 상태: " + nowStatus);
                        //Can know whether this user is professor or student.
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        ImageButton addTeamProject = (ImageButton) findViewById(R.id.addTeamProject);
        addTeamProject.setOnClickListener(new View.OnClickListener() { //Create team project
            @Override
            public void onClick(View view) {
                if (nowStatus.equals("Professor")) { //When user's status is professor
                    Intent NewActivity = new Intent(getApplicationContext(), AddTeamProject.class); //move to AddTeamProject activity
                    NewActivity.putExtra("courseNum", nowCourseNum); //Pass course number to AddTeamProject activity
                    setResult(RESULT_OK, NewActivity);
                    startActivity(NewActivity);
                } else //If user's status is not professor
                    startToast("학생은 팀프로젝트 생성 권한이 없습니다.");
            }
        });


        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView_tp);

        listView = (ListView) findViewById(R.id.listView_tp);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //When listView's item is clicked
                String strText = (String) listView.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), TeamView.class); //Move to teamView activity
                intent.putExtra("tpname", strText); //Pass team project name, course number, status to teamView activity
                intent.putExtra("coursenum", nowCourseNum);
                intent.putExtra("status", nowStatus);
                setResult(RESULT_OK, intent);
                startActivity(intent);

            }
        });

        database = FirebaseDatabase.getInstance();
        mPostReference = database.getReference("course_list/" + nowCourseNum + "/teamprojectlist");
        mPostReference.addListenerForSingleValueEvent(new ValueEventListener() { //Show team project list read from DB
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { //like Firebase data listener
                adapter.clear();
                for (DataSnapshot messageData : dataSnapshot.getChildren()) { //Repeat as many times as the data in child
                    String tp_list = messageData.getValue().toString(); //getValue()--> get TPName, teamNum, and totalStuNum.
                    tp_list = cuttingTPName(tp_list); //A function to get only TPName from a string
                    tpList.add(tp_list); //Add to list array
                    //Log.e("MMMYYTAGG", "tpList " +tp_list);
                    //Log.e("MMMYYTAGG", "tpList " +tpList);
                    adapter.add(tp_list);
                }
                adapter.notifyDataSetChanged();
                listView.setSelection(adapter.getCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Scroll the listview inside the scrollview
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }


    private String cuttingTPName(String msg) { //Get TPName from String
        msg = msg.substring(msg.indexOf("TPName=") + 7, msg.indexOf(", teamNum"));
        return msg;
    }

    private String cuttingEmail(String msg) {//Get email from String
        msg = msg.substring(msg.indexOf(", email=") + 8, msg.indexOf(", status"));
        return msg;
    }

    private String cuttingStatus(String msg) { //Get status from String
        msg = msg.substring(msg.indexOf(", status=") + 9, msg.indexOf("}"));
        return msg;
    }
    
    private String cuttingCourseNum(String msg) { //Get course number from String
        msg = msg.substring(msg.indexOf("(") + 1, msg.indexOf(")"));
        return msg;
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public String isContained(String tpName) { //for Unit Testing
        String TP = tpName;
        testList.add("Architectural design");
        testList.add("Design & Implementation");
        testList.add("System modeling");
        testList.add("Data cleaning");
        testList.add("Data encoding");
        testList.add("Proposal");
        testList.add("Assignment1");
        testList.add("Assignment2");

        if(testList.contains(TP))
        {
            return "Yes";
        }
        else{
            return "No";
        }
    }
}