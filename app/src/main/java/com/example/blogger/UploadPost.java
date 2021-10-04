package com.example.blogger;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadPost#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadPost extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UploadPost() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadPost.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadPost newInstance(String param1, String param2) {
        UploadPost fragment = new UploadPost();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_post, container, false);
    }

    private Button getLocation, uploadImage, uploadPost;
    private EditText name, title, description, location;
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;
    private FirebaseAuth mAuth;
    FirebaseStorage storage;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    StorageReference storageReference;
    private LocationManager locationManager;
    private LocationListener locationListener;

    Geocoder geocoder;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getLocation = view.findViewById(R.id.getLocation);
        uploadImage = view.findViewById(R.id.uploadImage);
        uploadPost = view.findViewById(R.id.uploadPost);
        location = view.findViewById(R.id.postLocation);
        name = view.findViewById(R.id.postName);
        title = view.findViewById(R.id.postTitle);
        description = view.findViewById(R.id.postDescription);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        geocoder= new Geocoder(getContext(), Locale.getDefault());
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location locations) {

                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(locations.getLatitude(), locations.getLongitude(), 1);
                    String cityName = addresses.get(0).getAddressLine(0);
                    location.setText(cityName);
                } catch (IOException e) {

                }
            }

            @Override
            public void onStatusChanged(String s,int i,Bundle bundle) {
            }
        };

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLoc();
            }
        });


        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });

        uploadPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadBlog();
            }
        });

    }

    private void uploadBlog() {
        String name_ = name.getText().toString().trim();
        String location_ = location.getText().toString().trim();
        String title_ = title.getText().toString().trim();
        String description_ = description.getText().toString().trim();
        if (name_.isEmpty()) {
            name.setError("Name is required");
            name.requestFocus();
            return;
        }
        if (title_.isEmpty()) {
            title.setError("Title is required");
            title.requestFocus();
            return;
        }
        if (description_.isEmpty() || description_.length() < 10) {
            description.setError("Minimum 10 words is required");
            description.requestFocus();
            return;
        }
        if (location_.isEmpty()) {
            location_ = "";
        }

        uploadImage(name_, title_, description_, location_, userID);
    }

    private void SelectImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,
                resultCode,
                data);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            filePath = data.getData();
        }
    }

    private void uploadImage(String name_, String title_, String description_, String location_, String postBy) {
        if (filePath != null) {
            ProgressDialog progressDialog
                    = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + UUID.randomUUID().toString());

            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String url = uri.toString();
                                            Post post = new Post(name_, title_, description_, location_, url, postBy);
                                            FirebaseDatabase.getInstance().getReference("Posts").push().setValue(post)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            name.setText("");
                                                            description.setText("");
                                                            location.setText("");
                                                            title.setText("");
                                                        }
                                                    });
                                        }
                                    });
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(getContext(),
                                                    "Post Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(getContext(),
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int) progress + "%");
                                }
                            });
        } else {
            Toast
                    .makeText(getContext(),
                            "Please select an image by clicking on change profile!!",
                            Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void getLoc() {
        try {
        if ((ContextCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ) && (ContextCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED )) {
            locationManager.requestLocationUpdates("gps", 5000, 10, locationListener);
        }else {
            if(ContextCompat.checkSelfPermission(
                    getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED){
                System.out.println("first");
            requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(ContextCompat.checkSelfPermission(
                    getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED){
                System.out.println("Second");
                requestPermissionLauncher.launch(
                        Manifest.permission.ACCESS_COARSE_LOCATION);
            }
        }
        }catch (Exception e){

        }
    }
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });


}