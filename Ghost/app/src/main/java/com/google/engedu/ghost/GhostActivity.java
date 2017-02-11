/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.concurrent.locks.Lock;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import android.util.Log;

import org.w3c.dom.Text;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private boolean gameOver = false;
    private Random random = new Random();
    private String wordFragment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);

        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            dictionary = new SimpleDictionary(inputStream);
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }

        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        gameOver = false;

        wordFragment = "";
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText(wordFragment);

        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
            Log.d("New Game", "User goes first");
            Log.d("Turn Begin", "User Turn");
        } else {
            label.setText(COMPUTER_TURN);
            Log.d("New Game", "Computer goes first");
            computerTurn();
        }

        ((Button) findViewById(R.id.challengeBtn)).setEnabled(true);
        return true;
    }

    public void challenge(View view){
        ((TextView) findViewById(R.id.gameStatus)).setText(userTurn ? "You have issued a challenge!" : "Your opponent challenged you!");

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView gameStatus = (TextView) findViewById(R.id.gameStatus);
                TextView ghostText = (TextView) findViewById(R.id.ghostText);
                boolean callerWins = dictionary.isWord(wordFragment) || dictionary.getAnyWordStartingWith(wordFragment) == null;

                Log.d("Challenger", userTurn ? "User" : "Computer");
                if(userTurn){
                    if(callerWins) {
                        gameStatus.setText("Congratulations! You win!");
                    }
                    else {
                        gameStatus.setText("Sorry! You lose.");
                        ghostText.setText(Html.fromHtml(wordFragment +
                                "<font color=#ff0000>" +
                                dictionary.getAnyWordStartingWith(wordFragment).substring(wordFragment.length()) +
                                "</font>"));
                    }
                } else {
                    if(callerWins) {
                        gameStatus.setText("Sorry! You lose.");
                    }
                    else {
                        gameStatus.setText("Congratulations! You win!");
                        ghostText.setText(dictionary.getAnyWordStartingWith(wordFragment));
                        ghostText.setText(Html.fromHtml(wordFragment +
                                "<font color=#ff0000>" +
                                dictionary.getAnyWordStartingWith(wordFragment).substring(wordFragment.length()) +
                                "</font>"));
                    }
                }

                gameOver = true;
                ((Button) findViewById(R.id.challengeBtn)).setEnabled(false);
            }
        }, 1000);
    }
    private void computerTurn() {
        Log.d("Turn Begin", "Computer Turn");
        ((Button) findViewById(R.id.challengeBtn)).setEnabled(false);
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            private int counter = 0;
            private int maxCounter = random.nextInt(6) + 1; // 1 to 6 "thinking dots"

            @Override
            public void run() {
                TextView gameStatus = (TextView) findViewById(R.id.gameStatus);
                TextView ghostText = (TextView) findViewById(R.id.ghostText);
                if (counter < maxCounter){
                    String thinkingText = "";
                    for(int i = 0; i <= counter % 3; i++)
                        thinkingText += '.';
                    ghostText.setText(wordFragment + thinkingText);
                    counter++;
                    h.postDelayed(this, 250);
                }
                else {
                    ghostText.setText(wordFragment);
                    if (dictionary.isWord(wordFragment)) {
                        Log.d("Challenge Issued", "Word is in dictionary!");
                        challenge(null);
                    } else {
                        String wordToBuild = dictionary.getAnyWordStartingWith(wordFragment);
                        if (wordToBuild == null) {
                            Log.d("Challenge Issued", "No such word exists!");
                            challenge(null);
                        } else {
                            Log.d("Word Chosen", wordToBuild);
                            Log.d("Key Pressed", "" + wordToBuild.charAt(wordFragment.length()));
                            wordFragment += wordToBuild.charAt(wordFragment.length());
                            ghostText.setText(wordFragment);

                            userTurn = true;
                            gameStatus.setText(USER_TURN);
                            ((Button) findViewById(R.id.challengeBtn)).setEnabled(true);
                            Log.d("Turn Begin", "User Turn");
                        }
                    }
                }
            }
        }, 500);
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(!gameOver && userTurn) {
            char key = (char) event.getUnicodeChar();
            TextView gameStatus = (TextView) findViewById(R.id.gameStatus);
            TextView ghostText = (TextView) findViewById(R.id.ghostText);

            if ((key >= 'a' && key <= 'z') || (key >= 'A' && key <= 'Z')) { // Key press is letter
                Log.d("Key Pressed", "" + key);
                wordFragment += key;
                ghostText.setText(wordFragment);

                userTurn = false;
                gameStatus.setText(COMPUTER_TURN);
                computerTurn();
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}
