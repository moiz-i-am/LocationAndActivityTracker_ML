package com.example.locationtracker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


public class RecordFragment extends Fragment{

    Button btnStartService, btnStopService;
    EditText et_activity_name;
    public static String activityName = "";
    Activity context;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container,false);

        context = getActivity();
        activityName = "unknown";

        btnStartService = view.findViewById(R.id.btn_start_service);
        btnStopService = view.findViewById(R.id.btn_stop_service);
        et_activity_name = view.findViewById(R.id.et_activity_name);


        if (!isWriteStoragePermissionGranted()){
//            return;
        }


        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (et_activity_name.getText().toString().trim().equals("")){

                    et_activity_name.setError("Please enter activity name");
                    return;
                }

                activityName = et_activity_name.getText().toString();
                startService();

                Toast.makeText(context, "start", Toast.LENGTH_SHORT).show();

            }
        });

        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent serviceIntent = new Intent(context, MyService.class);
                getActivity().stopService(serviceIntent);

                Toast.makeText(context, "stop", Toast.LENGTH_SHORT).show();

                et_activity_name.setText("");
            }
        });

        return view;

    }
    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
//                Log.v(TAG,"Permission is granted2");
                return true;
            } else {

//                Log.v(TAG,"Permission is revoked2");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
//            Log.v(TAG,"Permission is granted2");
            return true;
        }
    }


    public void startService(){

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {

            getActivity().startService(new Intent(context, MyService.class));
        }else {

            Intent serviceIntent = new Intent(context, MyService.class);
            ContextCompat.startForegroundService(context, serviceIntent);
        }

    }

}
