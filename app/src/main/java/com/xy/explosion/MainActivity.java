package com.xy.explosion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.xy.explosion.widget.ExplosionLayout;

public class MainActivity extends AppCompatActivity {

    private ExplosionLayout mExplosionLayout;
    private Button mStartBtn;
    private Button mEndBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mExplosionLayout = (ExplosionLayout) findViewById(R.id.explosion_layout);
        mStartBtn = (Button) findViewById(R.id.btn_start);
        mEndBtn = (Button) findViewById(R.id.btn_end);
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExplosionLayout.startAnim();
            }
        });
        mEndBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExplosionLayout.endAnim();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mExplosionLayout.onDestroy();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
