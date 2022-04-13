package com.kinfolkstech.madscalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private Button buttonLogin;
    private EditText edittextUsername,edittextPassword;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonLogin=findViewById(R.id.btnLogin);
        edittextUsername=findViewById(R.id.username);
        edittextPassword=findViewById(R.id.password);

        //Initializing Firebase Authentication Instance
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(MainActivity.this);

        ////////Button on Click Listener/////////

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ///Storing input in local variables///
                String userEmail=edittextUsername.getText().toString();
                String userPassword=edittextPassword.getText().toString();

                //Checking username is not null
                if(userEmail.isEmpty())
                {
                    edittextUsername.setError("Please enter username/email id");
                    edittextUsername.requestFocus();
                }

                //Checking password is not null
                else if(userPassword.isEmpty())
                {
                    edittextPassword.setError("Please enter the password");
                    edittextPassword.requestFocus();
                }
                else if(!(userEmail.isEmpty() && userPassword.isEmpty()))
                {
                    progressDialog.setMessage("PLease wait...");
                    progressDialog.show();

                    //Calling Google Firebase Authentication for Email and Password Authentication
                    firebaseAuth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                            if(!task.isSuccessful())
                            {
                                //On wrong credentials user get the error message
                                Toast.makeText(MainActivity.this, "Please check your credentials", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                //On Success user moved to calculator
                                SendUserToCalculatorActivity();
                                progressDialog.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e)
                        {
                            //On Failure user get the error message
                            Toast.makeText(MainActivity.this, "Error : "+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    ///Navigation Function - Intent function from MainActivity to Calculator Activity
    public void SendUserToCalculatorActivity()
    {
        startActivity(new Intent(MainActivity.this,CalculatorActivity.class));
    }
}