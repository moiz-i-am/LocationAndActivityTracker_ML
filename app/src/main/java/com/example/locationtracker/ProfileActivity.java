package com.example.locationtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private String recieverUserID, Current_State, senderUserID;
    private TextView userProfileName, userProfileEmail;
    private Button sendRequestButton;

    private DatabaseReference UserRef, RequestRef;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        RequestRef = FirebaseDatabase.getInstance().getReference().child("Requests");

        recieverUserID = getIntent().getExtras().get("visit_user_id").toString();
        senderUserID = mAuth.getCurrentUser().getUid();

        userProfileName = (TextView) findViewById(R.id.visit_profile_name);
        userProfileEmail = (TextView) findViewById(R.id.visit_profile_email);
        sendRequestButton = (Button) findViewById(R.id.send_request_button);

        Current_State = "new";

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
                    
                    ManageChatRequests();
                    
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ManageChatRequests() {

        RequestRef.child(senderUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(recieverUserID)){
                            String request_type = dataSnapshot.child(recieverUserID).child("request_type").getValue().toString();

                            if(request_type.equals("Sent")){
                                Current_State = "request_sent";
                                sendRequestButton.setText("Cancel Request");
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        if(!senderUserID.equals(recieverUserID)){

            sendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendRequestButton.setEnabled(false);
                    if(Current_State.equals("new")){
                        SendRequest();
                    }
                }
            });

        }else{
            sendRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    private void SendRequest() {
        RequestRef.child(senderUserID).child(recieverUserID)
                .child("request_type").setValue("Sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            RequestRef.child(recieverUserID).child(senderUserID)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendRequestButton.setEnabled(true);
                                                Current_State = "request_sent";
                                                sendRequestButton.setText("Cancel Request");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
