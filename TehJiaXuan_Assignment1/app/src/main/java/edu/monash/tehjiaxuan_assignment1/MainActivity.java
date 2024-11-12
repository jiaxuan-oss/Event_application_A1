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
import android.widget.Toast;

import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    EditText editTextUsername,editTextPassword,editTextPasswordConfirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextPassword = findViewById(R.id.ETRegisterPassword);
        editTextUsername = findViewById(R.id.ETRegisterUsername);
        editTextPasswordConfirmation= findViewById(R.id.ETRegisterPasswordConfirm);


    }

    public void onRegisterClick(View view){
        String password = editTextPassword.getText().toString();
        String username = editTextUsername.getText().toString();
        String passwordConfirmation = editTextPasswordConfirmation.getText().toString();

        if (passwordConfirmation.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Username and Password is required!", Toast.LENGTH_SHORT).show();
        }

        else if (password.equals(passwordConfirmation)) {
            Toast.makeText(getApplicationContext(), "Register Successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), LoginView.class);
            intent.putExtra("username", username);
            saveDataToSharedPreference(username, password);
            startActivity(intent);

        } else {
            Toast.makeText(getApplicationContext(), "Invalid Password Confirmation", Toast.LENGTH_SHORT).show();
        }
    }

    public void onLoginClick(View view){
        Intent intent = new Intent(getApplicationContext(), LoginView.class);
        startActivity(intent);
    }

    private void saveDataToSharedPreference(String username, String password){
        // initialise shared preference class variable to access Android's persistent storage
        SharedPreferences sharedPreferences = getSharedPreferences("USERNAME_PASSWORD", MODE_PRIVATE);

        // use .edit function to access file using Editor variable
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // save key-value pairs to the shared preference file
        editor.putString("KEY_USERNAME", username);
        editor.putString("KEY_PASSWORD", password);

        // use editor.apply() to save data to the file asynchronously (in background without freezing the UI)
        // doing in background is very common practice for any File Input/Output operations
        editor.apply();

        // or
        // editor.commit()
        // commit try to save data in the same thread/process as of our user interface
    }


}