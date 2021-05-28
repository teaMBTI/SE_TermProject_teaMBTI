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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListCourseRoom extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference mPostReference;
    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private ArrayAdapter<String> adapter;

    List<Object> courseList = new ArrayList<Object>();
    String id_listEmail;

    //User Information
    String nowEmail;
    String nowId;
    String nowCourseNum;
    String nowMbti;
    String nowStatus;


    public Button logOutButton;
    ImageButton addCourseButton;
    private static final String TAG = "ListCourseRoom";
    String topic = "null";

    // Backward
    private long backBtnTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_course_room);


        //Firebase Authentication Object Declaration
        mAuth = FirebaseAuth.getInstance();
        //Current User
        mFirebaseUser = mAuth.getCurrentUser();

        //Check for currently logged in users
        if (mFirebaseUser == null) //If user are not logged in,
             startLoginActivity();

        else { //If user are logged in,
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            int i=0;
            // Get current user email
            for (UserInfo profile : user.getProviderData()) {
                String currentUserEmail = profile.getUid();
                if(i==1)
                    nowEmail=currentUserEmail;
                i++;
            }
            database= FirebaseDatabase.getInstance();
            ref=database.getReference("id_list");
            ref.addValueEventListener(new ValueEventListener() { //Get current user status(Professor, Student)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        id_listEmail = snapshot.getValue().toString();
                        nowStatus =id_listEmail;
                        id_listEmail = cuttingEmail(id_listEmail);
                        if(nowEmail.equals(id_listEmail)) { //If the current user emails equals the email in listEmail
                            nowStatus =cuttingStatus(nowStatus);
                            break; //Put the student_id(학번) in nowId and break
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

            // Get current use nickname
            if (user != null) {
                for (UserInfo profile : user.getProviderData()) {
                    String name = profile.getDisplayName();
                }
            }
        }


        /* Set a list of current courses in the listView */
        ListView listView = (ListView) this.findViewById(R.id.listViewCourseRoom);
        adapter = new ArrayAdapter<String>( this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        listView.setAdapter(adapter);

        //Read the course list data from database
        database= FirebaseDatabase.getInstance();
        mPostReference=database.getReference("course_list");
        mPostReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clear();
                for(DataSnapshot messageData : dataSnapshot.getChildren()){
                    String course_list = messageData.getValue().toString();
                    course_list = cutting(course_list);
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

        /* Event when a course is clicked in the listView */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String course = (String) listView.getItemAtPosition(position);
                if(nowStatus.equals("Professor")){ //If the user is a professor
                    goToListTeamProject(course);
                }
                else if(nowStatus.equals("Student")) { //If the user is a student
                    //Course Entry Pop-up
                    AlertDialog.Builder builder = new AlertDialog.Builder(ListCourseRoom.this);
                    builder.setTitle("");
                    builder.setMessage("해당 강좌에 입장하시겠습니까?");
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {//Enter the course
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            readEmailAndPutId();
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
                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {//Not enter the course
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            startToast("해당 강좌에 입장하지 않습니다.");
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();}
            }
        });


        logOutButton = findViewById(R.id.logOutButton); //Logout Button
        logOutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    startLoginActivity(); //After logout, go Login activity
                }
            });

        addCourseButton = findViewById(R.id.addCourseButton); //Add Course Button
        addCourseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(nowStatus.equals("Professor")){ //AddCourse can be used only professor
                        Intent intent = new Intent(getApplicationContext(),AddCourse.class);
                        startActivity(intent);
                    }
                    else
                        startToast("학생은 강좌 개설 권한이 없습니다.");
                }
            });
    }

    //Enter the ListTeamProject activity
    private void goToListTeamProject(String course) {
        startToast(course);
        nowCourseNum=cuttingCourseNum(course);
        Intent NewActivity = new Intent(getApplicationContext(),
                com.seproject.mbtimatchingsystem.ListTeamProject.class);
        NewActivity.putExtra("course", course);
        setResult(RESULT_OK, NewActivity);
        startActivity(NewActivity);
    }

    /*Read current use email, get User studentId in nowId*/
    private void readEmailAndPutId() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        int i = 0;
        for (UserInfo profile : user.getProviderData()) {
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
                    id_listEmail = cuttingEmail(id_listEmail);

                    if (nowEmail.equals(id_listEmail)) {
                        nowId = cuttingId(nowId);
                        nowMbti = cuttingMbti(nowMbti);
                        break;
                    }
                }
                DatabaseReference courseRef = database.getReference().child("course_list");
                courseRef.child(nowCourseNum).child("st_Participate_id").child(nowId).setValue(nowMbti); //Write studentId, mbti data in database participate_id
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    //When initializing an activity, check that the user is currently logged in
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    //Go Login Activity
    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    //Toast function
    private void startToast(String msg)
    { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }

    // Cutting list to courseName+ CourseNum only ex)소프트웨어공학(10177002)
    private String cutting(String msg)
    { String temp = msg;
        msg= msg.substring(msg.indexOf(", courseName")+13,msg.indexOf(", pf_name"));
        temp = temp.substring( temp.indexOf(", courseNum")+12, temp.indexOf(", pf_id"));
        msg = msg+"("+temp+")";
        return msg; }
    // Cutting list to status only
    private String cuttingStatus(String msg) {
        msg= msg.substring(msg.indexOf(", status=")+9,msg.indexOf("}"));
        return msg;
    }
    // Cutting list to email only
    private String cuttingEmail(String msg) {
        msg= msg.substring(msg.indexOf(", email=")+8,msg.indexOf(", status"));
        return msg;
    }
    // Cutting list to student Id only
    private String cuttingId(String msg) {
        msg = msg.substring(msg.indexOf(", id=") + 5, msg.indexOf(", email"));
        return msg;
    }
    // Cutting list to user MBTI only
    private String cuttingMbti(String msg) {
        msg = msg.substring(msg.indexOf(", mbti=") + 7, msg.indexOf(", id"));
        return msg;
    }
    // Cutting list to Course num only
    private String cuttingCourseNum(String msg) {
        msg = msg.substring(msg.indexOf("(") + 1, msg.indexOf(")"));
        return msg;
    }

    //Back button event
    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if(0 <= gapTime && 2000 >= gapTime) {
            super.onBackPressed();
        }
        else {
            backBtnTime = curTime;
            Toast.makeText(this, "한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
        }

    }


    //Unit testing
    public String getStatus(String email) {
      String testStudentData=" email:harry@google.com, status:Student, email:mj@gmail.com, status:Student, email:bere@google.com, status:Student ...";
      String testSProfessorData="email:Wonkimtx@naver.com , status:Professor, email:kimy17@google.com, status:Professor ...";
      if(testStudentData.contains(email))
      return "Student";
      else if(testSProfessorData.contains(email))
          return "Professor";
      else
          return"Null"; //데이터에 없는 이메일인 경우
    }
    public String enterCourse(String courseNum) {
        String enterCourseData= "소프트웨어공학";
        String courseListData="소프트웨어공학(10177002), 데이터과학(10177003), 모바일프로그래밍(10178001), 모바일프로그래밍(10178002)";
        String temp = courseListData;
        if(courseListData.contains(courseNum)) {
            temp = temp.substring(0, courseListData.indexOf(courseNum)-1);
            enterCourseData = temp;
        }
        return enterCourseData;
    }
    public String getAddCourseInfo(String courseNum, String courseName, String pf_id, String pf_name) {
        String[] testCourseInfo = new String[]{courseNum,courseName,pf_id,pf_name};
        String[] getCourseInfo = new String[]{"10177002", "소프트웨어공학", "20090000", "정옥란"};
        if(Arrays.equals(testCourseInfo, getCourseInfo))
            return "Yes";

            return "NO";
    }

}