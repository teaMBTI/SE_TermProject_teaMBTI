package com.seproject.mbtimatchingsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class WritePInformation extends AppCompatActivity {
    private DatabaseReference mPostReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    public String name;
    public String id;
    public CheckBox checkBox;
    public String status;
    public String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinfo);

        mFirebaseAuth = FirebaseAuth.getInstance(); //Get current user information

        EditText name2 = findViewById(R.id.nameEditText);
        EditText id2 = findViewById(R.id.idEditText);
        checkBox = findViewById(R.id.professor);
        checkBox.setChecked(true);
        if(checkBox.isChecked()){
            status = "Professor";
        }

        Button save_prof = findViewById(R.id.save_prof);
        save_prof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = name2.getText().toString();
                id = id2.getText().toString();

                postFirebaseDatabase(true);

                startLoginActivity(); //Go to login screen on successful save
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Get user Email
                email = profile.getEmail();

            }
        }
    }
 
    // Save entered information to Firebase db
    public void postFirebaseDatabase(boolean add){
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            FirebasePost post = new FirebasePost(id, name, status, email);
            postValues = post.toMap();
        }
        childUpdates.put("/id_list/" + id, postValues);
        mPostReference.updateChildren(childUpdates);
    }

    // Go to login screen
    private void startLoginActivity()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}