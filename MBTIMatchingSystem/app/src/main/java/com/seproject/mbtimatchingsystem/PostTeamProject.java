package com.seproject.mbtimatchingsystem;

import java.util.HashMap;
import java.util.Map;

public class PostTeamProject {
    public String TPName;
    public int totalStuNum;
    public int teamNum;

    public PostTeamProject() {
        // Default constructor required for calls to DataSnapshot.getValue(PostTeamProject.class)
    }

    public PostTeamProject(String TPName, int totalStuNum, int teamNum) {
        this.TPName = TPName;
        this.totalStuNum = totalStuNum;
        this.teamNum = teamNum;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("TPName", TPName);
        result.put("totalStuNum", totalStuNum);
        result.put("teamNum", teamNum);
        return result;
    }
}