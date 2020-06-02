package com.example.loginusingvolley;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
Button btn_login;
EditText edtemail,edtpassword,edtfcmtoken;
Gson gson;
SharedPreferences sharedPreferences;
String URL= "https://mroyallegend.com/api/login";


    public static final String Sp_Status = "Status";
    public static final String MyPref = "MyPref";
    static int mStatusCode = 0;
    public String username, password,fcm_token;
    private Boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtemail= findViewById(R.id.edtemail_id);
        edtpassword= findViewById(R.id.edtpassword_id);
        edtfcmtoken= findViewById(R.id.edtfem_token_id);
        btn_login= findViewById(R.id.login_btn_id);


        OnClick();
        sharedPreferences= getSharedPreferences(MyPref, Context.MODE_PRIVATE);
        if(sharedPreferences.getString(MainActivity.Sp_Status,"").matches("LoggedIN")){
            startActivity(new Intent(MainActivity.this,Main2Activity.class));

        }

    }

    private void OnClick(){
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = edtemail.getText().toString().trim();
                password = edtpassword.getText().toString().trim();
                fcm_token = edtfcmtoken.getText().toString().trim();
                if (username.length() >= 1) {
                    if (password.length() >= 1) {
                        loginapi(username,password,fcm_token);
                    } else {
                        edtpassword.setError("please enter password");
                    }
                } else {
                    edtemail.setError("please enter email");
                }
            }
        });
    }
public  void onBackpressed(){
    if(exit){
        finish();// finish activity
    }else {
        Toast.makeText(this,"press back again to exit",Toast.LENGTH_SHORT).show();
        exit= true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                exit= false;
            }
        },3*1000);
    }
    }

    private void loginapi(final String username, final String password, final String fcm_token){
        StringRequest request = new StringRequest(Request.Method.POST, "https://mroyallegend.com/api/login", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", "onResponse: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "onErrorResponse: " + error.toString());
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                if (response.headers == null) {
                    // cant just set a new empty map because the member is final.
                    response = new NetworkResponse(
                            response.statusCode,
                            response.data,
                            Collections.<String, String>emptyMap(), // this is the important line, set an empty but non-null map.
                            response.notModified,
                            response.networkTimeMs);
                }
                return super.parseNetworkResponse(response);
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();


                map.put("email", username);
                map.put("password", password);
                map.put("fcm_token",fcm_token);
                Log.d("response", "getParams: " + map);
                return map;
            }
        };


        request.setRetryPolicy(new DefaultRetryPolicy(
                1000 * 60 * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.getCache().clear();
        queue.add(request);
    }
}


