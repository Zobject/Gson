package com.example.zobject.http;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
        result = (TextView) findViewById(R.id.textView);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button) {
            getresult();
        }
    }

    private void getresult() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url("http://www.tjracoj.xyz:8080/data.json").build();
                try {
                    Response response = client.newCall(request).execute();
                    String res = response.body().string();
                    showresult(res);
                    parJson(res);
                    //parseXMLWithPull(res);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void parJson(String res) {
        Gson gson =new Gson();
        List<APP> applist =gson.fromJson(res,new TypeToken<List<APP>>(){}.getType());
        for (APP app :applist){
            Log.d("MainActivity","id is "+app.getId());
            Log.d("MainActivity","name is "+app.getName());
            Log.d("MainActivity","viersion is "+app.getVersion());
        }
    }

    private void parseXMLWithPull(String res) {
        try {
            XmlPullParserFactory factory =XmlPullParserFactory.newInstance();
            XmlPullParser xmlPull =factory.newPullParser();
            xmlPull.setInput(new StringReader(res));
            int event=xmlPull.getEventType();
            String id="";
            String name="";
            while (event!=XmlPullParser.END_DOCUMENT){
                String nodename =xmlPull.getName();
                switch (event){
                    case XmlPullParser.START_TAG:{
                        if ("id".equals(nodename)){
                            id=xmlPull.nextText();
                        }
                        else if ("name".equals(nodename)){
                            name=xmlPull.nextText();
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG:{
                        if ("app".equals(nodename)){
                            Log.d("MainActivity","id is "+id);
                            Log.d("MainActivity","name is "+name);
                        }
                    }
                    default: break;
                }
                event=xmlPull.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void showresult(final String string) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                result.setText(string);
            }
        });
    }
}

