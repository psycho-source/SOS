package com.tarun.sos.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.tarun.sos.MainActivity
import com.tarun.sos.R

class PermissionErrorDialog : Fragment() {

    private lateinit var settingsButton: TextView
    private lateinit var closeButton: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.dialog_permission_error, container, false)

        settingsButton = view.findViewById(R.id.settings_button)
        closeButton = view.findViewById(R.id.close_button)

        settingsButton.setOnClickListener {
            MainActivity.showPermissionError.dismiss()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }

        closeButton.setOnClickListener {
            activity?.finish()
        }

        return view

    }

}