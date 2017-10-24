package com.example.bellamie.armapp;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class DemographicInfo extends AppCompatActivity {

    private static final String TAG = "BP";

    private RadioGroup mGender;
    private RadioButton mMale, mFemale;
    private EditText mAge;
    private Button mNext;
    private String gender;
    private String age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demographic_info);

        gender = "";

        mGender = (RadioGroup) findViewById(R.id.gender);
        mMale = mGender.findViewById(R.id.male);
        mFemale = mGender.findViewById(R.id.female);

        mGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (mFemale.isChecked()){
                    gender = "Female";
                } else if(mMale.isChecked()){
                    gender = "Male";
                }
            }
        });

        mAge = (EditText) findViewById(R.id.age);
        mNext = (Button) findViewById(R.id.next);

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMale.isChecked() || mFemale.isChecked()){
                    Log.i(TAG, "Gender is chosen");
                    age = mAge.getText().toString();
                    Log.i(TAG, "Age of user: " + age);
                    if(age != null){
                        Log.i(TAG, "Something is filled in the age field: " + age);
                        Intent mainActivityIntent = new Intent(DemographicInfo.this, MainActivity.class);
                        mainActivityIntent.putExtra("age", age);
                        mainActivityIntent.putExtra("gender", gender);
                        startActivity(mainActivityIntent);
                        Log.i(TAG, "Next button is clicked");
                    }
                }

            }
        });
    }

    public String getGender(){
        Log.i(TAG, "Getter for gender is accessed");
        return gender;
    }

    public String getAge(){
        Log.i(TAG, "Getter for Age is accessed");
        return age;
    }
}
