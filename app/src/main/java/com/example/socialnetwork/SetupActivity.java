package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.firestore.auth.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText Username, Fullname, Country;
    private Button button;
    private CircleImageView profilephoto;

    private FirebaseAuth mauth;                     //Firebase
    private DatabaseReference Userref;
    private ProgressDialog loadingbar;
    private StorageReference UserProfileRef;


    final static int Gallery_Pick = 1;


    //for user to pick image from gallery

    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Username = (EditText) findViewById(R.id.login_setup_username);
        Fullname = (EditText) findViewById(R.id.login_setup_fullname);
        Country = (EditText) findViewById(R.id.login_setup_country);
        button = (Button) findViewById(R.id.setup_button);
        profilephoto = (CircleImageView) findViewById(R.id.setup_profilephoto);


        loadingbar = new ProgressDialog(this);

        mauth = FirebaseAuth.getInstance();

        currentUserID = mauth.getCurrentUser().getUid();
        Userref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);


        UserProfileRef = FirebaseStorage.getInstance().getReference().child("Profile images");       //profile image folder (named UserProfileRef) in firebase


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveAccountSetupInfo();
            }
        });


        profilephoto.setOnClickListener(new View.OnClickListener() {            //user is directed to phone gallery
            @Override
            public void onClick(View view) {                               //for user to pick image from gallery
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);             //user picks one image
            }
        });
        Userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    ImageView imageView= (ImageView) findViewById(R.id.setup_profilephoto);
                    Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(imageView);
                    //String image= dataSnapshot.child("profilephoto").getValue().toString();
                    //Picasso.with(SetupActivity.this).load(image).placeholder(R.drawable.profile_icon).into(profilephoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {           //created this method for getting the result of picked image from gallery

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null)        // gallery pick means if the image is picked from gallery
        {
            Uri ImageUri = data.getData();

            //once the pick is done the user is sent to crop functionality

            CropImage.activity()                                                 // cropping functionality
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1).start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);     //crop activity result is stored in variable result

            if (resultCode == RESULT_OK)                              //If crop image doesnt work then request code is not ok
            {

                loadingbar.setTitle("Profile Image");
                loadingbar.setMessage("Please wait, while we updating your profile image...");
                loadingbar.show();
                loadingbar.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();                                 //get the result (URI) of that cropped image

                StorageReference filepath = UserProfileRef.child(currentUserID + ".jpg");       //filepath for the storage reference here

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {   //filepath.putFile(resultUri) Image is stored with this
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SetupActivity.this, "Profile image saved successfully", Toast.LENGTH_SHORT).show();

                            final String downloadUrl = task.getResult().getStorage().getDownloadUrl().toString(); //get the url of the image from the folder from firebase storage
                    //TODO below  system goes to else statement , task.successful() is not working
                            Userref.child("profileimage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {   //profile photo is saved in firebase database
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent selfIntent = new Intent(SetupActivity.this, SetupActivity.class);     //Send user to Setup activity
                                        startActivity(selfIntent);
                                        Log.e("rhn", "rohan");

                                        Toast.makeText(SetupActivity.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                    } else {
                                        String message = task.getException().getMessage();
                                        Toast.makeText(SetupActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                        Log.e("rhn", "nonononon");
                                        loadingbar.dismiss();
                                    }
                                }
                            });
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Error Occured: Image cannot be cropped. Try Again.", Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();
            }
        }
    }


    private void SaveAccountSetupInfo() {
        String username = Username.getText().toString();
        String fullname = Fullname.getText().toString();
        String country = Country.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please write your username...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(fullname)) {
            Toast.makeText(this, "Please write your full name...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(country)) {
            Toast.makeText(this, "Please write your country...", Toast.LENGTH_LONG).show();
        } else {

            loadingbar.setTitle("Saving Inforation");
            loadingbar.setMessage("Please Wait while we are Saving in your account");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);     //no touch works until Authentication

            //Log.e("xyz","><"+username+"><"+fullname+"><"+country);

            //HashMap userMap = new HashMap();
            HashMap<String, String> userMap = new HashMap();
            userMap.put("username", username);
            userMap.put("fullname", fullname);
            userMap.put("country", country);
            userMap.put("status", "Hey there, i am using Poster Social Network, developed by Coding Cafe.");
            userMap.put("gender", "none");
            userMap.put("dob", "none");
            userMap.put("relationshipstatus", "none");


            FirebaseFirestore.getInstance().collection("user_info").add(userMap)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {

                                SendUserToMainActivity();
                                Toast.makeText(SetupActivity.this, "Your Account is created Successfully.", Toast.LENGTH_LONG).show();
                                loadingbar.dismiss();
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(SetupActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                        }
                    });
        }
    }

    private void SendUserToMainActivity() {
        Intent LoginIntent = new Intent(SetupActivity.this, MainActivity.class);
        LoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
        finish();
    }
}
