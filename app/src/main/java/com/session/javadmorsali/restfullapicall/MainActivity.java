package com.session.javadmorsali.restfullapicall;

import android.accounts.NetworkErrorException;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Map<String, String>> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
///////////////////////////////////////////////////////////////////////
        listView = (ListView) findViewById(R.id.contactlist);
        contacts=new ArrayList<>();
        DownloadTask task = new DownloadTask();
        task.execute("https://api.androidhive.info/contacts/");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class DownloadTask extends AsyncTask<String, Void, Boolean> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("please waite");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected Boolean doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlconnection = null;

            try {
                url = new URL(urls[0]);
                urlconnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlconnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                return false;
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            }


            if (!result.equals("")) {
                try {
                    JSONObject jsonobject = new JSONObject(result);
                    String contactInfo = jsonobject.getString("contacts");
                    Log.i("contactInfo", contactInfo);

                    JSONArray arr = new JSONArray(contactInfo);

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject jsonPart = arr.getJSONObject(i);
                        String id = jsonPart.getString("id");
                        String name = jsonPart.getString("name");
                        String email = jsonPart.getString("email");
                        String address = jsonPart.getString("address");
                        String gender = jsonPart.getString("gender");

                        JSONObject phone = jsonPart.getJSONObject("phone");
                        String mobile = phone.getString("mobile");
                        String home = phone.getString("home");
                        String office = phone.getString("office");

                        HashMap<String, String> contact = new HashMap<>();
                        contact.put("id", id);
                        contact.put("name", name);
                        contact.put("email", email);
                        contact.put("address", address);
                        contact.put("mobile", mobile);
                        contact.put("gender", gender);
                        contact.put("home", home);
                        contact.put("office", office);

                        contacts.add(contact);
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
            ListAdapter adapter=new SimpleAdapter(MainActivity.this,
                    contacts,R.layout.list_item,
                    new String[]{"name","email","mobile"},
                    new int[] {R.id.name,R.id.email,R.id.mobile});

            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(MainActivity.this, "position : "+position, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
