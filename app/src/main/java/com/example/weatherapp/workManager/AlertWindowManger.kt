package com.example.weatherapp.workManager

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.example.weatherapp.R
import com.example.weatherapp.databinding.AlertWindowMangerBinding
import com.example.weatherapp.utility.getIcon

class AlertWindowManger (
    private val context: Context, private val description: String, private val icon: String)
{

    private var windowManager: WindowManager? = null
    private var customNotificationDialogView: View? = null
    private var binding: AlertWindowMangerBinding? = null

    fun setMyWindowManger() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        customNotificationDialogView =
            inflater.inflate(R.layout.alert_window_manger, null)
        binding = AlertWindowMangerBinding.bind(customNotificationDialogView!!)
        bindView()
        val LAYOUT_FLAG: Int
        LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width = (context.resources.displayMetrics.widthPixels * 0.85).toInt()
        val params = WindowManager.LayoutParams(
            width,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LOCAL_FOCUS_MODE,
            PixelFormat.TRANSLUCENT
        )
        windowManager!!.addView(customNotificationDialogView, params)
    }

    private fun bindView() {
        binding?.imageIcon?.setImageResource(getIcon(icon))
        binding?.textDescription?.text = description
        binding?.btnOk?.text = context.getString(R.string.btn_ok)
        binding?.btnOk?.setOnClickListener {
            close()
            stopMyService()
        }
    }

    private fun close() {
        try {
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(
                customNotificationDialogView
            )
            customNotificationDialogView!!.invalidate()
            (customNotificationDialogView!!.parent as ViewGroup).removeAllViews()
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
    }

    private fun stopMyService() {
        context.stopService(Intent(context, AlertService::class.java))
    }

}