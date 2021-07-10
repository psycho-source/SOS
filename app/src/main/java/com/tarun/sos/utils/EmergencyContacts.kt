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
    var deleteAvailable: Boolean,
    var defaultCall: Boolean = false
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
            R.drawable.ic_default_person,
            contactObject.getString("name"),
            contactObject.getString("phone"),
            deleteAvailable = contactObject.getBoolean("deleteAvailable"),
            defaultCall = contactObject.getBoolean("defaultCall")
        )
        contacts.add(contact)
    }
    contacts.add(EmergencyContacts(-1, R.drawable.ic_contact_add, "Add New Contact", "", false))
    return contacts
}

fun getEmergencyCallContact(context: Context): EmergencyContacts? {
    val sharedPreferences =
        context.getSharedPreferences(EMERGENCY_CONTACTS_FILE, Context.MODE_PRIVATE)
    val contactsJSON = JSONArray(sharedPreferences.getString("contacts", "[]"))
    for (i in 0 until contactsJSON.length()) {
        val contactObject = contactsJSON.getJSONObject(i)
        if (contactObject.getBoolean("defaultCall")) {
            return EmergencyContacts(
                i,
                R.drawable.ic_default_person,
                contactObject.getString("name"),
                contactObject.getString("phone"),
                deleteAvailable = contactObject.getBoolean("deleteAvailable"),
                defaultCall = contactObject.getBoolean("defaultCall")
            )
        }
    }
    return null
}

fun addNewContact(context: Context, contact: EmergencyContacts) {
    val sharedPreferences =
        context.getSharedPreferences(EMERGENCY_CONTACTS_FILE, Context.MODE_PRIVATE)
    val contactsJSON = JSONArray(sharedPreferences.getString("contacts", "[]"))
    val contactObject = JSONObject()
    contactObject.put("id", contactsJSON.length())
    contactObject.put("name", contact.name)
    contactObject.put("phone", contact.phone)
    contactObject.put("deleteAvailable", contact.deleteAvailable)
    contactObject.put("defaultCall", contact.defaultCall)
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

fun updateDefaultCall(context: Context, id: Int) {
    val sharedPreferences =
        context.getSharedPreferences(EMERGENCY_CONTACTS_FILE, Context.MODE_PRIVATE)
    val contactsJSON = JSONArray(sharedPreferences.getString("contacts", "[]"))
    for (i in 0 until contactsJSON.length()) {
        val contact = contactsJSON.getJSONObject(i)
        if (contact.getBoolean("defaultCall")) {
            contact.put("defaultCall", false)
            contact.put("deleteAvailable", true)
            contactsJSON.remove(i)
            contactsJSON.put(contact)
            break
        }
    }
    for (i in 0 until contactsJSON.length()) {
        val contact = contactsJSON.getJSONObject(i)
        if (contact.getInt("id") == id) {
            contact.put("defaultCall", true)
            contact.put("deleteAvailable", false)
            contactsJSON.remove(i)
            contactsJSON.put(contact)
            break
        }
    }
    with(sharedPreferences.edit()) {
        putString("contacts", contactsJSON.toString())
        commit()
    }
}