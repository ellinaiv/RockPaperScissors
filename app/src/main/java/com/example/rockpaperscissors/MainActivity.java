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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    // Game logic support
    String userName;
    final int ROCK = 0;
    final int SCISSORS = 1;
    final int PAPER = 2;

    ImageView clickedBtn;
    ImageView deviceBtn;

    int userChoice;
    int currentNrOfPlayers;

    DocumentReference databasePlayers;
    DocumentReference databaseChoices;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    String lastUpdatePlayers;
    DocumentSnapshot updatedSnapshotPlayers;

    String lastUpdateChoices;
    DocumentSnapshot updatedSnapshotChoices;

    String msgWinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        databasePlayers = db.collection("game").document("players");
        databasePlayers.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Listen to updates", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    lastUpdatePlayers = snapshot.toString();
                    updatedSnapshotPlayers = snapshot;
                    // Log.d("Listen to updates", "Current data: " + snapshot.getData().toString());
                    notifyNewJoined(snapshot);
                } else {
                    Log.d("Listen to updates", "Current data: null");
                    makeToast("You are the first, let's wait for participants!:)");
                }
            }
        });

        databaseChoices = db.collection("game").document("choices");
        databaseChoices.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Listen to updates", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    lastUpdateChoices = snapshot.toString();
                    updatedSnapshotChoices = snapshot;
                    Log.d("Listen to updates", "Current moves data: " + snapshot.getData().toString());
                    notifyPlayerMoved(snapshot);
                } else {
                    Log.d("Listen to updates", "Current moves data: null");
                }
            }
        });
    }

    public void notifyNewJoined(DocumentSnapshot update) {
        Log.d("Notify new join", "Current data: " + update);
        Map data = updatedSnapshotPlayers.getData();
        currentNrOfPlayers = data.size();
        Log.d("Players ", Integer.toString(data.size()));
        if (data.size() == 0) return;

        String msg = "Players in the game ";
        for (Object player : data.keySet()) {
            msg += " " + player + " ";
        }
        makeToast(msg);
    }

    public void notifyPlayerMoved(DocumentSnapshot update) {
        Log.d("A player moved ", "Current data: " + update);
        Map data = updatedSnapshotChoices.getData();
        Log.d("Number of moves ", Integer.toString(data.size()));
        Log.d("Moves", data.toString());

        int currentNrOfMoves = data.size();

        if (currentNrOfMoves == 0) return;

        if ((currentNrOfMoves == currentNrOfPlayers) && currentNrOfPlayers > 1) {
            HashMap<String, Integer> moves = new HashMap<String, Integer>();

            Set keys = data.keySet();

            for (Object key : keys) {
                if (userName != null && !key.toString().contains(userName)) {
                    String value = data.get(key).toString().replaceAll("[^0-2]", "");
                    int valueInt = Integer.parseInt(value);
                    moves.put(key.toString(), valueInt);
                }
            }

            findWinner(moves);

        }


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

    public void updateChoice(String choice) {

        Map<String, Object> data = new HashMap<>();
        data.put("move", choice);

        Map<String, Object> newEntry = new HashMap<>();
        newEntry.put(userName, data);
        db.collection("game").document("choices").set(newEntry, SetOptions.merge());


        DocumentReference docRef = db.collection("game").document("choices");
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
        } else if (lastUpdatePlayers != null && lastUpdatePlayers.contains(userName)) {
            makeToast("This username is taken, please choose another one!");

        } else {
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
            updateChoice(Integer.toString(userChoice));
        }

    }

    public void findWinner(Map data) {


        Set keys = data.keySet();
        ArrayList<String> keysList = new ArrayList<String>(keys);

        Collection values = data.values();
        ArrayList<Integer> valuesList = new ArrayList<Integer>(values);

        int winnerI = 0;
        for (int i = 0; i < data.size() - 1; i++) {
            winnerI = playTwo(valuesList.get(winnerI), valuesList.get(i++), winnerI, i++);
        }

        if (valuesList.get(winnerI) == userChoice) {
            makeToast("No one wins!");
        } else {
            winnerI = playTwo(valuesList.get(winnerI), userChoice, winnerI, -1);
            if (winnerI == -1) {
                makeToast("You win! " + msgWinner);
            } else {
                makeToast("You lost! " + keysList.get(winnerI) + " wins!" + msgWinner);
            }
        }

    }

    public int playTwo(int currentWinner, int opponent, int currentWinnerI, int opponentI) {

        if (currentWinner == ROCK && opponent == SCISSORS) {
            msgWinner = "Rock crushes scissors!";
            return currentWinnerI;
        } else if (currentWinner == SCISSORS && opponent == PAPER) {
            msgWinner = "Scissors cuts paper!";
            return currentWinnerI;
        } else if (currentWinner == PAPER && opponent == ROCK) {
            msgWinner = "Paper eats rock!";
            return currentWinnerI;
        } else if (currentWinner == SCISSORS && opponent == ROCK) {
            msgWinner = "Rock crushes scissors!";
            return opponentI;
        } else if (currentWinner == PAPER && opponent == SCISSORS) {
            msgWinner = "Scissors cuts paper!";
            return opponentI;
        } else if (currentWinner == ROCK && opponent == PAPER) {
            msgWinner = "Paper eats rock!";
            return opponentI;
        }
        return currentWinner;

    }

    public void playAgainstDevice() {

        Random rand = new Random();
        int deviceChoice = rand.nextInt(3);

        int res = playTwo(deviceChoice, userChoice, 0, 1);
        if (res == 0) {
            msgWinner += " You lost..";
        } else {
            msgWinner += " You won!";
        }

        makeToast(msgWinner);
    }


    public void stopGame(View view) {

        TextView player = findViewById(R.id.textInputUserName);
        player.setFocusable(true);
        player.setEnabled(true);

        Map<String, Object> updates = new HashMap<>();
        updates.put(userName, FieldValue.delete());


        databasePlayers.update(updates)
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


    }
}