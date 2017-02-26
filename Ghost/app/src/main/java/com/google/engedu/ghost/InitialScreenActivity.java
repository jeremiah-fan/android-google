package com.google.engedu.ghost;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class InitialScreenActivity extends AppCompatActivity {
    public static final int EASYMODE = 0;
    public static final int HARDMODE = 1;
    public final static String GAMEMODE = "com.google.engedu.ghost.GAMEMODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

       //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.activity_initial_screen);
    }

    public void startGame(View view){
        Intent intent = new Intent(this, GhostActivity.class);
        if (view.getId() == R.id.hard)
            intent.putExtra(GAMEMODE, HARDMODE);
        else if (view.getId() == R.id.easy)
            intent.putExtra(GAMEMODE, EASYMODE);
        startActivity(intent);
    }
}
