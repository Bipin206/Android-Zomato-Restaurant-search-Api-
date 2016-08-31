package zomatoapplication.zomato.com.zomato;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText mEdttxtCity;
    private  String input;
    Handler handler;
    String selectedLocation;
    private ListView listLocation,addedLocation;
    String[] place,straddedlocation;
    ArrayList<String> arrayList = new ArrayList<String>();

    Database mydb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new ViewHandler();
        mydb = new Database(this);

        mEdttxtCity = (EditText)findViewById(R.id.edttxtCity);
        listLocation = (ListView)findViewById(R.id.listLocation);
        addedLocation = (ListView)findViewById(R.id.SelectedLocation);

        mEdttxtCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input =  mEdttxtCity.getText().toString();
                if (input.length() >0) {
                    listLocation.setVisibility(View.VISIBLE);
                    addedLocation.setVisibility(View.GONE);


                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            OkHttpClient client = new OkHttpClient();


                            Request request = new Request.Builder()
                                    .url("https://api.zomato.com/v1/search.json?city_id=1&q="+input)
                                    .header("X-Zomato-API-Key", "YOUR_API_KEY")
                                    .build();

                            try {
                                Response response = client.newCall(request).execute();
                                String data = response.body().string();
                                Log.d("data", "run: " + data);

                                Message message = Message.obtain();
                                message.obj = data;
                                handler.sendMessage(message);


                                // Do something with the response.
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    thread.start();
                }


            }



            @Override
            public void afterTextChanged(Editable s) {

            }
        });







    }

    class ViewHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

                    try {
                        String jsonData = (String) msg.obj;
                        final JSONObject jsonObject = new JSONObject(jsonData);
                        Log.d("jsonobject", "jsonobject: "+jsonObject);

                        int resultfound = Integer.parseInt(jsonObject.getString("resultsFound"));
                        if(resultfound > 0){
                            int resultCount = jsonObject.getJSONArray("results").length();
                            String results = jsonObject.getJSONArray("results").toString();

                            place = new String[resultCount];



                            for (int i = 0;i < resultCount; i++){

                                String resultdata = jsonObject.getJSONArray("results").getJSONObject(i).get("result").toString();

                                String name = jsonObject.getJSONArray("results").getJSONObject(i).getJSONObject("result").get("name").toString();


                                place[i]= name;



                            }

                            final ArrayAdapter adapter = new ArrayAdapter<String>(MainActivity.this,R.layout.support_simple_spinner_dropdown_item,place);
                            listLocation.setAdapter(adapter);
                            listLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    selectedLocation = (String)adapter.getItem(position);
                                    try {
                                        String lat = jsonObject.getJSONArray("results").getJSONObject(position).getJSONObject("result").get("latitude").toString();
                                        String lon = jsonObject.getJSONArray("results").getJSONObject(position).getJSONObject("result").get("longitude").toString();

                                        arrayList.add(selectedLocation);
                                        final ArrayAdapter adapter = new ArrayAdapter<String>(MainActivity.this,R.layout.support_simple_spinner_dropdown_item,arrayList);
                                        addedLocation.setAdapter(adapter);


                                       boolean records = mydb.insertdata(selectedLocation,Double.valueOf(lat), Double.valueOf(lon));
                                        if(records == true){
                                            Toast.makeText(MainActivity.this, "Record Inserted", Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_SHORT).show();
                                        }
                                        listLocation.setVisibility(View.GONE);
                                        addedLocation.setVisibility(View.VISIBLE);
                                        addedLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                                               ArrayList<String> arrayListdata= mydb.location(position+1);
                                                Log.d("arraylist", "onItemClick: "+arrayListdata.get(0));

                                                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                                                intent.putExtra("name",arrayListdata.get(0));
                                                intent.putExtra("lat",arrayListdata.get(1));
                                                intent.putExtra("lon",arrayListdata.get(2));
                                                startActivity(intent);

                                            }
                                        });
                                    }catch (JSONException e){}


                                }
                            });





                        }






                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



        }
    }



}
