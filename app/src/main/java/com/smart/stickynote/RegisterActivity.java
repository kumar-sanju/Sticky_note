package com.smart.stickynote;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    Button b1;
    EditText et_name, et_usr, et_pass, et_cpass;
//    final String SEND_URL = "https://mytodoappserver.000webhostapp.com/register.php";
    final String SEND_URL = "https://facultative-shipmen.000webhostapp.com/wp-admin/includes/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#e3e0cf")));
        actionBar.setTitle(Html.fromHtml("<font color='#3fb0ac'>Register</font>"));

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.parseColor("#e3e0cf"));
        }
        b1 = findViewById(R.id.bt_register);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                et_name = findViewById(R.id.et_name);
                et_usr = findViewById(R.id.et_uname);
                et_pass = findViewById(R.id.et_pass);
                et_cpass = findViewById(R.id.et_confirmPass);

                final String name, username, pass, cpass;
                name = et_name.getText().toString();
                username = et_usr.getText().toString();
                pass = et_pass.getText().toString();
                cpass = et_cpass.getText().toString();
                if (name.length() > 1 && username.length() > 1 && pass.length() > 1)
                {
                    if (pass.equals(cpass)) // Password & Confirm password match
                    {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                // Toast.makeText(Register.this, response, Toast.LENGTH_LONG).show();
                                Snackbar.make(view, response, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap();
                                params.put("name", name);
                                params.put("username", username);
                                params.put("password", pass);
                                return params;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
                        requestQueue.add(stringRequest);

                    } else {
                        Toast.makeText(RegisterActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                    }
            }
            else
                {
                    Toast.makeText(RegisterActivity.this, "Every filed should've at least 2 characters", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}