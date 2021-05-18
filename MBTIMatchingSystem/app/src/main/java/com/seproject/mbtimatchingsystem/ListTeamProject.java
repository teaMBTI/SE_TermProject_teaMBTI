package com.seproject.mbtimatchingsystem;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class ListTeamProject extends AppCompatActivity {

    TextView courseName;
    Button participate;

    String id;
    String status;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_teamproject);

        courseName = (TextView) findViewById(R.id.course_name);

        Intent passedIntent = getIntent();
        if (passedIntent != null) {
            String course = passedIntent.getStringExtra("course");
            //courseName.setText(course);
        }

        // +버튼 이벤트 핸들러
        ImageButton addTeamProject = (ImageButton) findViewById(R.id.addTeamProject);
        addTeamProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                mFirebaseAuth = FirebaseAuth.getInstance(); //유저를 얻어온다
//                mFirebaseUser = mFirebaseAuth.getCurrentUser();//혹시 인증 유지가 안될 수 있으니 유저 확인
                DatabaseReference mDatabase;
                mFirebaseAuth = FirebaseAuth.getInstance();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    for (UserInfo profile : user.getProviderData()) {
//                        // 사용자 이름 가져오기
//                        String name = profile.getDisplayName();
//                        String email = profile.getEmail();
//                        startToast(name +" "+ email);
                        String uid = user!= null ? user.getUid(): null;


                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("id_list").child(uid).child("email")



                    }
                }




                Intent NewActivity = new Intent(getApplicationContext(), AddTeamProject.class);
                startActivity(NewActivity);


            }
        });

/*        Button participate = (Button) findViewById(R.id.participate);
        participate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startToast("수업에 참가하셨습니다.");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                for (UserInfo profile : user.getProviderData()) {
                    // 사용자 닉네임 가져오기
                    String name = profile.getDisplayName();
                }
                }
            }
        });*/









        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView_tp);

        String[] teamProject = {"Team Project 1", "Team Project 2", "Team Project 3", "Team Project 4", "Team Project 5", "Team Project 6",
                "Team Project 7", "Team Project 8"};
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

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
