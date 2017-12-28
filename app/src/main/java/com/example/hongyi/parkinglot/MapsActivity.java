package com.example.hongyi.parkinglot;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,OnMarkerClickListener {

    private GoogleMap mMap;
    PlaceAutocompleteFragment placeAutoComplete;
    private MainHandler mMainHandler = new MainHandler();


    ParkDBHelper parkdbhpr;
    Park2DBHelper park2dbhpr;
    LinearLayout startTOGO;
    TextView nameshow,carshow,motoshow,bicshow,lat1,lng1,lat2,lng2,textview2;
    Button startToGo;
    String OldParkname;
    String returnstring;
    ProgressDialog dialog;
    int status = 0,startgo = 0;
    ImageButton btn4;
    String totoals = "";
    String totoalmin = "";
    String[] steptogo;
    String[] stependlatlng,stepstartlatlng;
    int stepcnt = 0;



    private static final int overview = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.gc();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        dialog = new ProgressDialog(MapsActivity.this);
        dialog.setMessage("Please wait....");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        returnstring = getIntent().getStringExtra("returnstring");
        placeAutoComplete = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete);
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                Log.d("Maps", "Place selected: " + place.getName());

                mMap.clear();
                LatLng sydney = place.getLatLng();
                lat2.setText(String.valueOf(place.getLatLng().latitude));
                lng2.setText(String.valueOf(place.getLatLng().longitude));
                MarkerOptions mkopt = new MarkerOptions();
                mkopt.position(sydney);
                mkopt.title(place.getName().toString());

                mMap.addMarker(mkopt);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));

            }

            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        });

        parkdbhpr = new ParkDBHelper(MapsActivity.this);
        park2dbhpr = new Park2DBHelper(MapsActivity.this);

        ImageButton btn = (ImageButton)findViewById(R.id.imageButton);
        btn.setImageAlpha(200);
        ImageButton btn2 = (ImageButton)findViewById(R.id.imageButton2);
        btn2.setImageAlpha(200);
        ImageButton btn3 = (ImageButton)findViewById(R.id.imageButton3);
        btn3.setImageAlpha(200);
        btn4 = (ImageButton)findViewById(R.id.imageButton4);
        btn4.setImageAlpha(200);

        startTOGO = (LinearLayout) findViewById(R.id.StartGoToPark);
        nameshow = (TextView) findViewById(R.id.ParkNameShow);
        carshow = (TextView) findViewById(R.id.ParkCarShow);
        motoshow = (TextView) findViewById(R.id.ParkMotoShow);
        bicshow = (TextView) findViewById(R.id.ParkbicShow);
        startToGo = (Button) findViewById(R.id.GoStart);

        textview2 = (TextView) findViewById(R.id.textView2);

        lat1 = (TextView) findViewById(R.id.lat1);
        lng1 = (TextView) findViewById(R.id.lng1);
        lat2 = (TextView) findViewById(R.id.lat2);
        lng2 = (TextView) findViewById(R.id.lng2);

        startToGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                starttogo();
            }
        });



        final ImageButton btn5 = (ImageButton)findViewById(R.id.imageButton5);
        final ImageButton btn6 = (ImageButton)findViewById(R.id.imageButton6);
        final ImageButton btn7 = (ImageButton)findViewById(R.id.imageButton7);
        final ImageButton btn8 = (ImageButton)findViewById(R.id.imageButton8);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn5.getVisibility() == View.VISIBLE) {
                    btn5.setVisibility(View.INVISIBLE);
                    btn6.setVisibility(View.INVISIBLE);
                    btn7.setVisibility(View.INVISIBLE);
                    btn8.setVisibility(View.INVISIBLE);
                } else {
                    btn5.setVisibility(View.VISIBLE);
                    btn6.setVisibility(View.VISIBLE);
                    btn7.setVisibility(View.VISIBLE);
                    btn8.setVisibility(View.VISIBLE);
                }
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(startgo == 1) {
                    showDialog(MapsActivity.this);
                }
            }
        });


        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MapsActivity.this, "顯示所有停車場", Toast.LENGTH_SHORT).show();
                status = 0;
                btn5.setVisibility(View.INVISIBLE);
                btn6.setVisibility(View.INVISIBLE);
                btn7.setVisibility(View.INVISIBLE);
                btn8.setVisibility(View.INVISIBLE);
                startgo = 0;
                try {
                    SetMarker(0);
                } catch (JSONException e) {

                }
            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MapsActivity.this, "篩選：汽車停車場", Toast.LENGTH_SHORT).show();
                status = 1;
                btn5.setVisibility(View.INVISIBLE);
                btn6.setVisibility(View.INVISIBLE);
                btn7.setVisibility(View.INVISIBLE);
                btn8.setVisibility(View.INVISIBLE);
                startgo = 0;
                try {
                    SetMarker(1);
                } catch (JSONException e) {

                }
            }
        });

        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MapsActivity.this, "篩選：機車停車場", Toast.LENGTH_SHORT).show();
                status = 2;
                btn5.setVisibility(View.INVISIBLE);
                btn6.setVisibility(View.INVISIBLE);
                btn7.setVisibility(View.INVISIBLE);
                btn8.setVisibility(View.INVISIBLE);
                startgo = 0;
                try {
                    SetMarker(2);
                } catch (JSONException e) {

                }
            }
        });

        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MapsActivity.this, "篩選：腳踏車停車場", Toast.LENGTH_SHORT).show();
                status = 3;
                btn5.setVisibility(View.INVISIBLE);
                btn6.setVisibility(View.INVISIBLE);
                btn7.setVisibility(View.INVISIBLE);
                btn8.setVisibility(View.INVISIBLE);
                startgo = 0;
                try {
                    SetMarker(3);
                } catch (JSONException e) {

                }
            }
        });


        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                Intent intent = new Intent(MapsActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        mMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                // TODO Auto-generated method stub
                Log.d("arg0", arg0.latitude + "-" + arg0.longitude);
                if(startgo == 0)
                    startTOGO.setVisibility(View.INVISIBLE);
            }
        });

        if(ActivityCompat.checkSelfPermission(MapsActivity.this,
                ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MapsActivity.this,
                        ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //詢問權限
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[] {ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION },1
            );

            return;
        }
        mMap.setMyLocationEnabled(true);
        if(returnstring == null || returnstring.equals(""))
            DoInit();
        else {

            int height = 50;
            int width = 50;
            BitmapDrawable bitmapdraw= (BitmapDrawable) getResources().getDrawable(R.drawable.hw7_star);
            Bitmap bb = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(bb, width, height, false);

            String tmp[] = returnstring.split(",");
            lat2.setText(tmp[1]);
            lng2.setText(tmp[2]);
            LatLng sydney = new LatLng(Double.parseDouble(tmp[1]), Double.parseDouble(tmp[2]));
            MarkerOptions mkopt = new MarkerOptions();
            mkopt.position(sydney);
            mkopt.title(tmp[0]);
            mkopt.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            mMap.addMarker(mkopt);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));

            if(dialog.isShowing())
                dialog.dismiss();
        }

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }


    public void DoInit() {
        try{
            mMap.setMyLocationEnabled(true);
        }catch(SecurityException e) {

        }

        Thread thread = new Thread(new mThread());
        thread.start();

        //Thread thread2 = new Thread(new m2Thread());
        //thread2.start();

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        if(startgo == 0) {

            if (marker.getTitle().equals(OldParkname)) {
                if (startTOGO.getVisibility() == View.INVISIBLE) {

                    nameshow.setText(marker.getTitle());
                    startTOGO.setVisibility(View.VISIBLE);

                } else {
                    startTOGO.setVisibility(View.INVISIBLE);
                }
                Log.d("test", "123");
            } else {
                OldParkname = marker.getTitle();

                nameshow.setText(marker.getTitle());

                Cursor c = parkdbhpr.getParkbyName(OldParkname);
                c.moveToFirst();
                Log.d("marker_getCount", String.valueOf(c.getCount()));
                if (c.getCount() > 0) {
                    String s = c.getString(1);
                    try {
                        JSONObject jobt = new JSONObject(s);

                        double dx = Double.parseDouble(jobt.getString("tw97x"));
                        double dy = Double.parseDouble(jobt.getString("tw97y"));
                        double newdxdy[] = TWD97_convert_to_WGS84(new TMParameter(), dx, dy);

                        String tcar = jobt.getString("totalcar");
                        String tmoto = jobt.getString("totalmotor");
                        String tbic = jobt.getString("totalbike");

                        carshow.setText("總汽車車位數：" + tcar);
                        motoshow.setText("總機車車位數：" + tmoto);
                        bicshow.setText("總腳踏車車位數：" + tbic);

                        LocationManager locationManager = (LocationManager)
                                getSystemService(Context.LOCATION_SERVICE);
                        Criteria criteria = new Criteria();

                        Location location = locationManager.getLastKnownLocation(locationManager
                                .getBestProvider(criteria, false));
                        String latitude = String.valueOf(location.getLatitude());
                        String longitude = String.valueOf(location.getLongitude());

                        String lat = String.valueOf(newdxdy[0]);
                        String lng = String.valueOf(newdxdy[1]);

                        lat1.setText(latitude);
                        lng1.setText(longitude);
                        lat2.setText(lat);
                        lng2.setText(lng);

                        Log.d("markercliclk",lat + "," + lng);

                    } catch (JSONException | SecurityException e) {

                    }
                }


                startTOGO.setVisibility(View.VISIBLE);

            }
        }


        marker.showInfoWindow();


        Log.d("test",marker.getTitle());
        return true;
    }

    public void starttogo() {
        try {
            startgo = 1;
            startTOGO.setVisibility(View.INVISIBLE);
            btn4.setVisibility(View.VISIBLE);
            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            Location location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            double lat22 = Double.parseDouble(lat2.getText().toString());
            double lng22 = Double.parseDouble(lng2.getText().toString());

            com.google.maps.model.LatLng a = new  com.google.maps.model.LatLng(latitude,longitude);
            com.google.maps.model.LatLng b= new  com.google.maps.model.LatLng(lat22,lng22);

            mMap.clear();
            DirectionsResult results = getDirectionsDetails(
                    a,
                    b,
                    TravelMode.DRIVING);

            if (results != null) {
                addPolyline(results, mMap);
                positionCamera(results.routes[overview], mMap);
                addMarkersToMap(results, mMap);

                String ss = "";
                String sss = "";
                totoals = "";
                totoalmin = "";
                for(int i=0;i<results.routes[0].legs[0].steps.length;i++) {

                    String s = results.routes[0].legs[0].steps[i].htmlInstructions;

                    s = s.replaceAll("<b>","");
                    s = s.replaceAll("</b>","");
                    s = s.replaceAll("</div>","");
                    while(s.indexOf("<")>=0) {
                        if (s.indexOf("<") >= 0) {
                            s = s.substring(0, s.indexOf("<")) + " " + s.substring(s.indexOf(">") + 1, s.length());
                        }
                    }

                    double aaa = results.routes[0].legs[0].steps[i].endLocation.lat;
                    double bbb = results.routes[0].legs[0].steps[i].endLocation.lng;
                    double ccc = results.routes[0].legs[0].steps[i].startLocation.lat;
                    double ddd = results.routes[0].legs[0].steps[i].startLocation.lng;

                    ss += String.valueOf(aaa) + "," + String.valueOf(bbb) + "~";
                    sss += String.valueOf(ccc) + "," + String.valueOf(ddd) + "~";

                    totoals = totoals + s + ",";
                    totoalmin = totoalmin + results.routes[0].legs[0].steps[i].duration + ",";

                    Log.d("directionresult : " + results.routes[0].legs[0].steps[i].duration ,s); //華中橋 and 水源快速道路
                }

                totoals = totoals.substring(0,totoals.length()-1);
                totoalmin = totoalmin.substring(0,totoalmin.length()-1);

                stepstartlatlng = sss.split("~");
                stependlatlng = ss.split("~");
                steptogo = totoals.split(",");
                stepcnt = 0;

                Log.d("directionresult : ",""); //華中橋 and 水源快速道路
                textview2.setVisibility(View.VISIBLE);
                textview2.setText(steptogo[stepcnt]);

                Thread trd = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("thread start run","run");
                        while(stepcnt != (steptogo.length-1)) {
                            try {
                                LocationManager locationManager = (LocationManager)
                                        getSystemService(Context.LOCATION_SERVICE);
                                Criteria criteria = new Criteria();

                                Location location = locationManager.getLastKnownLocation(locationManager
                                        .getBestProvider(criteria, false));
                                //now location
                                float lat1 = Float.parseFloat(String.valueOf(location.getLatitude()));
                                float lng1 = Float.parseFloat(String.valueOf(location.getLongitude()));

                                String[] latlngend = stependlatlng[stepcnt].split(",");
                                float lat2 = Float.parseFloat(latlngend[0]);
                                float lng2 = Float.parseFloat(latlngend[1]);

                                float dis = distance(lat1, lng1, lat2, lng2);

                                if (dis <= 20) {
                                    Log.d("dis<100", "");
                                    stepcnt++;
                                    Message message = new Message();
                                    message.what = 3;

                                    mMainHandler.sendMessage(message);
                                } else {
                                    Log.d("dis>100", "");
                                }
                            } catch(Exception e) {

                            }


                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message message = new Message();
                        message.what = 4;
                        mMainHandler.sendMessage(message);

                    }
                });
                trd.start();

            } else {
                startgo = 0;
                textview2.setVisibility(View.INVISIBLE);
            }


        }catch(SecurityException e) {
            startgo = 0;
            textview2.setVisibility(View.INVISIBLE);
        }



    }

    private DirectionsResult getDirectionsDetails( com.google.maps.model.LatLng origin, com.google.maps.model.LatLng destination, TravelMode mode) {
        DateTime now = new DateTime();
        try {
            return  DirectionsApi.newRequest(getGeoContext())
                    .mode(mode)
                    .origin(origin)
                    .destination(destination)
                    .departureTime(now)
                    .language("zh-TW")
                    .await();
        } catch (com.google.maps.errors.ApiException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext
                .setQueryRateLimit(3)
                .setApiKey(getString(R.string.directionsApiKey))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }

    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].startLocation.lat,results.routes[overview].legs[overview].startLocation.lng)).title(results.routes[overview].legs[overview].startAddress));
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].endLocation.lat,results.routes[overview].legs[overview].endLocation.lng)).title(results.routes[overview].legs[overview].startAddress).snippet(getEndLocationTitle(results)));
    }

    private void positionCamera(DirectionsRoute route, GoogleMap mMap) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(route.legs[overview].startLocation.lat, route.legs[overview].startLocation.lng), 12));
    }

    private String getEndLocationTitle(DirectionsResult results){
        return  "Time :"+ results.routes[overview].legs[overview].duration.humanReadable + " Distance :" + results.routes[overview].legs[overview].distance.humanReadable;
    }

    //處理使用者選擇後的結果
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DoInit();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void SetMarker(int s) throws JSONException {
        mMap.clear();
        int height = 50;
        int width = 50;
        BitmapDrawable bitmapdraw= (BitmapDrawable) getResources().getDrawable(R.drawable.parking);

        if(s==0) {
            bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.parking);
        } else if(s==1) {
            bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.car);
        } else if (s==2) {
            bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.vespa);
        } else if(s==3) {
            bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.bicycle);
        }

        Bitmap bb = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(bb, width, height, false);

        //SONArray j = new JSONArray();

        Cursor c = parkdbhpr.getAllParks();
        c.moveToFirst();
        if(c.getCount()>0) {
            //Toast.makeText(MapsActivity.this, "NO data can display", Toast.LENGTH_SHORT).show();
            //return;
            try {
                JSONObject j2obj = new JSONObject(c.getString(1));
                Log.d("j0", j2obj.getString("UPDATETIME"));
            } catch (NullPointerException e) {
                Log.d("j0", "no updatetime");
            }
            c.moveToNext();

            for (int i = 1; i < c.getCount(); i++) {
                Log.d("j" + String.valueOf(i), "");
                String cstring = c.getString(1);
                JSONObject jobj = new JSONObject(cstring);

                String markername = jobj.getString("name");
                String summary = jobj.getString("summary");
                String totoalbicycle = jobj.getString("totalbike");
                double dx = Double.parseDouble(jobj.getString("tw97x"));
                double dy = Double.parseDouble(jobj.getString("tw97y"));
                double newdxdy[] = TWD97_convert_to_WGS84(new TMParameter(), dx, dy);
                String markerlat = String.valueOf(newdxdy[0]);
                String markerlng = String.valueOf(newdxdy[1]);
                //Log.d("totalbicycle",totoalbicycle);
                if ((s == 0) || (s == 1 && summary.indexOf("小型車") >= 0) || (s == 2 && summary.indexOf("機車") >= 0) || ( s== 3 && Integer.parseInt(totoalbicycle) > 0) ) {
                    LatLng sydney = new LatLng(Double.parseDouble(markerlat), Double.parseDouble(markerlng));
                    MarkerOptions mkopt = new MarkerOptions();
                    mkopt.position(sydney);
                    mkopt.title(markername);
                    mkopt.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    mMap.addMarker(mkopt);
                }

                Log.d("j" + String.valueOf(i), markername);

                c.moveToNext();

            }
        } else {
            Toast.makeText(MapsActivity.this, "NO data can display", Toast.LENGTH_SHORT).show();
        }
    }

    public void showDialog(Activity activity){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.setCancelable(false);
        dialog.setContentView(R.layout.routeview);

        final String ID_TITLE = "TITLE", ID_SUBTITLE = "SUBTITLE";

        ArrayList<HashMap<String,String>> myListData = new ArrayList<HashMap<String,String>>();
        String[] messageData = totoals.split(",");
        String[] messageData2 = totoalmin.split(",");
        //ArrayAdapter<String> messageAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,messageData);
        ListView listView = (ListView) dialog.findViewById(R.id.listview2);
        //listView.setAdapter(messageAdapter);

        for( int i=0;i<messageData.length ; ++i) {
            HashMap<String,String> item = new HashMap<String,String>();
            item.put(ID_TITLE,messageData2[i]);
            item.put(ID_SUBTITLE,messageData[i]);
            myListData.add(item);
        }

        listView.setAdapter( new SimpleAdapter(
                this,
                myListData,
                android.R.layout.simple_list_item_2,
                new String[] { ID_TITLE, ID_SUBTITLE },
                new int[] { android.R.id.text1, android.R.id.text2 } )
        );

        dialog.show();

    }

    public class MainHandler extends Handler
    {

        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == 1)
            {
                int height = 50;
                int width = 50;
                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.parking);
                Bitmap bb = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(bb, width, height, false);

                Log.d("msg","1,ok");
                Bundle b = msg.getData();

                String str[] = b.getStringArray("array");

                Cursor cc = parkdbhpr.getPark(  1);//.getString(1);
                cc.moveToFirst();
                int dontinsert = 0;
                int dontinit = 0;

                if(cc.getCount()>0) {
                    String ss = cc.getString(1);
                    if(str[0] == null) {
                        Log.d("msg", "str[0] null");
                        dontinit = 1;
                    } else if(ss == null) {
                        Log.d("msg", "ss null");
                        //dontinit = 1;
                    } else {

                        if (!ss.equals(str[0])) {
                            parkdbhpr.DeleteTable();
                            Log.d("msg", "update data");
                        } else {
                            Log.d("msg", "no update data");
                            dontinsert = 1;
                        }
                    }
                } else {
                    Log.d("msg","cc=0");
                }

                Log.d("insertParkinit",String.valueOf(dontinit));
                Log.d("insertParkinsert",String.valueOf(dontinsert));

                if(dontinit == 0) {
                    try {
                        if (dontinsert == 0) {
                            parkdbhpr.insertPark(0, str[0],"time","time");
                            Log.d("insertPark",str[0]);
                        }

                        for (int i = 1; i < 1530; i++) {
                            JSONObject jobt = new JSONObject(str[i]);
                            String markername = jobt.getString("name");
                            String idname = jobt.getString("id");
                            if (dontinsert == 0) {
                                parkdbhpr.insertPark(0, str[i],markername,idname);
                                //Log.d("insertPark",str[i]);
                            }
                            /*
                            double dx = Double.parseDouble(jobt.getString("tw97x"));
                            double dy = Double.parseDouble(jobt.getString("tw97y"));
                            double newdxdy[] = TWD97_convert_to_WGS84(new TMParameter(), dx, dy);
                            String markerlat = String.valueOf(newdxdy[0]);
                            String markerlng = String.valueOf(newdxdy[1]);

                            LatLng sydney = new LatLng(Double.parseDouble(markerlat), Double.parseDouble(markerlng));

                            MarkerOptions mkopt = new MarkerOptions();

                            mkopt.position(sydney);
                            mkopt.title(markername);
                            mkopt.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

                            mMap.addMarker(mkopt);
                            */

                        }


                        LocationManager locationManager = (LocationManager)
                                getSystemService(Context.LOCATION_SERVICE);
                        Criteria criteria = new Criteria();

                        Location location = locationManager.getLastKnownLocation(locationManager
                                .getBestProvider(criteria, false));
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));



                    } catch (JSONException | SecurityException | NullPointerException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (dialog.isShowing())
                        dialog.dismiss();

                } else {

                    try {
                        LocationManager locationManager = (LocationManager)
                                getSystemService(Context.LOCATION_SERVICE);
                        Criteria criteria = new Criteria();

                        Location location = locationManager.getLastKnownLocation(locationManager
                                .getBestProvider(criteria, false));
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));


                    } catch(SecurityException | NullPointerException e) {

                    }
                    if (dialog.isShowing())
                        dialog.dismiss();
                }

            } else if(msg.what == 2) {

                Bundle b = msg.getData();
                String str[] = b.getStringArray("array");

                Cursor cc = park2dbhpr.getPark(  1);//.getString(1);
                cc.moveToFirst();
                int dontinsert = 0;
                int dontinit = 0;

                if(cc.getCount()>0) {
                    String ss = cc.getString(1);
                    if(str[0] == null) {
                        Log.d("msg", "str[0] null");
                        dontinit = 1;
                    } else if(ss == null) {
                        Log.d("msg", "ss null");
                        //dontinit = 1;
                    } else {

                        if (!ss.equals(str[0])) {
                            parkdbhpr.DeleteTable();
                            Log.d("msg", "update data");
                        } else {
                            Log.d("msg", "no update data");
                            dontinsert = 1;
                        }
                    }
                } else {
                    Log.d("msg","cc=0");
                }

                if(dontinit == 0) {
                    try {
                        if (dontinsert == 0) {
                            park2dbhpr.insertPark(0, str[0], "time");
                            Log.d("insertPark2", str[0]);

                            for (int i = 1; i < 324; i++) {
                                JSONObject jobt = new JSONObject(str[i]);
                                String idname = jobt.getString("id");

                                park2dbhpr.insertPark(0, str[i], idname);
                                Log.d("insertPark2 " + String.valueOf(i),str[i]);

                            }
                        }



                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (dialog.isShowing())
                        dialog.dismiss();
                }

            } else if(msg.what == 3) {
                textview2.setText(steptogo[stepcnt]);
                Log.d("next step",steptogo[stepcnt]);

                String[] ab = stepstartlatlng[stepcnt-1].split(",");
                String[] ba = stependlatlng[stepcnt-1].split(",");

                PolylineOptions polylineOpt = new PolylineOptions();
                polylineOpt.add(new LatLng(Double.parseDouble(ab[0]),Double.parseDouble(ab[1])));
                polylineOpt.add(new LatLng(Double.parseDouble(ba[0]),Double.parseDouble(ba[1])));

                polylineOpt.color(Color.BLUE);
                Polyline polyline = mMap.addPolyline(polylineOpt);
                polyline.setWidth(10);
            } else if(msg.what == 4) {
                Toast.makeText(MapsActivity.this,"導航結束",Toast.LENGTH_SHORT);
                textview2.setVisibility(View.INVISIBLE);
                startTOGO.setVisibility(View.INVISIBLE);
                btn4.setVisibility(View.INVISIBLE);
                steptogo = null;
                stepstartlatlng = null;
                stependlatlng = null;
                mMap.clear();
                startgo = 0;

            }
        }
    }

    public class mThread extends Thread
    {
        @Override
        public void run()
        {


            String str[] = new String[1531];
            try {

                HttpClient httpclient = new DefaultHttpClient();

                HttpResponse httpResponse = httpclient.execute(new HttpGet("https://tcgbusfs.blob.core.windows.net/blobtcmsv/TCMSV_alldesc.gz"));

                InputStream inputStream = httpResponse.getEntity().getContent();

                if(inputStream != null) {
                    str = readToString(inputStream);
                }
            } catch (Exception e) {
                Log.d("GET error:", e.toString());
            }

            Message message = new Message();
            message.what = 1;

            Bundle bundle = new Bundle();
            bundle.putStringArray("array",str);
            message.setData(bundle);

            mMainHandler.sendMessage(message);
            //System.gc();
        }
    }

    public class m2Thread extends Thread
    {
        @Override
        public void run()
        {


            String str[] = new String[1531];
            try {

                HttpClient httpclient = new DefaultHttpClient();

                HttpResponse httpResponse = httpclient.execute(new HttpGet("https://tcgbusfs.blob.core.windows.net/blobtcmsv/TCMSV_allavailable.gz"));

                InputStream inputStream = httpResponse.getEntity().getContent();

                if(inputStream != null) {
                    str = readToString(inputStream);
                }
            } catch (Exception e) {
                Log.d("GET error:", e.toString());
            }

            Message message = new Message();
            message.what = 2;

            Bundle bundle = new Bundle();
            bundle.putStringArray("array",str);
            message.setData(bundle);

            mMainHandler.sendMessage(message);
            //System.gc();
        }
    }

    private String[] readToString(InputStream inputstream) throws IOException,JSONException {

        String str[] = new String[1530];

        GZIPInputStream stream = new GZIPInputStream(inputstream);
        int cnt = 0;
        Log.d("reutrn",String.valueOf(cnt));
        String tt="";
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, Charset.forName("BIG5")));
        String s;
        while((s = br.readLine()) != null) {

            if(s.indexOf("\"id\"")>=0) {
                cnt++;
                if (cnt > 1) {
                    String tmp = "{" + tt.substring(0,tt.length()-2);
                    str[cnt-1] = tmp;
                    tt = "";
                }
            }else if(s.indexOf("UPDATETIME")>=0) {
                String tmp = "{" + s.substring(0,s.length()-1)+"}";
                str[0] = tmp;
            }
            if(cnt>=1) {
                tt = tt + s;
            }
        }

        String tmp = "{" + tt.substring(0,tt.length()-2);
        str[cnt-1] = tmp;

        stream.close();
        Log.d("reutrn",String.valueOf(cnt));
        return str;
    }

    public double[] TWD97_convert_to_WGS84(TMParameter tm, double x, double y) {
        double dx = tm.getDx();
        double dy = tm.getDy();
        double lon0 = tm.getLon0();
        double k0 = tm.getK0();
        double a = tm.getA();
        double b = tm.getB();
        //double e = tm.getE();
        double e= Math.pow((1- Math.pow(b,2)/ Math.pow(a,2)), 0.5);
        x -= dx;
        y -= dy;

        // Calculate the Meridional Arc
        double M = y/k0;

        // Calculate Footprint Latitude
        double mu = M/(a*(1.0 - Math.pow(e, 2)/4.0 - 3* Math.pow(e, 4)/64.0 - 5* Math.pow(e, 6)/256.0));
        double e1 = (1.0 - Math.pow((1.0 - Math.pow(e, 2)), 0.5)) / (1.0 + Math.pow((1.0 - Math.pow(e, 2)), 0.5));

        double J1 = (3*e1/2 - 27* Math.pow(e1, 3)/32.0);
        double J2 = (21* Math.pow(e1, 2)/16 - 55* Math.pow(e1, 4)/32.0);
        double J3 = (151* Math.pow(e1, 3)/96.0);
        double J4 = (1097* Math.pow(e1, 4)/512.0);

        double fp = mu + J1* Math.sin(2*mu) + J2* Math.sin(4*mu) + J3* Math.sin(6*mu) + J4* Math.sin(8*mu);

        // Calculate Latitude and Longitude

        double e2 = Math.pow((e*a/b), 2);
        double C1 = Math.pow(e2* Math.cos(fp), 2);
        double T1 = Math.pow(Math.tan(fp), 2);
        double R1 = a*(1- Math.pow(e, 2))/ Math.pow((1- Math.pow(e, 2)* Math.pow(Math.sin(fp), 2)), (3.0/2.0));
        double N1 = a/ Math.pow((1- Math.pow(e, 2)* Math.pow(Math.sin(fp), 2)), 0.5);

        double D = x/(N1*k0);

        // lat
        double Q1 = N1* Math.tan(fp)/R1;
        double Q2 = (Math.pow(D, 2)/2.0);
        double Q3 = (5 + 3*T1 + 10*C1 - 4* Math.pow(C1, 2) - 9*e2)* Math.pow(D, 4)/24.0;
        double Q4 = (61 + 90*T1 + 298*C1 + 45* Math.pow(T1, 2) - 3* Math.pow(C1, 2) - 252*e2)* Math.pow(D, 6)/720.0;
        double lat = fp - Q1*(Q2 - Q3 + Q4);

        // long
        double Q5 = D;
        double Q6 = (1 + 2*T1 + C1)* Math.pow(D, 3)/6;
        double Q7 = (5 - 2*C1 + 28*T1 - 3* Math.pow(C1, 2) + 8*e2 + 24* Math.pow(T1, 2))* Math.pow(D, 5)/120.0;
        double lon = lon0 + (Q5 - Q6 + Q7)/ Math.cos(fp);

        return new double[] {Math.toDegrees(lat), Math.toDegrees(lon)};
    }

    public class TMParameter{

        public double getDx(){
            return 250000;
        }
        public double getDy(){
            return 0;
        }
        public double getLon0(){
            return 121 * Math.PI / 180;
        }
        public double getK0(){
            return 0.9999;
        }
        public double getA(){
            return 6378137.0;
        }
        public double getB(){
            return 6356752.314245;
        }
    }

    public float distance (float lat_a, float lng_a, float lat_b, float lng_b ) {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
    }

}
