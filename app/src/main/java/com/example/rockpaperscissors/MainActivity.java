package com.example.rockpaperscissors;

import androidx.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void joinUser(String username) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", username);
        data.put("paused", false);
        db.collection("players").document(username).set(data, SetOptions.merge());


        DocumentReference docRef = db.collection("players").document("Ellina");
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
        } else {
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

    public void notifyUser(String msg) {

        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.show();

    }
}