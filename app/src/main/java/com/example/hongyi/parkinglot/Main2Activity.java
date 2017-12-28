package com.example.hongyi.parkinglot;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    private LikeDBHelper dbHelper;
    EditText nameEditText, addressEditText;
    TextView textViewLAT, textViewLNG;
    Button deleteButton, saveButton;
    PlaceAutocompleteFragment placeAutoComplete;

    int LikeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR); getSupportActionBar().hide();

        LikeID = getIntent().getIntExtra("info", 0);
        setContentView(R.layout.activity_main2);

        nameEditText = (EditText) findViewById(R.id.editTextName);
        addressEditText = (EditText) findViewById(R.id.editTextAddress);
        textViewLAT = (TextView) findViewById(R.id.textViewLAT);
        textViewLNG= (TextView) findViewById(R.id.textViewLNG);

        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);

        placeAutoComplete = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete);
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                Log.d("Maps", "Place selected: " + place.getName());

                nameEditText.setText(place.getName().toString());
                addressEditText.setText(place.getAddress().toString());

                LatLng d = place.getLatLng();

                textViewLAT.setText(String.valueOf(d.latitude));
                textViewLNG.setText(String.valueOf(d.longitude));

                Log.d("Maps", "latlng: " + d.toString());
            }

            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        });


        dbHelper = new LikeDBHelper(this);

        if (LikeID > 0) {

            Cursor rs = dbHelper.getLike(LikeID);
            rs.moveToFirst();

            String LikeName = rs.getString(rs.getColumnIndex(LikeDBHelper.COLUMN_NAME));
            String LikeAddress = rs.getString(rs.getColumnIndex(LikeDBHelper.COLUMN_ADDRESS));
            String LikeLAT = rs.getString(rs.getColumnIndex(LikeDBHelper.COLUMN_LAT));
            String LikeLNG = rs.getString(rs.getColumnIndex(LikeDBHelper.COLUMN_LAT));

            if (!rs.isClosed()) {
                rs.close();
            }

            nameEditText.setText(LikeName);
            nameEditText.setFocusable(false);
            nameEditText.setClickable(false);

            addressEditText.setText(LikeAddress);
            addressEditText.setFocusable(false);
            addressEditText.setClickable(false);

            textViewLAT.setText(LikeLAT);
            textViewLNG.setText(LikeLNG);

        } else {
            nameEditText.setFocusable(false);
            nameEditText.setClickable(false);
            addressEditText.setFocusable(false);
            addressEditText.setClickable(false);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveButton:
                if(LikeID > 0) {
                    dbHelper.updateLike(LikeID, nameEditText.getText().toString(), addressEditText.getText().toString(),textViewLAT.getText().toString(), textViewLNG.getText().toString());
                    Toast.makeText(getApplicationContext(), "update", Toast.LENGTH_SHORT).show();
                } else {
                    dbHelper.insertLike(LikeID, nameEditText.getText().toString(), addressEditText.getText().toString(),textViewLAT.getText().toString(), textViewLNG.getText().toString());
                    Toast.makeText(getApplicationContext(), "insert", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return;
            case R.id.deleteButton:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("確定要刪除嗎？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dbHelper.deleteLike(LikeID);
                                Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle("刪除停車場?");
                d.show();
                return;
        }
    }
}
