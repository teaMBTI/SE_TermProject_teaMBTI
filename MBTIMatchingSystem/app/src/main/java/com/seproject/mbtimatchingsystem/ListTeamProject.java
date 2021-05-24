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

        Intent passedIntent = getIntent();
        if (passedIntent != null) {
            course = passedIntent.getStringExtra("course");
            courseName.setText(course);
            nowCourseNum = cuttingCourseNum(course);
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        int i = 0;
        for (UserInfo profile : user.getProviderData()) {// 현재 사용자(nowEmail) 이메일 가져오기
            String currentUserEmail = profile.getUid();
            if (i == 1)
                nowEmail = currentUserEmail;
            i++;
        }
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("id_list");
        ref.addValueEventListener(new ValueEventListener() {   //addTeamProject 버튼 눌렀을시 유저의 status불러옴
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    id_listEmail = snapshot.getValue().toString();
                    nowStatus = id_listEmail;
                    id_listEmail = cuttingEmail(id_listEmail); //value 값 필요한 부분만 자르기(이메일)
                    if (nowEmail.equals(id_listEmail)) {
                        nowStatus = cuttingStatus(nowStatus);
                        Log.e("MMMYYTAGG", "현재 유저 상태: " + nowStatus);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        ImageButton addTeamProject = (ImageButton) findViewById(R.id.addTeamProject); //팀프로젝트 생성
        addTeamProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nowStatus.equals("Professor")) { //교수 계정인 경우
                    Intent NewActivity = new Intent(getApplicationContext(), AddTeamProject.class); //팀프로젝트 생성 화면으로 이동
                    NewActivity.putExtra("courseNum", nowCourseNum);
                    setResult(RESULT_OK, NewActivity);
                    startActivity(NewActivity);
                } else
                    startToast("학생은 팀프로젝트 생성 권한이 없습니다.");
            }
        });


        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView_tp);

        listView = (ListView) findViewById(R.id.listView_tp);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //팀프로젝트 리스트뷰 아이템 클릭시
                String strText = (String) listView.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), TeamView.class); //TeamView 화면으로 이동
                intent.putExtra("tpname", strText);
                intent.putExtra("coursenum", nowCourseNum);
                intent.putExtra("status", nowStatus);
                setResult(RESULT_OK, intent);
                startActivity(intent);

            }
        });

        database = FirebaseDatabase.getInstance();
        mPostReference = database.getReference("course_list/" + nowCourseNum + "/teamprojectlist");
        mPostReference.addListenerForSingleValueEvent(new ValueEventListener() { //Firebase로부터 읽어온 팀프로젝트 목록 보여줌
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clear();
                for (DataSnapshot messageData : dataSnapshot.getChildren()) { // child 내에 있는 데이터만큼 반복
                    String tp_list = messageData.getValue().toString(); //getValue()하면 TPName, teamNum, totalStuNum 모두 가져옴
                    tp_list = cuttingTPName(tp_list); //문자열에서 TPName만 잘라 얻어오는 함수
                    tpList.add(tp_list); //list array에 추가
                    //Log.e("MMMYYTAGG", "tpList " +tp_list);
                    //Log.e("MMMYYTAGG", "tpList " +tpList);  //이 함수를 벗어나면 null이 됨
                    adapter.add(tp_list);
                }
                adapter.notifyDataSetChanged();
                listView.setSelection(adapter.getCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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


    private String cuttingTPName(String msg) { //TPName만 자르기
        msg = msg.substring(msg.indexOf("TPName=") + 7, msg.indexOf(", teamNum"));
        return msg;
    }

    private String cuttingEmail(String msg) {//이메일만 자르기
        msg = msg.substring(msg.indexOf(", email=") + 8, msg.indexOf(", status"));
        return msg;
    }

    private String cuttingStatus(String msg) { //Status만 자르기
        msg = msg.substring(msg.indexOf(", status=") + 9, msg.indexOf("}"));
        return msg;
    }
    
    private String cuttingCourseNum(String msg) { //해당 강좌 학수번호만 자르기
        msg = msg.substring(msg.indexOf("(") + 1, msg.indexOf(")"));
        return msg;
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public String isContained(String tpName) {
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