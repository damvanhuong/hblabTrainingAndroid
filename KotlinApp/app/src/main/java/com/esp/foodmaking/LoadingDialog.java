package com.esp.foodmaking;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;

public class LoadingDialog extends Dialog{

    private TextView loadingTv;
    int i = 0;
    Handler handler = new Handler();

    public LoadingDialog(@NonNull Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_dialog);
        loadingTv = findViewById(R.id.loading);
        updateText();
    }

    private void updateText() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (i == 0) {
                    loadingTv.setText("Loading.");
                    i = 1;
                    updateText();
                } else if (i == 1){
                    loadingTv.setText("Loading..");
                    i = 2;
                    updateText();
                } else {
                    loadingTv.setText("Loading...");
                    i = 0;
                    updateText();
                }
            }
        }, 500);
    }
}
