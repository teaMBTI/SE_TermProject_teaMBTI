package com.seproject.mbtimatchingsystem;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class FirebasePost {
    public String id;
    public String name;
    public String mbti;
    public String call;
    public String katalk;
    public String status;

    public FirebasePost(){
        // Default constructor required for calls to DataSnapshot.getValue(FirebasePost.class)
    }

    public FirebasePost(String id, String name, String mbti, String call, String katalk, String status) {
        this.id = id;
        this.name = name;
        this.mbti = mbti;
        this.call = call;
        this.katalk = katalk;
        this.status = status;

    }
    public FirebasePost(String id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("mbti", mbti);
        result.put("call", call);
        result.put("katalk", katalk);
        result.put("status", status);

        return result;
    }
}