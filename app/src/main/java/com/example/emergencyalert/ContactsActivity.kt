package com.example.emergencyalert

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.io.File
import java.io.Serializable

class ContactsActivity : AppCompatActivity() {

    private var contacts:MutableList<Contact> = mutableListOf()
    private lateinit var recyclerViewContacts: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manage_contacts)

        contacts = Utils2.loadContacts(applicationContext) // retrieve contacts from file

        recyclerViewContacts = findViewById(R.id.recyclerViewContacts)
        recyclerViewContacts.layoutManager = LinearLayoutManager(this)
        recyclerViewContacts.adapter = ContactAdapter(contacts)

    }

    private inner class ContactAdapter(private val contactList: List<Contact>) :
        RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

        // Define the ViewHolder for each contact item in the RecyclerView
        inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameTextView: TextView = itemView.findViewById(R.id.textViewContactName)
            val phoneTextView: TextView = itemView.findViewById(R.id.textViewContactNumber)
            val buttonDelete: Button = itemView.findViewById<Button>(R.id.button_delete)
            val buttonPersonalizeMessage: Button = itemView.findViewById<Button>(R.id.button_editPersonalizedMessage)
        }

        // Create a new ViewHolder for each contact item in the RecyclerView
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_contact, parent, false)
            return ContactViewHolder(itemView)
        }

        // Bind the data for each contact item to its corresponding ViewHolder
        override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
            val currentContact = contactList[position]
            holder.nameTextView.text = currentContact.name
            holder.phoneTextView.text = currentContact.phoneNumber
            holder.itemView.setOnClickListener {
                holder.buttonDelete.visibility = if (holder.buttonDelete.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                holder.buttonPersonalizeMessage.visibility = if (holder.buttonPersonalizeMessage.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }

            holder.buttonDelete.setOnClickListener {
                // Delete the contact from the list
                contacts.remove(currentContact) // Update the original list
                notifyItemRemoved(position)
                Utils2.writeToFile(File(applicationContext.filesDir, "contacts.txt"), Gson().toJson(contacts))
                notifyDataSetChanged() // Notify the adapter that the data has changed
                holder.buttonDelete.visibility = View.GONE
            }

            holder.buttonPersonalizeMessage.setOnClickListener{
                // TODO
                // Add intent to pull new Activity where the user will edit the personalized message.
                val intent = Intent(this@ContactsActivity, EditPersonalizedMessageActivity::class.java)
                intent.putExtra("CONTACT", currentContact as Serializable)
                intent.putExtra("CONTACT_LIST", contacts as Serializable)
                startActivityForResult(intent, 5)
            }
        }


        // Return the number of contact items in the RecyclerView
        override fun getItemCount() = contactList.size
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("request code", requestCode.toString())
        if (requestCode == 5 && resultCode == RESULT_OK) {
            // Reload the contacts list from file
            contacts = Utils2.loadContacts(applicationContext)

            // Update the existing RecyclerView adapter with the new list
            recyclerViewContacts.adapter?.notifyDataSetChanged()
        }
    }
}