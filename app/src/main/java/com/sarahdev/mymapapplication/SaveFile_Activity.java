package com.sarahdev.mymapapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

public class SaveFile_Activity extends AppCompatActivity {
    public final static String EXTRA_DATE = "com.sarahdev.mymapapplication.date";
    public final static String EXTRA_DISTANCE = "com.sarahdev.mymapapplication.distance";
    public final static String EXTRA_SPEED = "com.sarahdev.mymapapplication.speed";
    public final static String EXTRA_INFOS = "com.sarahdev.mymapapplication.infos";


    private TextView tvDistance, tvDate, tvSpeed;
    private EditText tvInfos;
    private ArrayAdapter<String> adapter;
    private String date ="";
    private String distance = "";
    private String speed = "";
    private String infos="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_file_layout);

        Intent intent = getIntent();
        date = intent.getStringExtra(EXTRA_DATE);
        distance = intent.getStringExtra(EXTRA_DISTANCE);
        speed = intent.getStringExtra(EXTRA_SPEED);

        tvDistance = findViewById(R.id.tvDistance);
        tvDate = findViewById(R.id.tvDate);
        tvSpeed = findViewById(R.id.tvSpeed);
        tvInfos = findViewById(R.id.etInfos);
        tvDate.setText("date : " + date);
        tvSpeed.setText("vitesse moyenne : " + speed);
        tvDistance.setText("distance parcourure " + distance);
        tvInfos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                infos = s.toString();
            }
        });
    }


    public void envoyer(View view) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_INFOS, infos);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

}