package com.seproject.mbtimatchingsystem;

import java.util.HashMap;
import java.util.Map;

public class DBData {
    public String courseNum;
    public String courseName;
    public String pf_id;
    public String pf_name;

    public DBData(){
        // Default constructor required for calls to DataSnapshot.getValue(DBData.class)
    }

    //When add Course button is clicked, DBdata is put data to database
    public DBData(String courseNum, String courseName, String pf_id, String pf_name ){
        this.courseNum =  courseNum;
        this.courseName = courseName;
        this.pf_id = pf_id;
        this.pf_name=pf_name;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("courseNum", courseNum);
        result.put("courseName", courseName);
        result.put("pf_id", pf_id);
        result.put("pf_name", pf_name);
        return result;
    }
}