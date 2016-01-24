package com.example.kitcat.kitcat;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
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
        //HACK
        final int[] upId = {1};
        final int[] downId = {2};

        final int i[] = {1};
        final ImageLoader imageLoader = ImageLoader.getInstance();

        super.onStart();
        final ImageView catUp = (ImageView) findViewById(R.id.catUp);
        final ImageView catDown = (ImageView) findViewById(R.id.catDown);

        final Button nextButton = (Button) findViewById(R.id.nextButton);
        final RadioButton voteUp = (RadioButton) findViewById(R.id.voteUp);
        final RadioButton voteDown = (RadioButton) findViewById(R.id.voteDown);

        firebase = new Firebase("https://blinding-torch-8480.firebaseio.com/");

        firebase.child("cats").child(String.valueOf(i[0]++)).addValueEventListener(new ValueEventListener() {
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

        firebase.child("cats").child(String.valueOf(i[0]++)).addValueEventListener(new ValueEventListener() {
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

        voteUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteUp.setEnabled(false);
                voteDown.setEnabled(false);
                nextButton.setEnabled(true);

                firebase.child("votes").child(String.valueOf(upId[0])).runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        if (mutableData.getValue() == null) {
                            mutableData.setValue(1);
                        } else {
                            mutableData.setValue(((Long) mutableData.getValue()) + 1);
                        }
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                        if (firebaseError != null) {
                            System.out.println("ERROR: COULD NOT INCREMENT: + " + firebaseError.toString());
                        } else {
                            System.out.println("Firebase counter increment succeeded.");
                        }
                    }
                });
            }
        });

        voteDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteUp.setEnabled(false);
                voteDown.setEnabled(false);
                nextButton.setEnabled(true);

                firebase.child("votes").child(String.valueOf(downId[0])).runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        if (mutableData.getValue() == null) {
                            mutableData.setValue(1);
                        } else {
                            mutableData.setValue(((Long) mutableData.getValue()) + 1);
                        }
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                        if (firebaseError != null) {
                            System.out.println("ERROR: COULD NOT INCREMENT: + " + firebaseError.toString());
                        } else {
                            System.out.println("Firebase counter increment succeeded.");
                        }
                    }
                });
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                voteUp.setChecked(false);
                voteDown.setChecked(false);
                voteUp.setEnabled(true);
                voteDown.setEnabled(true);
                nextButton.setEnabled(false);
                upId[0] += 2;
                downId[0] += 2;

                //load 2 new images
                firebase.child("cats").child(String.valueOf(i[0]++)).addValueEventListener(new ValueEventListener() {
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

                firebase.child("cats").child(String.valueOf(i[0]++)).addValueEventListener(new ValueEventListener() {
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
        });
    }
}
