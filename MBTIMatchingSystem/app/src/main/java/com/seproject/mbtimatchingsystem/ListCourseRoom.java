package com.seproject.mbtimatchingsystem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ListCourseRoom extends AppCompatActivity {

    private DatabaseReference mPostReference;
    private FirebaseAuth mAuth; //파이어베이스 인스턴스 선언
    private FirebaseUser mFirebaseUser;
    ImageButton addCourseButton;
    public String courseNum;
    public String courseName;
    public String pf_id;
    public Button logOutButton;


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
                    // 사용자 이름 가져오기
                    String name = profile.getDisplayName();
                    String email = profile.getEmail();
                    TextView a = findViewById(R.id.textView7);
                    //a.setText(name);

                }
            }
        }

        logOutButton = findViewById(R.id.logOutButton); //로그아웃 버튼
        logOutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    startLoginActivity(); //로그아웃되면 로그인 화면으로 이동
                }
            });


            //ListView생성
            ListView listView = (ListView) this.findViewById(R.id.listViewCourseRoom);
            //리스트뷰에 버튼을 넣어 버튼클릭시 강좌에 들어갈 수 있게 만든다.


            //수업 검색기능을 넣는게 좋을지도<-꼭 구현을 해야할까?
            String[] CourseNameArray = {"경제학개론", "졸업작품1", "중국어1", "모바일프로그래밍", "소프트웨어공학", "생명과 나눔", "한국사"};
            //CustomListAdapter whatever = new CustomListAdapter(getActivity(), nameArray, recordArray);
            //listViewCourseRoom.setAdapter(whatever);


            addCourseButton = findViewById(R.id.addCourseButton);
            /* addCourse button listener */
            addCourseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAddCourseDialog(); // AddCourse 팝업창을 띄운다.


                }
            });

    }





    //참고자료 메모
    //레이아웃 동적생성(courseroom들갈때 써야함) https://blog.naver.com/rain483/220812579755
    // 다이얼로그 #1  https://m.blog.naver.com/kittoboy/110133796492
    // 출처: https://saeatechnote.tistory.com/entry/android안드로이드-Login-dialog만들기 [새아의 테크노트]


    private void showAddCourseDialog() {

        //add_course_dialog.xml 불러옴
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout addCourseLayout = (LinearLayout) vi.inflate(R.layout.add_course_dialog, null);

        final EditText courseNum2 = (EditText)addCourseLayout.findViewById(R.id.courseNum);
        final EditText courseName2 = (EditText)addCourseLayout.findViewById(R.id.courseName);
        final EditText pf_id2 = (EditText)addCourseLayout.findViewById(R.id.pf_id);

        new AlertDialog.Builder(this)
                .setTitle("과목 추가")
                .setView(addCourseLayout)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {

                courseNum = courseNum2.getText().toString();
                courseName = courseName2.getText().toString();
                pf_id = pf_id2.getText().toString();

                Toast.makeText(ListCourseRoom.this,
                        "학수번호 : " + courseNum  + "@n과목명 : " +courseName  + "@n교수 교번 : " +pf_id , Toast.LENGTH_SHORT).show(); } }).show();

                 postDBData(true);

    }

    private void postDBData(boolean add) {
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            DBData post = new DBData(courseNum, courseName, pf_id);
            postValues = post.toMap();
        }
        childUpdates.put("/course_list/" + courseNum, postValues);
        mPostReference.updateChildren(childUpdates);

    }

    @Override
    //활동을 초기화할 때 사용자가 현재 로그인되어 있는지 확인합니다.
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void startLoginActivity()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void startToast(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


}
