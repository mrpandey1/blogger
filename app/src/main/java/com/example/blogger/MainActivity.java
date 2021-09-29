    package com.example.blogger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

    public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView register,forgotPassword;
    private EditText editTextEmail,editTextPassword;
    private Button signIn;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        register=(TextView) findViewById(R.id.register);
        register.setOnClickListener(this);
        signIn=(Button) findViewById(R.id.signIn);
        signIn.setOnClickListener(this);
        forgotPassword=(TextView) findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);
        editTextEmail=(EditText) findViewById(R.id.email);
        editTextPassword=(EditText) findViewById(R.id.password);
        progressBar=(ProgressBar) findViewById(R.id.progressBar2);
        mAuth=FirebaseAuth.getInstance();
    }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.register:
                    startActivity(new Intent(this,RegisterUser.class));
                    break;
                case R.id.signIn:
                    userLogin();
                    break;
                case R.id.forgotPassword:
                    startActivity(new Intent(this,ForgotPassword.class));
                    break;
            }
        }

        private void userLogin() {
            String email=editTextEmail.getText().toString().trim();
            String password=editTextPassword.getText().toString().trim();
            if(email.isEmpty()){
                editTextEmail.setError("Email is required");
                editTextEmail.requestFocus();
                return;
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                editTextEmail.setError("Email is invalid!");
                editTextEmail.requestFocus();
                return;
            }
            if(password.isEmpty()){
                editTextPassword.setError("Password is required");
                editTextPassword.requestFocus();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        progressBar.setVisibility(View.GONE);
                    }else{
                        Toast.makeText(MainActivity.this,"Invalid credentials!!",Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });

        }
    }