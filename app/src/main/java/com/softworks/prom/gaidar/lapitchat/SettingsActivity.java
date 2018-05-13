package com.softworks.prom.gaidar.lapitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
    public static final String STATUS_EXTRA = "status";
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private StorageReference mImageStorage;
    private CircleImageView mImage;
    private TextView mName;
    private TextView mStatus;
    private Button mChangeStatus;
    private Button mChangeImage;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mChangeImage = findViewById(R.id.change_image);
        mImage = findViewById(R.id.profile_image);
        mName = findViewById(R.id.display_name);
        mStatus = findViewById(R.id.status);
        mChangeStatus = findViewById(R.id.change_status);

        mImage.setImageResource(R.drawable.default_image);

        mChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);

            }
        });
        mChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, StatusActivity.class);
                String currentStatus = mStatus.getText().toString();
                intent.putExtra(STATUS_EXTRA, currentStatus);
                startActivity(intent);
            }
        });
        mImageStorage = FirebaseStorage.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        mDatabase.keepSynced(true);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                if (!image.equals("default"))
                    Glide.with(SettingsActivity.this.getApplicationContext()).
                            load(image).apply(new RequestOptions()
                            .placeholder(R.drawable.default_image))
                            .into(mImage);
                mName.setText(name);
                mStatus.setText(status);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            Uri uri = result.getUri();
            Log.i("file", "uri is " + uri == null ? "empty" : " not empty");
            File thumbPath = new File(uri.getPath());
            Bitmap bitmap = new Compressor(this)
                    .setMaxHeight(400)
                    .setMaxWidth(400)
                    .setQuality(75)
                    .compressToBitmap(thumbPath);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bytes = stream.toByteArray();
            pd = new ProgressDialog(this);
            pd.setTitle("Uploading Image...");
            pd.setMessage("Please wait while we upload your image");
            pd.setCanceledOnTouchOutside(false);
            pd.show();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference filepath = mImageStorage.child("profile_images").child(uid + ".jpg");
            filepath.putBytes(bytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        String imageUrl = task.getResult().getDownloadUrl().toString();
                        mDatabase.child("image").setValue(imageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    pd.dismiss();
                                    Toast.makeText(SettingsActivity.this, "Working", Toast.LENGTH_SHORT).show();
                                } else {
                                    pd.hide();
                                    Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {
                        pd.hide();
                        Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
