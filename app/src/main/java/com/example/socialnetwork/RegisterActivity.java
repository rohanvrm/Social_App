package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText Useremail, Userpassword, Userconfirmpassword;              //Variables declared under different types
    private Button Createaccountbutton;
    private FirebaseAuth mauth;

    private ProgressDialog loadingbar;                                                          //ProgressBar


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mauth= FirebaseAuth.getInstance();

        loadingbar= new ProgressDialog(this);

        Useremail =(EditText) findViewById(R.id.register_email);                            // Variables are defined
        Userpassword =(EditText)findViewById(R.id.register_password);
        Userconfirmpassword=(EditText)findViewById(R.id.register_passwordconfirm);
        Createaccountbutton= (Button)findViewById(R.id.register_createbutton);

        Createaccountbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                    Createnewaccout();
            }
        });



    }
    private void Createnewaccout()
    {
        String email= Useremail.getText().toString();
        String password=Userpassword.getText().toString();
        String confirmpassword=Userconfirmpassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"Enter Email Id",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Enter Password",Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(confirmpassword))
        {
            Toast.makeText(this,"Please Confirm Password",Toast.LENGTH_SHORT).show();
        }

        else if (!password.equals(confirmpassword))
        {
            Toast.makeText(this, "Password do not match with confirm Password",Toast.LENGTH_SHORT).show();
        }

        else
        {           loadingbar.setTitle("Creating New Account");
                    loadingbar.setMessage("Please Wait while we are creating your new account");
                    loadingbar.show();
                    loadingbar.setCanceledOnTouchOutside(true);     //no touch works until Authentication


                mauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String message="---";
                        if(task.isSuccessful())
                        {
                            SendUserToSetupActivity();

                            Toast.makeText(RegisterActivity.this, "You are Authenticated Successfully",Toast.LENGTH_SHORT).show();
                        }
                        else
                            message = task.getException().getMessage();
                            Toast.makeText(RegisterActivity.this, "Error occured"+ message,Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                    }
                });
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mauth.getCurrentUser();
        if(currentUser!=null)                                               //if authenticated, send user to login activity
        {                                                                       // allow user to go to homescreen directly
            SendUserToMainActivity();
        }
    }

    private void SendUserToMainActivity()
    {
        Intent LoginIntent =  new Intent(RegisterActivity.this, MainActivity.class);
        LoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
        finish();
    }

    private void SendUserToSetupActivity()
    {
        Intent SetupIntent =  new Intent(RegisterActivity.this, SetupActivity.class);
        SetupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(SetupIntent);
        finish();
    }


}
