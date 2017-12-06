package com.esp.weather.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.esp.weather.R;

public class ProgressDialog extends Dialog {

    private TextView textView;
    private ProgressBar progressBar;

    public ProgressDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.progress_dialog);
        textView = findViewById(R.id.message_pd);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setIndeterminate(true);
        progressBar.setProgress(10);
    }

    public void setMessage(String text) {
        if (text == null) {
            text = "";
        }
        textView.setText(text);
    }

    public void setProgressBarColor(int color) {
        progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }
}
