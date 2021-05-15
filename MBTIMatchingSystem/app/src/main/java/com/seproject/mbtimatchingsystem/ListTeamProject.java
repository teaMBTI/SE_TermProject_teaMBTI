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

import org.w3c.dom.Text;

public class ListTeamProject extends AppCompatActivity {

    TextView courseName;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_teamproject);

       courseName = (TextView) findViewById(R.id.course_name);


        Intent passedIntent = getIntent();
        if (passedIntent != null) {
            String course = passedIntent.getStringExtra("course");
            courseName.setText(course);
        }


        ImageButton addTeamProject = (ImageButton) findViewById(R.id.addTeamProject);
        addTeamProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent NewActivity = new Intent(getApplicationContext(), MakeTeam.class);
                startActivity(NewActivity);

            }
        });




        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView_tp);

        String[] teamProject= {"Team Project 1", "Team Project 2","Team Project 3","Team Project 4","Team Project 5","Team Project 6",
                "Team Project 7","Team Project 8","Team Project 9","Team Project 10","Team Project 11","Team Project 12","Team Project 13",
                "Team Project 14","Team Project 15"};
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

    private void startToast(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }









}
