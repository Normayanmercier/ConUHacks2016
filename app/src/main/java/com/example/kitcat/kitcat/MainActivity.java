package com.example.kitcat.kitcat;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends AppCompatActivity {

    private Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Firebase.setAndroidContext(this);
        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    protected void onStart() {
        int i = 1;
        final ImageLoader imageLoader = ImageLoader.getInstance();

        super.onStart();
        final ImageView catUp = (ImageView) findViewById(R.id.catUp);
        final ImageView catDown = (ImageView) findViewById(R.id.catDown);

        firebase = new Firebase("https://blinding-torch-8480.firebaseio.com/cats");

        firebase.child(String.valueOf(i++)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uri = (String) dataSnapshot.getValue();
                Bitmap bmp = imageLoader.loadImageSync(uri);
                catUp.setImageBitmap(bmp);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("ERROR: FIREBASE ERROR");
            }
        });

        firebase.child(String.valueOf(i)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uri = (String) dataSnapshot.getValue();
                Bitmap bmp = imageLoader.loadImageSync(uri);
                catDown.setImageBitmap(bmp);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("ERROR: FIREBASE ERROR");
            }
        });
    }
}
