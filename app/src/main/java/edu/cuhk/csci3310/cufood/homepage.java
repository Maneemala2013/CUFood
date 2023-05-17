package edu.cuhk.csci3310.cufood;

import android.content.Intent;
import android.os.Bundle;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class homepage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Button signup = findViewById(R.id.signup);
        Button login = findViewById(R.id.login);

        // send sign up part
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to signup activity
                Intent intent = new Intent(homepage.this, signupActivity.class);
                startActivity(intent);

            }
        });

        // send log in part (authentication)
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to login activity
                Intent intent = new Intent(homepage.this, loginActivity.class);
                startActivity(intent);

            }
        });
    }
}

