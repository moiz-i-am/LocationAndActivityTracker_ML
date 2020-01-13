package com.example.locationtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private String recieverUserID;
    private TextView userProfileName, userProfileEmail;
    private Button sendRequestButton;

    private DatabaseReference UserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        recieverUserID = getIntent().getExtras().get("visit_user_id").toString();


        userProfileName = (TextView) findViewById(R.id.visit_profile_name);
        userProfileEmail = (TextView) findViewById(R.id.visit_profile_email);
        sendRequestButton = (Button) findViewById(R.id.send_request_button);
        
        RetrieveUserInfo();



    }

    private void RetrieveUserInfo() {
        UserRef.child(recieverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userEmail = dataSnapshot.child("email").getValue().toString();

                    userProfileName.setText(userName);
                    userProfileEmail.setText(userEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
