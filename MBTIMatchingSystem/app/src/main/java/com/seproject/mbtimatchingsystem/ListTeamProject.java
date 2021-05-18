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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListTeamProject extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference mPostReference;
    private FirebaseAuth mAuth; //파이어베이스 인스턴스 선언
    private FirebaseUser mFirebaseUser;
    TextView courseName;
    Button participate;
    String id_listEmail;
    String nowId;
    String nowEmail;
    String nowCourseNum;
    List<Object> emailList = new ArrayList<Object>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_teamproject);

        courseName = (TextView) findViewById(R.id.course_name);

        Intent passedIntent = getIntent();
        if (passedIntent != null) {
            String course = passedIntent.getStringExtra("course");
            //courseName.setText(course);
            nowCourseNum = cuttingCourseNum(course);
        }

        ImageButton addTeamProject = (ImageButton) findViewById(R.id.addTeamProject); //팀프로젝트 생성
        addTeamProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent NewActivity = new Intent(getApplicationContext(), AddTeamProject.class);
                startActivity(NewActivity);
            }
        });


        participate = findViewById(R.id.participate); //참가하기
        participate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startToast("수업에 참가하셨습니다.");
                readEmailAndPutId();
            }
        });


        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView_tp);

        String[] teamProject = {"Team Project 1", "Team Project 2", "Team Project 3", "Team Project 4", "Team Project 5", "Team Project 6",
                "Team Project 7", "Team Project 8", "Team Project 9", "Team Project 10", "Team Project 11", "Team Project 12", "Team Project 13",
                "Team Project 14", "Team Project 15"};
        ListView listView = (ListView) findViewById(R.id.listView_tp);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, teamProject);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String strText = (String) listView.getItemAtPosition(position);
                //Toast.makeText(, strText, Toast.LENGTH_SHORT).show();
            }
        });

        // 스크롤뷰 안에 리스트뷰 스크롤
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }



    private void readEmailAndPutId() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        int i=0;
        for (UserInfo profile : user.getProviderData()) {
            // 현재 사용자 이메일 가져오기
            String currentUserEmail = profile.getUid();
            if(i==1) {
                nowEmail=currentUserEmail;
            }
            i++;
        }
        database= FirebaseDatabase.getInstance();
        mPostReference=database.getReference("id_list");
        mPostReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    id_listEmail = snapshot.getValue().toString();
                    nowId =id_listEmail;
                    id_listEmail = cuttingEmail(id_listEmail); //value 값 필요한 부분만 자르기(이메일)

                    if(nowEmail.equals(id_listEmail)) {
                        nowId =cuttingId(nowId);;
                        Log.e("MMMYYTAGG", "현재 유저 학번: " +nowId);
                        break; //현재 유저 이메일과 listEmail에 있는 이메일이 일치할시 nowId에 학번넣고 break;
                    }
                }
                DatabaseReference courseRef = database.getReference().child("course_list");
                courseRef.child(nowCourseNum).child("st_Participate_id").child(nowId).setValue(nowId);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }


    private String cuttingId(String msg) {//학번만 자르기
        String temp = msg;
        msg= msg.substring(msg.indexOf(", id=")+5,msg.indexOf(", email"));
        return msg;
    }
    private String cuttingEmail(String msg) {//이메일만 자르기
        String temp = msg;
        msg= msg.substring(msg.indexOf(", email=")+8,msg.indexOf(", status"));
        return msg;
    }
    private String cuttingCourseNum(String msg) { //해당 강좌 할수번호만 자르기
        msg= msg.substring(msg.indexOf("(")+1,msg.indexOf(")"));
        return msg;
    }
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
