package com.micgo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.micgo.others.OthersActivity;
import com.micgo.exoplayer.ExoPlayerActivity;
import com.micgo.ffmpeg.PrimaryActivity;
import com.micgo.studio.NativeLib;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NativeLib.getInstance();

    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id)
        {
            case R.id.ffmpeg_primary:
                Intent priaryIntent = PrimaryActivity.buildIntent(this);
                startActivity(priaryIntent);
                break;
            case R.id.ffmpeg_lab:
                break;
            case R.id.media_codec:
                break;
            case R.id.exoplayer:
                Intent exoIntent = ExoPlayerActivity.buildIntent(this);
                startActivity(exoIntent);
                break;
            case R.id.others:
                Intent demosIntent = OthersActivity.buildIntent(this);
                startActivity(demosIntent);
                break;
        }
    }
}
