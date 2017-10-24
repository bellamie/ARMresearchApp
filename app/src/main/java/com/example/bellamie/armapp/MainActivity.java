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
import com.android.volley.Cache;
import com.android.volley.Network;
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
import java.util.ListIterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BP";

    // layout
    private Button happybtn, neutralbtn, sadbtn, startbtn;
    private View root;
    private LinearLayout emotionPanel;
    private ButtonClickListener happyListener, neutralListener, sadListener;


    private String serverURL = "http://dennisdemenis.pythonanywhere.com/";
    private AlertDialog.Builder builder;

    // Information needed for experiment
    private String age;
    private String gender;
    private Integer[]humms;
    private Map<Integer, String> hummdict;
    private String []hummdescr;
    private ArrayList<Integer> colorlist;
    private ArrayList<Integer> hummlist;
    private ArrayList<Integer> rand_index;
    private ListIterator idx_iterator;
    private MediaPlayer mediaPlayer;
    private StopWatch stopWatch;
    private int counter_idx;

    public void initializeLayout(){
        root = (LinearLayout) findViewById(R.id.root);
        emotionPanel = (LinearLayout) findViewById(R.id.emotion_panel);
        happybtn = (Button) emotionPanel.findViewById(R.id.happy_btn);
        neutralbtn = (Button) emotionPanel.findViewById(R.id.neutral_btn);
        sadbtn = (Button) emotionPanel.findViewById(R.id.sad_btn);
        startbtn = (Button) findViewById(R.id.startExperiment);
    }

    public void initializeClickListeners(){
        happyListener = new ButtonClickListener();
        neutralListener = new ButtonClickListener();
        sadListener = new ButtonClickListener();

        happybtn.setOnClickListener(happyListener);
        neutralbtn.setOnClickListener(neutralListener);
        sadbtn.setOnClickListener(sadListener);

    }

    public void initializeVariables(){
        builder = new AlertDialog.Builder(MainActivity.this);
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        age = DemographicInfo.mInstance.getAge();
        gender = DemographicInfo.mInstance.getGender();
        mediaPlayer = new MediaPlayer();
        stopWatch = new StopWatch();
        counter_idx = 0;
    }

    public void randomizeVariables(){
        hummdict = new HashMap<Integer, String>();
        hummdescr = new String[]{"f1happy", "f1neutral", "f1sad", "f2happy", "f2neutral", "f2sad", "m1happy", "m1neutral", "m1sad", "m2happy", "m2neutral", "m2sad"};
        // Create two Lists: One that contains colors as int and the other hummings as int.
        // Together they all different stimuli.
        // humms = [1-12, 1-12, 1-12]
        humms = new Integer[36];
        for (int i = 0; i < 36; i++) {
            if (i < 12) {
                humms[i] = i + 1;
                hummdict.put(i, hummdescr[i]);
            } else {
                humms[i] = i % 12 + 1;
            }
        }

        hummlist = new ArrayList<>();
        hummlist.addAll(Arrays.asList(humms));

        colorlist = new ArrayList<>();
        colorlist.addAll(Arrays.asList(R.color.yellow, R.color.yellow, R.color.yellow, R.color.yellow, R.color.yellow, R.color.yellow, R.color.yellow, R.color.yellow, R.color.yellow, R.color.yellow, R.color.yellow, R.color.yellow,
                R.color.white, R.color.white, R.color.white, R.color.white, R.color.white, R.color.white, R.color.white, R.color.white, R.color.white, R.color.white, R.color.white, R.color.white,
                R.color.darkblue, R.color.darkblue, R.color.darkblue, R.color.darkblue, R.color.darkblue, R.color.darkblue, R.color.darkblue, R.color.darkblue, R.color.darkblue, R.color.darkblue, R.color.darkblue, R.color.darkblue));
        rand_index = new ArrayList<>();
        for (int j = 0; j < 36; j++) {
            rand_index.add(j);
        }
        Collections.shuffle(rand_index);
        idx_iterator = rand_index.listIterator();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeLayout();
        initializeClickListeners();
        initializeVariables();
        randomizeVariables();

        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startbtn.setVisibility(View.GONE);
                emotionPanel.setVisibility(View.VISIBLE);
                startbtn.setText("Next");

                counter_idx = (int) idx_iterator.next();

                stopWatch.start();
                mediaPlayer.start();
                root.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), colorlist.get(counter_idx)));



            }
        });
    }

    /**
     * This class is the buttonclick listener
     * if one of the btns is pressed, a new sequence of the task start
     */
    private class ButtonClickListener implements View.OnClickListener{

        double millis = 0.0;
        Boolean hasPlayed = false;

        @Override
        public void onClick(final View view) {
            Log.i(TAG, "Inside the onClick method from the ButtonClickListener: " + view.getResources().getResourceEntryName(view.getId()));

            // Check if the sound for this round has already been played
            if(hasPlayed){
                Log.i(TAG, "mediaplayer if statement is not playing");
                mediaPlayer.reset();
                stopWatch.reset();

                Log.i(TAG, "view id =" + view.getResources().getResourceEntryName(view.getId()));

                counter_idx = (int)idx_iterator.next();

                Uri myUri = Uri.parse("android.resource://" + getPackageName() + "/raw/sound" + hummlist.get(counter_idx));
                root.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), colorlist.get(counter_idx)));

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
                    Log.i(TAG, "trying to prepare the mediaplayer");
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "trying to start the mediaplayer");
                hasPlayed = false;
            }
            else{
                Log.i(TAG, "When the sound needs to be played");

                mediaPlayer.start();
                stopWatch.start();


                mediaPlayer.stop();
                stopWatch.stop();
                millis = stopWatch.getTime();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, serverURL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i(TAG, "inside the onResponse function");
                                builder.setTitle("Server Response");
                                builder.setMessage("Resonse: " + response);
                                AlertDialog alertDialog = builder.create();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i(TAG, "Inside the error response");
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
                        params.put("color", colorlist.get(counter_idx).toString());
                        params.put("emotion", view.getResources().getResourceEntryName(view.getId()));
                        //params.put("color", color);
                        //params.put("emotion", emotion);
                        //params.put("humming", humming);
                        params.put("humming", hummdict.get(hummlist.get(counter_idx-1)));


                        Log.i(TAG, "inside getParams:" + params);
                        return params;
                    }
                };
                MySingleton.getInstance(MainActivity.this).addTorequestque(stringRequest);
                Log.i(TAG, "Getting the instance from the Singleton");
            }
        }
    }
}
