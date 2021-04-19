package com.tarun.sos.utils

import android.content.Context
import com.tarun.sos.R
import org.json.JSONArray
import org.json.JSONObject

data class EmergencyContacts(
    var id: Int,
    var image: Int,
    var name: String,
    var phone: String,
    var deleteAvailable: Boolean
)

const val EMERGENCY_CONTACTS_FILE: String = "EMERGENCY_CONTACTS_LIST"

fun getEmergencyContacts(context: Context): ArrayList<EmergencyContacts> {
    val contacts = ArrayList<EmergencyContacts>()
    val sharedPreferences =
        context.getSharedPreferences(EMERGENCY_CONTACTS_FILE, Context.MODE_PRIVATE)
    val contactsJSON = JSONArray(sharedPreferences.getString("contacts", "[]"))
    for (i in 0 until contactsJSON.length()) {
        val contactObject = contactsJSON.getJSONObject(i)
        val contact = EmergencyContacts(
            i,
            R.drawable.ic_default_contact,
            contactObject.getString("name"),
            contactObject.getString("phone"),
            true
        )
        contacts.add(contact)
    }
    contacts.add(EmergencyContacts(-1, R.drawable.ic_contact_add, "Add New Contact", "", false))
    return contacts
}

fun addNewContact(context: Context, contact: EmergencyContacts) {
    val sharedPreferences =
        context.getSharedPreferences(EMERGENCY_CONTACTS_FILE, Context.MODE_PRIVATE)
    val contactsJSON = JSONArray(sharedPreferences.getString("contacts", "[]"))
    val contactObject = JSONObject()
    contactObject.putOpt("id", contactsJSON.length())
    contactObject.putOpt("name", contact.name)
    contactObject.putOpt("phone", contact.phone)
    contactsJSON.put(contactObject)
    with(sharedPreferences.edit()) {
        putString("contacts", contactsJSON.toString())
        commit()
    }
}

fun deleteContact(context: Context, id: Int) {
    val sharedPreferences =
        context.getSharedPreferences(EMERGENCY_CONTACTS_FILE, Context.MODE_PRIVATE)
    val contactsJSON = JSONArray(sharedPreferences.getString("contacts", "[]"))
    for (i in 0 until contactsJSON.length()) {
        val contact = contactsJSON.getJSONObject(i)
        if (contact.getInt("id") == id) {
            contactsJSON.remove(i)
            break
        }
    }
    with(sharedPreferences.edit()) {
        putString("contacts", contactsJSON.toString())
        commit()
    }
}