package edu.monash.tehjiaxuan_assignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Dashboard extends AppCompatActivity {
    Button newEventCatButton;
    Button addEventButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        newEventCatButton = findViewById(R.id.buttonEventCat);
        addEventButton = findViewById(R.id.buttonAddEvent);
    }

    public void onNewEventClick(View view){
        Intent intentNewEvent = new Intent(getApplicationContext(), NewEventCategory.class);
        startActivity(intentNewEvent);
    }

    public void onAddEventClick(View view){
        Intent intentAddEvent = new Intent(getApplicationContext(), AddNewEvent.class);
        startActivity(intentAddEvent);
    }


}