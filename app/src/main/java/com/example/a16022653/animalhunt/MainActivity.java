package com.example.a16022653.animalhunt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Uri filePath;
    int PICK_IMAGE_REQUEST = 111;
    ProgressDialog pd;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReferenceFromUrl("gs://animalhunt-7218c.appspot.com");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        Button btn = (Button) findViewById(R.id.btnCamera);
        Button btnCon = (Button) findViewById(R.id.btnConfirm);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
                uploadImage();
            }
        });

        btnCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseApp.initializeApp(MainActivity.this);
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(i);
                if (filePath != null) {
                    pd.show();
                    StorageReference childRef = storageReference.child("image.jpg");

                    //uploading the image
                    UploadTask uploadTask = childRef.putFile(filePath);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(MainActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void uploadImage() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

//    String mCurrentPhotoPath;
//
//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.getAbsolutePath();
//        return image;
//    }
//
//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//                Toast.makeText(MainActivity.this, "Upload Unsuccessful!", Toast.LENGTH_SHORT).show();
//            }
//            Toast.makeText(MainActivity.this, "Upload Successful!", Toast.LENGTH_SHORT).show();
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.example.android.fileprovider",
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
//            }
//        }
//    }
//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        iv.setImageBitmap(bitmap);

    }
}
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode,resultCode,data);
//        //Check if there's an error
//        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
//            Uri imageUri = data.getData();
//
//            CropImage.activity(imageUri)
//                    .setAspectRatio(1,1)
//                    .setMinCropResultSize(500,500)
//                    .start(this);
//
//        }
//
//        //Check if the request code is passed through the CropActivity - CROP_IMAGE_ACTIVITY_REQUEST_CODE
//        //makes sure the result retrieved is from the CropActivity
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//
//            if (resultCode == RESULT_OK) {
//                mProgresDialog = new ProgressDialog(SettingsActivity.this);
//                mProgresDialog.setTitle("Uploading Image");
//                mProgresDialog.setMessage("Please wait a moment while we upload your image.");
//                mProgresDialog.setCanceledOnTouchOutside(false);
//                mProgresDialog.show();
//
//                Uri resultUri = result.getUri();
//                String current_userID = mCurrentUser.getUid();
////                File thumb_filePath = new File(resultUri.getPath());
//
////                Bitmap thumb_bitmap = new Compressor(this)
////                        .setMaxWidth(200)
////                        .setMaxHeight(200)
////                        .setQuality(75)
////                        .compressToBitmap(thumb_filePath);
////
////                ByteArrayOutputStream baos = new ByteArrayOutputStream();
////                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
////                byte[] thumb_byte = baos.toByteArray();
//
//                StorageReference filepath = mImageStorage.child("profile_images").child( current_userID + ".jpg");
////                StorageReference thumb_filepath = mImageStorage.child("profile_images").child("profile_thumbs").child(current_userID + ".jpq");
//
//                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot😠) {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        if(task.isSuccessful()){
//                            String downloadUrl = task.getResult().getDownloadUrl().toString();
////                            UploadTask uploadTask = thumb_filePath.putBytes(thumb_byte);
//
//                            mUsersDatabase.child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void😠) {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()){
//                                        mProgresDialog.dismiss();
//                                        Toast.makeText(SettingsActivity.this, "Successfully uploaded", Toast.LENGTH_LONG).show();
//
//                                    }
//                                }
//                            });
//
//                        } else {
//                            mProgresDialog.dismiss();
//                            Toast.makeText(SettingsActivity.this, "Error in uploading...", Toast.LENGTH_LONG).show();
//
//                        }
//                    }
//                });
//
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//
//                Exception error = result.getError();
//
//            }
//        }
//    }

