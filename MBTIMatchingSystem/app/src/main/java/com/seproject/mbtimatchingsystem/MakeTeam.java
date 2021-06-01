package com.seproject.mbtimatchingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MakeTeam extends AppCompatActivity {

    // ArrayList for whole member
    ArrayList<String> mbti = new ArrayList<>();

    // Arraylist for NP/NJ/SP/SJ (1st classification)
    ArrayList<String> NP = new ArrayList<>();
    ArrayList<String> NJ = new ArrayList<>();
    ArrayList<String> SP = new ArrayList<>();
    ArrayList<String> SJ = new ArrayList<>();

    // Arraylist for P/J (2nd classification)
    ArrayList<String> P = new ArrayList<>();
    ArrayList<String> J = new ArrayList<>();

    // 2d array for saving team info
    String[][] result = new String[100][100];


    int minmember = 0;
    String courseNum;

    private DatabaseReference mDatabase;
    Button btn;
    String TPName;
    String readstr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_team);

        Intent passedIntent = getIntent();

        TextView project_name = (TextView) findViewById(R.id.tpname);

        TPName = passedIntent.getStringExtra("tpname");
        project_name.setText(TPName);

        courseNum = passedIntent.getStringExtra("coursenum");

        EditText whole = (EditText) findViewById(R.id.wholemember);
        EditText min = (EditText) findViewById(R.id.minmember);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        btn = (Button) findViewById(R.id.createTeam);


        // Getting the students' list of the course from the firebase
        // and then, put them to the declared arraylist
        mDatabase.child("course_list").child(courseNum).child("st_Participate_id").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {

                    readstr = readstr.concat(String.valueOf(task.getResult().getValue()));

                    while(readstr.indexOf("=") != -1) {

                        int idx = readstr.indexOf("=");
                        String idtemp = readstr.substring(idx - 9, idx);
                        String mbtitemp = readstr.substring(idx+1, idx+5);

                        mbti.add(mbtitemp + "(" + idtemp + ")");
                        readstr = readstr.substring(idx+5);

                    }

                }
            }
        });



        // when the button is clicked, update the team info in the database
        // and go to the teamview screen
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int wholemember = Integer.parseInt(whole.getText().toString());
                minmember = Integer.parseInt(min.getText().toString());

                team_matching();

                Intent intent = new Intent(getApplicationContext(), TeamView.class);
                HashMap<String, String> res = new HashMap<>();

                // Making a string for the team result
                for(int i=0; i<result.length; i++){
                    String temp ="";
                    for(int j=0; j<result[i].length; j++) {
                        if(result[i][j] != null) {
                            temp = temp.concat(result[i][j].substring(5,14) + " ");
                        }
                    }

                    if(result[i][0] != null){
                        res.put("Team " + i, temp);
                    }

                }

                // set the team info to the firebase
                mDatabase.child("course_list").child(courseNum).child("teaminfo").child(TPName).setValue(res);

                startActivity(intent);

            }
        });

    }

    public void team_matching(){
        /*
         * classify the members to NP/NJ/SP/SJ array
         * The classification is processed by their MBTI
         */
        while(mbti.size() != 0) {
            String temp = mbti.get(0);

            if(temp.substring(3,4).equals("P")) {
                if(temp.substring(1,2).equals("N"))
                    NP.add(temp);
                else
                    SP.add(temp);
            }
            else {
                if(temp.substring(1,2).equals("N"))
                    NJ.add(temp);
                else
                    SJ.add(temp);
            }

            mbti.remove(0);
        }


        /*
         * create team result array using NP/NJ/SP/SJ arraylists
         * Algorithm mechanism >
         * 1. check number of members in ArryayList
         * 2. number of members are more than minmembers, pick the number of minmembers and match a team by order
         * 3. Repeating step 2, when members are lower than 'minmember' in the arraylist, put the members in 'J' or 'P' array properly
         */
        int idx=0;

        // classify for members who have 'N' and 'P'
        while(NP.size() >= minmember) {
            for(int i=0; i<minmember; i++) {
                int rand = (int) (Math.random() * NP.size());
                result[idx][i] = NP.get(rand);
                NP.remove(rand);
            }
            idx++;
            if(0 < NP.size() && NP.size() < minmember) {
                while(NP.size() != 0) {
                    P.add(NP.get(0));
                    NP.remove(0);
                }
            }
        }

        // classify for members who have 'N' and 'J'
        while(NJ.size() >= minmember) {
            for(int i=0; i<minmember; i++) {
                result[idx][i] = NJ.get(0);
                NJ.remove(0);
            }
            idx++;
            if(0 < NJ.size() && NJ.size() < minmember) {
                while(NJ.size() != 0) {
                    J.add(NJ.get(0));
                    NJ.remove(0);
                }
            }
        }

        // classify for members who have 'S' and 'P'
        while(SP.size() >= minmember) {
            for(int i=0; i<minmember; i++) {
                result[idx][i] = SP.get(0);
                SP.remove(0);
            }
            idx++;
            if(0 < SP.size() && SP.size() < minmember) {
                while(SP.size() != 0) {
                    P.add(SP.get(0));
                    SP.remove(0);
                }
            }
        }

        // classify for members who have 'S' and 'J'
        while(SJ.size() >= minmember) {
            for(int i=0; i<minmember; i++) {
                result[idx][i] = SJ.get(0);
                SJ.remove(0);
            }
            idx++;
            if(0 < SJ.size() && SJ.size() < minmember) {
                while(SJ.size() != 0) {
                    J.add(SJ.get(0));
                    SJ.remove(0);
                }
            }
        }

        // classify again for 'P' group
        while(P.size() >= minmember) {
            for(int i=0; i<minmember; i++) {
                result[idx][i] = P.get(0);
                P.remove(0);
            }
            idx++;
            if(0 < P.size() && P.size() < minmember) {
                int tempidx=0;
                int added=0;
                while(P.size() != 0) {
                    if(result[tempidx%idx][0].substring(3,4).equals("P")) {
                        result[tempidx%idx][minmember+added] = P.get(0);
                        P.remove(0);
                    } else {
                        tempidx++;
                    }
                }
            }
        }


        // classify again for 'J' group
        while(J.size() >= minmember) {
            for(int i=0; i<minmember; i++) {
                result[idx][i] = J.get(0);
                J.remove(0);
            }
            idx++;
            if(0 < J.size() && J.size() < minmember) {
                int tempidx=0;
                int added=0;
                while(J.size() != 0) {
                    if(result[tempidx%idx][0].substring(3,4).equals("J")) {
                        result[tempidx%idx][minmember+added] = J.get(0);
                        J.remove(0);
                    } else {
                        tempidx++;
                    }
                }
            }
        }
    }

    /*
        Unit test method
        put minmember and minimal data
        and result array is not null, it is processing well
     */
    public String createTeamTest() {

        int minmember=2;

        mbti.add("ENTJ");
        mbti.add("ESTJ");
        mbti.add("ENTP");
        mbti.add("ESTP");
        mbti.add("INFJ");
        mbti.add("ISTJ");
        mbti.add("INFP");
        mbti.add("ISFP");

        team_matching();

        if(result[0][0] != null) return "Success";
        else return "Fail";
    }

}