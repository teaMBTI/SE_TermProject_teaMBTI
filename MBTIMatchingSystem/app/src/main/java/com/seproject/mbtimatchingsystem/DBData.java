package com.seproject.mbtimatchingsystem;

import java.util.HashMap;
import java.util.Map;

public class DBData {
    public String courseNum;
    public String courseName;
    public String pf_id;

    public DBData(){
        // Default constructor required for calls to DataSnapshot.getValue(DBData.class)
    }
    public DBData(String courseNum, String courseName, String pf_id ){
        this.courseNum =  courseNum;
        this.courseName = courseName;
        this.pf_id = pf_id;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("courseNum", courseNum);
        result.put("courseName", courseName);
        result.put("pf_id", pf_id);
        return result;
    }
}
//Realtime DB 사용법
// https://m.blog.naver.com/PostView.nhn?blogId=nife0719&logNo=221049879862&proxyReferer=https:%2F%2Fwww.google.com%2F