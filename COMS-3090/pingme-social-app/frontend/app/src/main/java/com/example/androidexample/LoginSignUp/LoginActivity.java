package com.example.androidexample.LoginSignUp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidexample.MainActivity;
import com.example.androidexample.R;
import com.example.androidexample.WelcomePage.WelcomeActivity;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signupButton;
    private Button welcomeButton;
    private CheckBox showPasswordCheckbox;

    private static final String LOGIN_URL = "http://coms-3090-029.class.las.iastate.edu:8080/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.login_username_edt);
        passwordEditText = findViewById(R.id.login_password_edt);
        loginButton = findViewById(R.id.login_login_btn);
        signupButton = findViewById(R.id.login_signup_btn);
        welcomeButton = findViewById(R.id.welcome_btn);
        showPasswordCheckbox = findViewById(R.id.show_password_checkbox);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String identifier = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (identifier.isEmpty() || password.isEmpty()) {
                    showPopup("Login Failed", "Fields cannot be empty.");
                } else {
                    loginUser(identifier, password);
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        welcomeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
            }
        });

        showPasswordCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    private void loginUser(String identifier, String password) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Login Response", response);



                        if ("Password correct".equals(response)) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("USERNAME", identifier); // Pass username
                            startActivity(intent);
                            finish();
                        } else {
                            showPopup("Login Failed", "Invalid credentials.");
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        showPopup("Login Failed", "Check your internet connection and try again.");
                    }
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("password", password);

                    if (identifier.contains("@")) {
                        params.put("email", identifier);
                    } else {
                        params.put("username", identifier);
                    }

                    return new JSONObject(params).toString().getBytes("utf-8");
                } catch (Exception e) {
                    return null;
                }
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void showPopup(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void onLoginSuccess(String username) {
        FirebaseMessaging.getInstance()
                .subscribeToTopic("user_" + username)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("LoginActivity", "FCM topic subscribe failed");
                    }
                });

    }
}
