package com.seproject.mbtimatchingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class TeamView extends AppCompatActivity {

    String TPName;
    String courseNum;

    private DatabaseReference mDatabase;
    String readstr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_view);

        TextView project_name = (TextView) findViewById(R.id.tpname);
        EditText team_make = (EditText) findViewById(R.id.teammake);

        Button maketeam = (Button) findViewById(R.id.maketeam);
        EditText team_view = (EditText) findViewById(R.id.teamview);

        /*
        Teamview xml can be accessed with two ways
            1. from maketeam
            2. from listcourseroom
         */

        // get the intent
        Intent passedIntent = getIntent();

        TPName = passedIntent.getStringExtra("tpname");
        courseNum = passedIntent.getStringExtra("coursenum");

        // set the value from the intent
        project_name.setText(TPName);

        /*
            get the team information to show
            change the default text "Team matching is completed"
            if the team exists, make the listview for team information

            if the team is selected, show the students' info in the team
            in this process, use the students' list of the firebase
         */
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("course_list").child(courseNum).child("teaminfo").child(TPName).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {

                    readstr = String.valueOf(task.getResult().getValue());

                    if(!readstr.equals("null")){
                        team_make.setText("팀 생성이 완료되었습니다");

                        ArrayList<String> items = new ArrayList<>();

                        readstr = readstr.substring(1);
                        do {
                            int idx = readstr.indexOf(",");

                            String temp = readstr.substring(0, idx);
                            readstr = readstr.substring(idx+2);

                            String[] teamString = temp.split("=");

                            items.add("[" + teamString[0] + "]" + " " + teamString[1]);

                        } while(readstr.indexOf(",") != -1);

                        ListView listView = (ListView) findViewById(R.id.teamlist);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, items);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //팀프로젝트 리스트뷰 아이템 클릭시
                                String strText = (String) listView.getItemAtPosition(position);

                                String[] member = strText.split(" ");

                                mDatabase.child("id_list").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (!task.isSuccessful()) {
                                            Log.e("firebase", "Error getting data", task.getException());
                                        } else {

                                            String whole_student = String.valueOf(task.getResult().getValue());
                                            whole_student = whole_student.substring(1);
                                            String teamdata = "";


                                            for(int i=2; i<member.length; i++) {
                                                String m = member[i];

                                                String substr = whole_student.substring(whole_student.indexOf(m));

                                                substr = substr.substring(0, substr.indexOf("}"));

                                                String[] info = new String[6];

                                                substr = substr.substring(11);
                                                for(int j=0; j<6; j++){
                                                    int sidx = substr.indexOf("=") + 1;
                                                    int eidx = substr.indexOf(",");
                                                    info[j] = substr.substring(sidx,eidx);

                                                    substr = substr.substring(eidx+1);
                                                }

                                                teamdata = teamdata.concat("[" + info[2] + " " + info[4] + "]" + " - " + info[3] + "\n" +
                                                                            "phone : " + info[1] + "\n" +
                                                                            "kakao ID : " + info[0] + "\n" +
                                                                            "e-mail : " + info[5] + "\n\n");

                                            }

                                            team_view.setText(teamdata);

                                        }
                                    }
                                });


                            }
                        });

                    }

                }
            }
        });


        // when the maketeam button is clicked, check the status
        // if the professor clicked the button, start the intent to create team
        // else view the toast message
        maketeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(passedIntent.getStringExtra("status").equals("Professor")) {
                    Intent intent = new Intent(getApplicationContext(), MakeTeam.class);
                    intent.putExtra("tpname", TPName);
                    intent.putExtra("coursenum", courseNum);
                    setResult(RESULT_OK, intent);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "학생은 팀 생성 권한이 없습니다" , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
