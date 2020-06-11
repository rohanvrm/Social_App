package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;
import android.widget.Toast;
import android.view.View;



import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout  drawerLayout;
    private RecyclerView postlist;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth= FirebaseAuth.getInstance();                                             //to check user Authentication
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");       //User node under which we will save database



        toolbar= (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);                                                   // For home screen toolbar in the top
        getSupportActionBar().setTitle("Home");

        drawerLayout= (DrawerLayout) findViewById(R.id.drawable_layout);                    // Small toggle button on hme screen
        navigationView= (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);                                        // Small toggle button on home screen
        // actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle= new ActionBarDrawerToggle(MainActivity.this,drawerLayout, R.string.drawer_open, R.string.drawer_close);     // Small toggle button on hme screen

        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                UserMenuSelector(item);
                return false;
            }
        });
    }



    @Override
    protected void onStart() {                                      //to check user Authentication

        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null)                                               //if not authenticated send user to login activity
        {                                                                       //dont allow user to go to homescreen directly
            SendUserTologinActivity();
        }
        else
        {
            CheckUserExistence();
        }
    }

    private void CheckUserExistence()
    {
        final String current_user_id=mAuth.getCurrentUser().getUid();

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.hasChild(current_user_id))                 //Most important validation in whole app
                {
                    SendUserToSetupActivity();                    //if user setup information is not in the database then move user to Setup Activity
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToSetupActivity()
    {
        Intent SetupIntent =  new Intent(MainActivity.this, SetupActivity.class);
        SetupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(SetupIntent);
        finish();
    }

    private void SendUserTologinActivity()                                                      //for sending user from one activity to another
    {
        Intent LoginIntent =  new Intent(MainActivity.this, LoginActivity.class);
        LoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {                      //if user clicks that toggle button on home screen
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item)                                            //Small msg comes when menu items is clicked
    {
        switch (item.getItemId())
        {
            case R.id.nav_profile:
                Toast.makeText(this, "Profile",Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_find_friends:
                Toast.makeText(this, "Find Friends",Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_friends:
                Toast.makeText(this, "Friends List",Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_logout:
                //Toast.makeText(this, "Logout",Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                SendUserTologinActivity();

                break;

        }    }


}
