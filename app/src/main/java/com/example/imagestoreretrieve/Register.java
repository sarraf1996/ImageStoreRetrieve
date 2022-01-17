package com.example.imagestoreretrieve;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import at.favre.lib.crypto.bcrypt.BCrypt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,16}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    CheckBox chk;
    EditText et1;
    EditText et2;
    EditText et3;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    boolean twice = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        et1 = (EditText) findViewById(R.id.username);
        et2 = (EditText) findViewById(R.id.email);
        et3 = (EditText) findViewById(R.id.password);
        chk = (CheckBox) findViewById(R.id.checkBox);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean Check) {
                if(Check) {
                    et3.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                else {
                    et3.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    public void register(View view) {
        final String username = et1.getText().toString();
        final String email = et2.getText().toString();
        final String password = et3.getText().toString();
        String bcryptHashPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        if(TextUtils.isEmpty(username)) {
            et1.setError("Username field cannot be empty.");
            et1.requestFocus();
        }
        else if(username.length() > 0 && username.length() < 3) {
            et1.setError("Username must be at least 3 characters long.");
            et1.requestFocus();
            Toast.makeText(this, "Enter a correct username. Username must be at least 3 characters long.", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(email)) {
            et2.setError("Email Address field cannot be empty.");
            et2.requestFocus();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et2.setError("Email Address entered is not valid.");
            et2.requestFocus();
        }
        else if(TextUtils.isEmpty(password)) {
            et3.setError("Password field cannot be empty.");
            et3.requestFocus();
        }
        else if(password.length() > 0 && (password.length() < 8 || password.length() > 16)) {
            et3.setError("Password must be at least 8 characters and at most 16 characters long.");
            et3.requestFocus();
            Toast.makeText(this, "Enter a correct password. Password must be at least 8 characters and at most 16 characters long.", Toast.LENGTH_LONG).show();
        }
        else if(PASSWORD_PATTERN.matcher(password).matches()) {
            progressBar.setVisibility(View.VISIBLE);
            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("username", username);
                        hashMap.put("password", bcryptHashPassword);
                        hashMap.put("email", email);
                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Registration successful.", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                    Intent i = new Intent(Register.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                    return;
                                }
                                Toast.makeText(Register.this, "Registration Unsuccessful, Please try again. Possible reason :- Error while inserting data into Database.", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                        return;
                    }
                    Toast.makeText(Register.this, "Registration Unsuccessful, Please try again. Possible reason :- User with the entered Email Address already registered.", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
        else {
            et3.setError("Password must contains at least one digit, one upper case alphabet, one lower case alphabet, one special character, and no blank space(s).");
            et3.requestFocus();
        }
    }

    public void cancel(View view) {
        Intent i = new Intent(Register.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(twice) {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
            System.exit(0);
        }
        twice = true;
        Toast.makeText(this, "Please press Back again to exit.", Toast.LENGTH_LONG).show();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                twice = false;
            }
        }, 3000);
    }
}
