package com.seproject.mbtimatchingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

public class TeamView extends AppCompatActivity {

    String strText;
    String courseNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_view);

        TextView project_name = (TextView) findViewById(R.id.tpname);
        EditText team_result = (EditText) findViewById(R.id.teamlist);
        Button maketeam = (Button) findViewById(R.id.maketeam);

        Intent passedIntent = getIntent();

        strText = passedIntent.getStringExtra("tpname");
        courseNum = passedIntent.getStringExtra("coursenum");

        project_name.setText(strText);

        Bundle teamlistBundle = passedIntent.getBundleExtra("BUNDLE");
        if(teamlistBundle == null) {

        } else {
            String result = "";

            for(int i=0; i<teamlistBundle.size(); i++) {
                String team = teamlistBundle.getString("Team " + i);

                Log.d("print",team);
                result = result.concat("Team " + i + ": \n" + team + "\n\n");
            }

            team_result.setText(result);
        }


        /*
        if(passedIntent != null) {

            Bundle receivedbundle = passedIntent.getBundleExtra("BUNDLE");


        }
         */

        maketeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MakeTeam.class);
                intent.putExtra("tpname",strText);
                intent.putExtra("coursenum",courseNum);
                setResult(RESULT_OK, intent);
                startActivity(intent);
            }
        });
    }
}
