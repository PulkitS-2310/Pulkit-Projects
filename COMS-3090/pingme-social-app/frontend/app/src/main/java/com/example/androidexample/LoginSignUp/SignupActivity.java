package com.example.androidexample.LoginSignUp;

import androidx.appcompat.app.AlertDialog;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidexample.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmEditText;
    private EditText emailEditText;
    private Button loginButton;
    private Button signupButton;
    private CheckBox showPasswordCheckbox;

    private static final String SIGNUP_URL = "http://coms-3090-029.class.las.iastate.edu:8080/signup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEditText = findViewById(R.id.signup_username_edt);
        passwordEditText = findViewById(R.id.signup_password_edt);
        confirmEditText = findViewById(R.id.signup_confirm_edt);
        emailEditText = findViewById(R.id.signup_email_edt);
        loginButton = findViewById(R.id.signup_login_btn);
        signupButton = findViewById(R.id.signup_signup_btn);
        showPasswordCheckbox = findViewById(R.id.show_password_checkbox);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirm = confirmEditText.getText().toString();
                String email = emailEditText.getText().toString();

                if (username.isEmpty() || password.isEmpty() || confirm.isEmpty() || email.isEmpty()) {
                    showPopup("Sign-up Failed", "Fields cannot be empty.");
                } else if (!password.equals(confirm)) {
                    showPopup("Sign-up Failed", "Passwords do not match.");
                } else {
                    sendSignupRequest(email, username, password);
                }
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

    private void sendSignupRequest(final String email, final String username, final String password) {
        String url = "http://coms-3090-029.class.las.iastate.edu:8080/signup";

        // Create request
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Signup Response", response);
                        showPopup("Sign-up Successful", response);  // Show raw response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = "Sign-up failed. ";
                        if (error.networkResponse != null) {
                            errorMsg += "Error Code: " + error.networkResponse.statusCode;
                        }
                        Log.e("Signup Error", error.toString());
                        showPopup("Sign-up Failed", errorMsg);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() {
                try {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("email", email);
                    jsonBody.put("username", username);
                    jsonBody.put("password", password);
                    return jsonBody.toString().getBytes("utf-8");
                } catch (Exception e) {
                    return null;
                }
            }
        };

        // Add request to queue
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }



    private void showPopup(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}
