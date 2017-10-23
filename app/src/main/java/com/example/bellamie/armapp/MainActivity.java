package com.example.bellamie.armapp;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.constraint.solver.Cache;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.StringRequest;

import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BP";

    private Button happybtn, neutralbtn, sadbtn;
    private String serverURL = "http://dennisdemenis.pythonanywhere.com/";
    private AlertDialog.Builder builder;

    private DemographicInfo di;

    private String age;
    private String gender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        happybtn = (Button) findViewById(R.id.happy_btn);
        neutralbtn = (Button) findViewById(R.id.neutral_btn);
        sadbtn = (Button) findViewById(R.id.sad_btn);

        builder = new AlertDialog.Builder(MainActivity.this);
        DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 1024*1024);
        BasicNetwork network = new BasicNetwork(new HurlStack());


        happybtn.setOnClickListener(new ButtonClickListener());
        neutralbtn.setOnClickListener(new ButtonClickListener());
        sadbtn.setOnClickListener(new ButtonClickListener());

        di = new DemographicInfo();

        age = di.getAge();
        gender = di.getGender();


    }

    /**
     * This class is the buttonclick listener
     * if one of the btns is pressed, a new sequence of the task start
     */
    private class ButtonClickListener implements View.OnClickListener{

        MediaPlayer mediaPlayer = new MediaPlayer();
        StopWatch stopWatch = new StopWatch();
        String song = "";
        double millis = 0.0;
        int counter = 1;

        @Override
        public void onClick(View view) {
            if(!mediaPlayer.isPlaying()){
                mediaPlayer.reset();
                stopWatch.reset();

                Uri myUri = Uri.parse("android.resource://" + getPackageName() + "/raw/sound" + counter);
                String message1 = myUri.toString();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                try{
                    Log.i(TAG, "tyring to setDataSource");
                    mediaPlayer.setDataSource(getApplicationContext(), myUri);
                    Log.i(TAG, "URI = " + message1);
                }catch (IOException e){
                    e.printStackTrace();
                }
                try{
                    Log.i(TAG, "trying to prepare");
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "trying to start");
                mediaPlayer.start();
                stopWatch.start();
            }
            else{
                mediaPlayer.stop();
                stopWatch.stop();
                millis = stopWatch.getTime();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, serverURL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                builder.setTitle("Server Response");
                                builder.setMessage("Resonse: " + response);
                                AlertDialog alertDialog = builder.create();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                                error.printStackTrace();
                            }
                        }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("gender", gender);
                        params.put("age", age);
                        params.put("duration", Double.toString(millis));
                        //params.put("color", color);
                        //params.put("humming", humming);

                        return params;
                    }
                };
                MySingleton.getInstance(MainActivity.this).addTorequestque(stringRequest);
            }
        }
    }
}