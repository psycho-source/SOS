package com.tarun.sos.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.tarun.sos.R
import com.tarun.sos.`interface`.FragmentChangeListener

class StartupIntro : Fragment() {

    private lateinit var contents: ConstraintLayout
    private lateinit var startButton: MaterialButton

    private val fragmentChange: FragmentChangeListener by lazy { activity as FragmentChangeListener }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_startup_intro, container, false)

        contents = view.findViewById(R.id.contents)
        startButton = view.findViewById(R.id.start_btn)

        contents.visibility = View.VISIBLE

        startButton.setOnClickListener {
            fragmentChange.changeFragment(PermissionsIntro())
        }

        return view
    }

}