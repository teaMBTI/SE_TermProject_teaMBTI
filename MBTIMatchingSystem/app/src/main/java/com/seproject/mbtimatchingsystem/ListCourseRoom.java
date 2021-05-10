package com.seproject.mbtimatchingsystem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class ListCourseRoom extends AppCompatActivity {


    Button addCourseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_course_room);


        //ListView생성
        ListView listView = (ListView) this.findViewById(R.id.listViewCourseRoom);

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
    // 다이얼로그 #1  https://m.blog.naver.com/kittoboy/110133796492
    // 출처: https://saeatechnote.tistory.com/entry/android안드로이드-Login-dialog만들기 [새아의 테크노트]


    private void showAddCourseDialog() {

        //add_course_dialog.xml 불러옴
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout addCourseLayout = (LinearLayout) vi.inflate(R.layout.add_course_dialog, null);

        final EditText courseNum = (EditText)addCourseLayout.findViewById(R.id.courseNum);
        final EditText courseName = (EditText)addCourseLayout.findViewById(R.id.courseName);
        final EditText profNum = (EditText)addCourseLayout.findViewById(R.id.profNum);

        new AlertDialog.Builder(this)
                .setTitle("과목 추가")
                .setView(addCourseLayout)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ListCourseRoom.this,
                        "학수번호 : " + courseNum.getText().toString()
                            + "@n과목명 : " +courseName.getText().toString()
                                + "@n교수 교번 : " +profNum.getText().toString()
                        , Toast.LENGTH_SHORT).show(); } }).show();

    }


}
