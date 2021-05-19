package com.seproject.mbtimatchingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import androidx.appcompat.app.AppCompatActivity;

public class TeamView extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_view);

        EditText team_result = (EditText) findViewById(R.id.teamlist);
        Button maketeam = (Button) findViewById(R.id.maketeam);

        Intent passedIntent = getIntent();
        Bundle receivedbundle = passedIntent.getBundleExtra("BUNDLE");

        if(passedIntent != null) {

            String result = "";

            for(int i=0; i<receivedbundle.size(); i++) {
                String team = receivedbundle.getString("Team " + i);

                result = result.concat("Team " + i + ": \n" + team + "\n\n");
            }

            team_result.setText(result);
        }

        maketeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MakeTeam.class);
                startActivity(intent);
            }
        });
    }
}
