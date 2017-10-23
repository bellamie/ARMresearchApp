package com.example.bellamie.armapp;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BP";

    private Button happybtn, neutralbtn, sadbtn;
    private String serverURL = "http://dennisdemenis.pythonanywhere.com/";
    private AlertDialog.Builder builder;
    private View root;

    private String age;
    private String gender;
    ArrayList<Integer> counter;
    Map<Integer, String> hummdict;
    String []hummdescr;

    private ArrayList<Integer> sounds;
    private ArrayList<Integer> colors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = (LinearLayout) findViewById(R.id.root);
        happybtn = (Button) findViewById(R.id.happy_btn);
        neutralbtn = (Button) findViewById(R.id.neutral_btn);
        sadbtn = (Button) findViewById(R.id.sad_btn);

        builder = new AlertDialog.Builder(MainActivity.this);
        DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 1024*1024);
        BasicNetwork network = new BasicNetwork(new HurlStack());


        happybtn.setOnClickListener(new ButtonClickListener());
        neutralbtn.setOnClickListener(new ButtonClickListener());
        sadbtn.setOnClickListener(new ButtonClickListener());

        age = DemographicInfo.mInstance.getAge();
        gender = DemographicInfo.mInstance.getGender();
        hummdict = new HashMap<Integer, String>();
        hummdescr = new String[]{"f1happy","f1neutral","f1sad","f2happy","f2neutral", "f2sad","m1happy","m1neutral", "m1sad","m2happy","m2neutral","m2sad"};

        colors = new ArrayList<>();
        colors.addAll(Arrays.asList(R.color.yellow, R.color.white, R.color.darkblue, R.color.yellow, R.color.white, R.color.darkblue, R.color.yellow, R.color.white, R.color.darkblue));
        Collections.shuffle(colors);

        counter =new ArrayList<Integer>();
        for(int i = 1; i<13; i++){
            counter.add(i);
            hummdict.put(i,hummdescr[i-1]);}
        Collections.shuffle(counter);
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
        int counter_idx = 0;

        @Override
        public void onClick(View view) {
            if(!mediaPlayer.isPlaying()){
                mediaPlayer.reset();
                stopWatch.reset();

                Uri myUri = Uri.parse("android.resource://" + getPackageName() + "/raw/sound" + counter.get(counter_idx));
                root.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), colors.get(counter_idx)));
                counter_idx++;
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
                        params.put("reactiontime", Double.toString(millis));
                        //params.put("color", color);
                        //params.put("emotion", emotion);
                        //params.put("humming", humming);
                        params.put("humming", hummdict.get(counter.get(counter_idx)));

                        return params;
                    }
                };
                MySingleton.getInstance(MainActivity.this).addTorequestque(stringRequest);
                counter_idx++;

            }
        }
    }
}
