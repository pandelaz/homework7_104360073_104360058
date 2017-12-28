package com.example.hongyi.parkinglot;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    int status = 0;
    private ListView listView;
    LikeDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR); getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.addNew);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                intent.putExtra("info", 0);
                startActivity(intent);
            }
        });

        Button deletebtn = (Button) findViewById(R.id.deleteOld);
        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(status == 0) {
                    status = 1;
                    for(int i=0;i<listView.getCount();i++) {
                        View tview = getViewByPosition(i, listView);//=  new View(listView.getContext());
                        ImageView imgv = tview.findViewById(R.id.imageView3);//(ImageView) view.findViewById(R.id.imageView);
                        imgv.setVisibility(View.VISIBLE);
                    }

                } else {
                    status = 0;
                    for(int i=0;i<listView.getCount();i++) {
                        View tview = getViewByPosition(i, listView);//=  new View(listView.getContext());
                        ImageView imgv = tview.findViewById(R.id.imageView3);//(ImageView) view.findViewById(R.id.imageView);
                        imgv.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        dbHelper = new LikeDBHelper(this);

        final Cursor cursor = dbHelper.getAllLikes();

        String [] columns = new String[] {
                LikeDBHelper.COLUMN_ID,
                LikeDBHelper.COLUMN_NAME,
                LikeDBHelper.COLUMN_ADDRESS,
                LikeDBHelper.COLUMN_LAT,
                LikeDBHelper.COLUMN_LNG
        };
        int [] widgets = new int[] {
                R.id.ParkID,
                R.id.ParkName,
                R.id.ParkAddress,
                R.id.textViewLAT,
                R.id.textViewLNG
        };

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.like_item,
                cursor, columns, widgets, 0);

        listView = (ListView)findViewById(R.id.listview);
        listView.setAdapter(cursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                Cursor itemCursor = (Cursor) MainActivity.this.listView.getItemAtPosition(position);

                //Toast.makeText(MainActivity.this,"你選的是" + list_item[i],Toast.LENGTH_SHORT).show();

                final int LikeID = itemCursor.getInt(itemCursor.getColumnIndex(LikeDBHelper.COLUMN_ID));
                final String pname = itemCursor.getString(itemCursor.getColumnIndex(LikeDBHelper.COLUMN_NAME));
                final String lat = itemCursor.getString(itemCursor.getColumnIndex(LikeDBHelper.COLUMN_LAT));
                final String lng = itemCursor.getString(itemCursor.getColumnIndex(LikeDBHelper.COLUMN_LNG));

                if(status==0) {
                    Toast.makeText(MainActivity.this, pname + "," + lat + "," + lng, Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("確定要在地圖上顯示此停車場嗎？")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                                    String returnstring = pname + "," + lat + "," + lng;
                                    intent.putExtra("returnstring", returnstring);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                                    //String returnstring = pname + "," + lat + "," + lng;
                                    //intent.putExtra("returnstring", returnstring);
                                    startActivity(intent);
                                }
                            });
                    AlertDialog d = builder.create();
                    d.setTitle("可怕的作業");
                    d.show();

                }else {
                    //delete or edit function

                    Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                    intent.putExtra("info", LikeID);
                    startActivity(intent);
                    status = 0;

                    for(int i=0;i<listView.getCount();i++) {
                        View tview = getViewByPosition(i, MainActivity.this.listView);//=  new View(listView.getContext());

                        ImageView imgv = tview.findViewById(R.id.imageView3);//(ImageView) view.findViewById(R.id.imageView);
                        imgv.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
