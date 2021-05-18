package com.seproject.mbtimatchingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListCourseRoom extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference mPostReference;
    private DatabaseReference ref;
    private FirebaseAuth mAuth; //파이어베이스 인스턴스 선언
    private FirebaseUser mFirebaseUser;
    private ChildEventListener mChild;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    List<Object> courseList = new ArrayList<Object>();
    String nowEmail;
    String id_listEmail;
    String nowStatus;
    public Button logOutButton;
    ImageButton addCourseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_course_room);

        mAuth = FirebaseAuth.getInstance(); //파이어베이스 인증 객체 선언
        mFirebaseUser = mAuth.getCurrentUser();//혹시 인증 유지가 안될 수 있으니 유저 확인

        if (mFirebaseUser == null) //현재 로그인된 유저가 있는지 확인
        {
            startLoginActivity(); //로그인이 안되어 있으면 회원가입 화면으로 이동
        }else { //현재 로그인 되어 있다면
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                for (UserInfo profile : user.getProviderData()) {
                    // 사용자 닉네임 가져오기
                    String name = profile.getDisplayName();
                }
            }
        }

        //참고자료 메모
        //중요_레이아웃 동적생성(courseroom들갈때 써야함) https://blog.naver.com/rain483/220812579755
        /*파이어베이스 데이터 받아서 리스트뷰연결
        https://angkeum.tistory.com/entry/firebase-android-connect-%ED%8C%8C%EC%9D%B4%EC%96%B4%EB%B2%A0%EC%9D%B4%EC%8A%A4-%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EC%97%B0%EA%B2%B0
        ****  https://steemit.com/kr-dev/@gbgg/firebase-3-firebase
        */

        //ListView에 목록 세팅
        ListView listView = (ListView) this.findViewById(R.id.listViewCourseRoom);
        adapter = new ArrayAdapter<String>( this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //강좌 누르면
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String course = (String) listView.getItemAtPosition(position);
                startToast(course);
                Intent NewActivity = new Intent(getApplicationContext(),
                        com.seproject.mbtimatchingsystem.ListTeamProject.class);
                NewActivity.putExtra("course", course);
                setResult(RESULT_OK, NewActivity);
                startActivity(NewActivity);
            }
        });


        database= FirebaseDatabase.getInstance();
        mPostReference=database.getReference("course_list");
        mPostReference.addListenerForSingleValueEvent(new ValueEventListener() { //리스트뷰(강좌목록)보여줌
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clear();
                for(DataSnapshot messageData : dataSnapshot.getChildren()){
                   String course_list = messageData.getValue().toString();
                   course_list = cutting(course_list); //value 값 필요한 부분만 자르기(강좌명, 학수번호)
                   courseList.add(course_list);
                   adapter.add(course_list);
                }
                adapter.notifyDataSetChanged();
                listView.setSelection(adapter.getCount()-1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        int i=0;
        for (UserInfo profile : user.getProviderData()) {// 현재 사용자(nowEmail) 이메일 가져오기
            String currentUserEmail = profile.getUid();
            if(i==1)
                nowEmail=currentUserEmail;
            i++;
        }
        database= FirebaseDatabase.getInstance();
        ref=database.getReference("id_list");
        ref.addValueEventListener(new ValueEventListener() {   //addCourse버튼 눌렀을시 유저의 status불러옴
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    id_listEmail = snapshot.getValue().toString();
                    nowStatus =id_listEmail;
                    id_listEmail = cuttingEmail(id_listEmail); //value 값 필요한 부분만 자르기(이메일)
                    if(nowEmail.equals(id_listEmail)) {
                        nowStatus =cuttingStatus(nowStatus);
                        Log.e("MMMYYTAGG", "현재 유저 상태: " +nowStatus);
                        break; //현재 유저 이메일과 listEmail에 있는 이메일이 일치할시 nowId에 학번넣고 break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        
        logOutButton = findViewById(R.id.logOutButton); //로그아웃 버튼
        logOutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    startLoginActivity(); //로그아웃되면 로그인 화면으로 이동
                }
            });

        addCourseButton = findViewById(R.id.addCourseButton); //강좌개설 버튼
        /* addCourse button listener */
        addCourseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   /* statusCheck();//상태를 가져온다*/
                    if(nowStatus.equals("Professor")){
                        Intent intent = new Intent(getApplicationContext(),AddCourse.class);
                        startActivity(intent);
                    }
                    else
                        startToast("학생은 강좌 개설 권한이 없습니다.");
                }
            });
    }



    @Override
    //활동을 초기화할 때 사용자가 현재 로그인되어 있는지 확인합니다.
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    private void startToast(String msg)
    { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }

    private String cutting(String msg) //소프트웨어공학(10177002)
    { String temp = msg;
        msg= msg.substring(msg.indexOf(", courseName")+13,msg.indexOf(", pf_name")); //courseName=자르기
        temp = temp.substring( temp.indexOf(", courseNum")+12, temp.indexOf(", pf_id")); //course_num 자르기
        msg = msg+"("+temp+")";
        return msg; }
    private String cuttingStatus(String msg) { //Status만 자르기
        msg= msg.substring(msg.indexOf(", status=")+9,msg.indexOf("}"));
        return msg;
    }
    private String cuttingEmail(String msg) {//이메일만 자르기
        msg= msg.substring(msg.indexOf(", email=")+8,msg.indexOf(", status"));
        return msg;
    }


}