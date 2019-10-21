package com.example.pingu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements pingChecker.AsyncResponse {


    public Button btn;
    public Button capture;
    public Button request;
    public EditText lat;
    public EditText lon;
    public EditText userID;
    Button metrics;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn=findViewById(R.id.btn1);
        request=findViewById(R.id.button4);
        lat=findViewById(R.id.editLat);
        lon=findViewById(R.id.editLong);
        userID=findViewById(R.id.userID);
        metrics=findViewById(R.id.button5);
        metrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(MainActivity.this,MetricsActivity.class);
                startActivity(myIntent);
            }
        });
        final MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.spinner);
        final ArrayList<String> myList=new ArrayList<>();
        myList.add("JIO");
        myList.add("AIRTEL");
        myList.add("VODAFONE");
        myList.add("BSNL");
        myList.add("IDEA");
        spinner.setItems(myList);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new pingChecker(MainActivity.this).execute();



            }
        });
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "SpeedTest Request Sent", Toast.LENGTH_SHORT).show();
                Map<String, Object> connection = new HashMap<>();
                connection.put("carrier",myList.get(spinner.getSelectedIndex()));
                connection.put("speed",0 );
                connection.put("flag",0);
                connection.put("lat",Double.parseDouble(lat.getText().toString()));
                connection.put("lon",Double.parseDouble(lon.getText().toString()));
                db.collection("connections").document(userID.getText().toString())
                        .set(connection)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Data Loaded", "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Failed", "Error writing document", e);
                            }
                        });
            }
        });
        capture=findViewById(R.id.button2);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapsIntent=new Intent(MainActivity.this,MapsActivity.class);
                startActivity(mapsIntent);
            }
        });
    }


    @Override
    public void processFinish(String output) {
        Toast.makeText(this, output, Toast.LENGTH_SHORT).show();
    }
}
