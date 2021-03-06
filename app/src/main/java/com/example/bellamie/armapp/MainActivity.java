package com.example.bellamie.armapp;

import android.content.Intent;
import android.graphics.Color;
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
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.StringRequest;

import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BP";

    // layout
    private Button happybtn, neutralbtn, sadbtn, startbtn;
    private View root;
    private LinearLayout emotionPanel;
    private ButtonClickListener happyListener, neutralListener, sadListener;
    private String username;


    private String serverURL = "http://dennisdemenis.pythonanywhere.com/";
    private AlertDialog.Builder builder;

    // Information needed for experiment
    private String age, gender;
    private Integer[]humms;
    private Map<Integer, String> hummdict;
    private String []hummdescr;
    private ArrayList<Integer> colorlist, hummlist, rand_index;
    private ListIterator idx_iterator;
    private MediaPlayer mediaPlayer;
    private StopWatch stopWatch;
    private int counter_idx;
    private int iterations;
    private int currentColorInt;
    private String currentHumm;
    private String convertedHumm;


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
        age = getIntent().getExtras().getString("age");
        Log.i(TAG, "Age inside initilizevariables: " + age);
        gender = getIntent().getExtras().getString("gender");
        Log.i(TAG, "gender inside the initializeVariables: " + gender);
        mediaPlayer = new MediaPlayer();
        stopWatch = new StopWatch();
        counter_idx = 0;
        username = createRandomUsername();
        iterations = 4;
        currentHumm = "";
        currentColorInt = 0;
        convertedHumm = "";
    }

    public String createRandomUsername(){
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(1000);
        return "user_" + randomInt;
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
        for (int i = 0; i<iterations; i++) {
            for (int j = 0; j < 36; j++) {
                rand_index.add(j);
            }
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

        if(idx_iterator.hasNext()) {
            counter_idx = (int) idx_iterator.next();
            startbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startbtn.setVisibility(View.GONE);
                    emotionPanel.setVisibility(View.VISIBLE);
                    startbtn.setText("Next");


                    Uri myUri = Uri.parse("android.resource://" + getPackageName() + "/raw/sound" + hummlist.get(counter_idx));
                    String message1 = myUri.toString();
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

                    stopWatch.start();
                    mediaPlayer.start();
                    root.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), colorlist.get(counter_idx)));
                    currentColorInt = ContextCompat.getColor(getApplicationContext(), colorlist.get(counter_idx));

                    currentHumm = "sound" + hummlist.get(counter_idx);
                    convertedHumm = getSound(currentHumm);
                    Log.i(TAG, "color: " + currentColorInt + " humm: " + currentHumm);
                }

            });
        }
    }

    public String getColorConvert(int colorInt){
        if (colorInt == -1){
            return "white";
        } else if (colorInt == -15132304){
            return "blue";
        }else if (colorInt == -256){
            return "yellow";
        }else
            return "ConvertError";
    }

    public String getSound(String sound){
        if(sound.equals("sound1")){
            return "f1happy";
        }else if(sound.equals("sound2")) {
            return "f1neutral";
        }else if(sound.equals("sound3")) {
            return "f1sad";
        }else if(sound.equals("sound4")) {
            return "f2happy";
        }else if(sound.equals("sound5")) {
            return "f2neutral";
        }else if(sound.equals("sound6")){
            return "f2sad";
        }else if(sound.equals("sound7")){
            return "m1happy";
        }else if (sound.equals("sound8")){
            return "m1neutral";
        }else if(sound.equals("sound9")){
            return "m1sad";
        }else if(sound.equals("sound10")){
            return "m2happy";
        }else if (sound.equals("sound11")){
            return "m2neutral";
        }else if (sound.equals("sound12")){
            return "m2sad";
        }else{
            return "something went wrong with converting the sounds";
        }
    }

    /**
     * This class is the buttonclick listener
     * if one of the btns is pressed, a new sequence of the task start
     */
    private class ButtonClickListener implements View.OnClickListener{

        double millis = 0.0;

        @Override
        public void onClick(final View view) {
            Log.i(TAG, "Inside the onClick method from the ButtonClickListener: " + view.getResources().getResourceEntryName(view.getId()));

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
                            Toast.makeText(MainActivity.this, "error in onResponse", Toast.LENGTH_SHORT).show();
                            NetworkResponse errorRes = error.networkResponse;
                            String stringData = "";
                            if(errorRes != null && errorRes.data != null){
                                try {
                                    stringData = new String(errorRes.data,"UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("gender", gender);
                    params.put("age", age);
                    params.put("reactiontime", Double.toString(millis));
                    params.put("color", ""+ getColorConvert(currentColorInt));
                    params.put("emotion", view.getResources().getResourceEntryName(view.getId()));
                    params.put("humm", convertedHumm);
                    params.put("name", username);


                    Log.i(TAG, "inside getParams:" + params);
                    return params;
                }
            };
            MySingleton.getInstance(MainActivity.this).addToRequestQueue(stringRequest);


            mediaPlayer.reset();
            stopWatch.reset();

            if (idx_iterator.hasNext()) {
                counter_idx = (int) idx_iterator.next();
            }else{
                Intent endIntent = new Intent(MainActivity.this, EndScreen.class);
                startActivity(endIntent);
            }
            root.setBackgroundColor(getResources().getColor(R.color.neutralGrey));

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


            startbtn.setVisibility(View.VISIBLE);
        }
    }
}

