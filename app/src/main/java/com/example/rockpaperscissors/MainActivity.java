package com.example.rockpaperscissors;
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
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    String userName;
    final int ROCK = 0;
    final int SCISSORS = 1;
    final int PAPER = 2;

    ImageView clickedBtn;
    ImageView deviceBtn;

    int deviceChoice;
    int userChoice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void startGame(View view) {
        CheckBox againstDevice = findViewById(R.id.checkBox);
        TextView player = findViewById(R.id.textInputUserName);
        userName = player.getText().toString();
        if(userName.length() == 0){
            Toast toast = Toast.makeText(this, "Please enter you name first", Toast.LENGTH_LONG);
            toast.show();
        }else if(againstDevice.isChecked()){
            View gameChoices = findViewById(R.id.gameChoices);
            gameChoices.setVisibility(View.VISIBLE);
        }else{
            // Start listening to other devices
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
        playAgainstDevice();
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
        playAgainstDevice();
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
        playAgainstDevice();
    }

    public void playAgainstDevice(){
        Random rand = new Random();
        deviceChoice = rand.nextInt(3);

        switch (deviceChoice){
            case ROCK:
                deviceBtn = findViewById(R.id.imageViewRock);
            case PAPER:
                deviceBtn = findViewById(R.id.imageViewPaper);
            case SCISSORS:
                deviceBtn = findViewById(R.id.imageViewScissors);

        }

        Log.d("choices", "" + userChoice + " " + deviceChoice);
        String result = "";
        if(deviceChoice == userChoice){
            result = "No one wins!";
            Toast toast = Toast.makeText(this, result, Toast.LENGTH_LONG);
            toast.show();
            return;
        }else if((deviceChoice == ROCK && userChoice == SCISSORS)
                || (deviceChoice == SCISSORS && userChoice == PAPER)
                || (deviceChoice == PAPER && userChoice == ROCK)){
            result = "You lost!";
        }else if((deviceChoice == SCISSORS && userChoice == ROCK)
                || (deviceChoice == PAPER && userChoice == SCISSORS)
                || (deviceChoice == ROCK && userChoice == PAPER)){
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


}