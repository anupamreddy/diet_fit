package kah.fict;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.Time;
import java.util.ArrayList;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class addwork extends AppCompatActivity{
    private DataBaseHelper db;

    private EditText editWeight;
    private EditText editTime;
    private  CheckBox sleeping;
    private  CheckBox jogging;
    private  CheckBox fastJogging;
    private  CheckBox walking2;
    private  CheckBox walking3;
    private  CheckBox walking35;
    private  CheckBox bicycleS;
    private  CheckBox bicycleM;
    private  CheckBox bicycleF;

    private Button saveButton;

    private int weight;
    private int time;
    private String work;
    private int calories;
    private double met;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_work);
        ActionBar actionBar = getSupportActionBar(); //Set back button on title bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("add your work");
        editWeight = (EditText) findViewById(R.id.weight);
        editTime = (EditText) findViewById(R.id.time);
        saveButton = (Button) findViewById(R.id.saveButton2);
        sleeping = (CheckBox) findViewById(R.id.sleeping);
        jogging = (CheckBox) findViewById(R.id.jogging);
        fastJogging = (CheckBox) findViewById(R.id.fastjogging);
        walking2 = (CheckBox) findViewById(R.id.walking2);
        walking3 = (CheckBox) findViewById(R.id.walking3);
        walking35 = (CheckBox) findViewById(R.id.walking3_5);
        bicycleS = (CheckBox) findViewById(R.id.bicycleslow);
        bicycleM = (CheckBox) findViewById(R.id.bicyclemedium);
        bicycleF = (CheckBox) findViewById(R.id.bicyclefast);

        db = new DataBaseHelper(this); //Create a DataBaseHelper

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = getDate();


                weight = Integer.parseInt(editWeight.getText().toString());
                time = Integer.parseInt(editTime.getText().toString());
                if (sleeping.isChecked()) {
                    work = "sleeping";
                    met = 0.9;
                }
                if (jogging.isChecked()) {
                    work = "jogging";
                    met = 7;
                }
                if (fastJogging.isChecked()) {
                    work = "fastjogging";
                    met = 8;
                }
                if (walking2.isChecked()) {
                    work = "walking 2mph";
                    met = 2.5;
                }
                if (walking3.isChecked()) {
                    work = "walking 3mph";
                    met = 3.3;
                }
                if (walking35.isChecked()) {
                    work = "walking 3.5mph";
                    met = 3.6;
                }
                if (bicycleS.isChecked()) {
                    work = "bicycling slow";
                    met = 3;
                }
                if (bicycleM.isChecked()) {
                    work = "bicycling medium";
                    met = 4;
                }
                if (bicycleF.isChecked()) {
                    work = "bicycling fast";
                    met = 5.5;
                }
                double var = 0.0175*weight*time*met;
                calories = (int)var;
                AlertDialog.Builder builder;
                //Determine which alert dialog builder based on API version
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    builder = new AlertDialog.Builder(addwork.this, android.R.style.Theme_Material_Dialog_Alert);
                else
                    builder = new AlertDialog.Builder(addwork.this);

                //Build the dialog message
                builder.setTitle("save work")
                        //       .setMessage(calories + " Calories: 0 \nNutrition: 0")
                        .setMessage("changes will be saved for today")
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Save the entry into the user's daily intake
                                String date = getDate();
                                if (db.dateExists(date)) { //If there is an entry for that date in the database then update and add food
                                    //updateFood(date, calories, protein, fat, carbohydrates); //Update the nutrition for the day with the food added
                                    updatework(date,calories,0,0,0);
                                    db.addWork(work,time,calories,date); //Add the food to the food history table that stores foods eaten
                                    startActivity(new Intent(addwork.this, DailyPage.class));
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Do nothing, go back
                            }
                        }).show();


            }
        });
    }


        public String getDate() {
            Date date = Calendar.getInstance().getTime(); //Get the current date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy"); //Set up a format to convert the date
            String formattedDate = dateFormat.format(date); //Convert date into the format
            return formattedDate;
        }

    public void updatework (String date, int calories, int protein, int fat, int carbohydrates) {
        int Calories, Protein, Fat, Carbohydrates, Weight;
        Calories = calories;
        Protein= protein;
        Fat = fat;
        Carbohydrates = carbohydrates;
        Weight = db.getWeight("0"); //Get the default weight from database

        //Add the food's nutritional values to the daily total
        Calories = db.getCalories(date)-Calories;
        Protein += db.getProtein(date);
        Fat += db.getFat(date);
        Carbohydrates += db.getCarbs(date);

        db.updateNutrition(date, Calories, Protein, Fat, Carbohydrates, Weight); //Update the nutrition for the day with food added
    }


    }