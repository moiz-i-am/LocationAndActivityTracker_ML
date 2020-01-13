package com.example.locationtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.Instant;


public class AddContactFragment extends Fragment {

    private RecyclerView FindFriendsRecyclerList;
    private DatabaseReference UsersRef;



    public AddContactFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_contact, container, false);

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        FindFriendsRecyclerList = (RecyclerView) view.findViewById(R.id.UsersList);
        FindFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(getContext()));


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(UsersRef,Users.class)
                .build();

        FirebaseRecyclerAdapter<Users,FindFriendsViewHolder> adapter = new FirebaseRecyclerAdapter<Users, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, final int position, @NonNull Users model) {
                holder.userName.setText(model.getName());
                holder.userEmail.setText(model.getEmail());
                holder.userActivity.setText(model.getActivity());

                // for click view of specific users in users list
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id = getRef(position).getKey();

                        Intent profileIntent = new Intent(getContext(),ProfileActivity.class);
                        profileIntent.putExtra("visit_user_id",visit_user_id);
                        startActivity(profileIntent);
                    }
                });

            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout,parent,false);
                FindFriendsViewHolder viewHolder = new FindFriendsViewHolder(view);
                return viewHolder;
            }
        };

        FindFriendsRecyclerList.setAdapter(adapter);
        adapter.startListening();


    }


    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{

        TextView userName,userEmail,userActivity;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_single_name);
            userEmail = itemView.findViewById(R.id.user_single_email);
            userActivity = itemView.findViewById(R.id.user_single_activity);
        }
    }


}
