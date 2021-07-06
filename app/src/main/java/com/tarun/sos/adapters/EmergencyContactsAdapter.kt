package com.tarun.sos.adapters

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.tarun.sos.MainActivity
import com.tarun.sos.R
import com.tarun.sos.utils.EmergencyContacts
import com.tarun.sos.utils.addNewContact
import com.tarun.sos.utils.deleteContact
import com.tarun.sos.utils.updateDefaultCall
import de.hdodenhof.circleimageview.CircleImageView

class EmergencyContactsAdapter(
    private val context: Context?,
    private val contacts: ArrayList<EmergencyContacts>
) : RecyclerView.Adapter<EmergencyContactsAdapter.ContactHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder =
        ContactHolder(
            LayoutInflater.from(context).inflate(R.layout.item_emergency_contact, parent, false),
            this
        )

    override fun onBindViewHolder(holder: ContactHolder, position: Int) = holder.bindView(position)

    override fun getItemCount() = contacts.size

    class ContactHolder(private val view: View, private val adapter: EmergencyContactsAdapter) :
        RecyclerView.ViewHolder(view) {

        private val contactItemRow: ConstraintLayout by lazy { view.findViewById(R.id.contact_item_root) }
        private val contactImage: CircleImageView by lazy { view.findViewById(R.id.image_contact) }
        private val contactName: TextView by lazy { view.findViewById(R.id.name_contact) }
        private val contactPhone: TextView by lazy { view.findViewById(R.id.number_contact) }
        private val contactDeleteButton: ImageView by lazy { view.findViewById(R.id.delete_contact) }
        private val contactDefaultCall: ImageView by lazy { view.findViewById(R.id.default_contact) }

        fun bindView(position: Int) {
            val contact = adapter.contacts[position]
            contactImage.setImageResource(contact.image)
            contactName.text = contact.name
            contactPhone.text = contact.phone
            contactDefaultCall.visibility =
                if (contact.defaultCall) View.VISIBLE else View.GONE
            contactDeleteButton.visibility =
                if (contact.deleteAvailable) View.VISIBLE else View.GONE
            if (contact.phone.isBlank()) contactPhone.visibility = View.GONE

            contactItemRow.setOnClickListener {
                if (adapter.context != null && !contact.deleteAvailable) {
                    val alertDialogBuilder = AlertDialog.Builder(adapter.context)
                    alertDialogBuilder.setTitle("Add New Contact")
                    val dialogView = LayoutInflater.from(adapter.context)
                        .inflate(R.layout.layout_add_contact_dialog, null)
                    alertDialogBuilder.setView(dialogView)
                    alertDialogBuilder.setPositiveButton("Add") { _, _ ->
                        val nameInputLayout: TextInputLayout =
                            dialogView.findViewById(R.id.name_field_layout)
                        val phoneInputLayout: TextInputLayout =
                            dialogView.findViewById(R.id.number_field_layout)
                        val nameInput: TextInputEditText =
                            dialogView.findViewById(R.id.name_field_input)
                        val phoneInput: TextInputEditText =
                            dialogView.findViewById(R.id.number_field_input)
                        if (nameInput.text.toString().isNullOrBlank()) {
                            nameInputLayout.isErrorEnabled = true
                            nameInputLayout.error = "Name Cannot be Empty"
                            return@setPositiveButton
                        } else {
                            nameInputLayout.isErrorEnabled = false
                        }
                        if (phoneInput.text.toString().isNullOrBlank()) {
                            phoneInputLayout.isErrorEnabled = true
                            phoneInputLayout.error = "Phone number cannot be empty"
                            return@setPositiveButton
                        } else {
                            phoneInputLayout.isErrorEnabled = false
                        }
                        val newContact = EmergencyContacts(
                            adapter.contacts.size - 1,
                            R.drawable.ic_default_person,
                            nameInput.text.toString(),
                            phoneInput.text.toString(),
                            deleteAvailable = (adapter.contacts.size != 1),
                            defaultCall = (adapter.contacts.size == 1)
                        )
                        adapter.contacts.add(adapter.contacts.lastIndex, newContact)
                        if (newContact.defaultCall)
                            MainActivity.defaultCallingContact = newContact
                        adapter.notifyItemInserted(adapter.contacts.lastIndex - 1)
                        addNewContact(adapter.context, newContact)
                    }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.cancel()
                        }
                    val dialog = alertDialogBuilder.create()
                    dialog.window?.setBackgroundDrawableResource(R.drawable.alert_diag_back)
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                        adapter.context.resources.getColor(
                            R.color.colorDanger,
                            adapter.context.theme
                        )
                    )
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                        adapter.context.resources.getColor(
                            R.color.colorAccent,
                            adapter.context.theme
                        )
                    )
                }
            }

            contactItemRow.setOnLongClickListener {
                if (adapter.context != null && contact.id >= 0 && !contact.defaultCall) {
                    if (MainActivity.defaultCallingContact != null) {
                        val defaultContactPosition = adapter.contacts.indexOf(MainActivity.defaultCallingContact)
                        adapter.contacts[defaultContactPosition].deleteAvailable = true
                        adapter.contacts[defaultContactPosition].defaultCall = false
                        adapter.notifyItemChanged(defaultContactPosition)
                    }
                    contact.deleteAvailable = false
                    contact.defaultCall = true
                    MainActivity.defaultCallingContact = contact
                    adapter.contacts[position] = contact
                    adapter.notifyItemChanged(position)
                    updateDefaultCall(adapter.context, contact.id)
                }
                return@setOnLongClickListener true
            }

            contactDeleteButton.setOnClickListener {
                if (adapter.context != null) {
                    adapter.contacts.remove(contact)
                    adapter.notifyItemRemoved(position)
                    deleteContact(adapter.context, contact.id)
                }
            }

        }

    }

}