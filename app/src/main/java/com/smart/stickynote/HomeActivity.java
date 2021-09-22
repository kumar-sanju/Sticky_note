package com.smart.stickynote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    FloatingActionButton fab; //Button to Add new task

    private View popupInputDialogView = null;
    private EditText taskName = null;
    private EditText taskDescription = null;
    private CheckBox status = null;
    private ImageView saveUserDataButton = null;
    private ImageView cancelUserDataButton = null;

    final List<Task> lstTask = new ArrayList<>() ;
    final int spanCount = 2; // 2 columns
    final int spacing = 40; // 40px
    final boolean includeEdge = true;

    private ImageView empty;

    final String ADD_TASK_URL = "https://facultative-shipmen.000webhostapp.com/wp-admin/includes/addTask.php";
    final String GET_ALL_TASK_URL = "https://facultative-shipmen.000webhostapp.com/wp-admin/includes/getTask.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        final String username = bundle.getString("username");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#269bd8")));
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>  Welcome " + username + "</font>"));

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#218fcf"));

        RecyclerView myrv = findViewById(R.id.recyclerview_id1);
        final RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(HomeActivity.this,lstTask);
        myrv.setLayoutManager(new GridLayoutManager(HomeActivity.this,2));
        myrv.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        myrv.setAdapter(myAdapter);

        empty = findViewById(R.id.imageView4);

        displayTask(GET_ALL_TASK_URL,username,myAdapter);
        initMainActivityControls();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
                // Set title, icon, can not cancel properties.
                alertDialogBuilder.setTitle("Add Task");
                alertDialogBuilder.setIcon(R.drawable.doc);
                alertDialogBuilder.setCancelable(true);

                initPopupViewControls();

                alertDialogBuilder.setView(popupInputDialogView);

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                saveUserDataButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String name = taskName.getText().toString();
                        final String description = taskDescription.getText().toString();
                        String TaskStatus = "0";
                        if(status.isChecked())
                            TaskStatus = "1";
                        if(!status.isChecked())
                            TaskStatus = "0";

                        if(name.length()>=1)
                        {
                            final String finalTaskStatus = TaskStatus;
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_TASK_URL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    if(response!=null){
                                        lstTask.add(new Task(name, description, finalTaskStatus, response));

                                        Snackbar.make(view, "Added Task", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();

                                            empty.setVisibility(View.GONE);

                                        myAdapter.notifyDataSetChanged();
                                    }
                                    else
                                    {
                                        Snackbar.make(view, "Unable to add Task", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                }
                            },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("name", name);
                                    params.put("description", description);
                                    params.put("status", finalTaskStatus);
                                    params.put("username", username);
                                    return params;
                                }
                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
                            requestQueue.add(stringRequest);
                            alertDialog.cancel();
                        }
                        else
                        {
                            Toast.makeText(HomeActivity.this,"Please enter Task Name",Toast.LENGTH_LONG).show();
                        }
                    }
                });

                cancelUserDataButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
            }
        });
    }

    private void initMainActivityControls()
    {
        if(fab == null)
        {
            fab = findViewById(R.id.fab);
        }
    }

    private void initPopupViewControls()
    {
        // Get layout inflater object.
        LayoutInflater layoutInflater = LayoutInflater.from(HomeActivity.this);

        // Inflate the popup dialog from a layout xml file.
        popupInputDialogView = layoutInflater.inflate(R.layout.addtask__popup, null);

        // Get user input edittext and button ui controls in the popup dialog.
        taskName = popupInputDialogView.findViewById(R.id.et_tName);
        taskDescription = popupInputDialogView.findViewById(R.id.et_tDescription);
        status = popupInputDialogView.findViewById(R.id.cb_status) ;
        saveUserDataButton = popupInputDialogView.findViewById(R.id.bt_save);
        cancelUserDataButton = popupInputDialogView.findViewById(R.id.bt_cancel);
    }

   void displayTask(String GET_ALL_TASK_URL, final String username, final RecyclerViewAdapter myAdapter )
   {
       StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_ALL_TASK_URL, new Response.Listener<String>() {
           @Override
           public void onResponse(String response) {
             //  Toast.makeText(homescreen.this, response, Toast.LENGTH_LONG).show();
               try {
                   JSONArray array = new JSONArray(response);
                   for (int i = 0; i < array.length(); i++)
                   {
                       JSONObject serverData = array.getJSONObject(i);
                       String Taskid = serverData.getString("TaskId");
                       String TaskName = serverData.getString("TaskName");
                       String TaskDescription = serverData.getString("TaskDescription");
                       String status = serverData.getString("status");
                       lstTask.add(new Task(TaskName,TaskDescription,status,Taskid));
                   }
                   if(lstTask.isEmpty())
                       empty.setVisibility(View.VISIBLE);

               }
               catch (Exception e) {
                   Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
               }

               //TODO :  *******************************************

              /* RecyclerView myrv = findViewById(R.id.recyclerview_id1);
               RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(homescreen.this,lstTask);
               myrv.setLayoutManager(new GridLayoutManager(homescreen.this,2));
               myrv.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
               myrv.setAdapter(myAdapter);*/

               myAdapter.notifyDataSetChanged();
           }
       },
               new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {
                       Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                   }
               }) {
           @Override
           protected Map<String, String> getParams() {
               Map<String, String> params = new HashMap();
               params.put("username", username);
               return params;
           }
       };
       RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
       requestQueue.add(stringRequest);
   }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:

                SharedPreferences sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
