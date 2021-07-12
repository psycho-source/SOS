package com.tarun.sos.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.tarun.sos.MainActivity
import com.tarun.sos.R
import com.tarun.sos.utils.EmergencyContacts
import com.tarun.sos.utils.addNewContact
import com.tarun.sos.utils.setFirstStart


class AddContactIntro : Fragment() {

    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var phoneInputLayout: TextInputLayout
    private lateinit var nameInput: TextInputEditText
    private lateinit var phoneInput: TextInputEditText
    private lateinit var saveContact: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_contact_intro, container, false)

        nameInputLayout =
            view.findViewById(R.id.name_field_layout)
        phoneInputLayout =
            view.findViewById(R.id.number_field_layout)
        nameInput =
            view.findViewById(R.id.name_field_input)
        phoneInput =
            view.findViewById(R.id.number_field_input)
        saveContact =
            view.findViewById(R.id.add_contact)

        saveContact.setOnClickListener {
            validateAndSaveEmergencyContact()
        }

        return view
    }

    private fun validateAndSaveEmergencyContact() {
        if (nameInput.text.toString().isNullOrBlank()) {
            nameInputLayout.isErrorEnabled = true
            nameInputLayout.error = "Name Cannot be Empty"
            return
        } else {
            nameInputLayout.isErrorEnabled = false
        }
        if (phoneInput.text.toString().isNullOrBlank()) {
            phoneInputLayout.isErrorEnabled = true
            phoneInputLayout.error = "Phone number cannot be empty"
            return
        } else {
            phoneInputLayout.isErrorEnabled = false
        }
        val newContact = EmergencyContacts(
            0,
            R.drawable.ic_default_person,
            nameInput.text.toString(),
            phoneInput.text.toString(),
            deleteAvailable = false,
            defaultCall = true
        )
        addNewContact(requireContext(), newContact)
        setFirstStart(context, false)
        activity?.startActivity(Intent(context, MainActivity::class.java))
        activity?.finishAfterTransition()
    }

}