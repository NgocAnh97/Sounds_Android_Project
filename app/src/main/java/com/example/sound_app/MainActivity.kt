package com.example.sound_app

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSetting.setOnClickListener {
            val intent = Intent(this, TimerActivity::class.java)
            // start your next activity
            startActivity(intent)
        }
    }
}
