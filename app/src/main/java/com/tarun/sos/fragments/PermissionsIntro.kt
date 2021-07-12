package com.tarun.sos.fragments

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.tarun.sos.R

class PermissionsIntro : Fragment() {

    private lateinit var allowPerms: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_permissions_intro, container, false)

        allowPerms = view.findViewById(R.id.allow_perms)

        allowPerms.setOnClickListener {
            com.tarun.sos.utils.requestPermissions(
                context as AppCompatActivity,
                4,
                arrayOf(
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }

        return view
    }


}