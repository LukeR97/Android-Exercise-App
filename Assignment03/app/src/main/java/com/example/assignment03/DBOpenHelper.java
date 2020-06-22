package com.example.assignment03;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
    //constructor for class. Map to super class!
    public DBOpenHelper(Context context, String name, CursorFactory factory, int version){
        super(context,name,factory,version);
    }

    //overidden method for db creation
    public void onCreate(SQLiteDatabase db){
        //create db
        db.execSQL(create_table);
    }

    //overidden method to updgrade db
    public void onUpgrade(SQLiteDatabase db, int version_old, int version_new){
        //drop the tables and recreate them
        db.execSQL(drop_table);
        db.execSQL(create_table);
    }

    //constraints
    private static final String create_table = "create table users(" +
            "ID integer primary key autoincrement, " +
            "NAME string, " +
            "STEPS integer DEFAULT 0," +
            "ALT string DEFAULT 000" +
            ")";
    private static final String drop_table = "drop table users";
}
