package edu.monash.tehjiaxuan_assignment1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Arrays;
import java.util.Random;
import java.util.StringTokenizer;

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

public class NewEventCategory extends AppCompatActivity {
    EditText categoryIDET, categoryNameET, eventCountET;
    String categoryNameStr, categoryIDStr, eventCountStr;
    boolean isActiveBool;
    int eventCountInt;
    Switch isActive;
    MyBroadCastReceiver myBroadCastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event_category);
        categoryIDET = findViewById(R.id.ETCategoryID);
        categoryNameET = findViewById(R.id.ETCategoryName);
        eventCountET = findViewById(R.id.ETEventCount);
        isActive = findViewById(R.id.SwitchIsActive);

        //ask for user permission
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.READ_SMS
        }, 0);

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
    public void onSaveCategoryClick(View view) {
        categoryNameStr = categoryNameET.getText().toString();
        eventCountStr = eventCountET.getText().toString();
        isActiveBool = isActive.isChecked();
        //if it is not empty then parse integer
        if (!eventCountStr.equals("")) {
            eventCountInt = Integer.parseInt(eventCountStr);
        }
        //if it is empty then ask for user input
        if (categoryNameStr.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Category Name is required!", Toast.LENGTH_SHORT).show();
        } else {
            categoryIDET.setText(randomCategoryID());
            categoryIDStr = categoryIDET.getText().toString();
            //let user know the input saved successfully
            String messageSuccess = String.format("Category saved successfully: %s", categoryIDStr);
            saveDataToSharedPreference(categoryIDStr, categoryNameStr, eventCountInt, isActiveBool);
            Toast.makeText(getApplicationContext(), messageSuccess, Toast.LENGTH_SHORT).show();
        }

    }

    public char randomChar() {
        Random random = new Random();
        int randomNumber = random.nextInt(26);
        //65 is A in ascii, randomly pick any alphabet
        randomNumber += 65;
        return (char) randomNumber;
    }

    public String randomCategoryID() {
        String randomStr = "C" + randomChar() + randomChar() + "-";
        Random random = new Random();
        //1000 - 9999 random number
        String randigit = String.valueOf(random.nextInt(9000) + 1000);
        randomStr += randigit;
        return randomStr;
    }

    private void saveDataToSharedPreference(String categoryID, String eventName, int eventCount, Boolean isActive) {//double confirm
        // initialise shared preference class variable to access Android's persistent storage
        SharedPreferences sharedPreferences = getSharedPreferences("EVENT_CATEGORY", MODE_PRIVATE);

        // use .edit function to access file using Editor variable
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // save key-value pairs to the shared preference file
        editor.putString("CATEGORY_ID", categoryID);
        editor.putString("EVENT_NAME", eventName);
        editor.putInt("EVENT_COUNT", eventCount);
        editor.putBoolean("IS_ACTIVE", isActive);

        // use editor.apply() to save data to the file asynchronously (in background without freezing the UI)
        // doing in background is very common practice for any File Input/Output operations
        editor.apply();

    }

    public boolean checkValidForm(String msg) {
        String[] semicolon = msg.split(";",-1);
        //if it is not length of 3
        if (semicolon.length != 3) {
            return false;
        }
        else {
            String eventNameCheck = semicolon[0];
            String eventCountCheck = semicolon[1];
            String isActiveCheck = semicolon[2].toUpperCase();
            //if the event name is empty return false
            if(eventNameCheck.isEmpty()){
                return false;
            }
            //if it is not true/ false and it is empty return false
            if (!isActiveCheck.equals("TRUE") && !isActiveCheck.equals("FALSE") && !isActiveCheck.isEmpty()) {
                return false;
            }
            // if it is not empty and it is not integer return false
            if (!eventCountCheck.isEmpty() && !isPosInteger(eventCountCheck)) {
                return false;
            }
        }
        return true;
    }

    public boolean isPosInteger(String str) {
        //if it is empty return false
        if (str == null || str.equals("0") ||str.isEmpty()) {
            return false;
        }
        //check every string is int
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    class MyBroadCastReceiver extends BroadcastReceiver {

        /*
         * This method 'onReceive' will get executed every time class SMSReceive sends a broadcast
         * */
        @Override
        public void onReceive(Context context, Intent intent) {
            /*
             * Retrieve the message from the intent
             * */
            String msg = intent.getStringExtra(SMSReceiver.SMS_MSG_KEY);
            //split by :
            String[] removeColon = msg.split(":");
            //copy all the tokens only, to avoid edge cases like "category:Melbourne:20:TRUE;;";
            String[] preprocessRemoveColon = Arrays.copyOfRange(removeColon, 1, removeColon.length);
            //join back all the tokens with :
            String allPartsContain = String.join(":", preprocessRemoveColon);


            //check the parts
            if (checkValidForm(allPartsContain) && removeColon[0].equals("category")) {
                String[] token = allPartsContain.split(";",-1);

                String nameCheck = token[0];
                categoryNameET.setText(nameCheck);

                String eventCountCheck = token[1];
                //if count check is not empty then set
                eventCountET.setText(eventCountCheck);


                String isActiveCheck = token[2].toUpperCase();
                if (!isActiveCheck.isEmpty()) {
                    if (isActiveCheck.equals("TRUE")) {
                        isActiveBool = true;
                    } else {
                        isActiveBool = false;
                    }
                    isActive.setChecked(isActiveBool);
                }
                else{
                    isActive.setChecked(false);
                }
            }
            else {
                    Toast.makeText(getApplicationContext(), "Invalid Command", Toast.LENGTH_SHORT).show();
                }
        }
    }
}