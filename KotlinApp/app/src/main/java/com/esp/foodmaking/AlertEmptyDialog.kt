package com.esp.foodmaking

import android.app.Dialog
import android.content.Context
import android.view.View


class AlertEmptyDialog(context: Context) : Dialog(context) {

    private var onCancelClickInterface: OnCancelClickInterface? = null

    interface OnCancelClickInterface {
        fun onClick()
    }

    init {
        setContentView(R.layout.alert_dialog)
        findViewById<View>(R.id.cancel_button).setOnClickListener {
            if (onCancelClickInterface != null) {
                onCancelClickInterface!!.onClick()
            }
        }
    }

    fun setOnCancelClickInterface(onCancelClickInterface: OnCancelClickInterface) {
        this.onCancelClickInterface = onCancelClickInterface
    }
}
