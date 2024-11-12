package edu.monash.tehjiaxuan_assignment1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Random;
import java.util.StringTokenizer;

public class AddNewEvent extends AppCompatActivity {
    EditText eventIDET, eventNameET, categoryIDET, ticketET;
    String eventIDStr, categoryIDStr, eventNameStr, ticketAvailableStr;
    int ticketsAvailableInt;
    Boolean isActiveBool;
    Switch isActiveSwitch;
    MyBroadCastReceiver myBroadCastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_event);
        eventIDET = findViewById(R.id.ETEventID);
        eventNameET = findViewById(R.id.ETEventName);
        categoryIDET = findViewById(R.id.ETAddCategoryID);
        ticketET = findViewById(R.id.ETTicketAvailable);
        isActiveSwitch = findViewById(R.id.SwitchAddIsActive);

        //request user permission to send sms
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.READ_SMS
        }, 0);
        //create broadcast receiver object
        myBroadCastReceiver = new MyBroadCastReceiver();
        /*
         * Register the broadcast handler with the intent filter that is declared in
         * */
        registerReceiver(myBroadCastReceiver, new IntentFilter(SMSReceiver.SMS_FILTER), RECEIVER_EXPORTED);
    }

    @Override
    public void onStop(){
        super.onStop();
        unregisterReceiver(this.myBroadCastReceiver);
    }

    public void onAddEventClick(View view){
        //get user input
        categoryIDStr = categoryIDET.getText().toString();
        eventNameStr = eventNameET.getText().toString();
        isActiveBool = isActiveSwitch.isChecked();
        ticketAvailableStr = ticketET.getText().toString();

        if (!ticketAvailableStr.equals("")){ //to avoid error like converting null to int
            ticketsAvailableInt = Integer.parseInt(ticketAvailableStr); //convert to int
        }

        //avoid empty input
        if(categoryIDStr.isEmpty() || eventNameStr.isEmpty()){
            Toast.makeText(getApplicationContext(), "Event Name and Category ID is required!", Toast.LENGTH_SHORT).show();
        }
        else{
            //get random event ID
            eventIDStr = randomEventID();
            eventIDET.setText(eventIDStr); //set
            //giving success toast
            String messageSuccess = String.format("Event saved: %s to %s", eventIDStr, categoryIDStr);
            //save to shared preference
            saveDataToSharedPreference(eventIDStr, categoryIDStr, eventNameStr, ticketsAvailableInt, isActiveBool);
            Toast.makeText(getApplicationContext(), messageSuccess, Toast.LENGTH_SHORT).show();
        }
    }

    public char randomChar(){
        //getting random char
        Random random = new Random();
        int randomNumber = random.nextInt(26); //getting random number between 0 to 26
        //65 is A in ascii, randomly pick any alphabet
        randomNumber += 65;
        return (char)randomNumber;
    }

    public String randomEventID(){
        //random event starts with E
        String randomStr = "E" + randomChar() + randomChar() + "-";
        Random random = new Random();
        //10000-99999 random number
        String randigit = String.valueOf(random.nextInt(90000) + 10000);
        randomStr += randigit;
        return randomStr;
    }
    private void saveDataToSharedPreference(String eventID, String categoryID, String eventName, int ticketAvai, Boolean isActive){//double confirm
        // initialise shared preference class variable to access Android's persistent storage
        SharedPreferences sharedPreferences = getSharedPreferences("EVENT_CATEGORY", MODE_PRIVATE);

        // use .edit function to access file using Editor variable
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // save key-value pairs to the shared preference file
        editor.putString("EVENT_ID", eventID);
        editor.putString("CATEGORY_ID", categoryID);
        editor.putString("EVENT_NAME", eventName);
        editor.putInt("TICKET_AVAILABLE", ticketAvai);
        editor.putBoolean("IS_ACTIVE", isActive);

        // use editor.apply() to save data to the file asynchronously (in background without freezing the UI)
        // doing in background is very common practice for any File Input/Output operations
        editor.apply();

    }

    public boolean checkValidForm(String msg) {
        //split the string by ; include the empty string
        String[] semicolon = msg.split(";",-1);

        //if it doesnt have 4 parts return false
        if (semicolon.length != 4) {
            return false;
        }

        else {
            //create attribute from the 4 parts
            String eventNameCheck = semicolon[0];
            String categoryIDCheck = semicolon[1];
            String ticketCheck = semicolon[2];
            String isActiveCheck = semicolon[3].toUpperCase();

            //if is empty then reject
            if(eventNameCheck.isEmpty()){
                return false;
            }
            //if it is not true not false and empty then reject
            if (!isActiveCheck.equals("TRUE") && !isActiveCheck.equals("FALSE") && !isActiveCheck.isEmpty()) {
                return false;
            }
            //if ticket check is not integer or is empty then reject
            if (!isPosInteger(ticketCheck) && !ticketCheck.isEmpty()) {
                return false;
            }
            //if categoryID is empty or it is not start with C then reject
            if (categoryIDCheck.isEmpty() || !(categoryIDCheck.charAt(0) == 67)) {
                return false;
            }
        }
        return true;
    }

    public boolean isPosInteger(String str) {
        //if is null then return false
        if (str == null || str.equals("0") || str.isEmpty()) {
            return false;
        }
        //loop through all the char check if it is int  only
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            /*
             * Retrieve the message from the intent
             * */
            String msg = intent.getStringExtra(SMSReceiver.SMS_MSG_KEY);
            //split it with :
            String[] removeColon = msg.split(":");
            //copy all the tokens only, to avoid edge cases like "category:Melbourne:20:TRUE;CME-1084;;";
            String[] preprocessRemoveColon = Arrays.copyOfRange(removeColon, 1, removeColon.length);
            //join back all the tokens with :
            String allPartsContain = String.join(":", preprocessRemoveColon);

            //check user input and the command is correct
            if (checkValidForm(allPartsContain) && removeColon[0].equals("event")) {
                //create an array to store all the token after checking
                String[] token = allPartsContain.split(";",-1);

                String nameCheck = token[0];
                eventNameET.setText(nameCheck);

                String categoryIDCheck = token[1];
                categoryIDET.setText(categoryIDCheck);

                String ticketCheck = token[2];
                ticketET.setText(ticketCheck);

                String isActiveCheck = token[3].toUpperCase();
                if (!isActiveCheck.isEmpty()) {
                    if (isActiveCheck.equals("TRUE")) {
                        isActiveBool = true;
                    } else {
                        isActiveBool = false;
                    }
                    isActiveSwitch.setChecked(isActiveBool);
                }
                else {
                    isActiveSwitch.setChecked(false);
                }

            }
            else {
                Toast.makeText(getApplicationContext(), "Invalid Command", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


