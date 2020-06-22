package com.example.assignment03;
//------------------------------------------------------------------------------
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
//------------------------------------------------------------------------------

public class MainActivity extends AppCompatActivity {

    //boolean to check if the FAB is currently expanded or not
    private boolean isFabOpen;
    private FloatingActionButton fab,subFab2,subFab3,subFab4;
    private CharSequence[] values = {"List Users", "Add User", "Delete User", "Select User"};
    //String to set our default title to
    private String currentUser = "No Current User";
    private DBOpenHelper odb;
    private SQLiteDatabase sdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(currentUser);
        setContentView(R.layout.activity_main);

        this.deleteDatabase("users.db");

        //subFab's are the buttons which appear after the main FAB has been activated
        fab = (FloatingActionButton)findViewById(R.id.fab);
        subFab2 = (FloatingActionButton)findViewById(R.id.subFab2);
        subFab3 = (FloatingActionButton)findViewById(R.id.subFab3);
        subFab4 = (FloatingActionButton)findViewById(R.id.subFab4);
        odb = new DBOpenHelper(this,"users.db",null,1);
        sdb = odb.getWritableDatabase();

        //Main fab listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFabOpen)
                    showFabMenu();
                else
                    hideFabMenu();
            }
        });
        //User Management SubFab
        subFab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageUsers();
            }
        });
        //Step Counter FAB
        subFab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser.equals("No Current User"))
                    Toast.makeText(getApplicationContext(),"Please Select a user first",Toast.LENGTH_LONG).show();
                else
                    stepCounter();
            }
        });
        //Report FAB
        subFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser.equals("No Current User"))
                    Toast.makeText(getApplicationContext(), "Please Select a user first", Toast.LENGTH_LONG).show();
                else
                    graphReport();
            }
        });
    }

    //function to 'open up' the FAB menu when main FAB is touched
    private void showFabMenu(){
        isFabOpen = true;
        //this animate makes the main FAB spin
        fab.animate().rotation(220);
        subFab2.animate().translationY(-getResources().getDimension(R.dimen.standard_70));
        subFab3.animate().translationY(-getResources().getDimension(R.dimen.standard_140));
        subFab4.animate().translationY(-getResources().getDimension(R.dimen.standard_210));
    }
    //as with function above, this closes down the FAB menu
    private void hideFabMenu(){
        isFabOpen = false;
        fab.animate().rotation(-180);
        subFab2.animate().translationY(0);
        subFab3.animate().translationY(0);
        subFab4.animate().translationY(0);
    }

    //function to begin the step counter activity
    private void stepCounter(){
        Intent intent = new Intent(getApplicationContext(), stepCounterActivity.class);
        intent.putExtra("CURRENT_USER",currentUser);
        startActivity(intent);
    }
    //function to begin the report activity
    private void graphReport(){
        Intent intent = new Intent(getApplicationContext(), reportActivity.class);
        intent.putExtra("CURRENT_USER", currentUser);
        startActivity(intent);
    }
    //function to begin the user management activity
    //this function uses a switch, hence the charSequence initialised above. Each option has their
    //own events and will prompt the user with difference tasks
    private void manageUsers() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Manage Users");
        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Toast.makeText(getApplicationContext(), "List Users", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder lstBuilder = new AlertDialog.Builder(MainActivity.this);
                        //print out DB contents
                        String table_name = "users";
                        // the columns that we wish to retrieve from the tables
                        String[] columns = {"ID", "NAME"};
                        // where clause of the query.
                        String where = null;
                        // arguments to provide to the where clause
                        String where_args[] = null;
                        // group by clause of the query.
                        String group_by = null;
                        // having clause of the query.
                        String having = null;
                        // order by clause of the query.
                        String order_by = null;
                        // run the query. this will give us a cursor into the database
                        // that will enable us to change the table row that we are working with
                        Cursor c = sdb.query(table_name, columns, where, where_args, group_by,
                                having, order_by);
                        // print out some data from the cursor to the screen
                        String total_text = "All Users: " + c.getCount() + "\n";
                        c.moveToFirst();
                        for (int i = 0; i < c.getCount(); i++) {
                            total_text += c.getString(1) + "\n";
                            c.moveToNext();
                        }
                        c.close();
                        lstBuilder.setMessage(total_text);
                        lstBuilder.show();
                        break;
                    case 1:
                        //add a user to the database
                        Toast.makeText(getApplicationContext(), "Add User", Toast.LENGTH_LONG).show();
                        AlertDialog.Builder addBuilder = new AlertDialog.Builder(MainActivity.this);
                        addBuilder.setTitle("Enter User Name");
                        final EditText input = new EditText(MainActivity.this);
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        addBuilder.setView(input);
                        addBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(input.getText().toString() == ""){dialog.cancel();}
                                ContentValues cv = new ContentValues();
                                cv.put("NAME", input.getText().toString());
                                sdb.insert("users",null,cv);
                            }
                        });
                        addBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        addBuilder.show();
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "Delete User", Toast.LENGTH_LONG).show();
                        AlertDialog.Builder delBuilder = new AlertDialog.Builder(MainActivity.this);
                        delBuilder.setTitle("User To Delete");
                        final EditText delInput = new EditText(MainActivity.this);
                        delInput.setInputType(InputType.TYPE_CLASS_TEXT);
                        delBuilder.setView(delInput);
                        delBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(currentUser == delInput.getText().toString())
                                    setTitle("No Current User");
                                sdb.delete("users","NAME" + "= '" + delInput.getText().toString() + "';",
                                        null);
                            }
                        });
                        delBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        delBuilder.show();
                        break;
                    case 3:
                        Toast.makeText(getApplicationContext(), "Select User", Toast.LENGTH_LONG).show();
                        final AlertDialog.Builder selBuilder = new AlertDialog.Builder(MainActivity.this);
                        selBuilder.setTitle("Select a User");
                        final EditText selInput = new EditText(MainActivity.this);
                        selInput.setInputType(InputType.TYPE_CLASS_TEXT);
                        selBuilder.setView(selInput);
                        selBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String nameToCheck = selInput.getText().toString();
                                String[] cols = {"ID","NAME"};
                                Cursor c = sdb.query("users", cols, null, null, null, null, null);
                                c.moveToFirst();
                                while(c.isFirst() || c.isLast() || c.moveToNext()){
                                    if(nameToCheck.equals(c.getString(1))){
                                        currentUser = nameToCheck;
                                        setTitle(currentUser);
                                        Toast.makeText(getApplicationContext(),"User Selected!",Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                    if(c.isLast()){
                                        Toast.makeText(getApplicationContext(),"Item Searched does not exist",Toast.LENGTH_LONG).show();
                                    }
                                    c.moveToNext();
                                }
                                c.close();
                            }
                        });
                        dialog.cancel();
                        selBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        selBuilder.show();
                        break;
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
