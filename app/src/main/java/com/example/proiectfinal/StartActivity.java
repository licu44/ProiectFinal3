package com.example.proiectfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{


    private Button butonReg;

    private EditText email;
    private EditText password;
    private Button login;

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        butonReg =findViewById(R.id.buttonRegiter);
        login=findViewById(R.id.buttonLogin);

        butonReg.setOnClickListener(this);
        login.setOnClickListener(this);



        email=findViewById(R.id.email);
        password=findViewById(R.id.password);


        auth=FirebaseAuth.getInstance();





    }

    private void loginUser(String txt_email, String txt_password) {
        auth.signInWithEmailAndPassword(txt_email, txt_password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(StartActivity.this, "Login cu suces", Toast.LENGTH_LONG).show();
                startActivity(new Intent(StartActivity.this, com.example.proiectfinal.MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.buttonLogin){
            String txt_email =email.getText().toString();
            String txt_password=password.getText().toString();

            if(TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                Toast.makeText(this, "Campuri goale", Toast.LENGTH_SHORT).show();
            }
            else{
            loginUser(txt_email, txt_password);
            Toast.makeText(StartActivity.this, "Login apasat", Toast.LENGTH_LONG).show();
            }


        }
        else{
            Toast.makeText(StartActivity.this, "Register apasat", Toast.LENGTH_LONG).show();
            startActivity(new Intent(StartActivity.this, com.example.proiectfinal.Register.class));
            finish();
        }
        Log.i("login", "Login apasat");
    }
}