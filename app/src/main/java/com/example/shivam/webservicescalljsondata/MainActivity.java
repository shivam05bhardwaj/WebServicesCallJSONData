package com.example.shivam.webservicescalljsondata;

import android.app.ProgressDialog;
import android.database.DataSetObserver;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG= MainActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    private ListView lv;

    private static final String url= "https://api.androidhive.info/contacts/";

    ArrayList<HashMap<String,String>> contatcList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contatcList= new ArrayList<>();

        lv= (ListView) findViewById(R.id.list);
        new GetContacts().execute();
    }
    private class GetContacts extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            progressDialog=  new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh= new HttpHandler();

            String jsonStr= sh.makeServiceCall(url);

            Log.e(TAG, "Response from url:"+jsonStr);

            if(jsonStr!=null){
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);

                    JSONArray contacts =jsonObject.getJSONArray("contacts");

                    for (int i = 0; i < contacts.length(); i++) {

                        JSONObject c = contacts.getJSONObject(i);

                        String id = c.getString("id");
                        String name = c.getString("name");
                        String email = c.getString("email");
                        String address = c.getString("address");
                        String gender = c.getString("gender");

                        JSONObject phone = c.getJSONObject("phone");
                        String mobile = phone.getString("mobile");
                        String home = phone.getString("home");
                        String office = phone.getString("office");

                        HashMap<String, String> contact = new HashMap<>();

                        contact.put("id", id);
                        contact.put("name", name);
                        contact.put("email", email);
                        contact.put("mobile", mobile);

                        contatcList.add(contact);
                    }
                }
                    catch(final JSONException e){
                        Log.e(TAG, "JSON Parsing Error"+e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "JSON parsing Error"+e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }else{
                    Log.e(TAG,"Could not get the JSOn from the server.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Could not get JSON form server. Check Logcat",Toast.LENGTH_LONG).show();

                        }
                    });
                }
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);

            if(progressDialog.isShowing())
                progressDialog.dismiss();

            ListAdapter la= new SimpleAdapter(
                    MainActivity.this,contatcList,R.layout.list_item,
                    new String[]{"name","email","mobile"},
                    new int [] {R.id.name,R.id.email,R.id.mobile});

            lv.setAdapter(la);
        }
    }

}
