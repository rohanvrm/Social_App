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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    private Button LoginButton;
    private EditText UserEmail, UserPassword;
    private TextView NeedNewAccountLink;



    private FirebaseAuth mauth;

    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        NeedNewAccountLink = (TextView) findViewById(R.id.activity_create_new);
        UserEmail= (EditText)findViewById(R.id.login_email);
        UserPassword= (EditText) findViewById(R.id.login_password);
        LoginButton= (Button)findViewById(R.id.login_createbutton);


        mauth= FirebaseAuth.getInstance();

        loadingbar= new ProgressDialog(this);

        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override                                                                   //When Button is clicked
            public void onClick(View view) {
                SendUserToRegisterActivity();
            }
        });


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Allowusertologin();
            }
        });
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

    private void SendUserToRegisterActivity()
    {
        Intent RegisterIntent =  new Intent(LoginActivity.this, RegisterActivity.class);
        //RegisterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(RegisterIntent);
        //finish();                             //finish() is removed so that when back is clicked , user go backs to login activity
    }

    private void Allowusertologin()
    {
        String email=UserEmail.getText().toString();
        String password= UserPassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"Enter Email Id",Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Enter Password",Toast.LENGTH_SHORT).show();
        }
        else
        {       loadingbar.setTitle("Logging In");
            loadingbar.setMessage("Please Wait while we are logging in your account");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);     //no touch works until Authentication

            mauth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {   String message="---";
                    if(task.isSuccessful())
                    {

                        SendUserToMainActivity();
                        Toast.makeText(LoginActivity.this, "Logged In Successfully",Toast.LENGTH_SHORT);



                        loadingbar.dismiss();
                    }
                    else
                    {
                        message = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, "Error" + message, Toast.LENGTH_SHORT);
                        loadingbar.dismiss();
                    }
                }
            });
        }
    }

    private void SendUserToMainActivity()
    {
        Intent LoginIntent =  new Intent(LoginActivity.this, MainActivity.class);
        LoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
        finish();
    }

}
