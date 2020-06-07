package com.example.socialnetwork;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText Username, Fullname, Country;
    private Button button;
    private CircleImageView profilephoto;

    private FirebaseAuth mauth;                     //Firebase
    private DatabaseReference Userref;


    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

            Username = (EditText)findViewById(R.id.login_setup_username);
            Fullname = (EditText) findViewById(R.id.login_setup_fullname);
            Country = (EditText) findViewById(R.id.login_setup_country);
            button= (Button)findViewById(R.id.setup_button);
            profilephoto= (CircleImageView) findViewById(R.id.setup_profilephoto);




            mauth= FirebaseAuth.getInstance();

        currentUserID=mauth.getCurrentUser().getUid();
            Userref=FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);



            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                        SaveAccountSetupInfo();
                }
            });
    }

    private void SaveAccountSetupInfo()
    {
        String username=Username.getText().toString();
        String fullname= Fullname.getText().toString();
        String country= Country.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this, "Please write your username...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(fullname))
        {
            Toast.makeText(this, "Please write your full name...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(country))
        {
            Toast.makeText(this, "Please write your country...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap userMap= new HashMap();
            userMap.put("username",userMap);
            userMap.put("fullname", fullname);
            userMap.put("country", country);
            userMap.put("status", "Hey there, i am using Poster Social Network, developed by Coding Cafe.");
            userMap.put("gender", "none");
            userMap.put("dob", "none");
            userMap.put("relationshipstatus", "none");
        }
    }
}
