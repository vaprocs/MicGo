package com.micgo.opengl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.micgo.R;
import com.micgo.opengl.trans.OpenGLTansActivity;

/**
 * Created by liuhongtian on 18/1/10.
 */

public class OpenGLActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opengl);

        setTitle("OpenGL");
    }

    public void onClick(View view) {
        int id = view.getId();
        switch (id)
        {
            case R.id.trans:
                Intent transIntent = OpenGLTansActivity.buildIntent(this);
                startActivity(transIntent);
                break;
            default:

                break;
        }
    }

    public static Intent buildIntent(Context context) {
        Intent intent = new Intent(context, OpenGLActivity.class);
        return intent;
    }

}