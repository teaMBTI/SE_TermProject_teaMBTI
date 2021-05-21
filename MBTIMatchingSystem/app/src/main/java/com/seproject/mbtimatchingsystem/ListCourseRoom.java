package com.seproject.mbtimatchingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

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
    String nowId;
    String nowCourseNum;
    String nowMbti;
    String nowStatus;
    String id_listEmail;
    public Button logOutButton;
    ImageButton addCourseButton;
    private static final String TAG = "ListCourseRoom";
    String topic = "null";

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


        /* ListView에 목록 세팅 */
        ListView listView = (ListView) this.findViewById(R.id.listViewCourseRoom);
        adapter = new ArrayAdapter<String>( this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        listView.setAdapter(adapter);


        database= FirebaseDatabase.getInstance();
        mPostReference=database.getReference("course_list");
        mPostReference.addListenerForSingleValueEvent(new ValueEventListener() { //강좌목록(리스트뷰)데이터 읽기
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


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //강좌 클릭 이벤트트
            @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String course = (String) listView.getItemAtPosition(position);
                if(nowStatus.equals("Professor")){ // Professor은 팝업창이 안뜬다.
                    goToListTeamProject(course);
                }
                else if(nowStatus.equals("Student")) { //Student 경우

                    /*if( st_participate_id.equals("presence")){ //이미 강좌에 입장한 학생이라면 바로 입장
                        goToListTeamProject(course);
                    }
                    else{ */          //처음 강좌에 입장하는 학생이면, 팝업
                    AlertDialog.Builder builder = new AlertDialog.Builder(ListCourseRoom.this); //강좌 입장하시겠습니까 팝업
                    builder.setTitle("");
                    builder.setMessage("해당 강좌에 입장하시겠습니까?");
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            readEmailAndPutId(); //db st_participate_id에 입력
                            String course = (String) listView.getItemAtPosition(position);
                            if (course.contains("소프트웨어공학")) {
                                topic = "SE";
                            } else if (course.contains("데이터과학")) {
                                topic = "DS";
                            } else if (course.equals("모바일프로그래밍(10178001)")) {
                                topic = "MP1";
                            } else if (course.equals("모바일프로그래밍(10178002)")) {
                                topic = "MP2";
                            }
                            nowCourseNum=cuttingCourseNum(course);
                            if (topic.equals("SE")) {
                                FirebaseMessaging.getInstance().subscribeToTopic("SE")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                String msg = "Subscribed to SE";
                                                if (!task.isSuccessful()) {
                                                    msg = "Failed to subscribe to SE";
                                                }
                                                Log.d(TAG, msg);
                                                Toast.makeText(ListCourseRoom.this, msg, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            if (topic.equals("DS")) {
                                FirebaseMessaging.getInstance().subscribeToTopic("DS")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                String msg = "Subscribed to DS";
                                                if (!task.isSuccessful()) {
                                                    msg = "Failed to subscribe to DS";
                                                }
                                                Log.d(TAG, msg);
                                                Toast.makeText(ListCourseRoom.this, msg, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            if (topic.equals("MP1")) {
                                FirebaseMessaging.getInstance().subscribeToTopic("MP1")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                String msg = "Subscribed to MP1";
                                                if (!task.isSuccessful()) {
                                                    msg = "Failed to subscribe to MP1";
                                                }
                                                Log.d(TAG, msg);
                                                Toast.makeText(ListCourseRoom.this, msg, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            if (topic.equals("MP2")) {
                                FirebaseMessaging.getInstance().subscribeToTopic("MP2")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                String msg = "Subscribed to MP2";
                                                if (!task.isSuccessful()) {
                                                    msg = "Failed to subscribe to MP2";
                                                }
                                                Log.d(TAG, msg);
                                                Toast.makeText(ListCourseRoom.this, msg, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            goToListTeamProject(course);
                        }
                    });
                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            startToast("해당 강좌에 입장하지 않습니다.");
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();}
            }
            /*  }*/
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
        ref.addValueEventListener(new ValueEventListener() { //유저의 상태를 받아온다.(Professor, Student)
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

    private void goToListTeamProject(String course) {
        startToast(course);
        nowCourseNum=cuttingCourseNum(course);
        Intent NewActivity = new Intent(getApplicationContext(),
                com.seproject.mbtimatchingsystem.ListTeamProject.class);
        NewActivity.putExtra("course", course);
        setResult(RESULT_OK, NewActivity);
        startActivity(NewActivity);
    }


    private void readEmailAndPutId() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        int i = 0;
        for (UserInfo profile : user.getProviderData()) {
            // 현재 사용자 이메일 가져오기
            String currentUserEmail = profile.getUid();
            if (i == 1) {
                nowEmail = currentUserEmail;
            }
            i++;
        }
        database = FirebaseDatabase.getInstance();
        mPostReference = database.getReference("id_list");
        mPostReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    id_listEmail = snapshot.getValue().toString();
                    nowId = id_listEmail;
                    nowMbti = id_listEmail;
                    id_listEmail = cuttingEmail(id_listEmail); //value 값 필요한 부분만 자르기(이메일)

                    if (nowEmail.equals(id_listEmail)) {
                        nowId = cuttingId(nowId);
                        nowMbti = cuttingMbti(nowMbti);
/*                         Log.e("MMMYYTAGG", "현재 유저 학번: " +nowId);
                         Log.e("MMMYYTAGG", "현재 유저 MBTI: " +nowMbti);*/
                        break; //현재 유저 이메일과 listEmail에 있는 이메일이 일치할시, nowId에 학번넣고 break;
                    }
                }
                DatabaseReference courseRef = database.getReference().child("course_list");
                courseRef.child(nowCourseNum).child("st_Participate_id").child(nowId).setValue(nowMbti); //st_participate_id에 학번,mbti 데이터쓰기
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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

    private String cuttingId(String msg) {//학번만 자르기
        msg = msg.substring(msg.indexOf(", id=") + 5, msg.indexOf(", email"));
        return msg;
    }

    private String cuttingMbti(String msg) { //MBTI만 자르기
        msg = msg.substring(msg.indexOf(", mbti=") + 7, msg.indexOf(", id"));
        return msg;
    }
    private String cuttingCourseNum(String msg) { //해당 강좌 학수번호만 자르기
        msg = msg.substring(msg.indexOf("(") + 1, msg.indexOf(")"));
        return msg;
    }


}