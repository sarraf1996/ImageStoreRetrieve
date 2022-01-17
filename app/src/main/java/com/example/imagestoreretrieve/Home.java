package com.example.imagestoreretrieve;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import java.util.HashMap;

public class Home extends AppCompatActivity {
    private StorageReference Ref;
    boolean flag = false;
    public Uri imguri;
    ImageView myImageView;
    private DatabaseReference myRef;
    ProgressBar progressBar;
    TextView tv;
    boolean twice = false;
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tv = (TextView) findViewById(R.id.textView9);
        myImageView = (ImageView) findViewById(R.id.imageView7);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            myRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    String uname = snapshot.child("username").getValue().toString();
                    tv.setText("Welcome " + uname);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(Home.this, "Failed to read value from Database.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_logout) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
            builder.setMessage("Do you really want to Logout?");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(Home.this, "Logged out successfully.", Toast.LENGTH_LONG).show();
                    Intent logout = new Intent(Home.this, MainActivity.class);
                    logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(logout);
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void browse(View view) {
        flag = true;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == -1 && data != null && data.getData() != null) {
            imguri = data.getData();
            myImageView.setImageURI(imguri);
        }
    }

    public void upload(View view) {
        if(!flag) {
            Toast.makeText(this, "No image is selected. Please click on Browse button and select image first.", Toast.LENGTH_LONG).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        if(uploadTask == null || !uploadTask.isInProgress()) {
            Cursor returnCursor = getContentResolver().query(imguri, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex("_display_name");
            returnCursor.moveToFirst();
            final String imageNameWithExtension = returnCursor.getString(nameIndex);
            returnCursor.close();
            Ref = FirebaseStorage.getInstance().getReference("Images").child(imageNameWithExtension);
            uploadTask = Ref.putFile(imguri).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("imagename", imageNameWithExtension);
                            hashMap.put("imageurl", uri.toString());
                            myRef.child("UploadedImages").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(Home.this, "Image uploaded successfully.", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        myImageView.setImageDrawable(null);
                                        flag = false;
                                        return;
                                    }
                                    Toast.makeText(Home.this, "Failed to upload the Image. Please try again.", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                    flag = false;
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    Toast.makeText(Home.this, "Failed to upload the Image. Please try again.", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
            return;
        }
        Toast.makeText(this, "Upload in progress...", Toast.LENGTH_LONG).show();
    }

    public void retrieve(View view) {
        Intent i = new Intent(Home.this, RetrieveImages.class);
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
