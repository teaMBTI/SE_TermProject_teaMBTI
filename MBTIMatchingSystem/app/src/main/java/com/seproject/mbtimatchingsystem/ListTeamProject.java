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
    String nowMbti;

    String nowStatus;




    List<Object> emailList = new ArrayList<Object>();
    private static final String TAG = "ListCourseRoom";
    String topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_teamproject);

        courseName = (TextView) findViewById(R.id.course_name);

        Intent passedIntent = getIntent();
        if (passedIntent != null) {
            String course = passedIntent.getStringExtra("course");
            courseName.setText(course);
            nowCourseNum = cuttingCourseNum(course);
        }

        ImageButton addTeamProject = (ImageButton) findViewById(R.id.addTeamProject); //팀프로젝트 생성
        addTeamProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkStatus();
                if(nowStatus.equals("Professor")){
                    Intent NewActivity = new Intent(getApplicationContext(), AddTeamProject.class);
                    startActivity(NewActivity);
                }
                else
                    startToast("학생은 팀프로젝트 개설 권한이 없습니다.");
            }
        });

        String course = courseName.getText().toString();
        if(course.contains("소프트웨어공학")){
            topic = "SE";
        }else if(course.contains("데이터과학"))
        {
            topic = "DS";
        }else if(course.equals("모바일 프로그래밍(10178001)"))
        {
            topic = "MP1";
        }else if(course.equals("모바일 프로그래밍(10178002)"))
        {
            topic = "MP2";
        }

        participate = findViewById(R.id.participate); //참가하기
        participate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startToast("수업에 참가하셨습니다.");
                readEmailAndPutId();

                if(topic.equals("SE"))
                {
                    FirebaseMessaging.getInstance().subscribeToTopic("SE")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    String msg = "Subscribed to SE";
                                    if (!task.isSuccessful()) {
                                        msg = "Failed to subscribe to SE";
                                    }
                                    Log.d(TAG, msg);
                                    Toast.makeText(ListTeamProject.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                if(topic.equals("DS"))
                {
                    FirebaseMessaging.getInstance().subscribeToTopic("DS")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    String msg = "Subscribed to DS";
                                    if (!task.isSuccessful()) {
                                        msg = "Failed to subscribe to DS";
                                    }
                                    Log.d(TAG, msg);
                                    Toast.makeText(ListTeamProject.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                if(topic.equals("MP1"))
                {
                    FirebaseMessaging.getInstance().subscribeToTopic("MP1")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    String msg = "Subscribed to MP1";
                                    if (!task.isSuccessful()) {
                                        msg = "Failed to subscribe to MP1";
                                    }
                                    Log.d(TAG, msg);
                                    Toast.makeText(ListTeamProject.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                if(topic.equals("MP2"))
                {
                    FirebaseMessaging.getInstance().subscribeToTopic("MP2")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    String msg = "Subscribed to MP2";
                                    if (!task.isSuccessful()) {
                                        msg = "Failed to subscribe to MP2";
                                    }
                                    Log.d(TAG, msg);
                                    Toast.makeText(ListTeamProject.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
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
        mPostReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    id_listEmail = snapshot.getValue().toString();
                    //Log.e("MMMYYTAGG", "listInfo: " +id_listEmail);




                    nowId =id_listEmail;
                    nowMbti =id_listEmail;
                    id_listEmail = cuttingEmail(id_listEmail); //value 값 필요한 부분만 자르기(이메일)

                    if(nowEmail.equals(id_listEmail)) {
                        nowId =cuttingId(nowId);
                        nowMbti=cuttingMbti(nowMbti);

                        // Log.e("MMMYYTAGG", "현재 유저 학번: " +nowId);
                        // Log.e("MMMYYTAGG", "현재 유저 MBTI: " +nowMbti);
                        break; //현재 유저 이메일과 listEmail에 있는 이메일이 일치할시 nowId에 학번넣고 break;
                    }
                }
                DatabaseReference courseRef = database.getReference().child("course_list");
                courseRef.child(nowCourseNum).child("st_Participate_id").child(nowId).setValue(nowMbti); //학번,mbti 데이터쓰기
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }


    private void checkStatus() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        for (UserInfo profile : user.getProviderData()) {
            // 현재 사용자 이메일 가져오기
            String currentUserEmail = profile.getUid();

                nowEmail=currentUserEmail;
                Log.e("MMMYYTAGG", "listInfo: " + nowEmail);

        }
        database= FirebaseDatabase.getInstance();
        mPostReference=database.getReference("id_list");
        mPostReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    id_listEmail = snapshot.getValue().toString();
                    Log.e("MMMYYTAGG", "listInfo: " +id_listEmail);

                    nowStatus =id_listEmail;

                    id_listEmail = cuttingEmail(id_listEmail); //value 값 필요한 부분만 자르기(이메일)

                    if(nowEmail.equals(id_listEmail)) {
                        nowStatus =cuttingStatus(nowStatus);
                        Log.e("MMMYYTAGG", "현재 유저 상태: " +nowStatus);

                        //break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }


    private String cuttingMbti(String msg) { //MBTI만 자르기
        msg=msg.substring(msg.indexOf(", mbti=")+7, msg.indexOf(", id"));
        return msg;
    }
    private String cuttingId(String msg) {//학번만 자르기
        msg= msg.substring(msg.indexOf(", id=")+5,msg.indexOf(", email"));
        return msg;
    }
    private String cuttingEmail(String msg) {//이메일만 자르기
        msg= msg.substring(msg.indexOf(", email=")+8,msg.indexOf(", status"));
        return msg;
    }

    private String cuttingStatus(String msg) { //Status만 자르기
        msg= msg.substring(msg.indexOf(", status=")+9,msg.indexOf("}"));
        return msg;
    }





    private String cuttingCourseNum(String msg) { //해당 강좌 학수번호만 자르기
        msg= msg.substring(msg.indexOf("(")+1,msg.indexOf(")"));
        return msg;
    }
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}