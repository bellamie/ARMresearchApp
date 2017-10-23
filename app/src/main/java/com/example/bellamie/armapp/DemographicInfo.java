package com.example.bellamie.armapp;

import android.content.Intent;
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
    private TextView mAge;
    private Button mNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demographic_info);

        mGender = (RadioGroup) findViewById(R.id.gender);
        mMale = (RadioButton) mGender.findViewById(R.id.male);
        mFemale = (RadioButton) mGender.findViewById(R.id.female);

        mAge = (EditText) findViewById(R.id.age);
        mNext = (Button) findViewById(R.id.next);

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMale.isChecked() || mFemale.isChecked()){
                    Log.i(TAG, "Gender is chosen");
                    if(mAge.getText() != null){
                        Log.i(TAG, "Something is filled in the age field");
                        Intent mainActivityIntent = new Intent(DemographicInfo.this, MainActivity.class);
                        startActivity(mainActivityIntent);
                    }
                }
                Log.i(TAG, "Something went wrong with the radiobuttons");
            }
        });
    }

    public String getGender(){
        if (mMale.isChecked()){
            return "Male";
        } else if (mFemale.isChecked()){
            return "Female";
        }else{
            return "something went wrong with radiobuttons";
        }
    }

    public String getAge(){
        if (mAge != null){
            return (String) mAge.getText();
        }
        else{
            return "Something went wrong with the age string";
        }
    }
}
