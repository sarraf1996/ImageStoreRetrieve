package com.example.imagestoreretrieve;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    EditText resetEmailInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        resetEmailInput = (EditText) findViewById(R.id.editText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
    }

    public void sendEmail(View view) {
        final String email = resetEmailInput.getText().toString();
        if(TextUtils.isEmpty(email)) {
            resetEmailInput.setError("Email Address field cannot be empty.");
            resetEmailInput.requestFocus();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            resetEmailInput.setError("Email Address entered is not valid.");
            resetEmailInput.requestFocus();
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(ForgetPassword.this, "Forget password link is sent successfully to " + email + ", Please check your Email account to reset the password.", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        Intent i = new Intent(ForgetPassword.this, MainActivity.class);
                        startActivity(i);
                        finish();
                        return;
                    }
                    String message = task.getException().getMessage();
                    Toast.makeText(ForgetPassword.this, "Error occurred : " + message, Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    public void onBackPressed() {
        Intent i = new Intent(ForgetPassword.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
