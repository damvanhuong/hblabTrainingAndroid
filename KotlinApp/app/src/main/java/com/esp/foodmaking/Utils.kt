package com.esp.foodmaking

import android.content.Context
import org.jetbrains.anko.sp

class Utils {
    companion object {
        fun getWidth(context: Context): Int {
            return context.resources.displayMetrics.widthPixels
        }

        fun getStatusHeight(context: Context): Int {
            val rId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            return if (rId > 0) {
                context.resources.getDimensionPixelSize(rId)
            } else {
                context.sp(25)
            }
        }
    }
}