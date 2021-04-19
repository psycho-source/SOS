package com.tarun.sos.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tarun.sos.MainActivity
import com.tarun.sos.R
import com.tarun.sos.adapters.EmergencyContactsAdapter

class BottomNavigationDrawer : Fragment() {

    private lateinit var contactsRecycler: RecyclerView
    private val contactsAdapter: EmergencyContactsAdapter by lazy {
        EmergencyContactsAdapter(
            context,
            MainActivity.emergencyContacts
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_bottom_navigation_drawer, container, false)

        contactsRecycler = view.findViewById(R.id.contact_recycler)

        contactsRecycler.layoutManager = LinearLayoutManager(this.context)
        contactsRecycler.adapter = contactsAdapter

        return view

    }

}