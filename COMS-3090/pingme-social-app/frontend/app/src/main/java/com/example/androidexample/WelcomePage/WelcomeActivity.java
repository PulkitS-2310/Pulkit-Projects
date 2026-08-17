package com.example.androidexample.WelcomePage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.androidexample.ActivityWebSocket;
import com.example.androidexample.LoginSignUp.LoginActivity;
import com.example.androidexample.LoginSignUp.SignupActivity;
import com.example.androidexample.R;

public class WelcomeActivity extends AppCompatActivity {

    private TextView messageText;   // define message textview variable
    private Button loginButton;     // define login button variable
    private Button signupButton;    // define signup button variable
    private Button logoutButton;
    private Button websocketButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);             // link to welcome activity XML

        /* initialize UI elements */
        messageText = findViewById(R.id.welcome_msg_txt);      // link to message textview in the welcome activity XML
        loginButton = findViewById(R.id.welcome_login_btn);    // link to login button in the welcome activity XML
        signupButton = findViewById(R.id.welcome_signup_btn);  // link to signup button in the welcome activity XML
        websocketButton = findViewById(R.id.websocket_demo_button);
        /* click listener on login button pressed */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when login button is pressed, use intent to switch to Login Activity */
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        /* click listener on signup button pressed */
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when signup button is pressed, use intent to switch to Signup Activity */
                Intent intent = new Intent(WelcomeActivity.this, SignupActivity.class);
                startActivity(intent);
            }

        });

        websocketButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, ActivityWebSocket.class);
            startActivity(intent);
        });




    }
}