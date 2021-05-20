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

    // 팀 개수 설정은 의미가 없는것 같아서 입력값은 최소 팀원 수만 입력해도 될 것 같아요
    int minmember = 0;
    String courseNum;

    // MBTI 별로 멤버를 저장하기 위한 ArrayList
    /*
     * 메커니즘 >
     * 1. NP/NJ/SP/SJ 별로 인원 분류 후 팀 매칭
     * 2. 나누어떨어지지 않는 인원들은 P/J 별로 분류 후 매칭
     */

    // 처음에 전체 인원을 담을 ArrayList
    ArrayList<String> mbti = new ArrayList<>();

    // NP/NJ/SP/SJ 별로 분류할 ArrayList
    ArrayList<String> NP = new ArrayList<>();
    ArrayList<String> NJ = new ArrayList<>();
    ArrayList<String> SP = new ArrayList<>();
    ArrayList<String> SJ = new ArrayList<>();

    // 1차 분류 후 P와 J 별로 분류할 ArrayList
    ArrayList<String> P = new ArrayList<>();
    ArrayList<String> J = new ArrayList<>();

    // 팀 매칭 결과를 담을 2d array
    String[][] result = new String[100][100];


    private DatabaseReference mDatabase;
    Button btn;
    String strText;
    String readstr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_team);

        Intent passedIntent = getIntent();

        TextView project_name = (TextView) findViewById(R.id.tpname);

        strText = passedIntent.getStringExtra("tpname");
        project_name.setText(strText);

        courseNum = passedIntent.getStringExtra("coursenum");

        EditText whole = (EditText) findViewById(R.id.wholemember);
        EditText min = (EditText) findViewById(R.id.minmember);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        btn = (Button) findViewById(R.id.createTeam);


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



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int wholemember = Integer.parseInt(whole.getText().toString());
                minmember = Integer.parseInt(min.getText().toString());

                /*
                 * 1. NP/NJ/SP/SJ 별로 인원 분류 후 팀 매칭
                 * 처음에 모든 인원을 담아놓은 MBTI에서 멤버별로 분류하는 부분
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
                 * NP/NJ/SP/SJ 순으로 팀을 result array로 옮기는 과정.
                 * 각 ArrayList 별로 4번 반복
                 * 메커니즘 >
                 * 1. NP ArryayList에 담겨있는 멤버수 체크
                 * 2. ArrayList에 담겨있는 멤버수가 minmembers 이상 있는 경우, 앞에서부터 순서대로 minmembers의 수 만큼 뽑아 팀 매칭
                 * 3. 이 과정을 반복하다가, ArrayList 안에 minmembers 보다 적게 남은 경우, 남아있는 멤버들을 J or P 에 맞는 ArrayList에 넣는다
                 */
                int idx=0;

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

                // P 재분류
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


                // J 재분류
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

                Intent intent = new Intent(getApplicationContext(), TeamView.class);
                Bundle teamlistBundle = new Bundle();


                for(int i=0; i<result.length; i++){
                    String temp ="";
                    for(int j=0; j<result[i].length; j++) {
                        if(result[i][j] != null) {
                            temp = temp.concat(result[i][j] + " ");
                        }
                    }

                    Log.d("test",temp);
                    if(result[i][0] != null)
                        teamlistBundle.putString("Team " + i, temp);

                }

                intent.putExtra("BUNDLE", teamlistBundle);
                setResult(RESULT_OK,intent);
                startActivity(intent);

            }
        });



    }
}