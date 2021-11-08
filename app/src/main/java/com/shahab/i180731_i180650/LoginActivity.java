package com.shahab.i180731_i180650;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class
LoginActivity extends AppCompatActivity {
    Button btn_login;
    private static final int pic_id = 1;
    TextView txtView_signup;

    EditText login_email;
    EditText login_password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtView_signup = findViewById(R.id.login_register);
        txtView_signup.setOnClickListener(view -> launchSignupActivity());

        btn_login = findViewById(R.id.login_loginbtn);
        btn_login.setOnClickListener(view -> launchMessagesPageActivity());

        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
    private void launchMessagesPageActivity() {

        String login_email_check = login_email.getText().toString();
        String login_password_check = login_password.getText().toString();

        if(login_email_check.equals("user1") && login_password_check.equals("user1") ) {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("message");

            myRef.setValue("Hello, World!");

            Intent StartIntent = new Intent(this, NavigationActivity.class);
            startActivity(StartIntent);

        }
        else {
            Toast.makeText(this, "Wrong credentials", Toast.LENGTH_SHORT).show();
        }
    }
    

    private void launchSignupActivity() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}
