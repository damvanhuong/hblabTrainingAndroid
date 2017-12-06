package com.esp.foodmaking

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView

class FeedbackDialog(context: Context) : Dialog(context) {

    var dialogInterface : DialogInterface? = null

    interface DialogInterface {
        fun onCancel(dialog: FeedbackDialog)
        fun onSend(message : String, dialog: FeedbackDialog)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.send_dialog)
        val feedbackEdt = findViewById<EditText>(R.id.feedbackEdt)
        val emptyTextView = findViewById<TextView>(R.id.emptyTextView)
        findViewById<TextView>(R.id.cancel_button).setOnClickListener {
            if (dialogInterface != null) {
                dialogInterface?.onCancel(this)
            }
        }
        findViewById<TextView>(R.id.send_button).setOnClickListener {
            val str = feedbackEdt.text.toString().trim()
            if (str.isEmpty()) {
                emptyTextView.visibility = View.VISIBLE
            } else {
                emptyTextView.visibility = View.VISIBLE
                dialogInterface?.onSend(str, this)
            }
        }
        feedbackEdt.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(text: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (text != null && text.isEmpty()) {
                    emptyTextView.visibility = View.GONE
                }
            }
        })
    }

    fun setOnClickListener(dialogInterface: DialogInterface) {
        this.dialogInterface = dialogInterface
    }
}