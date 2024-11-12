package edu.monash.tehjiaxuan_assignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginView extends AppCompatActivity {
    EditText editTextUsername, editTextPassword;
    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_view);
        editTextUsername = findViewById(R.id.ETLoginUsername);
        editTextPassword = findViewById(R.id.ETLoginPassword);

        SharedPreferences sharedPreferences = getSharedPreferences("USERNAME_PASSWORD", MODE_PRIVATE);
        if(sharedPreferences != null) {
            String username = sharedPreferences.getString("KEY_USERNAME", "");
            editTextUsername.setText(username);
        }

    }

    public void onLoginClick(View view){
        username = editTextUsername.getText().toString();
        password = editTextPassword.getText().toString();
        SharedPreferences sharedPreferences = getSharedPreferences("USERNAME_PASSWORD", MODE_PRIVATE);
        String usernameRestored = sharedPreferences.getString("KEY_USERNAME", "DEFAULT VALUE");
        String passwordRestored = sharedPreferences.getString("KEY_PASSWORD", "DEFAULT VALUE");
        String messageInvalid = "Authentication failure: Username or Password incorrect";
        String messageSuccess = "Login Successfully";

        if(username.equals(usernameRestored) && password.equals(passwordRestored)){
            Toast.makeText(getApplicationContext(), messageSuccess, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(getApplicationContext(), messageInvalid, Toast.LENGTH_SHORT).show();
        }
    }
    public void onRegisterClick(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}