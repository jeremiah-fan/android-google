package com.example.jerm.scarnedice;

import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import java.util.Random;

public class ScarneActivity extends AppCompatActivity {
    private final int PLAYER = 0, COMPUTER = 1;
    private int playerRoundScore, playerTotalScore, computerRoundScore, computerTotalScore;
    private Random rand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scarne);
        reset();
        rand = new Random();
    }

    public void reset(){
        playerRoundScore = 0;
        playerTotalScore = 0;
        computerRoundScore = 0;
        computerTotalScore = 0;
        setGameText();
        ((ImageView) findViewById(R.id.dice1)).setImageDrawable(getResources().getDrawable(R.drawable.dice1, getApplicationContext().getTheme()));
        ((ImageView) findViewById(R.id.dice2)).setImageDrawable(getResources().getDrawable(R.drawable.dice1, getApplicationContext().getTheme()));
        ((TextView) findViewById(R.id.turn)).setText("Player Turn");
        ((Button) findViewById(R.id.rollBtn)).setEnabled(true);
        ((Button) findViewById(R.id.holdBtn)).setEnabled(true);
        Log.d("Player Turn", "Begin");
    }

    public boolean roll(int player){
        assert(player == PLAYER || player == COMPUTER);

        ImageView dice1 = (ImageView) findViewById(R.id.dice1);
        ImageView dice2 = (ImageView) findViewById(R.id.dice2);

        int value1 = rand.nextInt(6) + 1;
        int value2 = rand.nextInt(6) + 1;
        int imageResource1 = getResources().getIdentifier("@drawable/dice" + value1, null, getPackageName());
        int imageResource2 = getResources().getIdentifier("@drawable/dice" + value2, null, getPackageName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dice1.setImageDrawable(getResources().getDrawable(imageResource1, getApplicationContext().getTheme()));
            dice2.setImageDrawable(getResources().getDrawable(imageResource2, getApplicationContext().getTheme()));
        } else {
            dice1.setImageDrawable(getResources().getDrawable(imageResource1));
            dice2.setImageDrawable(getResources().getDrawable(imageResource2));
        }

        if (player == PLAYER){
            if (value1 == 1 || value2 == 1) {
                playerRoundScore = 0;
                if(value1 == 1 && value2 == 1)
                    playerTotalScore = 0;
            }
            else {
                playerRoundScore += value1 + value2;
                Button holdBtn = (Button) findViewById(R.id.holdBtn);
                if (value1 == value2)
                    holdBtn.setEnabled(false);
                else
                    holdBtn.setEnabled(true);
            }
            Log.d("Player Roll", Integer.toString(value1) + " and " + Integer.toString(value2));
        } else {
            if (value1 == 1 || value2 == 1) {
                computerRoundScore = 0;
                if (value1 == 1 && value2 == 1)
                    computerTotalScore = 0;
            }
            else {
                computerRoundScore += value1 + value2;
            }
            Log.d("Computer Roll", Integer.toString(value1) + " and " + Integer.toString(value2));
        }
        setGameText();
        return value1 == 1 || value2 == 1;
    }

    public void hold(int player) {
        assert (player == PLAYER || player == COMPUTER);
        if (player == PLAYER) {
            playerTotalScore += playerRoundScore;
            playerRoundScore = 0;
        } else {
            computerTotalScore += computerRoundScore;
            computerRoundScore = 0;
            Log.d("Player Turn", "Begin");
            ((TextView) findViewById(R.id.turn)).setText("Player Turn");
        }
        setGameText();
    }

    public void setGameText(){
        TextView gameStatus = (TextView) findViewById(R.id.game_status);
        String gametext = "Your score: " + playerTotalScore + " Computer score: " + computerTotalScore;
        if (playerRoundScore != 0)
            gametext += " Your turn score: " + playerRoundScore;
        else if (computerRoundScore != 0)
            gametext += " Computer turn score: " + computerRoundScore;
        gameStatus.setText(gametext);
    }

    public void rollBtn(View view){
        if (roll(PLAYER)) // Turn is over
            computerTurn();
    }

    public void holdBtn(View view){
        hold(PLAYER);
        if (playerTotalScore >= 100 || computerTotalScore >= 100){ //Game over
            endGame();
        }else{
            computerTurn();
        }
    }

    public void resetBtn(View view){
        reset();
    }

    public void computerTurn(){
        Log.d("Computer Turn", "Begin");
        ((TextView) findViewById(R.id.turn)).setText("Computer Turn");
        final Button rollBtn = (Button) findViewById(R.id.rollBtn);
        final Button holdBtn = (Button) findViewById(R.id.holdBtn);
        rollBtn.setEnabled(false); // Disable roll and hold buttons
        holdBtn.setEnabled(false);

        final Handler h = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run(){
                boolean endTurn = roll(COMPUTER);
                if (computerRoundScore < 20 && !endTurn)
                    h.postDelayed(this, 600);
                else { // End of computer turn
                    hold(COMPUTER);
                    if (playerTotalScore >= 100 || computerTotalScore >= 100){ // Game over
                        endGame();
                    }else{
                        rollBtn.setEnabled(true); // Re-enable roll and hold buttons
                        holdBtn.setEnabled(true);
                    }
                }
            }
        };
        h.postDelayed(r, 1000);
    }

    public void endGame(){
        ((Button) findViewById(R.id.rollBtn)).setEnabled(false);
        ((Button) findViewById(R.id.holdBtn)).setEnabled(false);
        if (playerTotalScore >= 100)
            ((TextView) findViewById(R.id.turn)).setText("Congratulations! You win!");
        else
            ((TextView) findViewById(R.id.turn)).setText("Sorry! You lose!");
    }
}
