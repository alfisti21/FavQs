package com.ladopoulos.favqs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    SharedPreferences myPrefs;
    final String urlLogin = "https://favqs.com/api/user";
    final String apiToken = "69973e8d1208b4f6d35c4d3fff6b3c1c";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final EditText username = findViewById(R.id.username);
        final EditText email = findViewById(R.id.email);
        final EditText password = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.signupButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = username.getText().toString();
                String eMail = email.getText().toString();
                String passWord = password.getText().toString();
                if(userName.matches("")||passWord.matches("")){
                    Toast toast1 = Toast.makeText(getApplicationContext(), "Please fill in both\nusername & password", Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast1.show();
                }else{
                    final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    JSONObject jsonPreBody = new JSONObject();
                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("login", userName);
                        jsonBody.put("email", eMail);
                        jsonBody.put("password", passWord);
                        jsonPreBody.put("user",jsonBody);
                        Log.e("PREBODY", jsonPreBody.toString());
                        Log.e("PREBODY", jsonBody.toString());

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlLogin, jsonPreBody, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String check = response.getString("error_code");
                                    Log.e("TOKEN", response.toString());
                                    if(check.matches("31")){
                                        Toast toast = Toast.makeText(getApplicationContext(), "User session present.", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                                        toast.show();
                                    }else if(check.matches("32")){
                                        Toast toast = Toast.makeText(getApplicationContext(), "Email is not a valid email\nPassword is too short (minimum is 5 characters)", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                                        toast.show();
                                    }else{
                                        Log.e("TOKEN", response.toString());
                                        String token = response.getString("User-Token");
                                        Log.e("TOKEN", response.toString());
                                        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = myPrefs.edit();
                                        editor.putString("TOKEN", token);
                                        editor.apply();
                                        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(mainActivity);
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Unexpected Error", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "Token token="+apiToken);
                                return headers;
                            }
                        };
                        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                                50000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        queue.add(jsonObjectRequest);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()){
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

    }
}
