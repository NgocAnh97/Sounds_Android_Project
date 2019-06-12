package com.example.sound_app

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat

class TimerActivityFragment : PreferenceFragmentCompat(){
    @SuppressLint("ResourceType")
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.layout.preferences)
    }
}