package com.example.rockpaperscissors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    // Game logic support
    String userName;
    final int ROCK = 0;
    final int SCISSORS = 1;
    final int PAPER = 2;


    ImageView clickedBtn;
    ImageView deviceBtn;

    int userChoice;

    DocumentReference database;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    String lastUpdate;
    DocumentSnapshot updatedSnapshot;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        database = db.collection("game").document("players");

        database.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Listen to updates", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    lastUpdate = snapshot.toString();
                    updatedSnapshot = snapshot;
                    Log.d("Listen to updates", "Current data: " + snapshot.getData().toString());
                    notifyNewJoined(snapshot);
                } else {
                    Log.d("Listen to updates", "Current data: null");
                    makeToast("You are the first, let's wait for participants!:)");
                }
            }
        });


    }

    public void notifyNewJoined(DocumentSnapshot update) {
        Log.d("Notify new join", "Current data: " + update);
        Map data = updatedSnapshot.getData();

        if ( data.size() == 0 ) return;

        String msg = "Players in the game ";
        for (Object player : data.keySet()){
            msg += " " + player + " ";
        }
        makeToast(msg);


    }

    public void makeToast(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.show();

    }

    public void joinUser(String username) {


        Map<String, Object> data = new HashMap<>();
        data.put("paused", false);

        Map<String, Object> newEntry = new HashMap<>();
        newEntry.put(username, data);
        db.collection("game").document("players").set(newEntry, SetOptions.merge());


        DocumentReference docRef = db.collection("game").document("players");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("onComplete", "DocumentSnapshot data: " + document.getData());

                    } else {
                        Log.d("onComplete", "No such document");
                    }
                } else {
                    Log.d("onComplete", "get failed with ", task.getException());
                }
            }
        });

    }


    public void startGame(View view) {

        TextView player = findViewById(R.id.textInputUserName);
        userName = player.getText().toString();


        if (userName.length() == 0) {
            Toast toast = Toast.makeText(this, "Please enter you name first", Toast.LENGTH_LONG);
            toast.show();
        } else if (lastUpdate.contains(userName)) {
            makeToast("This username is taken, please choose another one!");

        }else {
            player.setFocusable(false);
            player.setEnabled(false);

            joinUser(userName);
            View gameChoices = findViewById(R.id.gameChoices);
            gameChoices.setVisibility(View.VISIBLE);
        }
    }

    public void choosePaper(View view) {
        clickedBtn = findViewById(R.id.imageViewPaper);
        clickedBtn.setColorFilter(ContextCompat.getColor(this, R.color.teal_700), android.graphics.PorterDuff.Mode.MULTIPLY);
        new Handler().postDelayed(new Runnable() {

            public void run() {
                clickedBtn.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.teal_200), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        }, 1000);

        userChoice = PAPER;

        play();
    }

    public void chooseRock(View view) {
        clickedBtn = findViewById(R.id.imageViewRock);
        clickedBtn.setColorFilter(ContextCompat.getColor(this, R.color.teal_700), android.graphics.PorterDuff.Mode.MULTIPLY);
        new Handler().postDelayed(new Runnable() {

            public void run() {
                clickedBtn.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.teal_200), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        }, 1000);

        userChoice = ROCK;


        play();
    }

    public void chooseScissors(View view) {
        clickedBtn = findViewById(R.id.imageViewScissors);
        clickedBtn.setColorFilter(ContextCompat.getColor(this, R.color.teal_700), android.graphics.PorterDuff.Mode.MULTIPLY);
        new Handler().postDelayed(new Runnable() {

            public void run() {
                clickedBtn.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.teal_200), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        }, 1000);

        userChoice = SCISSORS;

        play();
    }


    public void play() {

        CheckBox checkBox = findViewById(R.id.checkBox);
        if (checkBox.isChecked()) {
            playAgainstDevice();
        } else {


        }

    }


    public void playAgainstDevice() {
        Random rand = new Random();
        int deviceChoice = rand.nextInt(3);

        switch (deviceChoice) {
            case ROCK:
                deviceBtn = findViewById(R.id.imageViewRock);
            case PAPER:
                deviceBtn = findViewById(R.id.imageViewPaper);
            case SCISSORS:
                deviceBtn = findViewById(R.id.imageViewScissors);

        }

        Log.d("choices", "" + userChoice + " " + deviceChoice);
        String result = "";

        if (deviceChoice == userChoice) {
            result = "No one wins!";
            Toast toast = Toast.makeText(this, result, Toast.LENGTH_LONG);
            toast.show();
            return;
        } else if ((deviceChoice == ROCK && userChoice == SCISSORS)
                || (deviceChoice == SCISSORS && userChoice == PAPER)
                || (deviceChoice == PAPER && userChoice == ROCK)) {
            result = "You lost!";
        } else if ((deviceChoice == SCISSORS && userChoice == ROCK)
                || (deviceChoice == PAPER && userChoice == SCISSORS)
                || (deviceChoice == ROCK && userChoice == PAPER)) {
            result = "You won!";
        }

        deviceBtn.setColorFilter(ContextCompat.getColor(this, R.color.red), android.graphics.PorterDuff.Mode.MULTIPLY);

        new Handler().postDelayed(new Runnable() {

            public void run() {
                deviceBtn.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.teal_200), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        }, 1000);


        Toast toast = Toast.makeText(this, result, Toast.LENGTH_LONG);
        toast.show();
    }


    public void stopGame(View view) {

        Map<String,Object> updates = new HashMap<>();
        updates.put(userName, FieldValue.delete());

        database.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Stopping game", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Stopping game", "Error deleting document", e);
                    }
                });
        TextView player = findViewById(R.id.textInputUserName);
        player.setFocusable(false);
        player.setEnabled(false);
    }
}