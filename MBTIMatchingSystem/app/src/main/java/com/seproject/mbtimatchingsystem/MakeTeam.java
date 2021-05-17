package com.seproject.mbtimatchingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MakeTeam extends AppCompatActivity {

    // 팀 개수 설정은 의미가 없는것 같아서 입력값은 최소 팀원 수만 입력해도 될 것 같아요
    int minmembers = 5;

    // MBTI 별로 멤버를 저장하기 위한 ArrayList
    /*
     * 메커니즘 >
     * 1. NP/NJ/SP/SJ 별로 인원 분류 후 팀 매칭
     * 2. 나누어떨어지지 않는 인원들은 P/J 별로 분류 후 매칭
     */

    // 처음에 전체 인원을 담을 ArrayList
    ArrayList<String> student_ID = new ArrayList<>();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_team);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        btn = (Button) findViewById(R.id.createTeam);

        mDatabase.child("id_list").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {

                    // Object를 받아서 학생들의 학번을 ArrayList에 넣는 부분 구현해야함

                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * 1. NP/NJ/SP/SJ 별로 인원 분류 후 팀 매칭
                 * 처음에 모든 인원을 담아놓은 MBTI에서 멤버별로 분류하는 부분
                 */
                while (student_ID.size() != 0) {
                    String temp = student_ID.get(0);

                    if (temp.substring(3, 4).equals("P")) {
                        if (temp.substring(1, 2).equals("N"))
                            NP.add(temp);
                        else
                            SP.add(temp);
                    } else {
                        if (temp.substring(1, 2).equals("N"))
                            NJ.add(temp);
                        else
                            SJ.add(temp);
                    }

                    student_ID.remove(0);
                }


                /*
                 * NP/NJ/SP/SJ 순으로 팀을 result array로 옮기는 과정.
                 * 각 ArrayList 별로 4번 반복
                 * 메커니즘 >
                 * 1. NP ArryayList에 담겨있는 멤버수 체크
                 * 2. ArrayList에 담겨있는 멤버수가 minmembers 이상 있는 경우, 앞에서부터 순서대로 minmembers의 수 만큼 뽑아 팀 매칭
                 * 3. 이 과정을 반복하다가, ArrayList 안에 minmembers 보다 적게 남은 경우, 남아있는 멤버들을 J or P 에 맞는 ArrayList에 넣는다
                 */
                int idx = 0;

                while (NP.size() >= minmembers) {
                    for (int i = 0; i < minmembers; i++) {
                        int rand = (int) (Math.random() * NP.size());
                        result[idx][i] = NP.get(rand);
                        NP.remove(rand);
                    }
                    idx++;
                    if (0 < NP.size() && NP.size() < minmembers) {
                        while (NP.size() != 0) {
                            P.add(NP.get(0));
                            NP.remove(0);
                        }
                    }
                }

                while (NJ.size() >= minmembers) {
                    for (int i = 0; i < minmembers; i++) {
                        result[idx][i] = NJ.get(0);
                        NJ.remove(0);
                    }
                    idx++;
                    if (0 < NJ.size() && NJ.size() < minmembers) {
                        while (NJ.size() != 0) {
                            J.add(NJ.get(0));
                            NJ.remove(0);
                        }
                    }
                }

                while (SP.size() >= minmembers) {
                    for (int i = 0; i < minmembers; i++) {
                        result[idx][i] = SP.get(0);
                        SP.remove(0);
                    }
                    idx++;
                    if (0 < SP.size() && SP.size() < minmembers) {
                        while (SP.size() != 0) {
                            P.add(SP.get(0));
                            SP.remove(0);
                        }
                    }
                }

                while (SJ.size() >= minmembers) {
                    for (int i = 0; i < minmembers; i++) {
                        result[idx][i] = SJ.get(0);
                        SJ.remove(0);
                    }
                    idx++;
                    if (0 < SJ.size() && SJ.size() < minmembers) {
                        while (SJ.size() != 0) {
                            J.add(SJ.get(0));
                            SJ.remove(0);
                        }
                    }
                }

                // P 재분류
                while (P.size() >= minmembers) {
                    for (int i = 0; i < minmembers; i++) {
                        result[idx][i] = P.get(0);
                        P.remove(0);
                    }
                    idx++;
                    if (0 < P.size() && P.size() < minmembers) {
                        int tempidx = 0;
                        int added = 0;
                        while (P.size() != 0) {
                            if (result[tempidx % idx][0].substring(3, 4).equals("P")) {
                                result[tempidx % idx][minmembers + added] = P.get(0);
                                P.remove(0);
                            } else {
                                tempidx++;
                            }
                        }
                    }
                }


                // J 재분류
                while (J.size() >= minmembers) {
                    for (int i = 0; i < minmembers; i++) {
                        result[idx][i] = J.get(0);
                        J.remove(0);
                    }
                    idx++;
                    if (0 < J.size() && J.size() < minmembers) {
                        int tempidx = 0;
                        int added = 0;
                        while (J.size() != 0) {
                            if (result[tempidx % idx][0].substring(3, 4).equals("J")) {
                                result[tempidx % idx][minmembers + added] = J.get(0);
                                J.remove(0);
                            } else {
                                tempidx++;
                            }
                        }
                    }
                }


                // 분류된 팀 매칭 결과를 보여주는 부분 구현해야함
                // 로직
                // Result 2d 배열을 번들에 담은 후, 인텐트 실행


                Intent intent = new Intent(getApplicationContext(), TeamView.class);
                Bundle teamlistBundle = new Bundle();

                for(int i=0; i<result.length; i++){
                    String temp ="";
                    for(int j=0; j<result[i].length; j++) {
                        if(result[i][j] != null) {
                            temp.concat(result[i][j] + " ");
                        }
                    }

                    teamlistBundle.putString("Team " + i, temp);

                }

                intent.putExtra("BUNDLE", teamlistBundle);
                setResult(RESULT_OK,intent);

            }
        });

    }
}
