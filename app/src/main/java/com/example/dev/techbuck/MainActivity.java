package com.example.dev.techbuck;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    ImageView img;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        session = new Session(MainActivity.this);
        img = findViewById(R.id.logo3);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        img.setAnimation(animation);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                String unm;
                unm = session.checkLogin();

                if (unm.equalsIgnoreCase("")) {
                    startActivity(new Intent(MainActivity.this, login.class));
                    finish();
                } else {
                    startActivity(new Intent(MainActivity.this, Home.class));
                    finish();
                }
            }
        }, 4000);


    }
}
