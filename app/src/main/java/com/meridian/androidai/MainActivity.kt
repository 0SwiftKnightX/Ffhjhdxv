package com.meridian.androidai

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.meridian.androidai.runtime.MeridianRuntime

class MainActivity : Activity() {
    private lateinit var runtime: MeridianRuntime
    private lateinit var stateView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runtime = MeridianRuntime(applicationContext)
        runtime.start()
        stateView = TextView(this).apply {
            textSize = 18f
            text = runtime.snapshot().summary()
            setPadding(32, 48, 32, 32)
        }
        val stopButton = Button(this).apply {
            text = "STOP"
            setOnClickListener {
                runtime.stopAutonomousWork()
                stateView.text = runtime.snapshot().summary()
            }
        }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(stateView, LinearLayout.LayoutParams(-1, 0, 1f))
            addView(stopButton, LinearLayout.LayoutParams(-1, -2))
        }
        setContentView(root)
    }

    override fun onDestroy() {
        runtime.shutdown()
        super.onDestroy()
    }
}
