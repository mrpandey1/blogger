package com.example.blogger;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.InputStream;

public class AboutPost extends AppCompatActivity {
    private TextView name,title,description;
    private  ImageView img;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_post);
        String value = getIntent().getExtras().getString("Name");
        String value2 = getIntent().getExtras().getString("Title");
        String value3 = getIntent().getExtras().getString("Description");
        String value4 = getIntent().getExtras().getString("Img");
        name=findViewById(R.id.AboutPostName);
        title=findViewById(R.id.AboutPostTitle);
        description=findViewById(R.id.AboutPostDescription);
        name.setText("Author: "+value);
        title.setText(value2);
        description.setText(value3);
        progressBar=findViewById(R.id.AboutPostProgress);
        ImageView ivBasicImage = (ImageView) findViewById(R.id.AboutPostImage);
        Picasso.with(this).load(value4).into(ivBasicImage, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {

            }
        });

    }

}