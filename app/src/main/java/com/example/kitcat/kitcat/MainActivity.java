package com.example.kitcat.kitcat;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.HashMap;

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
        firebase = new Firebase("https://blinding-torch-8480.firebaseio.com/");
        //HACK
        final int[] upId = {1};
        final int[] downId = {2};

        final Context that = this;

        // 1: hungry cat
        // 2: tube cat
        // 3: grumpy cat
        // 4: lil bub
        // 5: flying cat
        // 6: lion cat
        final HashMap<Integer, Long> results = new HashMap<>();

        //populate hashmap
        for (final int[] i = {1}; i[0] < 7; ++i[0]) {
            firebase.child("votes").child(String.valueOf(i[0])).runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    if (mutableData.getValue() == null) {
                        results.put(i[0], Long.parseLong("0"));
                    } else {
                        results.put(i[0], (Long) mutableData.getValue());
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

        final int i[] = {1};
        final ImageLoader imageLoader = ImageLoader.getInstance();

        super.onStart();
        final ImageView catUp = (ImageView) findViewById(R.id.catUp);
        final ImageView catDown = (ImageView) findViewById(R.id.catDown);

        final Button nextButton = (Button) findViewById(R.id.nextButton);
        final RadioButton voteUp = (RadioButton) findViewById(R.id.voteUp);
        final RadioButton voteDown = (RadioButton) findViewById(R.id.voteDown);

        final EditText finalResults = (EditText) findViewById(R.id.finalResults);

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
                        Long total = Long.parseLong("0");
                        if (mutableData.getValue() == null) {
                            mutableData.setValue(1);
                            total = Long.parseLong("1");
                        } else {
                            total = ((Long) mutableData.getValue()) + 1;
                            mutableData.setValue(total);
                        }
                        results.put(upId[0], total);

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
                        Long total = Long.parseLong("0");
                        if (mutableData.getValue() == null) {
                            mutableData.setValue(1);
                            total = Long.parseLong("1");
                        } else {
                            total = ((Long) mutableData.getValue()) + 1;
                            mutableData.setValue(total);
                        }

                        results.put(downId[0], total);
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

                if (downId[0] == 6) {
                    String cat1 = getCatName(1) + ": " + results.get(1);
                    String cat2 = getCatName(2) + ": " + results.get(2);
                    String cat3 = getCatName(3) + ": " + results.get(3);
                    String cat4 = getCatName(4) + ": " + results.get(4);
                    String cat5 = getCatName(5) + ": " + results.get(5);
                    String cat6 = getCatName(6) + ": " + results.get(6);

                    String finalResultString = cat1 + "\n" + cat2 + "\n" + cat3 + "\n" + cat4 + "\n" + cat5 + "\n" + cat6;
                    finalResults.setText(finalResultString);
                    finalResults.setVisibility(View.VISIBLE);
                }

                upId[0] += 2;
                downId[0] += 2;
            }
        });
    }

    // 1: hungry cat
    // 2: tube cat
    // 3: grumpy cat
    // 4: lil bub
    // 5: flying cat
    // 6: lion cat
    private String getCatName(int i) {
        switch(i) {
            case 1:
                return "hungry cat";
            case 2:
                return "tube cat";
            case 3:
                return "grumpy cat";
            case 4:
                return "lil bub";
            case 5:
                return "flying cat";
            case 6:
                return "lion cat";
            default:
                break;
        }
        return "error cat";
    }
}
