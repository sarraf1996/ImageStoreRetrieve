package com.example.imagestoreretrieve;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class RetrieveImages extends AppCompatActivity {
    private DatabaseReference myRef;
    private RecyclerAdapter recyclerAdapter;
    RecyclerView recyclerView;
    private ArrayList<UploadedImages> uploadedImagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_images);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        myRef = FirebaseDatabase.getInstance().getReference();
        uploadedImagesList = new ArrayList<>();
        clearAll();
        getDataFromFirebase();
    }

    private void getDataFromFirebase() {
        myRef.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("UploadedImages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                clearAll();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UploadedImages uploadedImages = new UploadedImages();
                    uploadedImages.setImageName(dataSnapshot.child("imagename").getValue().toString());
                    uploadedImages.setImageUrl(dataSnapshot.child("imageurl").getValue().toString());
                    uploadedImagesList.add(uploadedImages);
                }
                recyclerAdapter = new RecyclerAdapter(RetrieveImages.this.getApplicationContext(), uploadedImagesList);
                recyclerView.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(RetrieveImages.this, "Failed to read value from Database.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void clearAll() {
        ArrayList<UploadedImages> arrayList = uploadedImagesList;
        if(arrayList != null) {
            arrayList.clear();
            RecyclerAdapter recyclerAdapter2 = recyclerAdapter;
            if(recyclerAdapter2 != null) {
                recyclerAdapter2.notifyDataSetChanged();
            }
        }
        uploadedImagesList = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_logout) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(RetrieveImages.this);
            builder.setMessage("Do you really want to Logout?");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(RetrieveImages.this, "Logged out successfully.", Toast.LENGTH_LONG).show();
                    Intent logout = new Intent(RetrieveImages.this, MainActivity.class);
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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(RetrieveImages.this, Home.class);
        startActivity(i);
        finish();
    }
}
