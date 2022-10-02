package com.maximshuhman.bsuirschedule

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

object Common {
    fun setProgressDialog(context: Context, text: String = "Загрузка..."): AlertDialog {
        val llPadding = 30

        val lParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lParam.gravity = Gravity.CENTER

        val lLayout = LinearLayout(context)
        with(lLayout) {
            orientation = LinearLayout.HORIZONTAL
            setPadding(llPadding, llPadding, llPadding, llPadding)
            gravity = Gravity.CENTER
            layoutParams = lParam
        }

        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = lParam

        val tvText = TextView(context)
        tvText.text = text
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 20f
        tvText.layoutParams = lParam

        lLayout.addView(progressBar)
        lLayout.addView(tvText)

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setView(lLayout)

        val dialog: AlertDialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()

        val window: Window? = dialog.getWindow()
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams
        }

        return dialog
    }

}