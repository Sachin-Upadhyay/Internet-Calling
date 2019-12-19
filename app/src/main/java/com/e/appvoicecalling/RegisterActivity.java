package com.e.appvoicecalling;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.e.appvoicecalling.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    EditText name, email, password;
    FirebaseAuth mauth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.reemail);
        password = findViewById(R.id.rePassword);
        mauth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("User");

    }

    public void registration(View view) {
        final String Name = name.getText().toString();
        final String Email = email.getText().toString();
        final String Password = password.getText().toString();

        mauth.createUserWithEmailAndPassword(Email,Password)
                .addOnCompleteListener(RegisterActivity.this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registered", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(RegisterActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();

                            FirebaseUser firebaseUser = mauth.getCurrentUser();
                            User user = new User(Name, Email, Password, firebaseUser.getUid());
                            reference.child(firebaseUser.getUid()).setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegisterActivity.this, "User Register", Toast.LENGTH_SHORT).show();
                                                finish();
                                                Intent i = new Intent(RegisterActivity.this, HomeActivity.class);
                                                startActivity(i);
                                            }
                                            else
                                                Toast.makeText(RegisterActivity.this, "Regiration Failed"+task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                    }
                });
    }
}
