package com.example.assignment03;

//---------------------------------------------------
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
//---------------------------------------------------

public class reportActivity extends AppCompatActivity {

    private DBOpenHelper odb;
    private SQLiteDatabase sdb;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_activity);

        odb = new DBOpenHelper(this,"users.db", null, 1);
        sdb = odb.getReadableDatabase();
        final String currentUser = getIntent().getStringExtra("CURRENT_USER");
        setTitle(currentUser);

        String[] cols = {"ID","NAME","STEPS","ALT"};
        //we need to get the number of steps and the altitudes to graph from the DB
        int steps2graph = 0;
        String alt2graph = "";
        Cursor c = sdb.query("users",cols,null,null,null,null,null);
        c.moveToFirst();
        while(c.isFirst() || c.isLast() || c.moveToNext()){
            if(currentUser.equals(c.getString(1))) {
                //Because SQLi does not accept arraylists, we had to store our altitudes as a single
                //string. We then split the string by regular expression and then send them into the
                //setGraphPoints function
                steps2graph = c.getInt(2);
                alt2graph = c.getString(3);
            }
            c.moveToNext();
        }

        //splitting our single string of altitudes into values separated by a comma
        String altitudes[] = alt2graph.split(",");

        //init our customView and set our graph points STEPS and ALTITUDE
        CustomView graph = (CustomView) findViewById(R.id.graphView);
        int graphArr[] = new int[steps2graph];
        for (int i = 0; i < graphArr.length; i++)
            graphArr[i] = i % 50;
        graph.setGraphPoints(graphArr, altitudes);
    }
}
